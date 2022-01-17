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

package com.huawei.mppdbide.view.search;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

import com.huawei.mppdbide.bl.search.SearchObjectEnum;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.PartitionMetaData;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.eclipse.dependent.EclipseContextDSKeys;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.presentation.search.SearchObjCore;
import com.huawei.mppdbide.presentation.search.SearchObjInfo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.ObjectBrowserLabelProvider;
import com.huawei.mppdbide.view.handler.ExecuteObjectBrowserItem;
import com.huawei.mppdbide.view.handler.RefreshObjectBrowserItem;
import com.huawei.mppdbide.view.handler.connection.PasswordDialog;
import com.huawei.mppdbide.view.ui.DBAssistantWindow;
import com.huawei.mppdbide.view.ui.DatabaseListControl;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchWindow.
 *
 * @since 3.0.0
 */
public class SearchWindow implements Observer {

    private SearchObjCore searchCore;
    private Combo connectionCombo;
    private Combo databaseCombo;
    private Combo schemaCombo;
    private TreeViewer resultViewer;
    private Button tableCheck;
    private Button funProcCheck;
    private Button viewCheck;
    private Button sequenceCheck;
    private Button synonymCheck;
    private Button triggerCheck;
    private Text searchText;
    private Combo nameMatchCombo;
    private Button matchCaseCheck;
    private Button searchBtn;
    private Button cancelhBtn;
    private Label recordFetched;
    private Label executionTime;
    private Text txtErrorMsg;
    private StatusMessage statusMsg;
    private SearchWorkerJob searchjob;
    private SearchObjInfo searchObjInfo;
    @Inject
    private ESelectionService searchSelectionService;
    @Inject
    private EHandlerService handlerService;

    private ToolItem refreshConnection;
    private ToolItem refreshDb;
    private ToolItem refreshSchema;

