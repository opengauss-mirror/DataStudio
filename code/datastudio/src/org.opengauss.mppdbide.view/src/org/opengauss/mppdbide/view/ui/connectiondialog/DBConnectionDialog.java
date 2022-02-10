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

package org.opengauss.mppdbide.view.ui.connectiondialog;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseUtils;
import org.opengauss.mppdbide.bl.serverdatacache.IJobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.helper.SchemaHelper;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.MemoryCleaner;
import org.opengauss.mppdbide.utils.SSLUtility;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.PasswordExpiryException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.ILogger;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.view.cmdline.CmdLineCharObject;
import org.opengauss.mppdbide.view.core.ConnectionNotification;
import org.opengauss.mppdbide.view.core.LoadLevel1Objects;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.HandlerUtilities;
import org.opengauss.mppdbide.view.handler.util.TableViewerUtil;
import org.opengauss.mppdbide.view.init.IDSCommandlineOptions;
import org.opengauss.mppdbide.view.ui.DBAssistantWindow;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.IDEMemoryAnalyzer;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.UIVerifier;
import org.opengauss.mppdbide.view.utils.UserPreference;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBConnectionDialog.
 *
 * @since 3.0.0
 */
public class DBConnectionDialog extends ConnectionDialog {

    private static final int CLEAR_ID = UIConstants.CLEAR_ID;
    private static final int CANCEL_ID = UIConstants.CANCEL_ID;

    private EModelService modelService;
    private MApplication application;
    
    /**
     * The doubleclick event.
     */
    protected HandleDoubleClickEvent doubleclickEvent;

    private IPasswordExpiryCallback callBackInf = null;

    private ConnectionProfileId profileId;
    private Label lblfirstSuccess;

    private boolean flag = false;

