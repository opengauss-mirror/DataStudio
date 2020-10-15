/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import javax.annotation.PreDestroy;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import com.huawei.mppdbide.view.utils.IUserPreference;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class
 * Description: The Class GridQueryArea.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GridQueryArea {
    private String query = null;

    private Composite queryComposite;

    private SourceViewer sourceViewer;

    private Text queryText;

    private SourceViewerDecorationSupport sourceViewerDecorationSupport;

    private SQLSyntax syntax;

    private boolean isExecutionPlantab = false;

    /**
     * Sets the execution plan tab flag.
     */
    public void setExecutionPlanTabFlag() {
        this.isExecutionPlantab = true;
    }

    /**
     * Instantiates a new grid query area.
     *
     * @param dataProvider the data provider
     */
    public GridQueryArea(IDSGridDataProvider dataProvider) {
        if (dataProvider instanceof DSResultSetGridDataProvider) {
            DSResultSetGridDataProvider rsDataProvider = (DSResultSetGridDataProvider) dataProvider;
            IQueryExecutionSummary summary = rsDataProvider.getSummary();
            if (null != summary) {
                this.query = summary.getQuery();
            }
        }
    }

    /**
     * Instantiates a new grid query area.
     *
     * @param queryToDisplay the query to display
     */
    public GridQueryArea(String queryToDisplay) {
        this.query = queryToDisplay;
    }

    /**
     * Creates the component.
     *
     * @param parent the parent
     * @param isVisualPlan the is visual plan
     */
    public void createComponent(Composite parent, boolean isVisualPlan) {
        if (null == query) {
            return;
        }

        this.queryComposite = createComposite(parent);
        addItemQueryText(isVisualPlan);

    }

    /**
     * Sets the SQL syntax.
     *
     * @param syntx the new SQL syntax
     */
    public void setSQLSyntax(SQLSyntax syntx) {
        this.syntax = syntx;
    }

    private Composite createComposite(Composite parent) {
        Composite newSearchComposite = new Composite(parent, SWT.TOOL | SWT.DIALOG_TRIM);
        DSGridComponent.setLayoutProperties(newSearchComposite);
        Object layoutDataObj = newSearchComposite.getLayoutData();
        if (layoutDataObj instanceof GridLayout) {
            GridLayout layout = (GridLayout) layoutDataObj;
            layout.marginHeight = 20;
        }
        GridDataFactory.fillDefaults().grab(true, false).applyTo(newSearchComposite);

        return newSearchComposite;
    }

    private void setDecoration() {
        IPreferenceStore prefStore = UserPreference.getInstance().getPrefernceStore();
        ISharedTextColors sharedTextColors = EditorsPlugin.getDefault().getSharedTextColors();

        sourceViewerDecorationSupport =
            new SourceViewerDecorationSupport(this.sourceViewer, null, null, sharedTextColors);
        sourceViewerDecorationSupport.setCursorLinePainterPreferenceKeys(IUserPreference.CURRENT_LINE_VISIBILITY,
            IUserPreference.CURRENTLINE_COLOR);
        sourceViewerDecorationSupport.install(prefStore);
    }

    private void addItemQueryText(boolean isVisualPlan) {
        GridData grid = new GridData();
        if (isVisualPlan) {
            grid.heightHint = 690;
        } else {
            grid.heightHint = 20;
        }
        grid.horizontalAlignment = SWT.FILL;
        grid.grabExcessHorizontalSpace = true;
        grid.grabExcessVerticalSpace = false;

        String lquery = validateQuery();
        if (lquery.length() > 100) {
            this.queryText = new Text(this.queryComposite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
            this.queryText.setLayoutData(grid);
            this.queryText.setText(this.query);
            this.queryText.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent event) {
                    if (event.stateMask == SWT.CTRL && event.keyCode == 'a') {
                        queryText.selectAll();
                        event.doit = false;
                    }
                }
            });
        } else {
            this.sourceViewer =
                new SourceViewer(this.queryComposite, null, null, false, SWT.V_SCROLL | SWT.WRAP | SWT.NONE);
            this.sourceViewer.getControl().setLayoutData(grid);
            this.sourceViewer.setEditable(false);
            this.sourceViewer.setDocument(new Document(validateQuery()));
            setDecoration();
            this.sourceViewer.configure(new SQLSourceViewerConfig(this.syntax));
            SQLDocumentPartitioner.connectDocument(sourceViewer.getDocument(), 0);
            Menu menu = new Menu(getControl());
            sourceViewer.getTextWidget().setMenu(menu);
            addCopyMenuItem(menu);
        }
    }

    private Control getControl() {
        Control ctrl = sourceViewer.getControl();
        if (ctrl instanceof Composite) {
            Composite cmpst = (Composite) ctrl;
            Control[] childControls = cmpst.getChildren();
            Control childControl = null;
            for (int i = 0; i < childControls.length; i++) {
                childControl = childControls[i];
                if (childControl instanceof StyledText) {
                    ctrl = childControl;
                    break;
                }
            }
        }
        return ctrl;
    }

    /**
     * Adds the copy menu item.
     *
     * @param menu the menu
     */
    protected void addCopyMenuItem(Menu menu) {
        MenuItem menuCopy = new MenuItem(menu, SWT.PUSH);
        // DTS2016011900019 Starts
        menuCopy.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_COPY));
        // DTS2016011900019 Ends
        menuCopy.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                ITextSelection textSel = (ITextSelection) sourceViewer.getSelectionProvider().getSelection();
                String selectedText = textSel.getText();
                String str = "";
                if (selectedText != null && selectedText.length() > 0) {
                    str = selectedText;
                } else {
                    str = sourceViewer.getDocument().get();
                }
                Clipboard cb = new Clipboard(Display.getDefault());

                cb.setContents(new Object[] {str}, new Transfer[] {TextTransfer.getInstance()});
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
        menuCopy.setImage(IconUtility.getIconImage(IiconPath.ICO_COPY, this.getClass()));
    }

    private String validateQuery() {
        String strQuery = "";
        if (null != this.query) {
            strQuery = this.query.replaceAll(MPPDBIDEConstants.NEW_LINE_SIGN, " ");
            strQuery = strQuery.replaceAll(MPPDBIDEConstants.LINE_SEPARATOR, " ");
        }
        return strQuery;
    }

    /**
     * Do hide query area.
     */
    public void doHideQueryArea() {
        GridUIUtils.toggleCompositeSectionVisibility(this.queryComposite, true, null, this.isExecutionPlantab);
    }

    /**
     * Do show query area.
     */
    public void doShowQueryArea() {
        GridUIUtils.toggleCompositeSectionVisibility(this.queryComposite, false, null, this.isExecutionPlantab);
    }

    /**
     * Checks if is quuery area visible.
     *
     * @return true, if is quuery area visible
     */
    public boolean isQuueryAreaVisible() {
        return this.queryComposite.isVisible();
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        if (sourceViewer != null) {
            syntax = null;
            sourceViewerDecorationSupport.uninstall();
            sourceViewerDecorationSupport = null;
            sourceViewer.unconfigure();
            sourceViewer = null;
        }
    }

    /**
     * Reconfigure.
     *
     * @param sytax the sytax
     */
    public void reconfigure(SQLSyntax sytax) {
        if (sourceViewer != null) {
            sourceViewer.unconfigure();
            sourceViewer.configure(new SQLSourceViewerConfig(sytax));
            if (sourceViewer.getDocument() != null) {
                SQLDocumentPartitioner.connectDocument(sourceViewer.getDocument(), 0);
            }
        }
    }
}
