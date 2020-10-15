/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.userrole;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.core.SourceEditorKeyListener;
import com.huawei.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerDecorationSupport;
import com.huawei.mppdbide.view.utils.IUserPreference;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SqlPreviewComponent.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
@SuppressWarnings("restriction")
public class SqlPreviewComponent {
    private SourceViewer sourceViewer;

    /**
     * Instantiates a new sql preview component.
     *
     * @param parent the parent
     * @param sqlSyntax the sql syntax
     */
    public SqlPreviewComponent(Composite parent, SQLSyntax sqlSyntax) {
        this.sourceViewer = new SourceViewer(parent, null,
                SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        this.sourceViewer.configure(new SQLSourceViewerConfig(sqlSyntax));

        Document document = new Document();
        SQLDocumentPartitioner.connectDocument(document, 0);
        this.sourceViewer.setDocument(document);

        ISharedTextColors sharedTextColors = EditorsPlugin.getDefault().getSharedTextColors();
        SQLSourceViewerDecorationSupport sourceViewerDecorationSupport = new SQLSourceViewerDecorationSupport(
                this.sourceViewer, null, null, sharedTextColors);
        sourceViewerDecorationSupport.setCursorLinePainterPreferenceKeys(IUserPreference.CURRENT_LINE_VISIBILITY,
                IUserPreference.CURRENTLINE_COLOR);
        sourceViewerDecorationSupport.installDecorations();

        Menu menu = createMenu(this.sourceViewer.getControl());
        this.sourceViewer.getTextWidget().setMenu(menu);

        this.sourceViewer.getTextWidget().addKeyListener(new SourceEditorKeyListener(this.sourceViewer));
    }

    /**
     * Gets the source viewer.
     *
     * @return the source viewer
     */
    public SourceViewer getSourceViewer() {
        return this.sourceViewer;
    }

    /**
     * Creates the menu.
     *
     * @param parent the parent
     * @return the menu
     */
    private Menu createMenu(Control parent) {
        Menu menu = new Menu(parent);

        MenuItem menuCopy = new MenuItem(menu, SWT.PUSH);
        menuCopy.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_COPY));
        menuCopy.setImage(IconUtility.getIconImage(IiconPath.ICO_COPY, this.getClass()));
        menuCopy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                sourceViewer.doOperation(ITextOperationTarget.COPY);
            }
        });

        MenuItem menuSelectAll = new MenuItem(menu, SWT.PUSH);
        menuSelectAll.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_SELECTALL));
        menuSelectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                sourceViewer.doOperation(ITextOperationTarget.SELECT_ALL);
            }
        });

        return menu;
    }
}
