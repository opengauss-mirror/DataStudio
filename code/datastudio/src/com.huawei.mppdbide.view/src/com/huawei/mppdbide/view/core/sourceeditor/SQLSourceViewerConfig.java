/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import javax.annotation.PreDestroy;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.keywordssyntax.KeywordsFactoryProvider;
import com.huawei.mppdbide.adapter.keywordssyntax.KeywordsToTrieConverter;
import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.comm.ISQLSyntax;
import com.huawei.mppdbide.gauss.sqlparser.comm.SQLFoldingRuleManager;
import com.huawei.mppdbide.view.prefernces.DSFormatterPreferencePage;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLSourceViewerConfig.
 *
 * @since 3.0.0
 */
public class SQLSourceViewerConfig extends SourceViewerConfiguration {
    private SQLContentAssistProcessor processor = null;
    private SQLObjectLinkDetector objectLinkDetector = null;
    private SQLObjectHyperLinkPresenter hyperlinkPresenter;
    private PresentationReconciler reconciler = null;
    private String[] retPrefixes;
    private SQLSyntax syntax;

    /**
     * Instantiates a new SQL source viewer config.
     *
     * @param syntax the syntax
     */
    public SQLSourceViewerConfig(SQLSyntax syntax) {
        this.syntax = syntax;
    }

