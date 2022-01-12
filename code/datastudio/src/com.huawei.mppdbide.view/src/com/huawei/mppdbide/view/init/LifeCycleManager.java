/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.init;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.bindings.EBindingService;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.impl.TrimmedWindowImpl;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import com.huawei.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;
import com.huawei.mppdbide.bl.util.BLUtils;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.SQLKeywords;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.aliasparser.AliasParserManager;
import com.huawei.mppdbide.view.autosave.AutoSaveManager;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.data.DSViewDataManager;
import com.huawei.mppdbide.view.diskmgr.OsUserDiskManager;
import com.huawei.mppdbide.view.handler.ConfigCheckHandler;
import com.huawei.mppdbide.view.handler.UIVersionHandler;
import com.huawei.mppdbide.view.lock.DSInstanceLock;
import com.huawei.mppdbide.view.save.DSSaveHandler;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: class Description: The Class LifeCycleManager. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class LifeCycleManager {
    
    @Inject
    private volatile IEclipseContext eclipseContext;

    @Inject
    private EModelService modelService;

    @Inject
    private volatile IEventBroker eventBroker;

    /**
     * Gets the event broker.
     *
     * @return the event broker
     */
    public IEventBroker getEventBroker() {
        return eventBroker;
    }

    @Optional
    @Inject
    private IWorkbench workbench;

    private EBindingService bindingService;

    private volatile ECommandService commandService = null;

    private volatile EHandlerService handlerService = null;

    private ScheduledExecutorService executor;
    

    /**
     * Post context create.
     *
     * @param eventBrker the event brker
     * @param context the context
     * @param modelSrvce the model srvce
     */
    /* Once context is created then this method is called */
    @PostContextCreate
    void postContextCreate(final IEventBroker eventBrker, IApplicationContext context, EModelService modelSrvce) {

        eventBrker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new AppStartupCompleteEventHandler());
    }

    /**
     * Gets the preferences.
     *
     * @return the preferences
     */
    private void getPreferences() {
        commandService = eclipseContext.get(ECommandService.class);
        Command cmd = commandService.getCommand("com.huawei.mppdbide.command.id.loadpreferencestore");
        ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
        handlerService = eclipseContext.get(EHandlerService.class);
        if (handlerService != null) {
            handlerService.executeHandler(parameterizedCmd);
        }
    }

    /**
     * Title: class Description: The Class AppStartupCompleteEventHandler.
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class AppStartupCompleteEventHandler implements EventHandler {
        @Override
        public void handleEvent(Event arg0) {
            // Shutdown hook
            TrimmedWindowImpl obj = (TrimmedWindowImpl) UIElement.getInstance().getMainWindow();
            final Shell shell = (Shell) obj.getWidget();

            final Display display = shell.getDisplay();
            display.addFilter(SWT.Close, new MainWindowShutdownListener());

            // Hide the Debug windows by default.
            hideDebugParts();

            // get platform arguments
            Map<String, String> connParamMap = new HashMap<String, String>(1);
            String[] args = Platform.getApplicationArgs();
            extractConnectionParameters(connParamMap, args);

            BLUtils.getInstance().setPlatformArgs(args);
            String readFilePath = readIniFileForLogFolderPath(args);
            if (!createFolderForDiskManager(readFilePath)) {
                return;
            }
            readUserpreferencesFromINIFile(args);

            bindingService = eclipseContext.get(EBindingService.class);
            commandService = eclipseContext.get(ECommandService.class);
            handlerService = eclipseContext.get(EHandlerService.class);
            setEclipseInjections();
            getPreferences();

            boolean isValidationSuccess = true;
            if (connParamMap.size() > 0 && System.console() != null) {
                if (connParamMap.containsKey(IDSCommandlineOptions.USER_CIPHER)) {
                    handleUserPasswordEnteredInCommandline();
                    return;
                }
                if (validateConnectionParamSyntax(connParamMap)) {
                    isValidationSuccess = validateConnectionParamValue(connParamMap);
                } else {
                    isValidationSuccess = false;
                }
                /*
                 * Close Datastudio workbench if connection param validation
                 * fails
                 */
                if (!isValidationSuccess) {
                    workbench.close();
                    return;
                }
            }
            handleActivitiesOnAppStartup(arg0, connParamMap, args, isValidationSuccess);
        }

        private void handleActivitiesOnAppStartup(Event arg0, Map<String, String> connParamMap, String[] args,
                boolean isValidationSuccess) {
            MPPDBIDELoggerUtility.setArgs(args);
            if (!MPPDBIDELoggerUtility.validateLogLevel()) {
                handleInvalidLogLevel();
            }
            registerSaveHandler(arg0);

            initSQLHistory();
            initAutoSaveManager();
            deleteTempFolders();
            /*
             * Launch Connection dialog if there are no command line connection
             * arguments supplied
             */
            if (connParamMap.size() > 0 && System.console() != null && isValidationSuccess) {
                connectWithCommandlineParams(connParamMap);
            } else {
                launchConnDialog();
            }
            startEventSenderThread();
            initAliasParserManager();
        }

        private void handleInvalidLogLevel() {
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getWarnFromConst(IMessagesConstants.INVALID_LOGGINGLEVEL));
            MPPDBIDELoggerUtility.info(MessageConfigLoader.getProperty(IMessagesConstants.INVALID_LOGGINGLEVEL));
        }

        private void handleUserPasswordEnteredInCommandline() {
            printToConsole(IDSCommandlineOptionValidationUtils.formInvalidParamErrorMsg());
            workbench.close();
        }
        
        private boolean isNullValue(String value) {
            if (null == value || "".equals(value)) {
                return true;
            }
            return false;
        }

        private boolean validateParamLevelOne(String key, String value, Map<String, String> connParamMap) {
            switch (key) {
                case IDSCommandlineOptions.CONNECTION_NAME: {
                    return validateConnectionName(key, value);
                }
                case IDSCommandlineOptions.HOST_IP: {
                    return validateHostIp(key, value);
                }
                case IDSCommandlineOptions.HOST_PORT: {
                    return validateHostPort(key, value);
                }
                case IDSCommandlineOptions.SAVE_CIPHER: {
                    return validateSavePasswordOption(key, value);
                }
                default: {
                    return validateParamLevelTwo(key, value, connParamMap);
                }
            }
        }

        private boolean validateSavePasswordOption(String key, String value) {
            if (isNullValue(value)) {
                printToConsole(IDSCommandlineOptionValidationUtils.formNullValueErrorMsg(key));
                return false;
            } else if (!IDSCommandlineOptionValidationUtils.getSavePasswordValueList().contains(value)) {
                printToConsole(IDSCommandlineOptionValidationUtils.formInvalidPasswordSaveOptionErrorMsg());
                return false;
            } else {
                return true;
            }
        }

        private boolean validateHostPort(String key, String value) {
            if (isNullValue(value)) {
                printToConsole(IDSCommandlineOptionValidationUtils.formNullValueErrorMsg(key));
                return false;

            } else if (!IDSCommandlineOptionValidationUtils.isHostPortNumberValid(value)) {
                printToConsole(IDSCommandlineOptionValidationUtils.formInvalidHostPortErrorMsg());
                return false;
            } else {
                return true;
            }
        }

        private boolean validateHostIp(String key, String value) {
            if (isNullValue(value)) {
                printToConsole(IDSCommandlineOptionValidationUtils.formNullValueErrorMsg(key));
                return false;
            } else if (!IDSCommandlineOptionValidationUtils.validateServerIpAddressForCommandline(value)) {
                printToConsole(IDSCommandlineOptionValidationUtils.formInvalidHostIpErrorMsg());
                return false;
            } else {
                return true;
            }
        }

        private boolean validateConnectionName(String key, String value) {
            if (isNullValue(value)) {
                printToConsole(IDSCommandlineOptionValidationUtils.formNullValueErrorMsg(key));
                return false;
            } else if (!IDSCommandlineOptionValidationUtils.isTextLengthValid(value)) {
                printToConsole(IDSCommandlineOptionValidationUtils.formInvalidTextLengthErrorMsg(key));
                return false;
            } else if (!IDSCommandlineOptionValidationUtils.isConnectionNameValid(value)) {
                printToConsole(IDSCommandlineOptionValidationUtils.formInvalidConnNameErrorMsg());
                return false;
            } else {
                return true;
            }
        }

        private boolean validateParamLevelTwo(String key, String value, Map<String, String> connParamMap) {
            switch (key) {
                case IDSCommandlineOptions.DB_NAME:
                case IDSCommandlineOptions.USER_NAME: {
                    if (isNullValue(value)) {
                        printToConsole(IDSCommandlineOptionValidationUtils.formNullValueErrorMsg(key));
                        return false;
                    } else if (!IDSCommandlineOptionValidationUtils.isTextLengthValid(value)) {
                        printToConsole(IDSCommandlineOptionValidationUtils.formInvalidTextLengthErrorMsg(key));
                        return false;
                    } else {
                        return true;
                    }
                }
                case IDSCommandlineOptions.SSL_ENABLE: {
                    isSSLEnabledValidation(connParamMap, value);
                    break;
                }
                case IDSCommandlineOptions.SSL_MODE: {
                    /*
                     * No need to handle value being null case. List.contains()
                     * method will take care of it
                     */
                    boolean isSSLEnabled = false;
                    if (connParamMap.containsKey(IDSCommandlineOptions.SSL_ENABLE)) {
                        isSSLEnabled = isSSLEnabledValidation(connParamMap,
                                connParamMap.get(IDSCommandlineOptions.SSL_ENABLE));
                    }

                    if (isSSLEnabled && !IDSCommandlineOptionValidationUtils.getSslModeValueList().contains(value)) {
                        printToConsole(IDSCommandlineOptionValidationUtils.formInvalidSslModeErrorMsg());
                        return false;
                    }
                    break;
                }

                default: {
                    return validateParamLevelThree(key, value, connParamMap);
                }
            }
            return true;
        }

        private boolean validateParamLevelThree(String key, String value, Map<String, String> connParamMap) {
            switch (key) {

                case IDSCommandlineOptions.SSL_CLIENT_CERT:
                case IDSCommandlineOptions.SSL_CLIENT_KEY:
                case IDSCommandlineOptions.SSL_ROOT_CERT: {
                    boolean isSSLEnabled = false;
                    if (connParamMap.containsKey(IDSCommandlineOptions.SSL_ENABLE)) {
                        isSSLEnabled = isSSLEnabledValidation(connParamMap,
                                connParamMap.get(IDSCommandlineOptions.SSL_ENABLE));
                    }

                    if (isSSLEnabled) {
                        if (isNullValue(value)) {
                            printToConsole(IDSCommandlineOptionValidationUtils.formNullValueErrorMsg(key));
                            return false;
                        } else if (!IDSCommandlineOptionValidationUtils.isFilePathValid(value)) {
                            printToConsole(IDSCommandlineOptionValidationUtils.formInvalidFileErrorMsg(key));
                            return false;
                        }
                    }
                    break;
                }
                default: {
                    printToConsole(key + " : " + value + MPPDBIDEConstants.LINE_SEPARATOR
                            + IDSCommandlineOptionValidationUtils.formInvalidParamErrorMsg());
                    return false;
                }
            }
            return true;
        }

        /**
         * function to validate the values supplied to commandline parameters
         * are correct or not.
         *
         * @param connParamMap the conn param map
         * @return validation success/failure
         */
        private boolean validateConnectionParamValue(Map<String, String> connParamMap) {
            String value;
            boolean status;
            for (Entry<String, String> entry : connParamMap.entrySet()) {
                value = entry.getValue();
                status = validateParamLevelOne(entry.getKey(), value, connParamMap);
                if (!status) {
                    return false;
                }
            }
            return true;
        }

        /**
         * check if sslEnable param is supplied in commandline, if yes validate
         * the value.
         *
         * @param connParamMap the conn param map
         * @param value the value
         * @return true if value is (true or false). False otherwise
         */
        private boolean isSSLEnabledValidation(Map<String, String> connParamMap, String value) {
            if (isNullValue(value) || !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                printToConsole(IDSCommandlineOptionValidationUtils.formEnableSslWarningMsg());
                connParamMap.replace(IDSCommandlineOptions.SSL_ENABLE, "false");
            }

            if (value.equalsIgnoreCase("true")) {
                return true;
            }
            return false;
        }

        /**
         * Delete temp folders.
         */
        private void deleteTempFolders() {
            final File[] staleTempFiles = collectStaleTempFiles();
            SQLKeywords.initMap();

            if (staleTempFiles != null) {
                Job tempFolderCleanupJob = new Job("tempFolderCleanupJob") {
                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        MPPDBIDELoggerUtility.trace("temp file cleaner started");
                        clearTempFolder(staleTempFiles);
                        MPPDBIDELoggerUtility.trace("temp file cleaner exiting");
                        return Status.OK_STATUS;
                    }
                };
                tempFolderCleanupJob.schedule();
            }
        }

        /**
         * Creates the folder for disk manager.
         *
         * @param readFilePath the read file path
         * @return true, if successful
         */
        private boolean createFolderForDiskManager(String readFilePath) {
            boolean isSuccess = false;
            String property1 = null;
            String property2 = null;
            try {
                OsUserDiskManager.getInstance().createParentFolderStructure(readFilePath);

                // Create lock to restrict multiple instances
                DSInstanceLock.getInstance().createLock();
                isSuccess = true;

            } catch (DataStudioSecurityException e) {
                property1 = IMessagesConstants.ERR_TITLE_LAUNCH_DATASTUDIO;
                property2 = IMessagesConstants.ERR_BL_LAUNCH_MULTIPLE_INSTANCE_DATASTUDIO;
                return false;
            } catch (MPPDBIDEException exception) {
                property1 = IMessagesConstants.ERR_TITLE_CREATE_FILE_DIRECTORY;
                property2 = IMessagesConstants.ERR_BL_CREATE_FILE_DIRECTORY;
                return false;
            } finally {
                if (!isSuccess) {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(property1), MessageConfigLoader.getProperty(property2));
                    workbench.close();
                }
            }
            return isSuccess;
        }

        /**
         * Sets the eclipse injections.
         */
        private void setEclipseInjections() {
            EclipseInjections ej = EclipseInjections.getInstance();
            ej.setEclipseContext(eclipseContext);
            ej.setCommandService(commandService);
            ej.setHandlerService(handlerService);
            ej.setBindingService(bindingService);
            ej.setWorkBench(workbench);
        }

        /**
         * Inits the alias parser manager.
         */
        private void initAliasParserManager() {
            AliasParserManager.createAliasParserManagerInstance();
            AliasParserManager aliasParserMgr = AliasParserManager.getInstance();
            aliasParserMgr.launchAliasParserWorkerThreads();
            aliasParserMgr.schedule();
        }

        /**
         * Collect stale temp files.
         *
         * @return the file[]
         */
        private File[] collectStaleTempFiles() {
            File[] listOfFiles = null;
            Path profileFolderPath = ConnectionProfileManagerImpl.getInstance().getDiskUtility().getProfileFolderPath();
            Path tempFolderPath = Paths.get(profileFolderPath.toString(), MPPDBIDEConstants.TEMP_FOLDER_PATH);
            if (Files.exists(tempFolderPath)) {
                File tempFolder = new File(tempFolderPath.toString());
                listOfFiles = tempFolder.listFiles();
            }
            return listOfFiles;
        }

        /**
         * Clear temp folder.
         *
         * @param listOfFiles the list of files
         */
        private void clearTempFolder(final File[] listOfFiles) {
            for (File aFile : listOfFiles) {
                try {
                    Files.deleteIfExists(aFile.toPath());
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("error while cleaning up temp folder", exception);
                }
            }
        }

        /**
         * Inits the auto save manager.
         */
        private void initAutoSaveManager() {
            AutoSaveManager autoSaveMgr = AutoSaveManager.getInstance();
            autoSaveMgr.load();
            autoSaveMgr.createPeriodicWorkerJob();

        }

        /**
         * Start event sender thread.
         */
        private void startEventSenderThread() {
            executor = Executors.newSingleThreadScheduledExecutor();

            Runnable periodicTask = new Runnable() {

                /**
                 * run
                 */
                public void run() {
                    sendCanExecuteEventCheck();
                }

                private void sendCanExecuteEventCheck() {
                    eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
                }

            };

            executor.scheduleAtFixedRate(periodicTask, 0, 400, TimeUnit.MILLISECONDS);

        }

        /**
         * used to retrieve the command line argument.
         *
         * @param clArg the cl arg
         * @return the CL argument value
         */
        private String getCLArgumentValue(String clArg) {
            String val = "";
            try {
                val = clArg.split("=")[1].trim();
            } catch (ArrayIndexOutOfBoundsException e) {
                val = "";
            }
            return val;
        }

        /**
         * Read userpreferences from INI file.
         *
         * @param args the args
         */
        private void readUserpreferencesFromINIFile(String[] args) {
            // Validation for logingTimeout and sessionTimeout from iniFile
            if (!ConfigCheckHandler.checkConfigFile() || null == args) {
                return;
            }
            int len = args.length;
            for (int i = 0; i < len; i++) {
                setAgrumentsFromINIFile(args, i);
            }
        }

        private void setAgrumentsFromINIFile(String[] args, int i) {
            if (args[i] != null && args[i].startsWith("-consoleLineCount")) {
                setConsoleLineCountPref(args, i);
            } else if (args[i] != null && args[i].startsWith("-enableSecurityWarning")) {
                setEnableSecurityWarningPrefs(args, i);
            } else if (args[i] != null && args[i].startsWith("-focusOnFirstResult")) {
                setFocusOnFirstResultPref(args, i);
            } else if (args[i] != null && args[i].startsWith("-testability")) {
                setEnableTestabilityPref(args, i);
            } else if (args[i] != null && args[i].startsWith("-TreeRenderPolicy")) {
                DSViewDataManager.getInstance().setTreeRenderPolicy(getCLArgumentValue(args[i]));
            } else if (args[i] != null && args[i].startsWith("-defaultDatabaseType")) {
                setDefaultDatabaseType(args, i);
            } else if (args[i] != null && args[i].startsWith("-enableSSL")) {
                setEnableSSL(args, i);
            }
        }
        
        /**
         * Sets the enable testability pref.
         *
         * @param args the args
         * @param i the i
         */
        private void setEnableTestabilityPref(String[] args, int i) {

            boolean enableTestability = Boolean.valueOf(getCLArgumentValue(args[i]));

            ((UserPreference) UserPreference.getInstance()).setIsenableTestability(enableTestability);

        }

        /**
         * Sets the focus on first result pref.
         *
         * @param args the args
         * @param i the i
         */
        private void setFocusOnFirstResultPref(String[] args, int i) {

            boolean focusOnFirstResult = Boolean.valueOf(getCLArgumentValue(args[i]));

            if (!focusOnFirstResult) {
                ((UserPreference) UserPreference.getInstance()).setFocusOnFirstResult(false);
            } else {
                ((UserPreference) UserPreference.getInstance()).setFocusOnFirstResult(true);
            }
        }

        /**
         * Sets the enable security warning prefs.
         *
         * @param args the args
         * @param i the i
         */
        private void setEnableSecurityWarningPrefs(String[] args, int i) {
            String enableSecurityWarningOpt = getCLArgumentValue(args[i]);
            if ("false".equalsIgnoreCase(enableSecurityWarningOpt)) {
                ((UserPreference) UserPreference.getInstance()).setEnableSecurityWarningOption(false);
            } else if ("true".equalsIgnoreCase(enableSecurityWarningOpt)) {
                ((UserPreference) UserPreference.getInstance()).setEnableSecurityWarningOption(true);
            } else {
                ((UserPreference) UserPreference.getInstance()).setEnableSecurityWarningOption(true);

                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                        Message.getWarnFromConst(IMessagesConstants.DS_INVALID_ENABLE_SECURITY_WARNING));
            }
        }

        /**
         * Sets the console line count pref.
         *
         * @param args the args
         * @param i the i
         */
        private void setConsoleLineCountPref(String[] args, int i) {
            String consoleLineCount = getCLArgumentValue(args[i]);
            int lineCount = convertToint(consoleLineCount);
            ((UserPreference) UserPreference.getInstance()).setConsoleLineCount(lineCount);
            if (lineCount <= 0) {
                ObjectBrowserStatusBarProvider.getStatusBar()
                        .displayMessage(Message.getWarnFromConst(IMessagesConstants.DS_DEFAULT_CONSOLE_LINE_COUNT));
            }
            if (lineCount > 5000) {
                ObjectBrowserStatusBarProvider.getStatusBar()
                        .displayMessage(Message.getWarnFromConst(IMessagesConstants.DS_DEFAULT_MAX_CONSOLE_LINE_COUNT));
            }
        }

        /**
         * Sets the default database type pref.
         *
         * @param args the args
         * @param i the i
         */
        private void setDefaultDatabaseType(String[] args, int i) {
            String defaultDBType = getCLArgumentValue(args[i]);
            if (UserPreference.getInstance() instanceof UserPreference) {
                ((UserPreference) UserPreference.getInstance()).setDefaultDatabaseType(defaultDBType);
            }
        }

        /**
         * Sets EnableSSL pref.
         *
         * @param args the args.
         * @param i the i
         */
        private void setEnableSSL(String[] args, int i) {
            boolean isEnableSSL;
            if ("false".equals(getCLArgumentValue(args[i]))) {
                isEnableSSL = false;
            } else {
                isEnableSSL = true;
            }
            if (UserPreference.getInstance() instanceof UserPreference) {
                ((UserPreference) UserPreference.getInstance()).setSslEnable(isEnableSSL);
            }
        }

        /**
         * Inits the SQL history.
         */
        private void initSQLHistory() {
            // Instantiated. start listener
            ISqlHistoryManager historyManager = SQLHistoryFactory.getInstance();
            historyManager.init(eventBroker);
        }

        /**
         * Hide debug related parts initially when DS launches.
         */
        private void hideDebugParts() {
            UIElement uiElement = UIElement.getInstance();
            uiElement.togglePart(UIConstants.UI_PART_CONSOLE_ID, false, UIConstants.UI_TOGGLE_CONSOLE_MENU);

        }

        /**
         * Title: class Description: The Class SqlHistoryPersist. Copyright (c)
         * Huawei Technologies Co., Ltd. 2012-2019.
         *
         * @author pWX553609
         * @version [DataStudio 6.5.1, 17 May, 2019]
         * @since 17 May, 2019
         */
        private final class SqlHistoryPersist implements Runnable {
            @Override
            public void run() {
                try {
                    SQLHistoryFactory.getInstance().purgeHistorybeforeClose();
                } catch (Exception e1) {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.PREF_LOAD_ERROR_TITLE),
                            MessageConfigLoader.getProperty(IMessagesConstants.ERR_PREFERENCE_LOAD_FAILURE_DETAIL));
                    workbench.close();
                }
            }
        }

        /**
         * The listener interface for receiving mainWindowShutdown events. The
         * class that is interested in processing a mainWindowShutdown event
         * implements this interface, and the object created with that class is
         * registered with a component using the component's
         * <code>addMainWindowShutdownListener<code> method. When the
         * mainWindowShutdown event occurs, that object's appropriate method is
         * invoked. MainWindowShutdownEvent
         */
        private final class MainWindowShutdownListener implements Listener {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                boolean isMainWindow = UIElement.getInstance().isMainWindow((Shell) event.widget);

                final String forcefulExit = "     "
                        + MessageConfigLoader.getProperty(IMessagesConstants.FORCE_EXIT_DATSTUDIO) + "     ";
                final String gracefulExit = "     "
                        + MessageConfigLoader.getProperty(IMessagesConstants.GRACEFULL_EXIT_DATASTUDIO) + "     ";

                final String cancel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_MSG)
                        + "     ";

                if (isMainWindow) {
                    final IJobManager jm = Job.getJobManager();
                    Job[] allJobs = jm.find(MPPDBIDEConstants.CANCELABLEJOB);
                    boolean isContinue = true;
                    if (allJobs.length != 0) {
                        isContinue = cancelRunningJobOnWindowClose(event);
                    } else {

                        isContinue = cleanUpOnWindowClose(event, forcefulExit, gracefulExit, cancel);
                    }
                    if (isContinue) {
                        DSViewDataManager.getInstance().setWbGoingToClose(true);
                        stopAllAliasParserThreads();
                        UIElement.getInstance().removeAllPartsFromEditorStack();
                        DSInstanceLock.getInstance().releaseLock();
                    }
                }
            }

            /**
             * Clean up on window close.
             *
             * @param event the event
             * @param forcefulExit the forceful exit
             * @param gracefulExit the graceful exit
             * @param cancel the cancel
             * @return true, if successful
             */
            private boolean cleanUpOnWindowClose(org.eclipse.swt.widgets.Event event, final String forcefulExit,
                    final String gracefulExit, final String cancel) {
                int returnVal = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.EXIT_APPLICATION),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXIT_APPLICATION_CONFIRMATION), forcefulExit,
                        gracefulExit, cancel);
                if (returnVal == 1) {
                    // saving dirty file terminal
                    if (!SQLTerminal.showDirtyFileTerminalOptions()) {
                        event.type = SWT.NONE;
                        event.doit = false;
                        return false;
                    }

                    BusyIndicator.showWhile(Display.getDefault(), new SqlHistoryPersist());

                    AutoSaveManager.getInstance().gracefulExit();
                    UIDisplayFactoryProvider.getUIDisplayStateIf().cleanUponWindowClose();
                    workbench.close();
                } else if (returnVal == 0) {
                    UIDisplayFactoryProvider.getUIDisplayStateIf().cleanUpSecurityOnWindowClose();
                } else if (returnVal == 2 || returnVal == -1) {
                    event.type = SWT.NONE;
                    event.doit = false;
                    return false;
                }
                return true;
            }

            /**
             * Cancel running job on window close.
             *
             * @param event the event
             * @return true, if successful
             */
            private boolean cancelRunningJobOnWindowClose(org.eclipse.swt.widgets.Event event) {
                int cancelJobVal = -1;
                cancelJobVal = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DS_EXIT_CONFIRMATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.DS_EXIT_MSG_FOR_JOBS));
                if (cancelJobVal == 0) {
                    // saving dirty file terminal
                    if (!SQLTerminal.showDirtyFileTerminalOptions()) {
                        event.type = SWT.NONE;
                        event.doit = false;
                        return false;
                    }

                    UIDisplayFactoryProvider.getUIDisplayStateIf().cleanUponWindowClose();
                } else {
                    event.type = SWT.NONE;
                    event.doit = false;
                    return false;
                }
                return true;
            }

            /**
             * Stop all alias parser threads.
             */
            private void stopAllAliasParserThreads() {
                /* Stop all alias parser worker threads */
                AliasParserManager.getInstance().stopAllAliasParserWorkerThreads();
                /* Stop alias parser manager thread */
                AliasParserManager.getInstance().setAliasParserManagerExitFlag();
            }
        }
    }

    /**
     * Launch conn dialog.
     */
    private void launchConnDialog() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {

                if (UIVersionHandler.isVersionCompatible()) {
                    // launch connection dialog
                    handlerService = eclipseContext.get(EHandlerService.class);
                    Command command = commandService.getCommand("com.huawei.mppdbide.command.id.newdbconnection");

                    ParameterizedCommand parameterizedCommand = new ParameterizedCommand(command, null);
                    handlerService.executeHandler(parameterizedCommand);
                }
            }
        });
    }
    
    /*
     * execute command to connect using commandline value
     */
    private void connectWithCommandlineParams(Map<String, String> connParamMap) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {

                handlerService = eclipseContext.get(EHandlerService.class);
                Command command = commandService
                        .getCommand("com.huawei.mppdbide.view.command.newdbconnectioncommandline");

                ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, connParamMap);
                handlerService.executeHandler(parameterizedCommand);
            }
        });
    }

    /*
     * function to check if mandatory params are passed for corresponding dbType
     */
    private boolean validateConnectionParamSyntax(Map<String, String> connParamMap) {
        /*
         * map size can't be zero, as caller validates this constraint. Check if
         * minimum required number of parameters are present
         */
        /* Mandatory param count for non-Gauss100 is atleast 7 */
        if (!connParamMap.keySet()
                .containsAll(IDSCommandlineOptionValidationUtils.getMandatoryParamListForNonGauss100())) {
            String msg = IDSCommandlineOptionValidationUtils.formLessParamErrorMsg();
            printToConsole(msg);
            return false;
        }
        return true;
    }

    private void extractConnectionParameters(Map<String, String> connParamMap, String[] args) {
        for (String arg : args) {
            /* skip arguments starting with - */
            if (arg.startsWith("-")) {
                continue;
            }
            /*
             * Extract argument=value pairs and put in map Not using split("=")
             * because, value can contain = character
             */
            int splitPos = arg.indexOf("=");
            if (splitPos != -1) {
                connParamMap.put(arg.substring(0, splitPos), arg.substring(splitPos + 1, arg.length()));
            }
        }
    }

    /**
     * Register save handler.
     *
     * @param event the event
     */
    public void registerSaveHandler(Event event) {
        MWindow window = UIElement.getInstance().getSelectedWindow();
        IEclipseContext context = window.getContext();
        context.set(ISaveHandler.class, new DSSaveHandler());
    }

    /**
     * Read ini file for log folder path.
     *
     * @param arguments the arguments
     * @return the string
     */
    private String readIniFileForLogFolderPath(String[] arguments) {

        String[] args = arguments;
        String logFolder = null;
        if (null != args) {
            int len = args.length;
            for (int i = 0; i < len; i++) {
                if (args[i] != null) {
                    if (args[i].startsWith("-logfolder")) {
                        logFolder = MPPDBIDELoggerUtility.getParameter(args[i]);
                        break;
                    }
                }
            }

        }
        return logFolder;
    }

    /**
     * Convert toint.
     *
     * @param stringValue the string value
     * @return the int
     */
    private int convertToint(String stringValue) {
        int intValue = 0;
        try {
            intValue = Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            intValue = -1;
        }
        return intValue;
    }

    private void printToConsole(String msg) {
        System.err.println(msg);
    }
}