    private String[] columns = new String[] {MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_NAME),
        MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_DETAILS),
        MessageConfigLoader.getProperty(IMessagesConstants.CONN_PROP_DB_VERSION)};

    private UIElement uiElementInstance;
    private Composite bodyComposite;
    private Label lblUserName;
    private Label lblMaxHostPort;
    private boolean isCommandlineFlow;
    private NonCloseableDialogWithCancel connectionNotifier;
    /**
     * btnToLoadChildObj
     */
    protected Button btnToLoadChildObj;

    /**
     * Instantiates a new DB connection dialog.
     *
     * @param parentShell the parent shell
     * @param modelService the model service
     * @param application the application
     */
    public DBConnectionDialog(Shell parentShell, EModelService modelService, MApplication application,
            boolean isCommandlineFlow) {
        super(parentShell);
        this.isCommandlineFlow = isCommandlineFlow;
        this.application = application;
        this.modelService = modelService;
        uiElementInstance = UIElement.getInstance();

        setDefaultImage(IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()));
        profileManager = ConnectionProfileManagerImpl.getInstance();
    }


    /**
     * Checks if is flag.
     *
     * @return true, if is flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * Sets the flag.
     *
     * @param flag the new flag
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * Creates and opens command line connection pop up
     */
    public void createConnectionNotifier() {
        connectionNotifier = new NonCloseableDialogWithCancel(
                MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ATTEMPTING_CONNECTION_MSG),
                Display.getDefault().getActiveShell(), this);
        connectionNotifier.setBlockOnOpen(false);
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                connectionNotifier.open();
            }
        });
    }

    /**
     * Sets the shell style.
     *
     * @param arg0 the new shell style
     */
    protected void setShellStyle(int arg0) {
        // Use the following not to show the default close X button in the title
        // bar and avoid the title bar itself
        super.setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation());
    }

    /**
     * Configure shell.
     *
     * @param shell the shell
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_TITLE));
        shell.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_WND_CONNECTION_WIZARD_001");
        shell.setImage(IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()));
    }

    /**
     * Gets the profile id.
     *
     * @return the profile id
     */
    public ConnectionProfileId getProfileId() {
        return profileId;
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {

        ScrolledComposite scMain = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        scMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        formComposite = new Composite(scMain, SWT.FILL | SWT.BORDER);
        formComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        formComposite.setLayout(new GridLayout(1, false));

        scMain.setContent(formComposite);

        createHeader(formComposite);

        Composite conProfComposite = new Composite(formComposite, SWT.NONE);
        GridLayout conProfCompositeLauout = getConProfLayout();
        conProfComposite.setLayout(conProfCompositeLauout);
        conProfComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite btnConProfComposite = createBtnConProfComposite(conProfComposite);
        createTableViewer(btnConProfComposite);
        Composite toolComposite = createToolComposite(btnConProfComposite);

        // delete
        createRemoveBtn(toolComposite);

        Composite connComposite = new Composite(conProfComposite, SWT.BORDER_SOLID);
        connComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout ctabLayout = getCTabLayout();

        connComposite.setLayout(ctabLayout);

        CTabFolder folder = new CTabFolder(connComposite, SWT.BORDER);
        folder.setLayout(new GridLayout(1, true));
        folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite generalComposite = createGeneralTab(folder);

        createSslTab(folder);

        createAdvTabComposite(folder);

        createFooter(connComposite);

        container = generalComposite;

        dbTypeSelectionReactor(UserPreference.getInstance().isSslEnable());

        setExceptionOccured(false);

        loadConnections();

        validateData();

        scMain.setExpandHorizontal(true);
        scMain.setExpandVertical(true);
        scMain.setMinSize(formComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scMain.pack();

        return parent;
    }

    private GridLayout getConProfLayout() {
        GridLayout layout = new GridLayout(2, false);
        layout.marginLeft = 10;
        layout.marginRight = 10;
        return layout;
    }

    private GridLayout getCTabLayout() {
        GridLayout ctabLayout = new GridLayout(1, false);
        ctabLayout.marginHeight = 0;
        ctabLayout.marginWidth = 0;
        ctabLayout.marginBottom = 0;
        return ctabLayout;
    }

    private Composite createToolComposite(Composite btnConProfComposite) {
        Composite toolComposite = new Composite(btnConProfComposite, SWT.NONE);
        toolComposite.setLayout(new GridLayout(1, true));
        GridData gData = new GridData();
        gData.widthHint = 150;
        toolComposite.setLayoutData(gData);
        return toolComposite;
    }

    private Composite createGeneralTab(CTabFolder folder) {
        Composite generalComposite = new Composite(folder, SWT.NONE);
        generalComposite.setLayout(new GridLayout(1, true));
        CTabItem tabGeneral = new CTabItem(folder, SWT.NONE);

        tabGeneral.setText("  " + MessageConfigLoader.getProperty(IMessagesConstants.CTAB_GENERAL) + "  ");
        folder.setSelection(tabGeneral);
        folder.setSelectionBackground(new Color[] {generalComposite.getBackground(), generalComposite.getBackground(),
            generalComposite.getBackground()}, new int[] {50, 100});

        tabGeneral.setControl(createGaussControls(generalComposite));
        return generalComposite;
    }

    private void createSslTab(CTabFolder folder) {
        Composite sslComposite = new Composite(folder, SWT.NONE);
        sslComposite.setLayout(new GridLayout(1, true));
        CTabItem sslTab = new CTabItem(folder, SWT.NONE);
        sslTab.setText("  " + MessageConfigLoader.getProperty(IMessagesConstants.CTAB_SSL) + "  ");
        sslTab.setControl(createSSLGaussControls(sslComposite));
    }

    private void createAdvTabComposite(CTabFolder folder) {
        Composite advComposite = new Composite(folder, SWT.NONE);
        advComposite.setLayout(new GridLayout(1, true));
        advComposite.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
        CTabItem advTab = new CTabItem(folder, SWT.NONE);
        advTab.setText("  " + MessageConfigLoader.getProperty(IMessagesConstants.CTAB_ADVANCED) + "  ");
        advTab.setControl(createAdvGaussControls(advComposite));
    }

    private void createRemoveBtn(Composite toolComposite) {
        GridData toolgriddata = new GridData();
        toolgriddata.grabExcessHorizontalSpace = true;
        toolgriddata.horizontalSpan = 2;
        toolgriddata.widthHint = 125;

        btnRemove = new Button(toolComposite, SWT.PUSH);
        btnRemove.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_SQLEXECUTE_BUTTON_001");
        btnRemove.setText(MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_PROFILE));
        btnRemove.setLayoutData(toolgriddata);
        btnRemove.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_BP));
        btnRemove.addSelectionListener(new HandleDeleteProfile());
    }

    private void createTableViewer(Composite btnConProfComposite) {
        viewer = new TableViewer(btnConProfComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        final Table table = viewer.getTable();
        viewer.setColumnProperties(columns);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        viewer.setContentProvider(new ArrayContentProvider());
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        viewer.getControl().setLayoutData(gridData);
        createColumns();
        doubleclickEvent = new HandleDoubleClickEvent();
        viewer.addDoubleClickListener(doubleclickEvent);
        btnConProfComposite.pack();
    }

    private Composite createBtnConProfComposite(Composite conProfComposite) {
        Composite btnConProfComposite = new Composite(conProfComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        GridData btngridData = new GridData();
        btngridData.grabExcessHorizontalSpace = false;
        btngridData.grabExcessVerticalSpace = true;
        btngridData.horizontalAlignment = SWT.FILL;
        btngridData.verticalAlignment = SWT.FILL;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        btngridData.heightHint = 20;
        btnConProfComposite.setLayout(layout);
        btnConProfComposite.setLayoutData(btngridData);
        return btnConProfComposite;
    }

    private void createColumns() {
        TableViewerColumn profileNameCol = new TableViewerColumn(viewer, SWT.NONE, 0);

        profileNameCol.getColumn().setWidth(150);
        profileNameCol.getColumn().setResizable(true);
        profileNameCol.setLabelProvider(new ProfileNameColumnLabelProvider());
        profileNameCol.getColumn().setText(columns[0]);
        profileNameCol.getColumn().setToolTipText(columns[0]);

        TableViewerColumn profileURLCol = new TableViewerColumn(viewer, SWT.NONE, 1);

        profileURLCol.getColumn().setWidth(200);
        profileURLCol.getColumn().setResizable(true);
        profileURLCol.setLabelProvider(new ProfileURLColumnLabelProvider());
        profileURLCol.getColumn().setText(columns[1]);
        profileURLCol.getColumn().setToolTipText(columns[1]);

        TableViewerColumn profileDatabaseVersion = new TableViewerColumn(viewer, SWT.NONE, 2);
        profileDatabaseVersion.getColumn().setWidth(150);
        profileDatabaseVersion.getColumn().setResizable(true);
        profileDatabaseVersion.setLabelProvider(new ProfileDatabaseVersionProvider());
        profileDatabaseVersion.getColumn().setText(columns[2]);
        profileDatabaseVersion.getColumn().setToolTipText(columns[2]);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class HandleDoubleClickEvent.
     */
    private final class HandleDoubleClickEvent implements IDoubleClickListener {
        @Override
        public void doubleClick(DoubleClickEvent event) {
            ISelection selection = event.getSelection();
            if (selection instanceof IStructuredSelection) {
                Object obj = ((IStructuredSelection) selection).getFirstElement();
                if (obj instanceof String[]) {
                    lblfirstSuccess.setVisible(false);
                    String[] item = (String[]) obj;
                    IServerConnectionInfo info = profileManager.getProfile(item[0]);

                    populateConnectionInfoFromPreference(info);
                    validateData();
                    // updating the combo
                    getDriverCombotext = dbTypeCombo.getText();
                }
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ProfileNameColumnLabelProvider.
     */
    private static class ProfileNameColumnLabelProvider extends ColumnLabelProvider {
        @Override
        public String getText(Object element) {
            return ((String[]) element)[0];
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ProfileURLColumnLabelProvider.
     */
    private static class ProfileURLColumnLabelProvider extends ColumnLabelProvider {
        @Override
        public String getText(Object element) {
            return ((String[]) element)[1];
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ProfileDatabaseVersionProvider.
     */
    private static class ProfileDatabaseVersionProvider extends ColumnLabelProvider {
        @Override
        public String getText(Object element) {
            return ((String[]) element)[2];
        }
    }

    /**
     * Load connections.Load profiles into the table
     */
    protected void loadConnections() {
        viewer.getTable().setRedraw(false);

        try {
            List<IServerConnectionInfo> profiles = profileManager.getAllProfiles();
            clearPasswordSaveOption();

            int index = 0;

            loadTableWithConnectionDetail(profiles, index);
            viewer.getTable().setRedraw(true);
            displayExceptionOnFailure();
        } catch (MPPDBIDEException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.LOAD_CONN_ERR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.LOAD_CONN_ERR_MSG, exception.getMessage()));

            setExceptionOccured(true);
            asyncClose();
        } catch (IOException ioException) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.LOAD_CONN_ERR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.LOAD_CONN_ERR_MSG, ioException.getMessage()));
        }
    }

    private void displayExceptionOnFailure() {
        List<String> exceptionList = ConnectionProfileManagerImpl.getInstance().getExceptionList();

        for (String exception : exceptionList) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.LOAD_CONN_ERR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.LOAD_CONN_ERR_MSG, exception));
        }
        ConnectionProfileManagerImpl.getInstance().clearExceptionList();
    }

    private void loadTableWithConnectionDetail(List<IServerConnectionInfo> profiles, int index) {
        List<IServerConnectionInfo> connProfile = new ArrayList<IServerConnectionInfo>();
        for (IServerConnectionInfo connInfo : profiles) {
            if (!connInfo.getConectionName().trim().isEmpty()) {
                connProfile.add(connInfo);
            }
        }
        int length = connProfile.size();
        String[][] values = new String[length][viewer.getTable().getColumnCount()];
        IServerConnectionInfo info = null;
        for (; index < length; index++) {
            info = connProfile.get(index);
            if (info != null) {
                values[index][0] = info.getConectionName();
                values[index][1] = info.getDsUsername() + '@' + info.getServerIp() + ':' + info.getServerPort() + '/'
                        + info.getDatabaseName();
                values[index][2] = CustomStringUtility.parseServerVersion(info.getDBVersion());
            }
        }
        connProfile.clear();
        viewer.setInput(values);
    }

    private void clearPasswordSaveOption() throws DatabaseOperationException, DataStudioSecurityException, IOException {
        if (!getEnablePermanentPasswordSaveOption()) {
            profileManager.clearPermanentSavePwd();
        }
    }

    /**
     * Creates the header.
     *
     * @param formComps the form comps
     */
    protected void createHeader(Composite formComps) {
        Composite headerComposite = new Composite(formComps, SWT.NONE);

        GridLayout headerGridLayout = new GridLayout();
        headerGridLayout.numColumns = 2;
        headerGridLayout.makeColumnsEqualWidth = true;
        headerComposite.setLayout(headerGridLayout);
        headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite subHeaderComposite = new Composite(headerComposite, SWT.NONE);
        subHeaderComposite.setLayout(new GridLayout(2, false));

        Label lblImg = new Label(subHeaderComposite, SWT.NONE);
        lblImg.setImage(IconUtility.getIconImage(IiconPath.ICO_ACCEPT_DB, this.getClass()));

        RowLayout textHeaderRowLayout = new RowLayout();
        textHeaderRowLayout.type = SWT.VERTICAL;
        Composite headerTitleComposite = new Composite(subHeaderComposite, SWT.NONE);
        headerTitleComposite.setLayout(textHeaderRowLayout);

        lblInfo = new Label(headerTitleComposite, SWT.BOLD);
        lblInfo.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_TITLE));
        lblInfo.setFont(new Font(Display.getDefault(), "Arial", 12, SWT.BOLD));

        lblSubInfo = new Label(headerTitleComposite, SWT.BOLD);
        lblSubInfo.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_MSG));
        lblSubInfo.setFont(new Font(Display.getDefault(), "Arial", 10, SWT.NORMAL));
        lblSubInfo.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
        lblSubInfo.redraw();

        Composite lblComposite = new Composite(headerComposite, SWT.NONE);
        lblComposite.setLayout(new GridLayout(1, false));
        GridData lblGridData = new GridData();
        lblGridData.horizontalAlignment = GridData.END;
        lblGridData.grabExcessHorizontalSpace = true;
        lblComposite.setLayoutData(lblGridData);
        CLabel lblHelp = new CLabel(lblComposite, SWT.NONE);
        lblHelp.setImage(IconUtility.getIconImage(IiconPath.ICO_HELP, this.getClass()));
        lblHelp.setText(MessageConfigLoader.getProperty(IMessagesConstants.HELP_LABEL));
        Font font = new Font(lblHelp.getDisplay(), "Arial", 12, SWT.BOLD);
        lblHelp.setFont(font);
        lblHelp.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE));
        lblHelp.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_HELP));

        lblHelp.addMouseListener(new FooterMouseListener());
    }

    /**
     * Footer with help icon, progress bar and buttons.
     *
     * @param formComps the form comps
     */
    private void createFooter(Composite formComps) {
        final Composite footerComposite = new Composite(formComps, SWT.NONE);
        footerComposite.setLayout(new GridLayout(2, false));
        footerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        progressBar = new ProgressBar(footerComposite, SWT.BORDER | SWT.INDETERMINATE);
        GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, true, true);
        progressBar.setLayoutData(data);
        progressBar.setVisible(false);
        progressBar.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_TO_SERVER));

        progressBar.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent event) {
                String string = MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_TO_SERVER);
                Point point = progressBar.getSize();

                FontMetrics fontMetrics = event.gc.getFontMetrics();
                int width = fontMetrics.getAverageCharWidth() * string.length();
                int height = fontMetrics.getHeight();
                if (footerComposite.getDisplay() != null) {
                    event.gc.setForeground(footerComposite.getDisplay().getSystemColor(SWT.COLOR_BLACK));
                }
                event.gc.drawString(string, (point.x - width) / 2, (point.y - height) / 2, true);
            }
        });

        buttonBar = createButtonBar(footerComposite);
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        okButton = createButton(parent, OK_ID, okLabel, true);
        okButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_OK_001");

        clearButton = createButton(parent, CLEAR_ID, clearLabel, false);
        clearButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CLEAR_001");

        closeButton = createButton(parent, UIConstants.CLOSE_ID, closeLabel, false);
        closeButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CANCEL_001");

        okButton.setEnabled(false);
        setButtonLayoutData(okButton);
    }

    /**
     * Create Gauss database related information controls.
     *
     * @param formComps the form comps
     * @return the control
     */
    private Control createGaussControls(Composite formComps) {
        bodyComposite = new Composite(formComps, SWT.NONE);

        bodyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 30;
        bodyComposite.setLayout(layout);

        dbTypeOption(bodyComposite);
        dbTypeCombo.addSelectionListener(getDbTypeSelectionListener());

        addConnectionNameUi(bodyComposite);
        /* Host IP Address */
        addHostIpAddressUi(bodyComposite);

        /* Host Port */
        Composite compositeHostPort = addHostPortUi(bodyComposite);

        /* Max value for port */
        addMaxValueForPortUi(compositeHostPort);
        addDataBaseName(bodyComposite);

        /* User Name */
        addUserNameUi(bodyComposite);
        /* Pswd */
        addPswdUi(bodyComposite);

        Label lblSavePswdOptions = new Label(bodyComposite, SWT.NULL);
        lblSavePswdOptions.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONN_DIALOG_SAVE_CIPHER));
        addPasswordSaveOptions(bodyComposite);

        Label lblSSLEnable = new Label(bodyComposite, SWT.NONE);
        lblSSLEnable.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_ENABLE_SSL));

        /* Is SSL Enabled */
        addSSLCheckBox(bodyComposite);
        return formComps;
    }

    private void addSSLCheckBox(Composite bodyComposite) {
        gaussSSLEnableButton = new Button(bodyComposite, SWT.CHECK);
        gaussSSLEnableButton.setSelection(UserPreference.getInstance().isSslEnable());
        setSelectionListener(gaussSSLEnableButton);
    }

    private void addPasswordSaveOptions(Composite bodyComposite) {
        savePswdOptions = new Combo(bodyComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        String[] saveOptions = null;
        if (getEnablePermanentPasswordSaveOption()) {
            saveOptions = new String[] {
                MessageConfigLoader.getProperty(SavePrdOptions.PERMANENTLY.toString() + "_SAVE"),
                MessageConfigLoader.getProperty(SavePrdOptions.CURRENT_SESSION_ONLY.toString()),
                MessageConfigLoader.getProperty(SavePrdOptions.DO_NOT_SAVE.toString())};
        } else {
            saveOptions = new String[] {MessageConfigLoader.getProperty(SavePrdOptions.CURRENT_SESSION_ONLY.toString()),
                MessageConfigLoader.getProperty(SavePrdOptions.DO_NOT_SAVE.toString())};
        }

        savePswdOptions.setItems(saveOptions);
        savePswdOptions.select(getComboSelectionIndex(SavePrdOptions.CURRENT_SESSION_ONLY));

    }

    private void addPswdUi(Composite bodyComposite) {
        Label lblPrd = new Label(bodyComposite, SWT.NULL);
        lblPrd.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_PSW));

        gaussPrd = new Text(bodyComposite, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
        setInitTextPropertiesGeneralTab(gaussPrd);
        gaussPrd.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONNECTION_PASSWORD_001");
        UIVerifier.verifyTextSize(gaussPrd, 32);
    }

    private void addUserNameUi(Composite bodyComposite) {
        lblUserName = new Label(bodyComposite, SWT.NULL);
        lblUserName.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_USER_NAME));

        gaussUserName = new Text(bodyComposite, SWT.BORDER | SWT.SINGLE);
        setInitTextPropertiesGeneralTab(gaussUserName);
        gaussUserName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONNECTION_USERNAME_001");
        UIVerifier.verifyTextSize(gaussUserName, 63);
    }

    private void addDataBaseName(Composite bodyComposite) {
        lblDBName = new Label(bodyComposite, SWT.NULL);
        lblDBName.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_DB_NAME_OLAP));
        gaussDbName = new Text(bodyComposite, SWT.BORDER | SWT.SINGLE);
        setInitTextProperties(gaussDbName);
        gaussDbName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONNECTION_DBNAME_001");

        UIVerifier.verifyTextSize(gaussDbName, 63);
    }

    private void addMaxValueForPortUi(Composite compositeHostPort) {
        lblMaxHostPort = new Label(compositeHostPort, SWT.NULL);
        lblMaxHostPort.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_MAX_VALUE) + ' '
                + MPPDBIDEConstants.MAX_HOST_PORT);
        lblMaxHostPort.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
        lblMaxHostPort.setFont(new Font(Display.getDefault(), "Arial", 9, SWT.ITALIC));

        GridLayout portLayout = new GridLayout(2, false);
        portLayout.horizontalSpacing = 0;
        portLayout.marginWidth = 0;
        portLayout.marginHeight = 5;
        portLayout.verticalSpacing = 0;
        compositeHostPort.setLayout(portLayout);
    }

    private Composite addHostPortUi(Composite bodyComposite) {
        Label lblHostPort = new Label(bodyComposite, SWT.NULL);
        lblHostPort.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_HOST_PORT));

        Composite compositeHostPort = new Composite(bodyComposite, SWT.BORDER_SOLID);

        Composite composite = new Composite(compositeHostPort, SWT.None);
        gaussHostPort = new Text(composite, SWT.BORDER | SWT.SINGLE);
        setInitTextPropertiesGeneralTab(gaussHostPort);
        Point pt = gaussHostPort.getSize();
        gaussHostPort.setSize(pt.x + 1, pt.y);
        gaussHostPort.setSize(gaussHostPort.computeSize(50, 15));

        gaussHostPort.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONNECTION_PORT_001");
        PortKeyListner keyListner = new PortKeyListner();
        gaussHostPort.addKeyListener(keyListner);
        DBConnectionValidator verifyListener = new DBConnectionValidator(gaussHostPort,
                MPPDBIDEConstants.MAX_HOST_PORT);
        gaussHostPort.addVerifyListener(verifyListener);
        return compositeHostPort;
    }

    private void addHostIpAddressUi(Composite bodyComposite) {
        Label lblHostAddr = new Label(bodyComposite, SWT.NULL);
        lblHostAddr.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_HOST));

        gaussHostAddr = new Text(bodyComposite, SWT.BORDER | SWT.SINGLE);
        setInitTextPropertiesGeneralTab(gaussHostAddr);
        gaussHostAddr.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONNECTION_HOST_001");
        gaussHostAddr.setTextLimit(253);
    }

    private void addConnectionNameUi(Composite bodyComposite) {
        Label lblConnectionName = new Label(bodyComposite, SWT.NULL);
        lblConnectionName.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_CONN_NAME));

        gaussConnectionName = new Text(bodyComposite, SWT.BORDER | SWT.SINGLE);
        setInitTextPropertiesGeneralTab(gaussConnectionName);
        gaussConnectionName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONNECTION_CONNECTIONNAME_001");

        ConnectionNameValidator nameValidator = new ConnectionNameValidator(gaussConnectionName);
        gaussConnectionName.addVerifyListener(nameValidator);
        UIVerifier.verifyTextSize(gaussConnectionName, 63);
    }

    /**
     * Db type option.
     *
     * @param formComps the form comps
     */
    protected void dbTypeOption(Composite formComps) {
        String[] dbOptionTxt = new String[] {MessageConfigLoader.getProperty(IMessagesConstants.OPEN_GAUSS)};

        Label lblPrivilegeEnable = new Label(formComps, SWT.None);
        lblPrivilegeEnable.setText(MessageConfigLoader.getProperty(IMessagesConstants.LBL_DBTYPE));
        GC gc = new GC(getShell());
        dbTypeCombo = new Combo(formComps, SWT.READ_ONLY);
        dbTypeCombo.setItems(dbOptionTxt);
        dbTypeCombo.select(0);
        dbTypeCombo.setEnabled(false);
        getDriverCombotext = dbTypeCombo.getText();
        gc.setLineStyle(3);
        gc.dispose();
    }

    /**
     * Gets the db type selection listener.
     *
     * @return the db type selection listener
     */
    protected SelectionListener getDbTypeSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                // updating the combo text
                getDriverCombotext = dbTypeCombo.getText();
                // disabling the ok-button if DB type is changed from oltp to
                // olap after populating
                /* adding Database name field */
                if (null == lblDBName || lblDBName.isDisposed()) {
                    loadDbNameComp();
                }
                okButton.setEnabled(false);
                dbTypeSelectionReactor(UserPreference.getInstance().isSslEnable());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        };
    }

    /**
     * Load db name comp.
     */
    public void loadDbNameComp() {
        addDataBaseName(bodyComposite);
        lblDBName.moveAbove(lblUserName);
        gaussDbName.moveBelow(lblDBName);
        lblDBName.getParent().pack();
    }

    /**
     * Db type selection reactor.
     *
     * @param isOlap the is olap
     */
    protected void dbTypeSelectionReactor(boolean isSslEnabled) {
        if (null != gaussDbName && !gaussDbName.isDisposed()) {
            gaussDbName.setEnabled(true);
        }
        gaussSSLEnableButton.setSelection(isSslEnabled);
        gaussSSLEnableButton.setEnabled(true);
        enableDisableSSLTabAttributes(gaussSSLEnableButton.getSelection());
        enableDisableAdvancedTabAttributes(true);
    }

    private Control createSSLGaussControls(Composite formComps) {
        Composite sslGaussControlComposite = new Composite(formComps, SWT.NONE);

        sslGaussControlComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        GridLayout bodyLayout = new GridLayout(2, true);
        bodyLayout.horizontalSpacing = 0;
        bodyLayout.marginWidth = 0;
        bodyLayout.marginHeight = 0;
        bodyLayout.verticalSpacing = 0;

        sslGaussControlComposite.setLayout(bodyLayout);

        GridLayout valueFieldLayout = new GridLayout(2, false);
        valueFieldLayout.horizontalSpacing = 0;
        valueFieldLayout.marginWidth = 0;
        valueFieldLayout.marginHeight = 5;
        valueFieldLayout.verticalSpacing = 2;

        // for ClientSSLCertificate
        addSslCertSelectionUi(sslGaussControlComposite, valueFieldLayout);

        // for clientSSL key
        addSslClientKeyPathSelectionUi(sslGaussControlComposite, valueFieldLayout);

        // for root key
        addRootKeySelectionUi(sslGaussControlComposite, valueFieldLayout);

        // for ssl password
        addSslPasswordui(sslGaussControlComposite, valueFieldLayout);

        // Dropdown list for SSL MODE

        addSslModeSelectionUi(sslGaussControlComposite, valueFieldLayout);

        addSuccessMsgUi(sslGaussControlComposite);
        return formComps;
    }

    private void addSuccessMsgUi(Composite bodyComposite) {
        lblfirstSuccess = new Label(bodyComposite, SWT.NULL);
        lblfirstSuccess.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_SSL_PROPERTY));
        lblfirstSuccess.setForeground(new org.eclipse.swt.graphics.Color(lblfirstSuccess.getDisplay(), 255, 0, 0));
        lblfirstSuccess.setVisible(false);
    }

    private void addSslModeSelectionUi(Composite bodyComposite, GridLayout valueFieldLayout) {
        Label lblSSLModesOptions = new Label(bodyComposite, SWT.NONE);

        lblSSLModesOptions.setText(MessageConfigLoader.getProperty(IMessagesConstants.DROPDOWN_SSLMODE));

        Composite sslModeComposite = new Composite(bodyComposite, SWT.NULL);
        sslModeComposite.setLayout(valueFieldLayout);
        sslModeComposite.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, false));

        sslModeOptions = new Combo(sslModeComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        sslModeOptions.setLayout(valueFieldLayout);

        sslModeOptions.setItems(sslModes);
        sslModeOptions.setText(sslModes[0]);
        sslModeOptions.addSelectionListener(new SslModeOptionSelectionListener());
    }

    private void addSslPasswordui(Composite bodyComposite, GridLayout valueFieldLayout) {
        final Label sslPassword = new Label(bodyComposite, SWT.NULL);
        sslPassword.setText(MessageConfigLoader.getProperty(IMessagesConstants.SSL_CIPHER));
        Composite sslPasswordComposite = new Composite(bodyComposite, SWT.NULL);
        sslPasswordComposite.setLayout(valueFieldLayout);
        sslPasswordComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        sslPrd = new Text(sslPasswordComposite, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
        setInitTextProperties(sslPrd);
        sslPrd.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONNECTION_PASSWORD_001");
        UIVerifier.verifyTextSize(sslPrd, 32);
    }

    private void addRootKeySelectionUi(Composite bodyComposite, GridLayout valueFieldLayout) {
        final Label rootCert = new Label(bodyComposite, SWT.NULL);
        rootCert.setText(MessageConfigLoader.getProperty(IMessagesConstants.ROOTCERT_FILETEXT));
        Composite rootCertComposite = new Composite(bodyComposite, SWT.NULL);
        rootCertComposite.setLayout(valueFieldLayout);
        rootCertComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        rootCertFilePathText = new Text(rootCertComposite, SWT.READ_ONLY | SWT.BORDER);
        rootCertFilePathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        rootCertFilePathText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        setInitTextProperties(rootCertFilePathText);

        rootCertBrowseBtn = new Button(rootCertComposite, SWT.NONE);

        rootCertBrowseBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_BROWSE));
        rootCertBrowseBtn.addSelectionListener(new RootCertBrowseBtnSelectionListener());
    }

    private void addSslClientKeyPathSelectionUi(Composite bodyComposite, GridLayout valueFieldLayout) {
        final Label keySSl = new Label(bodyComposite, SWT.NULL);
        keySSl.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_LBL_CLIENT_SSL_KEY));
        Composite clientKeyComposite = new Composite(bodyComposite, SWT.NULL);
        clientKeyComposite.setLayout(valueFieldLayout);
        clientKeyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        clSSLKeyFilePathText = new Text(clientKeyComposite, SWT.READ_ONLY | SWT.BORDER);
        clSSLKeyFilePathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        clSSLKeyFilePathText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        setInitTextProperties(clSSLKeyFilePathText);

        sslKeyBrowseBtn = new Button(clientKeyComposite, SWT.NONE);

        sslKeyBrowseBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_BROWSE));
        sslKeyBrowseBtn.addSelectionListener(new SslKeyBrosweBtnSelectionListener());
    }

    private void addSslCertSelectionUi(Composite bodyComposite, GridLayout valueFieldLayout) {
        final Label certSSl = new Label(bodyComposite, SWT.NONE);
        certSSl.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_LBL_CLIENT_SSL_CERT));
        Composite clientCertComposite = new Composite(bodyComposite, SWT.NONE);

        clientCertComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        clientCertComposite.setLayout(valueFieldLayout);

        clSSLCertFilePathText = new Text(clientCertComposite, SWT.READ_ONLY | SWT.BORDER);
        clSSLCertFilePathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        clSSLCertFilePathText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        setInitTextProperties(clSSLCertFilePathText);

        sslCertBrowseBtn = new Button(clientCertComposite, SWT.NONE);

        sslCertBrowseBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_BROWSE));
        sslCertBrowseBtn.addSelectionListener(new SslCertBrowseBtnSelectionListener());
    }

    /**
     * The listener interface for receiving sslModeOptionSelection events. The
     * class that is interested in processing a sslModeOptionSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addSslModeOptionSelectionListener<code> method. When the
     * sslModeOptionSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * SslModeOptionSelectionEvent
     */
    private class SslModeOptionSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent arg0) {
            if (!isDisposed()) {
                if (isDialogComplete()) {
                    okButton.setEnabled(true);
                } else {
                    okButton.setEnabled(false);
                }
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent arg0) {
        }

    }

    /**
     * The listener interface for receiving rootCertBrowseBtnSelection events.
     * The class that is interested in processing a rootCertBrowseBtnSelection
     * event implements this interface, and the object created with that class
     * is registered with a component using the component's
     * <code>addRootCertBrowseBtnSelectionListener<code> method. When the
     * rootCertBrowseBtnSelection event occurs, that object's appropriate method
     * is invoked.
     *
     * RootCertBrowseBtnSelectionEvent
     */
    private class RootCertBrowseBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
            dialog.setFilterNames(
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BROWSE_BTN_ROOT_CERT)});
            dialog.setFilterExtensions(
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BROWSE_BTN_ROOT_CERT),
                        MessageConfigLoader.getProperty(IMessagesConstants.BROWSE_BTN_PEM_CERT)});

            String rootPath = dialog.open();
            rootCertFilePathText.setText(rootPath != null ? rootPath : "");

            validateData();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
        }

    }

    /**
     * The listener interface for receiving sslKeyBrosweBtnSelection events. The
     * class that is interested in processing a sslKeyBrosweBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addSslKeyBrosweBtnSelectionListener<code> method. When the
     * sslKeyBrosweBtnSelection event occurs, that object's appropriate method
     * is invoked.
     *
     * SslKeyBrosweBtnSelectionEvent
     */
    private class SslKeyBrosweBtnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
            dialog.setFilterNames(
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_BTN_CLIENT_KEY)});
            dialog.setFilterExtensions(
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_BTN_CLIENT_KEY)});

            String clientKeyPath = dialog.open();
            clSSLKeyFilePathText.setText(clientKeyPath != null ? clientKeyPath : "");
            validateData();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * The listener interface for receiving sslCertBrowseBtnSelection events.
     * The class that is interested in processing a sslCertBrowseBtnSelection
     * event implements this interface, and the object created with that class
     * is registered with a component using the component's
     * <code>addSslCertBrowseBtnSelectionListener<code> method. When the
     * sslCertBrowseBtnSelection event occurs, that object's appropriate method
     * is invoked.
     *
     * SslCertBrowseBtnSelectionEvent
     */
    private class SslCertBrowseBtnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
            dialog.setFilterNames(
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_BTN_CLIENT_CERT)});
            dialog.setFilterExtensions(
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_BTN_CLIENT_CERT)});

            String clientCertPath = dialog.open();
            clSSLCertFilePathText.setText(clientCertPath != null ? clientCertPath : "");
            validateData();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
        }

    }

    private Control createAdvGaussControls(Composite formComps) {
        Group parent = new Group(formComps, SWT.NONE);
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        parent.setLayout(new GridLayout(1, false));

        parent.setText(MessageConfigLoader.getProperty(IMessagesConstants.LBL_FAST_LOAD_OPTIONS));

        GridLayout grpFieldLayout = getGridLayoutForField();
        addIncludeExcludeSchemaUi(parent, grpFieldLayout);

        Group obAccess = new Group(parent, SWT.NONE);
        obAccess.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, false, true));
        obAccess.setLayout(grpFieldLayout);
        obAccess.setText(MessageConfigLoader.getProperty(IMessagesConstants.LBL_GRP_OB_ACESS));

        GridLayout layout = getGridLayoutForLoadOption();

        addObjectBrowserLoadOptions(obAccess, layout);

        Composite compo = new Composite(obAccess, SWT.NONE);

        GridLayout loadOptionsLayout = new GridLayout(1, false);
        loadOptionsLayout.marginBottom = 5;
        loadOptionsLayout.marginWidth = 5;

        compo.setLayout(loadOptionsLayout);
        compo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        btnToLoadChildObj = new Button(compo, SWT.CHECK);
        btnToLoadChildObj.setText(MessageConfigLoader.getProperty(IMessagesConstants.LABEL_LOAD_CHILD_OBJECT));
        btnToLoadChildObj.addSelectionListener(addBtnLoadObjSelectionListener());

        GridLayout maxloadlimitLayout = getGridLayoutMaxLoadLimit();

        Composite compositeloadLimit = new Composite(obAccess, SWT.BORDER_SOLID);
        compositeloadLimit.setLayout(maxloadlimitLayout);
        compositeloadLimit.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, false));

        addLoadLimitUi(compositeloadLimit);

        /* Max value for LoadLimit */
        addMaxForLoadLimit(compositeloadLimit);
        loadAdvaceTabData();
        return formComps;
    }

    private void loadAdvaceTabData() {
        btnToLoadChildObj.setSelection(true);
        loadLimit.setEnabled(btnToLoadChildObj.getSelection());
    }

    private SelectionListener addBtnLoadObjSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadLimit.setEnabled(btnToLoadChildObj.getSelection());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        };
    }

    private GridLayout getGridLayoutForField() {
        GridLayout grpFieldLayout = new GridLayout(1, false);
        grpFieldLayout.horizontalSpacing = 0;
        grpFieldLayout.marginWidth = 0;
        grpFieldLayout.marginHeight = 3;
        grpFieldLayout.verticalSpacing = 05;
        grpFieldLayout.marginBottom = 0;
        return grpFieldLayout;
    }

    private GridLayout getGridLayoutForLoadOption() {
        GridLayout loadOptionsLayout = new GridLayout(3, false);
        loadOptionsLayout.horizontalSpacing = 13;
        loadOptionsLayout.marginWidth = 5;
        loadOptionsLayout.marginHeight = 0;
        loadOptionsLayout.verticalSpacing = 0;
        loadOptionsLayout.marginTop = 0;
        loadOptionsLayout.marginBottom = 5;
        return loadOptionsLayout;
    }

    private GridLayout getGridLayoutMaxLoadLimit() {
        GridLayout maxloadlimitLayout = new GridLayout(2, false);
        maxloadlimitLayout.horizontalSpacing = 13;
        maxloadlimitLayout.marginWidth = 5;
        maxloadlimitLayout.marginHeight = 0;
        maxloadlimitLayout.verticalSpacing = 0;
        maxloadlimitLayout.marginTop = 0;
        maxloadlimitLayout.marginBottom = 13;
        return maxloadlimitLayout;
    }

    private GridLayout getGridLayoutForLoadLimit() {
        GridLayout loadlimitLayout = new GridLayout(2, false);
        loadlimitLayout.horizontalSpacing = 42;
        loadlimitLayout.marginWidth = 5;
        loadlimitLayout.marginHeight = 0;
        loadlimitLayout.verticalSpacing = 0;
        loadlimitLayout.marginTop = 0;
        loadlimitLayout.marginBottom = 13;
        return loadlimitLayout;
    }

    private void addLoadLimitUi(Composite compositeloadLimit) {
        GridLayout loadlimitLayout = getGridLayoutForLoadLimit();
        Composite composite = new Composite(compositeloadLimit, SWT.None);
        composite.setLayout(loadlimitLayout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, true, true));

        Label lblloadLimit = new Label(composite, SWT.NONE);
        lblloadLimit.setText(MessageConfigLoader.getProperty(IMessagesConstants.LBL_LOAD_LIMIT));
        loadLimit = new Text(composite, SWT.BORDER | SWT.SINGLE);
        lblloadLimit.setAlignment(SWT.LEFT);
        setInitTextProperties(loadLimit);
        loadLimit.setText(DEFAULT_LOAD_LIMIT);

        DBConnectionValidator verifyListener = new DBConnectionValidator(loadLimit, MPPDBIDEConstants.MAX_LOAD_LIMIT);
        loadLimit.addVerifyListener(verifyListener);
    }

    private void addMaxForLoadLimit(Composite compositeloadLimit) {
        Label lblMax = new Label(compositeloadLimit, SWT.WRAP);
        lblMax.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_MAX_VALUE) + ' '
                + MPPDBIDEConstants.MAX_LOAD_LIMIT);
        lblMax.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
        lblMax.setFont(new Font(Display.getDefault(), "Arial", 8, SWT.ITALIC));

        Label loadLimitUnit = new Label(compositeloadLimit, SWT.WRAP);
        loadLimitUnit.setText(MessageConfigLoader.getProperty(IMessagesConstants.LOAD_LIMIT_UNIT));
        loadLimitUnit.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
        loadLimitUnit.setFont(new Font(Display.getDefault(), "Arial", 8, SWT.ITALIC));
        loadLimitUnit.setAlignment(SWT.LEFT);
        loadLimitUnit.pack();
    }

    private void addObjectBrowserLoadOptions(Group obAccess, GridLayout loadOptionsLayout) {
        Composite compObAccessMode = new Composite(obAccess, SWT.NONE);
        compObAccessMode.setLayout(loadOptionsLayout);
        compObAccessMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label lblPrivilegeEnable = new Label(compObAccessMode, SWT.NONE);
        lblPrivilegeEnable.setText(MessageConfigLoader.getProperty(IMessagesConstants.LBL_OBJS));

        disablePrivilege = new Button(compObAccessMode, SWT.RADIO);
        disablePrivilege.setSelection(false);
        Label lblPrivilegeEnableRadio = new Label(compObAccessMode, SWT.NONE);
        lblPrivilegeEnableRadio.setText(MessageConfigLoader.getProperty(IMessagesConstants.OB_ACCESS_TYPE_ALL));

        Label spaceHolder = new Label(compObAccessMode, SWT.NONE);
        spaceHolder.setText("");
        enablePrivilege = new Button(compObAccessMode, SWT.RADIO);
        enablePrivilege.setSelection(true);
        Label lblPrivilegeDisbleRadio = new Label(compObAccessMode, SWT.NONE);
        lblPrivilegeDisbleRadio.setText(MessageConfigLoader.getProperty(IMessagesConstants.OB_ACCESS_TYPE_PRIVILEGED));
    }

    private void addIncludeExcludeSchemaUi(Group parent, GridLayout grpFieldLayout) {
        Group schema = new Group(parent, SWT.NONE);
        schema.setLayoutData(new GridData(SWT.FILL, SWT.WRAP, false, true));
        schema.setLayout(grpFieldLayout);
        schema.setText(MessageConfigLoader.getProperty(IMessagesConstants.LBL_GRP_SCHEMA));

        GridLayout valueFieldLayout = new GridLayout(2, false);
        valueFieldLayout.horizontalSpacing = 60;
        valueFieldLayout.marginWidth = 5;
        valueFieldLayout.marginHeight = 10;
        valueFieldLayout.verticalSpacing = 20;

        Composite compSchemaInclude = new Composite(schema, SWT.NONE);
        compSchemaInclude.setLayout(valueFieldLayout);
        compSchemaInclude.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label lblSchemaInclude = new Label(compSchemaInclude, SWT.NONE | SWT.LEFT);
        lblSchemaInclude.setText(MessageConfigLoader.getProperty(IMessagesConstants.LBL_INCLUDE));
        schemaInclude = new Text(compSchemaInclude, SWT.BORDER | SWT.SINGLE | SWT.LEFT);

        setInitTextProperties(schemaInclude);
        ((Text) schemaInclude).addVerifyListener(new TextLengthVerifyListner());

        Label lblSchemaExclude = new Label(compSchemaInclude, SWT.NONE | SWT.LEFT);
        lblSchemaExclude.setText(MessageConfigLoader.getProperty(IMessagesConstants.LBL_EXCLUDE));
        schemaExclude = new Text(compSchemaInclude, SWT.BORDER | SWT.SINGLE | SWT.LEFT);
        setInitTextProperties(schemaExclude);
        ((Text) schemaExclude).addVerifyListener(new TextLengthVerifyListner());
    }

    private void setSelectionListener(final Button sslCheckButton) {
        sslCheckButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (sslCheckButton.getSelection()) {
                    sslCertBrowseBtn.setEnabled(true);
                    clSSLCertFilePathText.setEnabled(true);
                    sslKeyBrowseBtn.setEnabled(true);
                    clSSLKeyFilePathText.setEnabled(true);
                    rootCertBrowseBtn.setEnabled(true);
                    rootCertFilePathText.setEnabled(true);
                    sslModeOptions.setEnabled(true);
                    sslPrd.setEnabled(true);
                    sslPrd.setText("");
                } else {
                    sslCertBrowseBtn.setEnabled(false);
                    clSSLCertFilePathText.setEnabled(false);
                    clSSLCertFilePathText.setText("");
                    sslKeyBrowseBtn.setEnabled(false);
                    clSSLKeyFilePathText.setEnabled(false);
                    clSSLKeyFilePathText.setText("");
                    rootCertBrowseBtn.setEnabled(false);
                    rootCertFilePathText.setEnabled(false);
                    rootCertFilePathText.setText("");
                    sslModeOptions.setEnabled(false);
                    sslPrd.setEnabled(false);
                    sslPrd.setText("");
                }
                validateData();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // Do nothing
            }
        });
    }

    /**
     * Set the initial text properties.
     *
     * @param ctrlTxt the new inits the text properties
     */
    private void setInitTextProperties(Text ctrlTxt) {
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        ctrlTxt.setLayoutData(gd);

        ctrlTxt.setText("");
        ctrlTxt.addKeyListener(ctrlTextKeyListener());

        ctrlTxt.addListener(SWT.MenuDetect, new InitListener());
    }

    private void setInitTextPropertiesGeneralTab(Text ctrlTxt) {
        GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
        gd.widthHint = 215;
        ctrlTxt.setLayoutData(gd);

        ctrlTxt.setText("");
        ctrlTxt.addKeyListener(ctrlTextKeyListener());

        ctrlTxt.addListener(SWT.MenuDetect, new InitListener());
    }

    private KeyListener ctrlTextKeyListener() {
        return new KeyListener() {
            @Override
            public void keyPressed(KeyEvent event) {
            }

            @Override
            public void keyReleased(KeyEvent event) {
                /**
                 * Validate the connection info data.
                 */
                validateData();
            }
        };
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
    private static class InitListener implements Listener {
        @Override
        public void handleEvent(Event event) {
            event.doit = false;
        }
    }

    /**
     * Checks if is dialog complete.
     *
     * @return true, if is dialog complete
     */
    public boolean isDialogComplete() {
        boolean isDialogCompleted = !gaussHostAddr.getText().trim().isEmpty() && !gaussHostPort.getText().isEmpty()
                && !gaussDbName.getText().trim().isEmpty() && !gaussUserName.getText().trim().isEmpty()
                && !gaussPrd.getText().trim().isEmpty();

        if (gaussSSLEnableButton.getSelection()) {
            sslModeOptions.setEnabled(true);
            return !gaussConnectionName.getText().trim().isEmpty() && isDialogCompleted;
        } else {
            sslModeOptions.setEnabled(false);
            return !gaussConnectionName.getText().trim().isEmpty() && isDialogCompleted;
        }
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        switch (buttonId) {
            case OK_ID: {
                getDriverCombotext = dbTypeCombo.getText();
                okButton.setEnabled(false);
                clearButton.setEnabled(false);
                onOKButtonSuccess();
                break;
            }
            case CANCEL_ID: {
                onCancelPressed();
                break;
            }
            case CLEAR_ID: {
                onClearButtonPressed();
                break;
            }
            default: {
                break;
            }
        }

    }

    private void onOKButtonSuccess() {
        if (onOKButtonPressed()) {
            okButton.setEnabled(true);
            clearButton.setEnabled(true);
            toggleCheckProgress(false);
        }
    }

    private void onCancelPressed() {
        if (cancelLabel.equals(closeButton.getText())) {
            onCancelButtonPressed();
        } else {
            onCloseButtonPressed();
        }
    }

    private void onClearButtonPressed() {
        if (!this.isDisposed()) {
            closeButton.setEnabled(true);
            okButton.setEnabled(true);
            clearFields();
        }
    }

    private void onCloseButtonPressed() {
        if (!this.isDisposed()) {
            cancelPressed();
        }
    }

    private void onCancelButtonPressed() {
        if (!this.isDisposed()) {
            closeButton.setEnabled(false);
            generateCancelPopup(false);
        }
    }

    /**
     * Action to do when cancel button is pressed when connection is in progress
     * using command line arguments
     */
    public void onCancelButtonPressedCommandline() {
        generateCancelPopup(true);
    }

    private boolean onOKButtonPressed() {
        if (validateServerIpAddress()) {
            // Disable the Connection Dialog Window
            formComposite.setEnabled(true);
            return onValidSSLConstraint();
        }
        return true;
    }

    private boolean onValidSSLConstraint() {
        if (checkSSLConstraints() && checkEditConnectionConstraints()) {
            toggleCheckProgress(true);
            enableDbNameText();
            newConnectionPressed();
            return false;
        } else {
            // Disable the Connection Dialog Window
            formComposite.setEnabled(true);
        }
        return true;
    }

    private void enableDbNameText() {
        gaussDbName.setEnabled(false);
    }

    /**
     * Generate cancel popup.
     */
    protected void generateCancelPopup(boolean isCommandlineFlow) {
        int userChoice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_CONNECTION_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_CONNECTION_BODY),
                new String[] {MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION),
                    MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION)},
                1);

        if (OK_ID == userChoice) {
            cancelOnJobOperation();
        }

        else {
            if (!isCommandlineFlow && !this.isDisposed()) {
                clearButton.setEnabled(true);
                closeButton.setEnabled(true);
            }
            return;
        }

    }

    /**
     * Check edit connection constraints.method to call removeConnection
     * implemented in EditConnectionDialog
     *
     * @return true, if successful
     */
    protected boolean checkEditConnectionConstraints() {
        return true;
    }

    /**
     * Check SSL constraints.
     *
     * @return true, if successful
     */
    protected boolean checkSSLConstraints() {
        boolean isChecked = gaussSSLEnableButton.getSelection();
        if (validateForSSLConstraint(isChecked)) {
            int popUpreturn = generateSSLpopup();
            return returnOnPopupClose(popUpreturn);
        }
        return true;
    }

    /**
     * Handle security warning message display when ssl is disabled
     *
     * @param parameterMap the parameter map
     * @return true, if successful
     */
    public boolean checkSSLConstraintsCommandline(Map<String, String> parameterMap) {
        boolean isChecked;

        if (parameterMap.containsKey(IDSCommandlineOptions.SSL_ENABLE)) {
            if (parameterMap.get(IDSCommandlineOptions.SSL_ENABLE).equalsIgnoreCase("true")) {
                isChecked = true;
            } else {
                isChecked = false;
            }
        } else {
            isChecked = false;
        }

        if (validateForSSLConstraintCommandline(isChecked, parameterMap)) {
            int popUpreturn = generateSSLpopup();
            return returnOnPopupClose(popUpreturn);
        }
        return true;
    }

    private boolean returnOnPopupClose(int popUpreturn) {
        if (popUpreturn == OK) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validateForSSLConstraintCommandline(boolean isChecked, Map<String, String> parameterMap) {
        return !isChecked && UIDisplayFactoryProvider.getUIDisplayStateIf().getSSLoff() && getEnableSecurityWarning();
    }

    private boolean validateForSSLConstraint(boolean isChecked) {
        return !isChecked && UIDisplayFactoryProvider.getUIDisplayStateIf().getSSLoff() && getEnableSecurityWarning();
    }

    /**
     * Generate SS lpopup.
     *
     * @return the int
     */
    public int generateSSLpopup() {
        int result = 0;
        SSLWarningDialog dialog = new SSLWarningDialog(Display.getDefault().getActiveShell(),
                MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_SECURITY_ALERT), null,
                MessageConfigLoader.getProperty(IMessagesConstants.SSL_DISABLED_CONTINUE_OR_CANCEL),
                MessageDialog.WARNING,
                new String[] {MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CONT),
                    MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)},
                1);
        result = dialog.open();

        return result;
    }

    private boolean validateServerIpAddress() {
        String ipAddress = gaussHostAddr.getText().trim();
        Pattern pattern = Pattern.compile(IDBConnectionValidationRegEx.REGEX_HOST_IPADDRESS);
        Matcher matcher = pattern.matcher(ipAddress);
        if (ipAddress.matches(IDBConnectionValidationRegEx.REGEX_IS_HOST_IPADDRESS)) {
            if (!(matcher.matches())) {
                int result = MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_INVALID_SERVER_IP_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_INVALID_SERVER_IP_MSG));
                MPPDBIDELoggerUtility
                        .debug(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_VALID_STATUS, result));

                gaussPrd.setText("");
                okButton.setEnabled(false);
                closeButton.setEnabled(true);
                progressBar.setVisible(false);
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if is disposed.
     *
     * @return true, if is disposed
     */
    protected boolean isDisposed() {
        return (null == this.getShell()) || this.getShell().isDisposed();
    }

    /**
     * New connection pressed.
     */
    protected void newConnectionPressed() {
        flag = true;
        final DBConnProfCache connProfCache = DBConnProfCache.getInstance();
        final ServerConnectionInfo connInfo = getServerConnectionInfo();

        final IJobCancelStatus status = new JobCancelStatus();

        job = new Job("Refresh connection objects") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                return dbConnectionDialogInnerJob(connProfCache, connInfo, status);
            }

            @Override
            protected void canceling() {
                status.setCancel(true);
                if (null != getThread()) {
                    getThread().interrupt();
                }
            }
        };

        job.schedule();
    }

    /**
     * new connection initiation for commandline arguments
     * 
     * @param parameterMap input param map
     */
    public void newConnectionPressedCommandline(Map<String, String> parameterMap, CmdLineCharObject cmdLinePassword) {
        flag = true;
        final DBConnProfCache connProfCache = DBConnProfCache.getInstance();
        final ServerConnectionInfo connInfo = getServerConnectionInfoCommandline(parameterMap, cmdLinePassword);

        final IJobCancelStatus cancelStatus = new JobCancelStatus();
        job = new Job("Refresh connection objects") {
            @Override
            protected IStatus run(IProgressMonitor progressMonitor) {
                return dbConnectionDialogInnerJob(connProfCache, connInfo, cancelStatus);
            }

            @Override
            protected void canceling() {
                cancelStatus.setCancel(true);
                if (null != getThread()) {
                    getThread().interrupt();
                }
            }
        };

        job.schedule();
    }

    /**
     * generate server connection info from parameter map
     * 
     * @param parameterMap input map
     * @return prepared connection info
     */
    public ServerConnectionInfo getServerConnectionInfoCommandline(Map<String, String> parameterMap,
            CmdLineCharObject cmdLinePassword) {
        ServerConnectionInfo info = new ServerConnectionInfo();

        fillGeneralTabFieldsCommandline(parameterMap, info, cmdLinePassword);

        fillSSLTabFieldsCommandline(parameterMap, info);

        fillAdvancedTabFieldsCommandline(info);
        HandlerUtilities.setServerInfo(info);
        return info;
    }

    private void fillAdvancedTabFieldsCommandline(ServerConnectionInfo info) {
        info.setSchemaInclusionList(new LinkedHashSet<String>());
        info.setSchemaExclusionList(new LinkedHashSet<String>());
        info.setPrivilegeBasedObAccess(true);
    }

    private void fillGeneralTabFieldsCommandline(Map<String, String> parameterMap, ServerConnectionInfo info,
            CmdLineCharObject cmdLinePassword) {
        info.setConectionName(parameterMap.get(IDSCommandlineOptions.CONNECTION_NAME));
        info.setServerIp(parameterMap.get(IDSCommandlineOptions.HOST_IP));
        info.setServerPort(Integer.parseInt(parameterMap.get(IDSCommandlineOptions.HOST_PORT)));

        info.setDriverName(IDSCommandlineOptions.DB_TYPE_OPEN_GAUSS);

        info.setDatabaseName(parameterMap.get(IDSCommandlineOptions.DB_NAME));
        info.setUsername(parameterMap.get(IDSCommandlineOptions.USER_NAME));
        info.setPrd(cmdLinePassword.getPrd());
        cmdLinePassword.clearPssrd();

        if (parameterMap.containsKey(IDSCommandlineOptions.SAVE_CIPHER)) {
            String savePwdStr = parameterMap.get(IDSCommandlineOptions.SAVE_CIPHER);
            if (savePwdStr.equals(IDSCommandlineOptions.SAVE_CIPHER_DONT_SAVE)) {
                info.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
            } else {
                info.setSavePrdOption(SavePrdOptions.CURRENT_SESSION_ONLY);
            }
        } else {
            info.setSavePrdOption(SavePrdOptions.CURRENT_SESSION_ONLY);
        }
    }

    private void fillSSLTabFieldsCommandline(Map<String, String> parameterMap, ServerConnectionInfo info) {
        boolean isSSLEnabled;

        if (parameterMap.containsKey(IDSCommandlineOptions.SSL_ENABLE)) {
            info.setSSLEnabled(Boolean.parseBoolean(parameterMap.get(IDSCommandlineOptions.SSL_ENABLE)));
            isSSLEnabled = info.isSSLEnabled();

            if (isSSLEnabled && parameterMap.containsKey(IDSCommandlineOptions.SSL_CLIENT_CERT)) {
                info.setClientSSLCertificate(parameterMap.get(IDSCommandlineOptions.SSL_CLIENT_CERT));
            } else {
                info.setClientSSLCertificate("");
            }

            if (isSSLEnabled && parameterMap.containsKey(IDSCommandlineOptions.SSL_CLIENT_KEY)) {
                info.setClientSSLKey(parameterMap.get(IDSCommandlineOptions.SSL_CLIENT_KEY));
            } else {
                info.setClientSSLKey("");
            }

            if (isSSLEnabled && parameterMap.containsKey(IDSCommandlineOptions.SSL_ROOT_CERT)) {
                info.setRootCertificate(parameterMap.get(IDSCommandlineOptions.SSL_ROOT_CERT));
            } else {
                info.setRootCertificate("");
            }

            if (isSSLEnabled && parameterMap.containsKey(IDSCommandlineOptions.SSL_MODE)) {
                info.setSSLMode(parameterMap.get(IDSCommandlineOptions.SSL_MODE).replace('_', '-'));
            } else {
                /* Add default if entry is not supplied */
                info.setSSLMode(IDSCommandlineOptions.SSL_MODE_ALLOW);
            }
        }
    }

    private IStatus dbConnectionDialogInnerJob(final DBConnProfCache connProfCache, final ServerConnectionInfo connInfo,
            final IJobCancelStatus status) {
        Exception connectionFailureException = null;
        IExecTimer timer = new ExecTimer("Connection error");
        IExecTimer timersuccess = new ExecTimer("Connection Success");
        Path folderPath = null;
        ConnectionProfileManagerImpl connProfImpl = ConnectionProfileManagerImpl.getInstance();
        if (connProfImpl.isProfileMapEmpty()) {
            try {
                connProfImpl.getAllProfiles();
            } catch (DatabaseOperationException | DataStudioSecurityException | IOException excep) {
                MPPDBIDELoggerUtility.error("Failed to load profiles", excep);
            }
        }
        boolean exceptionOccured = false;
        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_CON, true);
        try {
            folderPath = getProfileId(connProfCache, connInfo, status, timer, timersuccess, connProfImpl);
        } catch (OutOfMemoryError error) {
            handleOutOfMemoryError(timer, error);
            exceptionOccured = true;
        } catch (PasswordExpiryException exception) {
            flag = false;
            asyncConnectionFailedUserMessage(exception, "Password Expire Information");
            connectionFailureException = exception;
            exceptionOccured = true;
        } catch (DataStudioSecurityException se) {
            asyncConnectionFailedUserMessage(se, getSecurityExceptionMsg());
            connectionFailureException = se;
            exceptionOccured = true;
        } catch (final DatabaseOperationException databaseOperationException) {
            connectionFailureException = handleDataBaseOperationException(databaseOperationException);
            exceptionOccured = true;
        } catch (MPPDBIDEException exception) {
            connectionFailureException = handleMPPDBIDEException(timer, exception);
            exceptionOccured = true;
            return Status.OK_STATUS;
        } catch (final Exception exception) {
            flag = false;
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_CONN_PROFILE_ERROR), exception);
            asyncConnectionFailedUserMessage(exception, null);
            connectionFailureException = exception;
            exceptionOccured = true;
            return Status.OK_STATUS;
        } finally {
            handleFinalOperationOnConnection(connProfCache, connInfo, connectionFailureException, timer, timersuccess,
                    folderPath, connProfImpl, exceptionOccured);
        }

        return Status.OK_STATUS;
    }

    private void handleFinalOperationOnConnection(final DBConnProfCache connProfCache,
            final ServerConnectionInfo connInfo, Exception connectionFailureException, IExecTimer timer,
            IExecTimer timersuccess, Path folderPath, ConnectionProfileManagerImpl connProfImpl,
            boolean exceptionOccured) {
        closeConnectionNotifier();
        if (exceptionOccured && null != folderPath
                && !connProfImpl.isProfileInfoAvailableInMetaData(connInfo.getConectionName())) {
            try {
                connProfImpl.getDiskUtility().deleteFolder(folderPath);
            } catch (IOException ioException) {
                MPPDBIDELoggerUtility.error("Deleting folder failed.", ioException);
            }
        }
        finalCleanUp(connProfCache, connectionFailureException, timersuccess);
        showPasswordExpiryWarning(timer);
    }

    /**
     * closes connection notifier pop up
     */
    public void closeConnectionNotifier() {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                if (null != connectionNotifier) {
                    connectionNotifier.close();
                    if (null != callBackInf) {
                        callBackInf.call();
                    }
                }
            }
        });
    }

    private String getSecurityExceptionMsg() {
        return MessageConfigLoader.getProperty(IMessagesConstants.TITLE_DS_SECURITY) + MPPDBIDEConstants.LINE_SEPARATOR
                + MessageConfigLoader.getProperty(IMessagesConstants.SECURITY_FAILURE_SUGGESTION);
    }

    private Path getProfileId(final DBConnProfCache connProfCache, final ServerConnectionInfo connInfo,
            final IJobCancelStatus status, IExecTimer timer, IExecTimer timersuccess,
            ConnectionProfileManagerImpl connProfImpl)
            throws IOException, FileOperationException, DataStudioSecurityException, DatabaseOperationException,
            MPPDBIDEException, PasswordExpiryException, OutOfMemoryError, DatabaseCriticalException {
        Path folderPath;
        timer.start();
        timersuccess.start();

        folderPath = connProfImpl.generateSecurityFolderInsideProfile(connInfo);

        /* Connect to server */
        profileId = connProfCache.initConnectionProfile(connInfo, status);

        operationsOnSuccessfulConnection(connProfCache, connInfo);
        return folderPath;
    }

    private void operationsOnSuccessfulConnection(final DBConnProfCache connProfCache,
            final ServerConnectionInfo connInfo)
            throws DatabaseOperationException, DatabaseCriticalException, DataStudioSecurityException {
        // initialize DBAssist
        initializeDbAssist();

        // get ConnectionLogin notification
        getConnectionLoginNotification();

        UIDisplayFactoryProvider.getUIDisplayStateIf().setConnectedProfileId(profileId);

        MPPDBIDELoggerUtility.info("GUI: DBConnectionWizard: Connected to DB server");

        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                .getProperty(IMessagesConstants.DB_CONN_DIA_CONN_PROFILE, connInfo.getConectionName())));

        // UI actions such as creating SQL Terminal, updating windows operations
        uiOperationsOnsuccessfulConnection();

        ServerConnectionInfo info = connInfo.getClone();

        // Save connection details in profile
        saveConnectionDetails(connProfCache, info);

    }

    private void showPasswordExpiryWarning(IExecTimer timer) {
        try {
            showPasswordExpiryWarning(profileId);
        } catch (DatabaseCriticalException databaseCriticalException) {
            handleMPPDBIDEException(timer, databaseCriticalException);
        } catch (DatabaseOperationException databaseOperationException) {
            handleDataBaseOperationException(databaseOperationException);
        }
    }

    private void finalCleanUp(final DBConnProfCache connProfCache, Exception connectionFailureException,
            IExecTimer timersuccess) {
        enableConnectionDialog();

        if (null != connectionFailureException) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().resetConnectedProfileId();
        } else {
            addSSLLoginStatus();
            Database db = loadDatabaseObjects(connProfCache);
            if (null != db) {
                checkForInvalidSchemasInIncludeExcludeList(db);
            }
            stopAndLogTimer(timersuccess);
            asyncClose();
        }
    }

    private void stopAndLogTimer(IExecTimer timersuccess) {
        try {
            timersuccess.stopAndLog();
        } catch (DatabaseOperationException databaseOperationException) {
            MPPDBIDELoggerUtility.error("DBConnectionDialog: loading objects failed.", databaseOperationException);
        }
    }

    private void enableConnectionDialog() {
        if (!isDisposed()) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    // Enable the Connection Dialog Window
                    formComposite.setEnabled(true);
                }
            });
            clearPrdField();
        }
    }

    private Database loadDatabaseObjects(final DBConnProfCache connProfCache) {
        Database db = connProfCache.getDbForProfileId(profileId);
        LoadLevel1Objects load = new LoadLevel1Objects(db, null);
        try {
            load.loadObjects();
        } catch (DatabaseCriticalException databaseCriticalException) {
            MPPDBIDELoggerUtility.error("DBConnectionDialog: loading objects failed.", databaseCriticalException);
        }
        return db;
    }

    private void addSSLLoginStatus() {
        if (!isDisposed()) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    if (validateSSLData()) {
                        SSLUtility.putSSLLoginStatus(
                                gaussHostAddr.getText().trim() + ':' + gaussHostPort.getText().trim(), true);
                    }
                }

                private boolean validateSSLData() {
                    return gaussSSLEnableButton.getSelection() && !SSLUtility
                            .getStatus(gaussHostAddr.getText().trim() + ':' + gaussHostPort.getText().trim());
                }
            });
        }
    }

    private Exception handleMPPDBIDEException(IExecTimer timer, MPPDBIDEException exception) {
        flag = false;
        closeConnectionNotifier();
        Exception connectionFailureException = null;
        if (exception.getMessage()
                .equalsIgnoreCase(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED))) {
            handleOutOfMemoryError(timer, exception);
        } else {
            StringBuilder errMsg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED), exception);

            errMsg.append(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED));

            if (null != exception.getServerMessage()) {
                errMsg.append(MPPDBIDEConstants.LINE_SEPARATOR);
                errMsg.append(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_REASON))
                        .append(exception.getServerMessage());
                errMsg.append(MPPDBIDEConstants.LINE_SEPARATOR);

                appendSSLErrorMessage(exception, errMsg);
            } else {
                errMsg.append(MPPDBIDEConstants.LINE_SEPARATOR)
                        .append(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_INVALID_CONNECTION_DETAILS));
            }

            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_GUI_ERROR_MSG),
                    exception);

            asyncConnectionFailedUserMessage(exception, errMsg.toString());
            connectionFailureException = exception;
        }
        return connectionFailureException;
    }

    private Exception handleDataBaseOperationException(final DatabaseOperationException databaseOperationException) {
        flag = false;
        closeConnectionNotifier();
        String msg = getServerMessage(databaseOperationException);
        Exception connectionFailureException = null;

        if (validateErrorForWritingProfile(databaseOperationException, msg)) {
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_PROFILE_WRITE_DISK_TITLE),
                    databaseOperationException.getMessage());
        } else if (msg.contains(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_SERVER_DOMAIN_NAME_ERROR))) {
            StringBuilder domainErrMsg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            domainErrMsg.append(msg);
            domainErrMsg.append(MPPDBIDEConstants.LINE_SEPARATOR);
            domainErrMsg.append(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DOMAIN_NAME_ERROR));
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_SERVER_DOMAIN_NAME_ERROR)
                            + MPPDBIDEConstants.LINE_SEPARATOR
                            + MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DOMAIN_NAME_ERROR),
                    databaseOperationException);
            asyncConnectionFailedUserMessage(databaseOperationException,
                    MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_GAUSS_SERVER_ERROR)
                            + MPPDBIDEConstants.LINE_SEPARATOR + domainErrMsg.toString());
            connectionFailureException = databaseOperationException;
        } else {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED),
                    databaseOperationException);

            StringBuilder errorMsg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            errorMsg.append(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_GAUSS_SERVER_ERROR));
            errorMsg.append(MPPDBIDEConstants.LINE_SEPARATOR);
            errorMsg.append(msg);

            displayErrorForSSLCertificate(msg, errorMsg);

            asyncConnectionFailedUserMessage(databaseOperationException, errorMsg.toString());
            connectionFailureException = databaseOperationException;
        }
        return connectionFailureException;
    }

    private void displayErrorForSSLCertificate(String msg, StringBuilder errorMsg) {
        if (msg.contains("certificate")) {
            errorMsg.append(MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_GUI_CONNECTION_FAIL_FOR_SSL));
        }
    }

    private boolean validateErrorForWritingProfile(final DatabaseOperationException exception, String msg) {
        return msg.contains(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PROFILE_WRITE_DISK)) || exception
                .getMessage().contains(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PROFILE_WRITE_DISK));
    }

    private void appendSSLErrorMessage(MPPDBIDEException mppdbideException, StringBuilder errMsg) {
        if (mppdbideException.getServerMessage().contains("SSL")) {
            errMsg.append(MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_GUI_CONNECTION_FAIL_FOR_SSL));
        } else {
            errMsg.append(MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_GUI_CONNECTION_FAIL));
            if (mppdbideException.getServerMessage().contains("Open socket failed")) {
                errMsg.append(
                        MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_GUI_CONNECTION_OPEN_SOCKET_FAIL));
            }
        }

    }

    private void handleOutOfMemoryError(IExecTimer timer, MPPDBIDEException mppdbideException) {
        String elapsedTime = null;
        try {
            timer.stop();
            elapsedTime = timer.getElapsedTime();
        } catch (DatabaseOperationException e1) {
            MPPDBIDELoggerUtility.info("Execute timer operation failed.");
        }
        UIElement.getInstance().outOfMemoryCatch(elapsedTime, mppdbideException.getMessage());
    }

    private String getServerMessage(final DatabaseOperationException databaseOperationException) {
        String msg = databaseOperationException.getServerMessage();
        if (validateForMessage(msg)) {
            msg = databaseOperationException.getMessage();
        }
        if (validateForMessage(msg)) {
            msg = databaseOperationException.getDBErrorMessage();
        }
        return msg;
    }

    private boolean validateForMessage(String msg) {
        return null == msg || "".equals(msg);
    }

    private void handleOutOfMemoryError(IExecTimer timer, OutOfMemoryError error) {
        String elapsedTime = null;
        try {
            timer.stop();
            elapsedTime = timer.getElapsedTime();
        } catch (DatabaseOperationException e1) {
            MPPDBIDELoggerUtility.info("Execute timer operation failed.");
        }
        UIElement.getInstance().outOfMemoryCatch(elapsedTime, error.getMessage());

    }

    private void saveConnectionDetails(final DBConnProfCache connProfCache, ServerConnectionInfo info)
            throws DatabaseOperationException, DataStudioSecurityException {
        Server server = connProfCache.getServerById(profileId.getServerId());
        if (server != null) {
            info.setDBVersion(server.getServerVersion(true));
            server.persistConnectionDetails(info);
        }
    }

    private void uiOperationsOnsuccessfulConnection() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_OBJBRWSR_POPULATE, true);
                Database database = profileId.getDatabase();
                createSQLTerminal(database);
                updateWindows(profileId);
                uiElementInstance.getObjectBrowserOnFocus();

                MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_OBJBRWSR_POPULATE, false);
                MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_CON, false);

                IDEMemoryAnalyzer.validateMemoryUsage();
                if (database != null) {
                    uiElementInstance.updateTextEditorsIconAndConnButtons(database.getServer());
                }
            }

            private void createSQLTerminal(Database database) {
                if (database != null) {
                    UIElement.getInstance().createNewTerminal(database);
                }
            }

        });
    }

    private void initializeDbAssist() throws DatabaseOperationException, DatabaseCriticalException {
        /* add the logic for db_assistant. */
        DBAssistantWindow.setIssueTime("no");

        Database db = profileId.getDatabase();
        if (null != db) {
            DBAssistantWindow.setDocRealVersion(db);
        }

        DBAssistantWindow.setDisplayVersionNote(false);
    }

    private void getConnectionLoginNotification() {
        MPPDBIDELoggerUtility.info(MessageConfigLoader.getProperty(IMessagesConstants.LOGIN_NOTIFICATION_STARTED));

        if (DBTYPE.OPENGAUSS == profileId.getDatabase().getDBType()) {
            ConnectionNotification notification = new ConnectionNotification(profileId.getDatabase());
            notification.loadnotification();
        }

        MPPDBIDELoggerUtility.info(MessageConfigLoader.getProperty(IMessagesConstants.LOGIN_NOTIFICATION_ENDED));
    }

    /**
     * Check for invalid schemas in include exclude list.
     *
     * @param db the db
     */
    protected void checkForInvalidSchemasInIncludeExcludeList(Database db) {
        String invalidIncludeSchema = SchemaHelper.checkForInvalidNamespacesInIncludeList(db);
        String invalidExcludeSchema = SchemaHelper.checkForInvalidNamespacesInExcludeList(db);
        String informationMsg = null;
        informationMsg = addMessageForInValidSchemas(invalidIncludeSchema, invalidExcludeSchema);
        informationMsg = addMsgForInvalidExcludeSchema(invalidExcludeSchema, informationMsg);
        informationMsg = addMsgForInvalidIncludeSchema(invalidIncludeSchema, informationMsg);
        displayInvalidSchemaDetails(informationMsg);
    }

    private String addMsgForInvalidIncludeSchema(String invalidIncludeSchema, String informationMsg) {
        if (validateEnteredSchema(informationMsg, invalidIncludeSchema)) {
            informationMsg = MessageConfigLoader.getProperty(IMessagesConstants.INVALID_INCLUDE_SCHEMA,
                    invalidIncludeSchema);
        }
        return informationMsg;
    }

    private String addMsgForInvalidExcludeSchema(String invalidExcludeSchema, String informationMsg) {
        if (validateEnteredSchema(informationMsg, invalidExcludeSchema)) {
            informationMsg = MessageConfigLoader.getProperty(IMessagesConstants.INVALID_EXCLUDE_SCHEMA,
                    invalidExcludeSchema);
        }
        return informationMsg;
    }

    private String addMessageForInValidSchemas(String invalidIncludeSchema, String invalidExcludeSchema) {
        if (validateIncludeExcludeSchema(invalidIncludeSchema, invalidExcludeSchema)) {
            return MessageConfigLoader.getProperty(IMessagesConstants.INVALID_INCLUDE_EXCLUDE_SCHEMA,
                    invalidExcludeSchema, invalidIncludeSchema);
        }
        return null;
    }

    private boolean validateEnteredSchema(String informationMsg, String invalidExcludeSchema) {
        return null == informationMsg && null != invalidExcludeSchema && !invalidExcludeSchema.isEmpty();
    }

    private boolean validateIncludeExcludeSchema(String invalidIncludeSchema, String invalidExcludeSchema) {
        return null != invalidExcludeSchema && null != invalidIncludeSchema && !invalidExcludeSchema.isEmpty()
                && !invalidIncludeSchema.isEmpty();
    }

    private void displayInvalidSchemaDetails(final String informationMsg) {
        if (null != informationMsg) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.INVALID_INCLUDE_EXCLUDE_TITLE),
                            informationMsg);
                }
            });
        }
    }

    /**
     * Cancel on job operation.
     */
    protected void cancelOnJobOperation() {
        cancelJob();
        UIDisplayFactoryProvider.getUIDisplayStateIf().cleanupUIItems();
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (validateProfileId(objectBrowserModel)) {
            objectBrowserModel.refresh(profileId);
        }
    }

    private boolean validateProfileId(ObjectBrowser objectBrowserModel) {
        return null != profileId && objectBrowserModel != null;
    }

    private void cancelJob() {
        if (null != job) {
            job.cancel();
        }
    }

    /**
     * Clear password fields
     */
    private void clearPrdField() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                gaussPrd.setText("");

                clSSLCertFilePathText.setText("");
                clSSLKeyFilePathText.setText("");
                sslPrd.setText("");
                rootCertFilePathText.setText("");
                gaussPrd.setFocus();
            }
        });
    }

    private void showPasswordExpiryWarning(ConnectionProfileId profId)
            throws DatabaseCriticalException, DatabaseOperationException {
        float deadLine = 0;
        if (profId != null) {
            Database database = profId.getDatabase();
            String deadlineStamp = null;
            deadlineStamp = getDeadLineInfo(database);
            deadLine = validateDeadLineStamp(deadLine, deadlineStamp);
            allowedPasswordExpiry(deadLine);
        }
    }

    private void allowedPasswordExpiry(float deadLine) {
        if (deadLine < 0) {
            loggingAllowedAfterPasswordExpiry();
        }
    }

    private float validateDeadLineStamp(float deadLine, String deadlineStamp) {
        if (deadlineStamp != null) {
            deadLine = Float.parseFloat(deadlineStamp);
        }
        return deadLine;
    }

    private String getDeadLineInfo(Database database) throws DatabaseCriticalException, DatabaseOperationException {
        if (database != null) {
            return DatabaseUtils.getDeadlineInfo(MPPDBIDEConstants.FETCH_COUNT, database);
        }
        return null;
    }

    private void loggingAllowedAfterPasswordExpiry() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRY_MSG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRY_INFORMATION),
                        MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
            }
        });
    }

    private void asyncClose() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                close();
            }
        });
    }

    /**
     * Async connection failed user message.
     *
     * @param exception the exception
     * @param errMsg the err msg
     */
    protected void asyncConnectionFailedUserMessage(final Exception exception, final String errMsg) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {

                if (!isCommandlineFlow) {
                    toggleCheckProgress(false);
                    if (!isDisposed()) {
                        gaussDbName.setEnabled(true);
                    }
                } else if (null != connectionNotifier) {
                    connectionNotifier.close();
                }

                if (exception instanceof MPPDBIDEException) {
                    handleMPPDBIDEException(errMsg);
                } else if (exception instanceof PasswordExpiryException) {
                    MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                            IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                            MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRE_CONFIRMATION),
                            MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRED),
                            new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)}, 0);
                } else {
                    MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                            MessageConfigLoader.getProperty(IMessagesConstants.BREAKPOINT_WINDOW_ERROR_MSG_HEADING),
                            MessageConfigLoader.getProperty(IMessagesConstants.BREAKPOINT_WINDOW_ERROR_MSG),
                            new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)}, 0);
                }

                if (!isCommandlineFlow) {
                    if (!closeButton.isDisposed()) {
                        closeButton.setEnabled(true);
                    }

                    if (!clearButton.isDisposed()) {
                        clearButton.setEnabled(true);
                    }
                    validateData();
                }
            }
        });
    }
    
    private void handleMPPDBIDEException(final String errMsg) {
        if (errMsg.contains(MessageConfigLoader.getProperty(IMessagesConstants.SECURITY_FAILURE_SUGGESTION))) {
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED),
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_DS_SECURITY),
                    MessageConfigLoader.getProperty(IMessagesConstants.SECURITY_FAILURE_SUGGESTION), null);
        } else {
            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED), errMsg,
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)}, 0);
        }
    }

    /**
     * Validate data.
     */
    public void validateData() {
        if (!dbTypeCombo.isDisposed()) {
            getDriverCombotext = dbTypeCombo.getText();
        }
        if (!isDisposed()) {
            enableDisableButtons();
        }
    }

    private void enableDisableButtons() {
        if (isDialogComplete()) {
            okButton.setEnabled(true);
            clearButton.setEnabled(true);
            closeButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
    }

    /**
     * Update Object browser with the debug object found.
     *
     * @param profId the prof id
     */
    private void updateWindows(ConnectionProfileId profId) {
        List<MPart> objectBrowserParts = modelService.findElements(application, UIConstants.UI_PART_OBJECT_BROWSER_ID,
                MPart.class, null);

        try {
            if (validateObjectBrowserParts(objectBrowserParts)) {
                objectBrowserRefresh(profId, objectBrowserParts);
            } else {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_LOADING_FUN_PROC_TRIG_FAILED,
                                MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_FUN_TRI_PROC)));

                ObjectBrowserStatusBarProvider.getStatusBar()
                        .displayMessage(Message.getError(
                                MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_LOADING_FUN_PROC_TRIG_FAILED,
                                        MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_FUN_TRI_PROC))));
            }
        } catch (Exception exception) {
            MPPDBIDELoggerUtility.debug("Exception happened while updating Object Browser.");
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_UPDATE_OBJ_BROW_ERROR), exception);
            MPPDBIDEDialogs.generateErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.BREAKPOINT_WINDOW_ERROR_MSG_HEADING),
                    MessageConfigLoader.getProperty(IMessagesConstants.BREAKPOINT_WINDOW_ERROR_MSG), exception);
        }

    }

    private void objectBrowserRefresh(ConnectionProfileId profId, List<MPart> objectBrowserParts) {
        ObjectBrowser objectBrowser = (ObjectBrowser) objectBrowserParts.get(0).getObject();
        if (uiElementInstance.isObjectBrowserPartOpen()) {
            MPPDBIDELoggerUtility.debug("refresh OB for profile id ");
            objectBrowser.refresh(profId);
        }
    }

    private boolean validateObjectBrowserParts(List<MPart> objectBrowserParts) {
        return null != objectBrowserParts && null != objectBrowserParts.get(0);
    }

    /**
     * Clear fields.
     */
    protected void clearFields() {
        gaussConnectionName.setText("");
        clearAllFields();
    }

    /**
     * Clear all fields.
     */
    protected void clearAllFields() {
        gaussHostAddr.setText("");
        gaussHostPort.setText("");
        if (null != gaussDbName && !gaussDbName.isDisposed()) {
            gaussDbName.setText("");
        }
        gaussUserName.setText("");
        gaussPrd.setText("");
        savePswdOptions.select(getComboSelectionIndex(SavePrdOptions.CURRENT_SESSION_ONLY));
        gaussSSLEnableButton.setSelection(UserPreference.getInstance().isSslEnable());
        sslCertBrowseBtn.setEnabled(true);
        clSSLCertFilePathText.setEnabled(true);
        sslKeyBrowseBtn.setEnabled(true);
        clSSLKeyFilePathText.setEnabled(true);
        rootCertBrowseBtn.setEnabled(true);
        rootCertFilePathText.setEnabled(true);
        clSSLCertFilePathText.setText("");
        clSSLKeyFilePathText.setText("");
        rootCertFilePathText.setText("");
        sslPrd.setText("");
        sslModeOptions.setText(sslModes[0]);
        sslModeOptions.setEnabled(true);
        clSSLCertFilePathText.setText("");
        clSSLKeyFilePathText.setText("");
        rootCertFilePathText.setText("");
        // ADVANCE OPTIONS
        schemaInclude.setText("");
        schemaExclude.setText("");
        btnToLoadChildObj.setSelection(false);
        loadLimit.setText(DEFAULT_LOAD_LIMIT);
        loadLimit.setEnabled(btnToLoadChildObj.getSelection());
        okButton.setEnabled(false);
    }

    /**
     * Toggle check progress.
     *
     * @param state the state
     */
    protected void toggleCheckProgress(boolean state) {
        if (!isDisposed()) {
            if (state) {
                closeButton.setText(cancelLabel);
                viewer.removeDoubleClickListener(doubleclickEvent);
                gaussConnectionName.setEnabled(false);
                enableDisableGeneralAttributes(false);
                dbTypeCombo.setEnabled(false);
                enableDisableAdvancedTabAttributes(false);
                enableDisableSSLTabAttributes(false);

            } else {
                closeButton.setText(closeLabel);
                viewer.addDoubleClickListener(doubleclickEvent);
                gaussConnectionName.setEnabled(true);
                enableDisableGeneralAttributes(true);
                dbTypeCombo.setEnabled(false);
                enableDisableAdvancedTabAttributes(true);
                enableDisableSSLTabAttributes(gaussSSLEnableButton.getSelection());
            }
        }
        if (!progressBar.isDisposed()) {
            progressBar.setVisible(state);
            container.layout();
        }
    }

    /**
     * Enable disable general attributes.
     *
     * @param value the value
     */
    protected void enableDisableGeneralAttributes(boolean value) {

        gaussHostAddr.setEnabled(value);
        gaussHostPort.setEnabled(value);
        gaussPrd.setEnabled(value);
        gaussUserName.setEnabled(value);
        savePswdOptions.setEnabled(value);
        gaussSSLEnableButton.setEnabled(value);

    }

    /**
     * Enable disable SSL tab attributes.
     *
     * @param value the value
     */
    protected void enableDisableSSLTabAttributes(boolean value) {
        sslModeOptions.setEnabled(value);
        sslCertBrowseBtn.setEnabled(value);
        sslKeyBrowseBtn.setEnabled(value);
        rootCertBrowseBtn.setEnabled(value);
        clSSLCertFilePathText.setEnabled(value);
        clSSLKeyFilePathText.setEnabled(value);
        rootCertFilePathText.setEnabled(value);
        sslPrd.setEnabled(value);
    }

    /**
     * Enable disable advanced tab attributes.
     *
     * @param value the value
     */
    protected void enableDisableAdvancedTabAttributes(boolean value) {

        schemaInclude.setEnabled(value);
        schemaExclude.setEnabled(value);
        btnToLoadChildObj.setEnabled(value);
        loadLimit.setEnabled(value && btnToLoadChildObj.getSelection());
        enablePrivilege.setEnabled(value);
        disablePrivilege.setEnabled(value);
    }

    /**
     * Gets the server connection info.
     *
     * @return the server connection info
     */
    public ServerConnectionInfo getServerConnectionInfo() {
        ServerConnectionInfo info = new ServerConnectionInfo();

        info.setConectionName(gaussConnectionName.getText().trim());
        info.setServerIp(gaussHostAddr.getText().trim());
        info.setServerPort(Integer.parseInt(gaussHostPort.getText()));
        if (null != gaussDbName && !gaussDbName.isDisposed()) {
            info.setDatabaseName(gaussDbName.getText());
        } else {
            info.setDatabaseName("");
        }
        info.setUsername(gaussUserName.getText());
        info.setPrd(gaussPrd.getTextChars());
        info.setDriverName(dbTypeCombo.getItem(dbTypeCombo.getSelectionIndex()));

        info.setSavePrdOption(savePswdOptions.getSelectionIndex(), getEnablePermanentPasswordSaveOption());
        info.setSSLEnabled(gaussSSLEnableButton.getSelection());

        info.setClientSSLCertificate(clSSLCertFilePathText.getText());
        info.setClientSSLKey(clSSLKeyFilePathText.getText());
        info.setRootCertificate(rootCertFilePathText.getText());
        info.setSSLMode(sslModeOptions.getItem(sslModeOptions.getSelectionIndex()));
        info.setSSLPrd(sslPrd.getTextChars());

        info.setSchemaInclusionList(new LinkedHashSet<String>(getSchemaInclusionList()));
        info.setSchemaExclusionList(new LinkedHashSet<String>(getschemaExclusionList()));

        updateLoadLimit(info);
        info.setPrivilegeBasedObAccess(enablePrivilege.getSelection());
        HandlerUtilities.setServerInfo(info);
        return info;
    }

    private void updateLoadLimit(ServerConnectionInfo info) {
        info.setCanLoadChildObjects(btnToLoadChildObj.getSelection());

        if (!"".equals(loadLimit.getText().trim())) {
            info.setLoadLimit(Integer.parseInt(loadLimit.getText()));
        }
    }

    /**
     * Gets the schema exclusion list.
     *
     * @return the schema exclusion list
     */
    protected List<String> getschemaExclusionList() {
        String getSchemaExclusion = schemaExclude.getText();
        String[] getSchemaExclusionList = getSchemaExclusion.split("\\s*,\\s*");

        return Arrays.asList(getSchemaExclusionList);
    }

    /**
     * Gets the schema inclusion list.
     *
     * @return the schema inclusion list
     */
    protected List<String> getSchemaInclusionList() {
        String getSchemaInclusion = schemaInclude.getText();
        String[] getSchemaInclusionList = getSchemaInclusion.split("\\s*,\\s*");
        return Arrays.asList(getSchemaInclusionList);
    }

    /**
     * Populate connection info from preference.
     *
     * @param info the info
     */
    public void populateConnectionInfoFromPreference(IServerConnectionInfo info) {
        if (info != null) {
            ConnectionProfileManagerImpl connProfImp = ConnectionProfileManagerImpl.getInstance();
            connProfImp.getDiskUtility().getDecryptedPrd(info);
            clearFields();
            dbTypeCombo.setText(info.getDriverName());
            populatePrimaryFields(info);

            // updating the combo text
            getDriverCombotext = dbTypeCombo.getText();
            setUIonDBType(info);

            checkSSLPopulate(info);
            info.setPrd(new char[0]);
            MemoryCleaner.cleanUpMemory();
        }

        focusPasswordField();
    }

    private void setUIonDBType(IServerConnectionInfo info) {
        if (null == lblDBName || lblDBName.isDisposed()) {
            loadDbNameComp();
        }
        gaussDbName.setText(info.getDatabaseName());
        dbTypeSelectionReactor(UserPreference.getInstance().isSslEnable());
        populateAdvanceTabFields(info);
    }

    private void focusPasswordField() {
        if (validateConnectionDetails()) {
            gaussPrd.setFocus();
        }
    }

    private boolean validateConnectionDetails() {
        return validateConnNameHostAdd() && !gaussHostPort.getText().isEmpty() && validateDbName()
                && !gaussUserName.getText().isEmpty();
    }

    private boolean validateConnNameHostAdd() {
        return !gaussConnectionName.getText().isEmpty() && !gaussHostAddr.getText().isEmpty();
    }

    private boolean validateDbName() {
        if (null != gaussDbName && !gaussDbName.isDisposed()) {
            return !gaussDbName.getText().isEmpty() || !gaussDbName.isEnabled();
        }
        return true;
    }

    private void populateAdvanceTabFields(IServerConnectionInfo info) {
        schemaInclude.setText(convertListtoString(info.getSchemaInclusionList()));

        schemaExclude.setText(convertListtoString(info.getSchemaExclusionList()));
        btnToLoadChildObj.setSelection(info.canLoadChildObjects());
        if (0 == info.getLoadLimit()) {
            loadLimit.setText(DEFAULT_LOAD_LIMIT);
        } else {
            loadLimit.setText(Integer.toString(info.getLoadLimit()));
        }
        loadLimit.setEnabled(info.canLoadChildObjects());
        enablePrivilege.setSelection(info.isPrivilegeBasedObAccessEnabled());
        disablePrivilege.setSelection(!info.isPrivilegeBasedObAccessEnabled());
    }

    private void populatePrimaryFields(IServerConnectionInfo info) {
        gaussConnectionName.setText(info.getConectionName());
        gaussHostAddr.setText(info.getServerIp().trim());
        gaussHostPort.setText(Integer.toString(info.getServerPort()));
        gaussUserName.setText(info.getDsUsername());
        gaussPrd.setTextChars(info.getPrd());
        savePswdOptions.select(getComboSelectionIndex(info.getSavePrdOption()));
    }

    /**
     * Convert listto string.
     *
     * @param list the list
     * @return the string
     */
    protected String convertListtoString(Set<String> list) {
        StringBuilder formString = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        for (String temp : list) {
            formString.append(temp);
            formString.append(",");
        }

        // Regular expression to remove trailing commas at the end
        return formString.toString().replaceAll(",$", "");

    }

    /**
     * Gets the combo selection index.
     *
     * @param option the option
     * @return the combo selection index
     */
    protected int getComboSelectionIndex(SavePrdOptions option) {
        if (getEnablePermanentPasswordSaveOption()) {
            return option.ordinal();
        } else {
            return option.ordinal() - 1;
        }
    }

    private boolean getEnablePermanentPasswordSaveOption() {
        return UserPreference.getInstance().getEnablePermanentPasswordSaveOption();
    }

    private boolean getEnableSecurityWarning() {
        return UserPreference.getInstance().getEnableSecurityWarningOption();
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        if (viewer != null) {
            TableViewerUtil.disposeCurrentColumns(viewer);
            viewer.getControl().dispose();
            viewer = null;
        }
    }

    /**
     * sets Password expiry callback
     * 
     * @param callBackIf callback function
     * @return true/false
     */
    public boolean isOpenExpiryPopup(IPasswordExpiryCallback callBackIf) {
        if (null == this.connectionNotifier) {
            return false;
        }
        this.callBackInf = callBackIf;
        return true;
    }
}