    /**
     * Gets the configured content types.
     *
     * @param sourceViewer the source viewer
     * @return the configured content types
     */
    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] {IDocument.DEFAULT_CONTENT_TYPE, ISQLSyntax.SQL_MULTILINE_COMMENT,
            ISQLSyntax.SINGLE_LINE_COMMENT, ISQLSyntax.SQL_COMMENT, ISQLSyntax.SQL_CODE, ISQLSyntax.SQL_STRING,
            ISQLSyntax.SQL_DOUBLE_QUOTES_IDENTIFIER};
    }

    /**
     * Gets the content assistant.
     *
     * @param sourceViewer the source viewer
     * @return the content assistant
     */
    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

        SQLContentAssist assistant = new SQLContentAssist();
        processor = new SQLContentAssistProcessor(assistant);
        assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
        assistant.enableAutoActivation(true);
        assistant.setAutoActivationDelay(500);
        assistant.enableAutoInsert(false);
        assistant.setShowEmptyList(false);
        assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));

        Color whiteColor = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
        Color blackColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
        assistant.setProposalSelectorForeground(blackColor);
        assistant.setProposalSelectorBackground(whiteColor);

        return assistant;
    }

    /**
     * Gets the configured document partitioning.
     *
     * @param sourceViewer the source viewer
     * @return the configured document partitioning
     */
    @Override
    public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
        return SQLCodeScanner.SQL_PARTITIONING;
    }

    /**
     * Gets the presentation reconciler.
     *
     * @return the presentation reconciler
     */
    public IPresentationReconciler getPresentationReconciler() {
        return this.reconciler;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SQLSingleTokenScanner.
     */
    private static class SQLSingleTokenScanner extends BufferedRuleBasedScanner {

        /**
         * Instantiates a new SQL single token scanner.
         *
         * @param attr the attr
         */
        public SQLSingleTokenScanner(TextAttribute attr) {
            setDefaultReturnToken(new Token(attr));
        }
    }

    /**
     * Gets the double click strategy.
     *
     * @param sourceViewer the source viewer
     * @param contentType the content type
     * @return the double click strategy
     */
    @Override
    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
        return new SQLDoubleClickStrategy();
    }

    /**
     * Load SQL syntax.
     *
     * @return the SQL syntax
     */
    public SQLSyntax loadSQLSyntax() {
        if (this.syntax == null) {
            SQLSyntax syntx = new SQLSyntax();
            return KeywordsToTrieConverter.convertKeywordstoTrie(syntx,
                    KeywordsFactoryProvider.getKeywordsFactory().getKeywords());
        }
        return this.syntax;

    }

    /**
     * Gets the presentation reconciler.
     *
     * @param sourceViewer the source viewer
     * @return the presentation reconciler
     */
    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        reconciler = new PresentationReconciler();
        String docPartitioning = getConfiguredDocumentPartitioning(sourceViewer);
        reconciler.setDocumentPartitioning(docPartitioning);

        SQLEditorPlugin.getDefault().setSQLCodeScanner(loadSQLSyntax());

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
                SQLEditorPlugin.getDefault().getSQLCodeScanner(loadSQLSyntax()));
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        SQLSyntaxColorProvider colorProvider = new SQLSyntaxColorProvider();

        // rule for multi-line comments
        // We just need a scanner that does nothing but returns a token with
        // the corresponding text attributes
        configureDamagerRepairer(reconciler, colorProvider, ISQLSyntax.SQL_MULTILINE_COMMENT);

        // Add a "damager-repairer" for changes within one-line SQL comments.
        configureDamagerRepairer(reconciler, colorProvider, ISQLSyntax.SQL_COMMENT);

        // Add a "damager-repairer" for changes within quoted literals.
        configureDamagerRepairer(reconciler, colorProvider, ISQLSyntax.SQL_STRING);

        // Add a "damager-repairer" for changes within delimited identifiers.
        configureDamagerRepairer(reconciler, colorProvider, ISQLSyntax.SQL_DOUBLE_QUOTES_IDENTIFIER);

        return reconciler;
    }

    /**
     * Configure damager repairer.
     *
     * @param drReconciler the dr reconciler
     * @param colorProvider the color provider
     * @param contentType the content type
     */
    private void configureDamagerRepairer(PresentationReconciler drReconciler, SQLSyntaxColorProvider colorProvider,
            String contentType) {
        TextAttribute colorAttribute = colorProvider.createTextAttribute(contentType);
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new SQLSingleTokenScanner(colorAttribute));
        drReconciler.setDamager(dr, contentType);
        drReconciler.setRepairer(dr, contentType);
    }

    /**
     * Gets the undo manager.
     *
     * @param sourceViewer the source viewer
     * @return the undo manager
     */
    @Override
    public IUndoManager getUndoManager(ISourceViewer sourceViewer) {
        return super.getUndoManager(sourceViewer);
    }

    /**
     * Sets the database.
     *
     * @param database the new database
     */
    public void setDatabase(Database database) {
        if (null != processor) {
            processor.setDatabase(database);
        }
        if (null != this.objectLinkDetector) {
            this.objectLinkDetector.setDatabase(database);
        }
    }

    /**
     * Gets the hyperlink detectors.
     *
     * @param sourceViewer the source viewer
     * @return the hyperlink detectors
     */
    @Override
    public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
        if (sourceViewer != null) {
            this.objectLinkDetector = new SQLObjectLinkDetector();
            return new IHyperlinkDetector[] {this.objectLinkDetector};
        }

        return new IHyperlinkDetector[0];
    }

    /**
     * Gets the hyperlink presenter.
     *
     * @param sourceViewer the source viewer
     * @return the hyperlink presenter
     */
    @Override
    public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
        this.hyperlinkPresenter = new SQLObjectHyperLinkPresenter();
        return hyperlinkPresenter;
    }

    /**
     * Gets the text hover.
     *
     * @param sourceViewer the source viewer
     * @param contentType the content type
     * @return the text hover
     */
    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        return null;
    }

    /**
     * Gets the tab width.
     *
     * @param sourceViewer the source viewer
     * @return the tab width
     */
    @Override
    public int getTabWidth(ISourceViewer sourceViewer) {
        return DSFormatterPreferencePage.getIndentSize();
    }

    /**
     * Gets the indent prefixes.
     *
     * @param sourceViewer the source viewer
     * @param contentType the content type
     * @return the indent prefixes
     */
    @Override
    public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
        if (retPrefixes == null) {
            setUpdatedPrefixes(sourceViewer);
        }

        return retPrefixes.clone();
    }

    /**
     * Sets the updated prefixes.
     *
     * @param sourceViewer the new updated prefixes
     */
    public void setUpdatedPrefixes(ISourceViewer sourceViewer) {

        if (DSFormatterPreferencePage.isTabToSpaceEnabled()) {
            retPrefixes = new String[] {"    ", "\t", ""};
            retPrefixes[0] = DSFormatterPreferencePage.getStringWithSpaces();
        } else {
            retPrefixes = new String[] {"\t", "    ", ""};
            retPrefixes[1] = DSFormatterPreferencePage.getStringWithSpaces();
        }

    }

    /**
     * Gets the reconciler.
     *
     * @param sourceViewer the source viewer
     * @return the reconciler
     */
    public IReconciler getReconciler(ISourceViewer sourceViewer) {

        if (!(sourceViewer instanceof ProjectionViewer)) {
            return null;

        }

        Object sqlStrategy = sourceViewer.getTextWidget().getData(SQLFoldingConstants.SQLRECONCILINGSTRATEGY);

        if (null != sqlStrategy && sqlStrategy instanceof SQLReconcilingStrategy) {

            MonoReconciler mReconciler = new MonoReconciler((SQLReconcilingStrategy) sqlStrategy, true);
            mReconciler.setDelay(300);
            sourceViewer.getTextWidget().setData(SQLFoldingConstants.MONORECONCILER, mReconciler);
            return mReconciler;

        } else {
            SQLReconcilingStrategy strategy = new SQLReconcilingStrategy();

            SQLEditorParser lSQLEditorParser = new SQLEditorParser();

            lSQLEditorParser.setSourceViewer((ProjectionViewer) sourceViewer);

            SQLFoldingRuleManager lSQLRuleManager = new SQLFoldingRuleManager();

            lSQLRuleManager.refreshRules();

            lSQLEditorParser.setRuleManager(lSQLRuleManager);

            strategy.setEditor(lSQLEditorParser);

            MonoReconciler mReconciler = new MonoReconciler(strategy, true);
            mReconciler.setDelay(300);
            sourceViewer.getTextWidget().setData(SQLFoldingConstants.SQLRECONCILINGSTRATEGY, strategy);
            sourceViewer.getTextWidget().setData(SQLFoldingConstants.MONORECONCILER, mReconciler);
            return mReconciler;

        }

    }
    
    /**
     * pre destroy
     */
    @PreDestroy
    public void preDestroy() {
        processor = null;
        objectLinkDetector = null;
        hyperlinkPresenter = null;
        reconciler = null;
        syntax = null;
    }
}