    /**
     * The Constant NAME_MAX_LEN.
     */
    public static final int NAME_MAX_LEN = 63;
    private Label searchLblLength;

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param partService the part service
     * @param modelService the model service
     * @param application the application
     * @param menuService the menu service
     * @param mpart the mpart
     */
    @Inject
    public void createPartControl(Composite parent, EPartService partService, EModelService modelService,
            MApplication application, EMenuService menuService, MPart mpart) {

        parent.setLayout(new GridLayout(1, false));
        parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        searchCore = (SearchObjCore) mpart.getObject();
        searchObjInfo = new SearchObjInfo();
        searchCore.setSearchInfo(searchObjInfo);
        MUIElement ele = modelService.cloneSnippet(application, "com.huawei.mppdbide.view.part.common", null);
        MPart part = (MPart) ele;
        if (part != null) {
            mpart.getMenus().addAll(part.getMenus());
        }
        createSearchWindow(parent, menuService);

        searchCore.addObserver(this);
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            databaseListControl.addObserver(this);
        }
        displayNameMatchList();
        loadConnectionDetails();
        loadObjectBrowserSelectionDetails();
    }

    private void createSearchWindow(Composite parent, EMenuService menuService) {
        final ScrolledComposite mainSc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        mainSc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite composite = new Composite(mainSc, SWT.NONE);
        composite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        mainSc.setContent(composite);
        mainSc.setBackgroundMode(SWT.INHERIT_DEFAULT);

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        composite.setLayoutData(gridData);
        composite.setLayout(new GridLayout(2, false));

        Composite searchOptionComp = new Composite(composite, SWT.NONE);
        GridData searchOptionGrid = new GridData();
        searchOptionGrid.widthHint = 550;
        searchOptionGrid.verticalAlignment = SWT.FILL;
        searchOptionGrid.grabExcessVerticalSpace = false;
        searchOptionComp.setLayoutData(searchOptionGrid);
        searchOptionComp.setLayout(new GridLayout());

        Group searchObjGroup = new Group(searchOptionComp, SWT.CENTER);
        searchObjGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        searchObjGroup.setLayout(new GridLayout(2, false));
        searchObjGroup.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_SCOPE_LBL));

        Composite comboComposite = new Composite(searchObjGroup, SWT.NONE);
        GridLayout layout1 = new GridLayout();
        layout1.horizontalSpacing = 5;
        layout1.verticalSpacing = 10;
        layout1.marginHeight = 20;
        layout1.numColumns = 3;
        comboComposite.setLayout(layout1);
        comboComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

        getConnectionSelectionOption(comboComposite);

        getDatabaseSelectionOption(comboComposite);

        getSchemaSelectionOption(comboComposite);

        addWithinFilterOptions(searchObjGroup);

        Group searchOptions = addSearchText(searchOptionComp);

        searchCriteriaOptions(searchOptions);

        addErrorMessageArea(searchOptionComp);

        addButtonArea(searchOptionComp);

        Composite searchResultComp = addSearchResultArea(menuService, composite);

        addSearchResultStatus(searchResultComp);
        addHandler(UIConstants.UI_COMMAND_REFRESH_DBOBJECT, new RefreshObjectBrowserItem());
        addHandler(UIConstants.UI_COMMAND_EXECUTE_DBOBJECT, new ExecuteObjectBrowserItem(resultViewer));

        mainSc.setExpandHorizontal(true);
        mainSc.setExpandVertical(true);
        mainSc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mainSc.pack();
    }

    private void addSearchResultStatus(Composite searchResultComp) {
        Composite statusComposite = new Composite(searchResultComp, SWT.NONE);
        GridData gd = new GridData();
        gd.heightHint = 50;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        statusComposite.setLayoutData(gd);
        statusComposite.setLayout(new GridLayout(2, true));

        recordFetched = new Label(statusComposite, SWT.LEFT);
        recordFetched.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        executionTime = new Label(statusComposite, SWT.RIGHT);
        executionTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    }

    private void addSeperator(Composite composite) {
        Label seperator = new Label(composite, SWT.SEPARATOR);
        GridData gdCompst = new GridData();
        gdCompst.widthHint = 5;
        gdCompst.grabExcessVerticalSpace = true;
        gdCompst.verticalAlignment = SWT.FILL;
        gdCompst.verticalSpan = 2;
        seperator.setLayoutData(gdCompst);
    }

    private Composite addSearchResultArea(EMenuService menuService, Composite composite) {
        Composite searchResultComp = new Composite(composite, SWT.NONE);
        GridData searchResultGrid = new GridData();
        searchResultGrid.horizontalAlignment = SWT.FILL;
        searchResultGrid.grabExcessHorizontalSpace = true;
        searchResultGrid.grabExcessVerticalSpace = true;
        searchResultGrid.verticalAlignment = SWT.FILL;
        searchResultGrid.widthHint = 400;
        searchResultComp.setLayoutData(searchResultGrid);
        searchResultComp.setLayout(new GridLayout(2, false));

        addSeperator(searchResultComp);

        Group resultGrp = new Group(searchResultComp, SWT.NONE);
        resultGrp.setLayout(new GridLayout(1, true));
        resultGrp.setLayoutData(searchResultGrid);
        resultGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_RESULT_LBL));

        resultViewer = new TreeViewer(resultGrp, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.MULTI);
        resultViewer.getTree().setLayoutData(searchResultGrid);

        resultViewer.setUseHashlookup(true);
        resultViewer.setLabelProvider(new ObjectBrowserLabelProvider());
        resultViewer.getTree().setItemCount(DBConnProfCache.getInstance().getServers().size());
        resultViewer.setAutoExpandLevel(3);
        resultViewer.addTreeListener(new TreeviwerHelper());
        resultViewer.getTree().setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TRE_OBJECTBROWSER_TREE_001");
        resultViewer.addSelectionChangedListener(new OBSelectionChangedListener());

        resultViewer.getControl().addKeyListener(new ResultViewerKeyListner());

        menuService.registerContextMenu(resultViewer.getControl(), UIConstants.UI_MENUITEM_CONNECTIONPROFILE_ID);

        resultViewer.addSelectionChangedListener(new ResultViewerSelectionChangeListner());
        return searchResultComp;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ResultViewerKeyListner.
     */
    private class ResultViewerKeyListner implements KeyListener {

        @Override
        public void keyPressed(KeyEvent keyEvent) {

        }

        @Override
        public void keyReleased(KeyEvent keyReleasedEvent) {

            if (isSelectAllKeyPress(keyReleasedEvent)) {
                Tree viewer = (Tree) keyReleasedEvent.getSource();
                viewer.selectAll();
                searchSelectionService.setSelection(resultViewer.getSelection());
            }

        }

        private boolean isSelectAllKeyPress(KeyEvent keyEvent) {
            // CTRL+a or CTRL+A
            return ((keyEvent.stateMask & SWT.CONTROL) != 0)
                    && ((keyEvent.keyCode == 'a') || (keyEvent.keyCode == 'A'));
        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ResultViewerSelectionChangeListner.
     */
    private class ResultViewerSelectionChangeListner implements ISelectionChangedListener {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {

            ISelection selection = event.getSelection();
            searchSelectionService.setSelection(selection);

            if (selection instanceof IStructuredSelection) {
                Object obj = ((IStructuredSelection) selection).getFirstElement();

                if (obj instanceof ServerObject) {
                    ServerObject sobj = (ServerObject) obj;
                    IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();
                    eclipseContext.set(EclipseContextDSKeys.SERVER_OBJECT, sobj);
                }
            }

        }

    }

    private void addButtonArea(Composite searchOptionComp) {
        Composite buttonComposite = new Composite(searchOptionComp, SWT.NONE);
        GridData gd5 = new GridData(SWT.RIGHT, SWT.UP, false, true);
        gd5.widthHint = 170;
        buttonComposite.setLayoutData(gd5);
        buttonComposite.setLayout(new GridLayout(2, true));

        searchBtn = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);
        searchBtn.setLayoutData(getGridData(25, 70));
        searchBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_BTN));
        searchBtn.addSelectionListener(new SearchListner());
        searchBtn.setEnabled(false);

        cancelhBtn = new Button(buttonComposite, SWT.PUSH | SWT.CENTER);
        cancelhBtn.setLayoutData(getGridData(25, 70));
        cancelhBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_CANCEL_BTN));
        cancelhBtn.setEnabled(false);
        cancelhBtn.addSelectionListener(new CancelListner());
    }

    private void addErrorMessageArea(Composite searchOptionComp) {
        Composite errorMsgComposite = new Composite(searchOptionComp, SWT.NONE);
        GridData errorGrid = new GridData(SWT.FILL, SWT.NONE, true, false);
        errorGrid.heightHint = 25;
        errorMsgComposite.setLayoutData(errorGrid);
        errorMsgComposite.setLayout(new GridLayout());

        txtErrorMsg = new Text(errorMsgComposite, SWT.BOLD | SWT.READ_ONLY | SWT.MULTI);
        txtErrorMsg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        txtErrorMsg.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        txtErrorMsg.setVisible(false);
    }

    private void searchCriteriaOptions(Group searchOptions) {
        Composite nameMatchComposite = new Composite(searchOptions, SWT.NONE);
        nameMatchComposite.setLayout(new GridLayout(3, false));

        Label nameMatchlbl = new Label(nameMatchComposite, SWT.NONE);
        nameMatchlbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NAME_MATCH_LBL));
        nameMatchlbl.setLayoutData(getGridData(20, 100));

        nameMatchCombo = new Combo(nameMatchComposite, SWT.READ_ONLY);
        nameMatchCombo.setLayoutData(getGridData(20, 200));

        matchCaseCheck = new Button(nameMatchComposite, SWT.CHECK);
        matchCaseCheck.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_MATCH_CASE_LBL));

        GridData gd2 = getGridData(20, 100);
        gd2.horizontalIndent = 10;
        matchCaseCheck.setLayoutData(gd2);
    }

    private Group addSearchText(Composite searchOptionComp) {
        Group searchOptions = new Group(searchOptionComp, SWT.NONE);
        searchOptions.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        searchOptions.setLayout(new GridLayout());
        searchOptions.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OPTIONS_LBL));

        Composite searchTextComposite = new Composite(searchOptions, SWT.NONE);
        searchTextComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        GridLayout lay = new GridLayout(3, false);
        lay.horizontalSpacing = 10;
        searchTextComposite.setLayout(lay);

        Label searchLbl = new Label(searchTextComposite, SWT.NONE);
        searchLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NAME_LBL));
        searchLbl.setLayoutData(getGridData(18, 100));

        searchText = new Text(searchTextComposite, SWT.BORDER);
        searchText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        searchText.addVerifyListener(new SearchVerifyListner(searchText));
        searchText.addListener(SWT.MenuDetect, new InitListener());
        searchText.setTextLimit(63);
        searchText.addModifyListener(new ModifyListener() {

            /**
             * Modify text.
             *
             * @param event the e
             */
            public void modifyText(ModifyEvent event) {
                doTextWidgetChanged(event.widget);
            }
        });

        getControlDecoration(searchLbl, searchTextComposite,
                MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NAME_MAND_MSG));
        searchLblLength = new Label(searchTextComposite, SWT.FILL);
        searchLblLength.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR,
                searchText.getText().length(), NAME_MAX_LEN));
        searchLblLength.setLayoutData(getGridData(20, 40));
        return searchOptions;
    }

    private void addWithinFilterOptions(Group searchObjGroup) {

        GridData grid = new GridData();
        grid.horizontalAlignment = SWT.FILL;
        grid.grabExcessHorizontalSpace = true;
        grid.verticalAlignment = SWT.FILL;
        grid.grabExcessVerticalSpace = true;

        Group searchWithinGrp = new Group(searchObjGroup, SWT.None);
        searchWithinGrp.setLayoutData(grid);
        searchWithinGrp.setLayout(new GridLayout());
        searchWithinGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_WITHIN_LBL));

        Composite withinComposite = new Composite(searchWithinGrp, SWT.NONE);
        GridLayout layout2 = new GridLayout();
        layout2.horizontalSpacing = 5;
        layout2.verticalSpacing = 10;
        layout2.marginHeight = 20;
        layout2.numColumns = 1;
        withinComposite.setLayout(layout2);

        tableCheck = new Button(withinComposite, SWT.CHECK);
        tableCheck.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_WITHIN_TABLE_LBL));
        tableCheck.setSelection(true);
        tableCheck.addSelectionListener(new ButtonSelectionListner());

        funProcCheck = new Button(withinComposite, SWT.CHECK);
        funProcCheck.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_WITHIN_FUN_LBL));
        funProcCheck.setSelection(true);
        funProcCheck.addSelectionListener(new ButtonSelectionListner());

        viewCheck = new Button(withinComposite, SWT.CHECK);
        viewCheck.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_WITHIN_VIEWS));
        viewCheck.setSelection(true);
        viewCheck.addSelectionListener(new ButtonSelectionListner());

        sequenceCheck = new Button(withinComposite, SWT.CHECK);
        sequenceCheck.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEQUENCE));
        sequenceCheck.setSelection(true);
        sequenceCheck.addSelectionListener(new ButtonSelectionListner());

        synonymCheck = new Button(withinComposite, SWT.CHECK);
        synonymCheck.setText(MessageConfigLoader.getProperty(IMessagesConstants.SYNONYM_GROUP_NAME));
        synonymCheck.setSelection(true);
        synonymCheck.addSelectionListener(new ButtonSelectionListner());
        
        triggerCheck = new Button(withinComposite, SWT.CHECK);
        triggerCheck.setText(MessageConfigLoader.getProperty(IMessagesConstants.TRIGGER_GROUP_NAME));
        triggerCheck.setSelection(true);
        triggerCheck.addSelectionListener(new ButtonSelectionListner());

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ButtonSelectionListner.
     */
    private class ButtonSelectionListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent evnt) {
            setErrorMsg("");

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    private void getSchemaSelectionOption(Composite comboComposite) {
        Label schemaLabel = new Label(comboComposite, SWT.NONE);
        schemaLabel.setLayoutData(getGridData(20, 80));
        schemaLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NAMESPACE_LBL));

        schemaCombo = new Combo(comboComposite, SWT.READ_ONLY);
        schemaCombo.setLayoutData(getGridData(20, 150));
        schemaCombo.addSelectionListener(new SchemaListner());
        schemaCombo.setEnabled(false);
        getControlDecoration(schemaLabel, comboComposite,
                MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NAMESPACE_MAND_MSG));

        ToolBar toolBar2 = new ToolBar(comboComposite, SWT.FLAT | SWT.FOCUSED);
        toolBar2.setLayoutData(new GridData(SWT.BOTTOM, SWT.BOTTOM, true, false));
        toolBar2.pack();

        refreshSchema = new ToolItem(toolBar2, SWT.NONE);
        refreshSchema.setImage(IconUtility.getIconImage(IiconPath.ICO_REFRESH, getClass()));
        refreshSchema.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.OB_REFRESH_TOOL));
        refreshSchema.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                displaySchemaList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    private void getDatabaseSelectionOption(Composite comboComposite) {
        Label databaseLbl = new Label(comboComposite, SWT.NONE);
        databaseLbl.setLayoutData(getGridData(20, 80));
        databaseLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_DATABASE_LBL));
        getControlDecoration(databaseLbl, comboComposite,
                MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_DATABASE_MAND_MSG));
        databaseCombo = new Combo(comboComposite, SWT.READ_ONLY);

        GridData gdComposite = new GridData(SWT.LEFT, SWT.UP, false, false, 1, 1);
        gdComposite.widthHint = 150;
        gdComposite.horizontalIndent = 3;
        gdComposite.verticalIndent = 5;
        databaseCombo.setLayoutData(gdComposite);
        databaseCombo.addSelectionListener(new DatabaseListner());
        databaseCombo.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseDown(MouseEvent mouseEvent) {

                displayNameMatchList();
            }

            @Override
            public void mouseDoubleClick(MouseEvent mouseEvent) {

            }
        });
        databaseCombo.setEnabled(false);

        ToolBar toolBar1 = new ToolBar(comboComposite, SWT.FLAT | SWT.FOCUSED);
        toolBar1.setLayoutData(new GridData(SWT.BOTTOM, SWT.BOTTOM, true, false));
        toolBar1.pack();
        refreshDb = new ToolItem(toolBar1, SWT.NONE);
        refreshDb.setImage(IconUtility.getIconImage(IiconPath.ICO_REFRESH, getClass()));
        refreshDb.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.OB_REFRESH_TOOL));
        refreshDb.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                displayDatabaseList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    private void getConnectionSelectionOption(Composite comboComposite) {
        Label connectionLbl = new Label(comboComposite, SWT.NONE);
        connectionLbl.setLayoutData(getGridData(20, 80));
        connectionLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_CONNECTION_LBL));

        getControlDecoration(connectionLbl, comboComposite,
                MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_CONNECTION_MAND_MSG));

        connectionCombo = new Combo(comboComposite, SWT.READ_ONLY);
        connectionCombo.setLayoutData(getGridData(20, 150));

        connectionCombo.addSelectionListener(new ConnectionListner());

        ToolBar toolBar = new ToolBar(comboComposite, SWT.FLAT | SWT.FOCUSED);
        toolBar.setLayoutData(new GridData(SWT.BOTTOM, SWT.BOTTOM, true, false));
        toolBar.pack();
        refreshConnection = new ToolItem(toolBar, SWT.NONE);
        refreshConnection.setImage(IconUtility.getIconImage(IiconPath.ICO_REFRESH, getClass()));
        refreshConnection.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.OB_REFRESH_TOOL));
        refreshConnection.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                displayProfileList();
                displayDatabaseList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {

            }
        });
    }

    private void doTextWidgetChanged(Widget widgetText) {
        if (widgetText == searchText) {
            searchLblLength.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR,
                    searchText.getText().length(), NAME_MAX_LEN));
        }
    }

    /**
     * The listener interface for receiving init events. The class that is
     * interested in processing a init event implements this interface, and the
     * object created with that class is registered with a component using the
     * component's <code>addInitListener<code> method. When the init event
     * occurs, that object's appropriate method is invoked.
     *
     * InitEvent
     */
    private static final class InitListener implements Listener {
        @Override
        public void handleEvent(Event event) {
            event.doit = false;
        }
    }

    private void getControlDecoration(Label connectionLbl, Composite comboComposite, String msg) {
        final ControlDecoration decofk = new ControlDecoration(connectionLbl, SWT.TOP | SWT.RIGHT, comboComposite);

        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

        decofk.setDescriptionText(msg);
        decofk.setImage(image);
        decofk.setShowOnlyOnFocus(false);

    }

    private void addHandler(final String id, final Object handlerObject) {
        resultViewer.getControl().addListener(SWT.Activate, new OBListener(id, handlerObject));
    }

    /**
     * The listener interface for receiving OB events. The class that is
     * interested in processing a OB event implements this interface, and the
     * object created with that class is registered with a component using the
     * component's <code>addOBListener<code> method. When the OB event occurs,
     * that object's appropriate method is invoked.
     *
     * OBEvent
     */
    private class OBListener implements Listener {
        private String id;
        private Object object;

        /**
         * Instantiates a new OB listener.
         *
         * @param id the id
         * @param object the object
         */
        protected OBListener(String id, Object object) {
            this.id = id;
            this.object = object;
        }

        @Override
        public void handleEvent(Event event) {
            handlerService.activateHandler(id, object);
        }
    }

    private GridData getGridData(int height, int width) {
        GridData gdComposite = new GridData(SWT.LEFT, SWT.UP, false, false, 1, 1);
        gdComposite.heightHint = height;
        gdComposite.widthHint = width;
        gdComposite.horizontalIndent = 3;
        gdComposite.verticalIndent = 5;
        return gdComposite;
    }

    /**
     * Gets the result viewer.
     *
     * @return the result viewer
     */
    public TreeViewer getResultViewer() {
        return resultViewer;
    }

    /**
     * Gets the search core.
     *
     * @return the search core
     */
    public SearchObjCore getSearchCore() {
        return this.searchCore;
    }

    /**
     * Sets the search core.
     *
     * @param searchCore the new search core
     */
    public void setSearchCore(SearchObjCore searchCore) {
        this.searchCore = searchCore;
    }

    /**
     * Load connection details.
     */
    public void loadConnectionDetails() {
        displayProfileList();
        displayDatabaseList();

    }

    /**
     * Prints the status.
     */
    public void printStatus() {
        MPPDBIDELoggerUtility.info("Search completed ..");
        String recdfetched = null;
        if (searchCore.getRowsFetched() == 0) {
            recdfetched = MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NO_RECORD_FOUND_MSG);
        } else {
            recdfetched = MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_RECORD_FETCHED_MSG,
                    searchCore.getRowsFetched());
        }
        recordFetched.setText(recdfetched);
        MPPDBIDELoggerUtility.info(recdfetched);

        String exeTime = MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_EXE_TIME_MSG,
                searchCore.getExecutionTime());
        this.executionTime.setText(exeTime);
        MPPDBIDELoggerUtility.info(exeTime);
    }

    /**
     * Sets the error msg.
     *
     * @param errMsg the new error msg
     */
    public void setErrorMsg(String errMsg) {
        if (null == errMsg || errMsg.trim().isEmpty()) {
            txtErrorMsg.setVisible(false);
        } else {
            txtErrorMsg.setVisible(true);
        }
        if (null != errMsg) {
            if (errMsg.contains("42P01")) {
                txtErrorMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.ERR_SYNONYM_NOT_SUPPORTED));
            } else {
                txtErrorMsg.setText(errMsg);
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TreeviwerHelper.
     */
    private class TreeviwerHelper implements ITreeViewerListener {

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {

        }

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            Object obj = event.getElement();
            if (obj instanceof TableMetaData || obj instanceof ViewMetaData || obj instanceof DebugObjects) {
                resultViewer.setExpandedState(obj, false);
            }
        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ConnectionListner.
     */
    private class ConnectionListner implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent selectionEvent) {
            updateChildCombo();
            searchObjInfo.setSelectedserver(connectionCombo.getSelectionIndex());
            schemaCombo.removeAll();
            databaseCombo.setEnabled(true);
            displayDatabaseList();
            enableDisableSearchButton();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent selectionEvent) {

        }

    }

    private void updateChildCombo() {
        databaseCombo.removeAll();
        schemaCombo.removeAll();
        databaseCombo.clearSelection();
        schemaCombo.clearSelection();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DatabaseListner.
     */
    private class DatabaseListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent selectionEvent) {
            searchObjInfo.setSelectedDB(databaseCombo.getSelectionIndex());
            schemaCombo.setEnabled(true);
            displaySchemaList();
            enableDisableSearchButton();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent selectionEvent) {

        }

    }

    private void enableDisableSearchButton() {
        if (schemaCombo.getItemCount() > 0 && !searchText.getText().isEmpty() && null != searchCore.getSelectedDb()
                && searchCore.getSelectedDb().isConnected()) {
            searchBtn.setEnabled(true);
        } else {
            searchBtn.setEnabled(false);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SchemaListner.
     */
    private class SchemaListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent selectionEvent) {
            searchObjInfo.setSelectedNamespace(schemaCombo.getSelectionIndex());
            enableDisableSearchButton();

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent selectionEvent) {

        }

    }

    /**
     * Gets the search window.
     *
     * @return the search window
     */
    public SearchWindow getSearchWindow() {
        return this;

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CancelListner.
     */
    private class CancelListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent selectionEvent) {
            if (null != searchjob) {
                searchjob.cancel();
            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent selectionEvent) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SearchListner.
     */
    private class SearchListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            if (null != resultViewer.getInput()) {
                clearResultData();

            }
            if (schemaCombo.getSelectionIndex() >= 0 && tableCheck.getSelection() || funProcCheck.getSelection()
                    || viewCheck.getSelection() || sequenceCheck.getSelection() || synonymCheck.getSelection() || triggerCheck.getSelection()) {
                searchCore.clearData();
                getUserInPut();
                boolean isConnection = false;
                try {
                    setErrorMsg("");
                    isConnection = getSearchObjectConnection();
                } catch (DatabaseOperationException e1) {
                    searchCore.setSearchStatus(SearchObjectEnum.SEARCH_END);
                }
                if (isConnection) {
                    searchCore.setSearchStatus(SearchObjectEnum.SEARCH_START);

                    final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();

                    StatusMessage statMssage = new StatusMessage(
                            MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_PROGRESS_STATS_MSG));
                    String jobName = ProgressBarLabelFormatter.getProgressLabelForSchema(
                            searchCore.getSelectedNs().getName(), searchCore.getSelectedDb().getName(),
                            searchCore.getSelectedServer().getName(), IMessagesConstants.SEARCH_PROGRESS_MONITOR_MSG);
                    searchjob = new SearchWorkerJob(jobName, searchCore, statMssage, getSearchWindow());
                    searchjob.setTaskDB(searchCore.getSelectedDb());
                    setStatusMsg(statMssage);
                    StatusMessageList.getInstance().push(statMssage);
                    if (bttmStatusBar != null) {
                        bttmStatusBar.activateStatusbar();
                    }
                    searchjob.schedule();
                } else {
                    setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_NO_CONN_MSG));
                    searchCore.setSearchStatus(SearchObjectEnum.SEARCH_END);

                }

            } else {
                setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_SELECT_SEARCH_WITHIN_OPT));
            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    private void clearResultData() {
        resultViewer.setInput(null);
        recordFetched.setText("");
        executionTime.setText("");
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SearchVerifyListner.
     */
    private class SearchVerifyListner implements VerifyListener {

        private Text searchText;

        /**
         * Instantiates a new search verify listner.
         *
         * @param searchText the search text
         */
        public SearchVerifyListner(Text searchText) {
            this.searchText = searchText;
        }

        @Override
        public void verifyText(VerifyEvent verifyEvent) {
            final String oldSearchText = searchText.getText();
            final String newSearchText = oldSearchText.substring(0, verifyEvent.start) + verifyEvent.text
                    + oldSearchText.substring(verifyEvent.end);

            if (schemaCombo.getItemCount() > 0 && !newSearchText.isEmpty() && null != searchCore.getSelectedDb()
                    && searchCore.getSelectedDb().isConnected()) {
                searchBtn.setEnabled(true);
            } else {
                searchBtn.setEnabled(false);
            }

        }

    }

    /**
     * The listener interface for receiving OBSelectionChanged events. The class
     * that is interested in processing a OBSelectionChanged event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addOBSelectionChangedListener<code> method. When the
     * OBSelectionChanged event occurs, that object's appropriate method is
     * invoked.
     *
     * OBSelectionChangedEvent
     */
    private class OBSelectionChangedListener implements ISelectionChangedListener {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            searchSelectionService.setSelection(selection);
            MPPDBIDELoggerUtility.debug("GUI: ObjectBrowser: Tree item selected.");
        }
    }

    /**
     * Gets the user in put.
     *
     * @return the user in put
     */
    public void getUserInPut() {
        searchObjInfo.setTableSelected(tableCheck.getSelection());
        searchObjInfo.setFunProcSelected(funProcCheck.getSelection());
        searchObjInfo.setViewsSelected(viewCheck.getSelection());
        searchObjInfo.setSearchText(searchText.getText());
        searchObjInfo.setNameMatch(nameMatchCombo.getSelectionIndex());
        searchObjInfo.setMatchCase(matchCaseCheck.getSelection());
        searchObjInfo.setSequenceSelected(sequenceCheck.getSelection());
        searchObjInfo.setSynonymSelected(synonymCheck.getSelection());
        searchObjInfo.setTriggerSelected(triggerCheck.getSelection());

    }

    /**
     * Gets the search object connection.
     *
     * @return the search object connection
     * @throws DatabaseOperationException the database operation exception
     */
    public boolean getSearchObjectConnection() throws DatabaseOperationException {
        if (!searchCore.getSelectedServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)) {
            searchCore.getConnection();

        } else {
            PasswordDialog helper = new PasswordDialog(Display.getDefault().getActiveShell(),
                    searchCore.getSelectedDb());
            int returnValue = helper.open();
            if (returnValue == 0) {
                searchCore.getConnection();
            } else {
                return false;
            }
        }
        return true;

    }

    /**
     * Gets the status msg.
     *
     * @return the status msg
     */
    public StatusMessage getStatusMsg() {
        return statusMsg;
    }

    /**
     * Sets the status msg.
     *
     * @param statusMsg the new status msg
     */
    public void setStatusMsg(StatusMessage statusMsg) {
        this.statusMsg = statusMsg;
    }

    /**
     * Update.
     *
     * @param o the o
     * @param arg the arg
     */
    @Override
    public void update(Observable o, Object arg) {
        SearchObjectEnum serachEnum = (SearchObjectEnum) arg;

        switch (serachEnum) {

            case SEARCH_START: {
                searchBtn.setEnabled(false);
                cancelhBtn.setEnabled(true);
                break;
            }
            case DATABASELIST_UPDATE: {
                if (null != searchCore.getSelectedDb() && !searchCore.getSelectedDb().isConnected()) {
                    searchBtn.setEnabled(false);
                    cancelhBtn.setEnabled(false);
                }
                if (null != searchCore.getSearchedDatabase()
                        && !searchCore.getSearchedDatabase().getDb().isConnected()) {
                    clearResultData();
                }
                break;
            }
            case SEARCH_END: {
                searchBtn.setEnabled(true);
                cancelhBtn.setEnabled(false);
                break;
            }
            default: {
                searchBtn.setEnabled(false);
                cancelhBtn.setEnabled(false);
                break;
            }
        }

    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        searchCore.clearData();
        searchCore.cleanUpSearch();
        if (UIElement.getInstance().getDatabaseListControl() != null) {
            UIElement.getInstance().getDatabaseListControl().deleteObserver(this);
        }
        UIElement.getInstance().removePartFromStack(UIConstants.UI_PART_SEARCHWINDOW_ID);
    }

    /**
     * Display profile list.
     */
    public void displayProfileList() {
        connectionCombo.removeAll();
        ArrayList<String> profileList = searchCore.getAllProfiles();
        for (String profile : profileList) {
            connectionCombo.add(profile);
        }
        connectionCombo.select(0);
        searchObjInfo.setSelectedserver(0);
        updateChildCombo();
    }

    /**
     * Display database list.
     */
    public void displayDatabaseList() {
        databaseCombo.removeAll();
        ArrayList<String> dbs = searchCore.getAllDatabases();
        if (dbs.size() > 0) {
            databaseCombo.setEnabled(true);
            for (String db : dbs) {
                databaseCombo.add(db);
            }
            databaseCombo.select(0);
            searchObjInfo.setSelectedDB(0);
            schemaCombo.setEnabled(true);
            displaySchemaList();
        } else {
            schemaCombo.clearSelection();
            schemaCombo.removeAll();
        }
        enableDisableSearchButton();
    }

    /**
     * Display schema list.
     */
    public void displaySchemaList() {
        schemaCombo.removeAll();
        if (databaseCombo.getSelectionIndex() != -1) {
            ArrayList<String> namespaces = searchCore.getNamespaceList();
            if (namespaces.size() > 0) {
                for (String namespace : namespaces) {
                    schemaCombo.add(namespace);
                }

                schemaCombo.select(0);
                searchObjInfo.setSelectedNamespace(schemaCombo.getSelectionIndex());
            }
        }
    }

    /**
     * Display name match list.
     */
    public void displayNameMatchList() {
        nameMatchCombo.removeAll();
        ArrayList<String> nameMatchList = searchCore.getNameMatchList();
        for (String nameMatch : nameMatchList) {
            nameMatchCombo.add(nameMatch);
        }
        nameMatchCombo.select(0);
    }

    /**
     * Load object browser selection details.
     */
    public void loadObjectBrowserSelectionDetails() {
        IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();

        Object obj = eclipseContext.get(EclipseContextDSKeys.SERVER_OBJECT);

        if (null == obj) {
            onSelectedObjIsNull();
            return;
        }

        connectionCombo.clearSelection();
        searchText.setText("");
        updateChildCombo();
        if (validateForServerLevelObject(obj)) {
            Server server = getServerObject(obj);
            updateSearchUIForServer(server);
        } else if (obj instanceof Database) {
            loadDatabaseList(obj);
        } else if (validateForDBObjects(obj)) {
            Namespace ns = getNamespace(obj);

            updateSearchUIForSchema(ns);
        } else if (obj instanceof OLAPObjectGroup<?>) {
            loadObjectGroupList(obj);
        } else if (obj instanceof OLAPObjectList<?>) {
            loadObjectlist(obj);
        } else {
            connectionCombo.removeAll();
            searchText.setText("");
        }

        onSelectedObjIsNull();
    }

    private Namespace getNamespace(Object object) {
        Namespace namespace = null;
        if (object instanceof Namespace) {
            namespace = (Namespace) object;
        } else if (validateForFirstLevelChilds(object)) {
            ServerObject firstLevelChild = (ServerObject) object;
            namespace = (Namespace) firstLevelChild.getParent();
        } else if (validateForSeconfLevelChild(object)) {
            ServerObject seciondLevelChild = (ServerObject) object;
            TableMetaData table = (TableMetaData) seciondLevelChild.getParent();
            namespace = table.getNamespace();
        } else {
            ServerObject serverObject = (ServerObject) object;
            ViewMetaData view = (ViewMetaData) serverObject.getParent();
            namespace = view.getNamespace();
        }
        return namespace;
    }

    private boolean validateForDBObjects(Object obj) {
        return obj instanceof Namespace || validateForFirstLevelChilds(obj) || validateForSeconfLevelChild(obj)
                || obj instanceof ViewColumnMetaData;
    }

    private boolean validateForSeconfLevelChild(Object obj) {
        return obj instanceof ColumnMetaData || obj instanceof ConstraintMetaData || obj instanceof IndexMetaData
                || obj instanceof PartitionMetaData;
    }

    private boolean validateForFirstLevelChilds(Object obj) {
        return obj instanceof TableMetaData || obj instanceof ViewMetaData || obj instanceof SequenceMetadata
                || obj instanceof DebugObjects;
    }

    private boolean validateForServerLevelObject(Object obj) {
        return obj instanceof Server || obj instanceof Tablespace || obj instanceof UserRole;
    }

    private Server getServerObject(Object obj) {
        Server server = null;
        if (obj instanceof Server) {
            server = (Server) obj;
        } else if (obj instanceof Tablespace) {
            Tablespace tablespace = (Tablespace) obj;
            server = tablespace.getServer();
        } else {
            UserRole userRole = (UserRole) obj;
            server = userRole.getServer();
        }

        return server;

    }

    private void onSelectedObjIsNull() {
        enableDisableSearchButton();
        if (resultViewer.getInput() != null) {
            clearResultData();
        }
    }

    private void loadObjectlist(Object obj) {
        OLAPObjectList<?> objList = (OLAPObjectList<?>) obj;
        if (objList.getParent() instanceof TableMetaData) {
            TableMetaData table = (TableMetaData) objList.getParent();
            updateSearchUIForSchema(table.getNamespace());
        }
        if (objList.getParent() instanceof ViewMetaData) {
            ViewMetaData view = (ViewMetaData) objList.getParent();
            updateSearchUIForSchema(view.getNamespace());
        }
    }

    private void loadObjectGroupList(Object obj) {
        OLAPObjectGroup<?> objGroup = (OLAPObjectGroup<?>) obj;
        if (objGroup.getParent() instanceof Database) {
            loadDatabaseList(objGroup.getParent());

        } else if (objGroup.getParent() instanceof ServerObject) {
            Namespace ns = (Namespace) objGroup.getParent();
            updateSearchUIForSchema(ns);
        } else if (objGroup.getParent() instanceof Server) {
            Server server = (Server) objGroup.getParent();
            updateSearchUIForServer(server);
        }
    }

    private void loadDatabaseList(Object obj) {
        Database db = (Database) obj;
        int connIndex = searchCore.getObjectBrowserSelectedServer(db.getServer().getDisplayName());
        connectionCombo.select(connIndex);
        searchObjInfo.setSelectedserver(connIndex);
        if (db.isConnected()) {
            displayDatabaseList();
            int dbIndex = searchCore.getObjectBrowserSelectedDatabase(db.getName());
            databaseCombo.select(dbIndex);
            searchObjInfo.setSelectedDB(dbIndex);
            displaySchemaList();
        }
    }

    private void updateSearchUIForServer(Server server) {
        int connIndex = searchCore.getObjectBrowserSelectedServer(server.getDisplayName());
        connectionCombo.select(connIndex);
        searchObjInfo.setSelectedserver(connIndex);
        displayDatabaseList();
    }

    private void updateSearchUIForSchema(Namespace ns) {
        int connIndex = searchCore.getObjectBrowserSelectedServer(ns.getServer().getDisplayName());
        connectionCombo.select(connIndex);
        searchObjInfo.setSelectedserver(connIndex);
        displayDatabaseList();
        Database db = (Database) ns.getParent();
        int dbIndex = searchCore.getObjectBrowserSelectedDatabase(db.getName());
        databaseCombo.select(dbIndex);
        searchObjInfo.setSelectedDB(dbIndex);
        displaySchemaList();
        int schemaIndex = searchCore.getObjectBrowserSelectedSchema(ns.getName());
        schemaCombo.select(schemaIndex);
        searchObjInfo.setSelectedNamespace(schemaIndex);
    }

    /**
     * Gets the selection.
     *
     * @return the selection
     */
    public Object getSelection() {
        return searchSelectionService.getSelection();
    }
    
    /**
     * On focus
     */
    @Focus
    public void onFocus() {
        DBAssistantWindow.toggleCurrentAssitant(true);
    }
}
