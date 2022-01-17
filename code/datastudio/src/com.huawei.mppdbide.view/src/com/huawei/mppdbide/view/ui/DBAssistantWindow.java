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

package com.huawei.mppdbide.view.ui;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * Title: class Description: The Class DBAssistantWindow.
 *
 * @since 3.0.0
 */
public class DBAssistantWindow {
    private static String systemLineOperators = "";

    private static boolean isUserClosedWindow;

    private static boolean supportedVersionEnableDisable = true;

    private static final Object LOCK = new Object();

    /**
     * Checks if is supported version enable disable.
     *
     * @return true, if is supported version enable disable
     */
    public static boolean isSupportedVersionEnableDisable() {
        return supportedVersionEnableDisable;
    }

    /**
     * Sets the supported version enable disable.
     *
     * @param supportedVersionEnableDisable the new supported version enable
     * disable
     */
    public static void setSupportedVersionEnableDisable(boolean supportedVersionEnableDisable) {
        DBAssistantWindow.supportedVersionEnableDisable = supportedVersionEnableDisable;
    }

    /**
     * Gets the system line operators.
     *
     * @return the system line operators
     */
    public static String getSystemLineOperators() {
        return systemLineOperators;
    }

    /**
     * Sets the system line operators.
     *
     * @param systemLineOperators the new system line operators
     */
    public static void setSystemLineOperators(String systemLineOperators) {
        DBAssistantWindow.systemLineOperators = systemLineOperators;
    }

    private static Browser browser = null;

    /**
     * Gets the browser.
     *
     * @return the browser
     */
    public static Browser getBrowser() {
        return browser;
    }

    /**
     * Sets the browser.
     *
     * @param browser the new browser
     */
    public static void setBrowser(Browser browser) {
        DBAssistantWindow.browser = browser;
    }

    private static String curentSQL = "";

    /**
     * Gets the curent SQL.
     *
     * @return the curent SQL
     */
    public static String getCurentSQL() {
        return curentSQL;
    }

    /**
     * Sets the curent SQL.
     */
    public static void setCurentSQL() {
        if (getViewer() != null && getViewer().getControl().isVisible()) {
            String selection = getViewer().getTextWidget().getSelectionText();
            if ("".equals(selection)) {
                selection = getViewer().getTextWidget().getText();
            }
            String inputStr = "";
            if (selection.length() > 1024) {
                inputStr = selection.substring(selection.length() - 1024);
                inputStr = inputStr.replaceAll(getSystemLineOperators(), " ").trim();
            } else {

                inputStr = selection.replaceAll(getSystemLineOperators(), " ").trim();
            }
            curentSQL = inputStr;
        } else {
            curentSQL = "";
        }
    }

    private static Database database = null;

    /**
     * Gets the database.
     *
     * @return the database
     */
    public static Database getDatabase() {
        return database;
    }

    /**
     * Sets the database.
     *
     * @param database the new database
     */
    public static void setDatabase(Database database) {
        DBAssistantWindow.database = database;
    }

    private static SourceViewer viewer = null;

    /**
     * Gets the viewer.
     *
     * @return the viewer
     */
    public static SourceViewer getViewer() {
        return viewer;
    }

    /**
     * Sets the viewer.
     *
     * @param viewer the new viewer
     */
    public static void setViewer(SourceViewer viewer) {
        DBAssistantWindow.viewer = viewer;
    }

    private static boolean isSupportedVersion = true;

    /**
     * Checks if is supported version.
     *
     * @return true, if is supported version
     */
    public static boolean isSupportedVersion() {
        return isSupportedVersion;
    }

    /**
     * Sets the supported version.
     *
     * @param isSupportedVers the new supported version
     */
    public static void setSupportedVersion(boolean isSupportedVers) {
        DBAssistantWindow.isSupportedVersion = isSupportedVers;
    }

    private static boolean allTerminalsClosed;

    /**
     * Checks if is all terminals closed.
     *
     * @return true, if is all terminals closed
     */
    public static boolean isAllTerminalsClosed() {
        return allTerminalsClosed;
    }

    /**
     * Sets the all terminals closed.
     *
     * @param allTerminalsClosed the new all terminals closed
     */
    public static void setAllTerminalsClosed(boolean allTerminalsClosed) {
        DBAssistantWindow.allTerminalsClosed = allTerminalsClosed;
    }

    private static boolean enable = true;

    /**
     * Checks if is enable.
     *
     * @return true, if is enable
     */
    public static boolean isEnable() {
        return enable;
    }

    /**
     * Sets the enable A.
     *
     * @param enble the new enable A
     */
    public static void setEnableA(boolean enble) {
        DBAssistantWindow.enable = enble;
    }

    private static boolean visible = true;

    /**
     * Checks if is visible.
     *
     * @return true, if is visible
     */
    public static boolean isVisible() {
        return visible;
    }

    private static boolean displayVersionNote = false;

    /**
     * Checks if is display version note.
     *
     * @return true, if is display version note
     */
    public static boolean isDisplayVersionNote() {
        return displayVersionNote;
    }

    /**
     * Sets the display version note.
     *
     * @param displayVersionNote the new display version note
     */
    public static void setDisplayVersionNote(boolean displayVersionNote) {
        DBAssistantWindow.displayVersionNote = displayVersionNote;
    }

    private static String issueTime = "no";

    private static String docTempVersion = "";

    /**
     * Gets the doc temp version.
     *
     * @return the doc temp version
     */
    public static String getDocTempVersion() {
        return docTempVersion;
    }

    /**
     * Sets the doc temp version.
     *
     * @param docTempVersion the new doc temp version
     */
    public static void setDocTempVersion(String docTempVersion) {
        DBAssistantWindow.docTempVersion = docTempVersion;
    }

    private static String docRealVersion = "";

    /**
     * Gets the doc real version.
     *
     * @return the doc real version
     */
    public static String getDocRealVersion() {
        return docRealVersion;
    }

    /**
     * Sets the doc real version.
     *
     * @param db the new doc real version
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void setDocRealVersion(Database db) throws DatabaseOperationException, DatabaseCriticalException {
        String dbVersion = db.getServerVersion();
        try {
            if (dbVersion.contains("openGauss")) {
                int startP = dbVersion.indexOf("openGauss");
                DBAssistantWindow.docRealVersion = dbVersion.substring(startP, startP + 15);
            } else {
                DBAssistantWindow.docRealVersion = dbVersion.substring(0, 25);
            }
        } catch (IndexOutOfBoundsException indexExpection) {
            MPPDBIDELoggerUtility.error("IndexOutOfBoundsException while substring the db version", indexExpection);
        }

        // check if the version exists, if not exists then set the defaile
        // version
        String windowBuildPath = DBAssistantWindow.getBuildPath();

        if (!isDBAssistVersionExist(windowBuildPath, docRealVersion)) {
            String filePathByDB = getCommonDBAssistVersion(db);
            if (null != filePathByDB) {
                docRealVersion = filePathByDB;
            }

        }

    }

    private static String getCommonDBAssistVersion(Database db) {

        String buildPath1 = DBAssistantWindow.getBuildPath();

        String propFilePath = buildPath1 + EnvirnmentVariableValidator.validateAndGetFileSeperator() + "db_assistant"
                + EnvirnmentVariableValidator.validateAndGetFileSeperator() + "dbassist.properties";

        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(propFilePath)) {

            // load a properties file
            prop.load(input);

        } catch (IOException ex) {
            MPPDBIDELoggerUtility.error("IOException while reading dbassist.properties file", ex);
            MPPDBIDELoggerUtility.error("PATH :" + propFilePath);
            return null;
        }

        String versionKey = "GAUSS200";
        String dbAssistVersionName = prop.getProperty(versionKey);

        if (StringUtils.isNotEmpty(dbAssistVersionName)) {

            if (isDBAssistVersionExist(buildPath1, dbAssistVersionName)) {
                return dbAssistVersionName;
            }

        }

        return null;
    }

    /**
     * Checks if is DB assist version exist.
     *
     * @param buildPath the build path
     * @param dbAssistVersionName the db assist version name
     * @return true, if is DB assist version exist
     */
    public static boolean isDBAssistVersionExist(String buildPath, String dbAssistVersionName) {
        if (null == buildPath) {
            buildPath = DBAssistantWindow.getBuildPath();
        }

        String dbAssistPath = buildPath + EnvirnmentVariableValidator.validateAndGetFileSeperator() + "db_assistant"
                + EnvirnmentVariableValidator.validateAndGetFileSeperator() + dbAssistVersionName;

        File dbPathFileObj = new File(dbAssistPath);

        if (dbPathFileObj.exists() && dbPathFileObj.isDirectory()) {
            return true;
        }

        return false;
    }

    private static String realAssitantPath = "";

    /**
     * Gets the real assitant path.
     *
     * @return the real assitant path
     */
    public static String getRealAssitantPath() {
        return realAssitantPath;
    }

    /**
     * Sets the real assitant path.
     *
     * @param realAssitantPath the new real assitant path
     */
    public static void setRealAssitantPath(String realAssitantPath) {
        DBAssistantWindow.realAssitantPath = realAssitantPath;
    }

    private static String docRealLang = "";

    private static String buildPath = "";

    private static Composite composite;

    /**
     * Gets the composite.
     *
     * @return the composite
     */
    public static Composite getComposite() {
        return composite;
    }

    /**
     * Sets the composite.
     *
     * @param composite the new composite
     */
    public static void setComposite(Composite composite) {
        DBAssistantWindow.composite = composite;
    }

    /**
     * Format date.
     *
     * @param date the date
     * @return the string
     * @throws ParseException the parse exception
     */
    public static String formatDate(Date date) throws ParseException {
        synchronized (LOCK) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
            return simpleDateFormat.format(date);
        }
    }

    /**
     * Parses the.
     *
     * @param strDate the str date
     * @return the date
     * @throws ParseException the parse exception
     */
    public static Date parse(String strDate) throws ParseException {
        synchronized (LOCK) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
            return simpleDateFormat.parse(strDate);
        }
    }

    private static boolean noNeedToPrint = true;

    /**
     * Checks if is no need to print.
     *
     * @return true, if is no need to print
     */
    public static boolean isNoNeedToPrint() {
        return noNeedToPrint;
    }

    /**
     * Sets the no need to print.
     *
     * @param noNeedToPrint the new no need to print
     */
    public static void setNoNeedToPrint(boolean noNeedToPrint) {
        DBAssistantWindow.noNeedToPrint = noNeedToPrint;
    }

    /**
     * Gets the issue time.
     *
     * @return the issue time
     */
    public static String getIssueTime() {
        return issueTime;
    }

    /**
     * Sets the issue time.
     *
     * @param issueTime the new issue time
     */
    public static void setIssueTime(String issueTime) {
        DBAssistantWindow.issueTime = issueTime;
    }

    /**
     * Checks if is user closed window.
     *
     * @return true, if is user closed window
     */
    public static boolean isUserClosedWindow() {
        return isUserClosedWindow;
    }

    /**
     * Sets the user closed window.
     *
     * @param isUserClosed the new user closed window
     */
    public static void setUserClosedWindow(boolean isUserClosed) {
        DBAssistantWindow.isUserClosedWindow = isUserClosed;
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        removeMinimizedTagFromDBAssistantPart(false);
        if (getBrowser() != null) {
            browser.dispose();
            setBrowser(null);
        }
        setVisible(false);
        setUserClosedWindow(true);

        if (DBAssistantWindow.isEnable()
                && DbAssistantSupportedVersions.isDbAssistantSupported(DBAssistantWindow.getDocRealVersion())) {
            setSupportedVersionEnableDisable(false);
        }
    }

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param partService the part service
     * @param modelService the model service
     * @param application the application
     */
    @PostConstruct
    public void createPartControl(Composite parent, EPartService partService, EModelService modelService,
            MApplication application) {
        IDEStartup.getInstance().init(partService, modelService, application);
        setComposite(parent);
        char c13 = 13;
        char c10 = 10;
        setSystemLineOperators(c13 + "" + c10 + "" + '|' + "" + c13 + "" + '|' + "" + c10 + "" + '|' + "\'");
        setUserClosedWindow(false);
    }

    /**
     * Inits the browser for open.
     */
    public static void initBrowserForOpen() {
        visible = true;
        initBrowser(getComposite());
    }

    private static void initBrowser(Composite parent) {
        synchronized (LOCK) {
            String filePath = null;

            String lang = MessageConfigLoader.getProperty("DB_ASSISTANT_LANGUAGE");
            if ("zh_CN".equals(lang)) {
                docRealLang = "zh";
            } else {
                docRealLang = "en";
            }
            filePath = buildPath + EnvirnmentVariableValidator.validateAndGetFileSeperator() + "db_assistant"
                    + EnvirnmentVariableValidator.validateAndGetFileSeperator() + "index_" + docRealLang + ".html";
            File file = new File(filePath);
            if (file.exists()) {
                try {
                    if (getBrowser() == null || getBrowser().isDisposed()) {
                        setBrowser(new Browser(parent, SWT.NONE));
                        FillLayout layout = new FillLayout();
                        layout.marginHeight = 0;
                        layout.marginWidth = 0;
                        layout.spacing = 0;
                        getBrowser().setLayout(layout);
                    }
                } catch (SWTError swtError) {
                    /* Failed to create browser widget */
                    MPPDBIDELoggerUtility.error("Failed to create Browser object", swtError);
                    DBAssistantWindow.setVisible(false);
                    return;
                }

                getBrowser().setUrl("file:" + File.separator + File.separator + File.separator + filePath);
                new DBAssistantFunctionHide(getBrowser(), "myCallDBAssistant");
                new DBAssistantFunctionLoad(getBrowser(), "myCallDBAssistantLoad");
                new DBAssistantFunctionDisableNote(getBrowser(), "myCallDBAssistantDisableNote");
                new DBAssistantFunctionProcessSelection(getBrowser(), "myCallDBAssistantProcessSelection");
                new DBAssistantFunctionProcessSelection2(getBrowser(), "myCallDBAssistantProcessSelection2");
            }
        }
    }

    private static File getVersionFile(File tempP) {
        File tempF = null;
        if (tempP.exists()) {
            File[] files = tempP.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });
            if (files != null && files.length > 0) {
                tempF = files[0];
            }
        }
        return tempF;
    }

    /**
     * Gets the local version path.
     *
     * @return the local version path
     */
    public static String getLocalVersionPath() {
        buildPath = getBuildPath();

        setRealAssitantPath(buildPath + EnvirnmentVariableValidator.validateAndGetFileSeperator() + "db_assistant"
                + EnvirnmentVariableValidator.validateAndGetFileSeperator());
        String tempPath = getRealAssitantPath() + getDocRealVersion();
        File tempF = new File(tempPath + EnvirnmentVariableValidator.validateAndGetFileSeperator() + issueTime);
        File tempP = new File(tempPath);
        File tempFC = getVersionFile(tempP);
        try {
            if (tempF.exists() || (tempP.exists() && "no".equals(issueTime))
                    || (tempP.exists() && tempFC != null && !(parse(issueTime).after(parse(tempFC.getName()))))) {
                return "";
            } else {
                delFolderA(new File(tempPath));
                return tempPath;
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static String getBuildPath() {
        URL url = DBAssistantWindow.class.getProtectionDomain().getCodeSource().getLocation();
        StringBuilder builder = new StringBuilder(url.getFile());
        builder.deleteCharAt(0);
        builder.delete(builder.lastIndexOf("/"), builder.length());
        builder.delete(builder.lastIndexOf("/"), builder.length());

        return builder.toString();
    }

    /**
     * Sets the visible.
     *
     * @param value the new visible
     */
    public static void setVisible(boolean value) {
        visible = value;

        if (visible) {
            setCurentSQL();
            MPartStack partS = (MPartStack) EclipseInjections.getInstance().getMS()
                    .find(UIConstants.UI_PARTSTACK_ID_SQL_ASSISTANT, EclipseInjections.getInstance().getApp());

            MPart part = (MPart) EclipseInjections.getInstance().getMS().find(UIConstants.UI_PART_ID_SQL_ASSISTANT,
                    EclipseInjections.getInstance().getApp());

            if (!DBAssistantWindow.getDocTempVersion().equals(getDocRealVersion())
                    && !partS.getTags().contains(IPresentationEngine.MINIMIZED)) {
                EclipseInjections.getInstance().getPS().activate(part);
                partS.getTags().add(IPresentationEngine.MINIMIZED);
                partS.getTags().remove(IPresentationEngine.MINIMIZED);
                initBrowser(getComposite());
                partS.setVisible(true);
            } else if (!DBAssistantWindow.getDocTempVersion().equals(getDocRealVersion())
                    && partS.getTags().contains(IPresentationEngine.MINIMIZED)) {
                initBrowser(getComposite());
            } else {
                if (null != getBrowser()) {
                    getBrowser().execute(
                            "processInput('" + getCurentSQL().replaceAll(getSystemLineOperators(), " ").trim() + "')");
                }
            }
            DBAssistantWindow.setDocTempVersion(getDocRealVersion());
        } else {
            removeMinimizedTagFromDBAssistantPart(true);
            DBAssistantWindow.setDocTempVersion("");
        }
    }

    /**
     * Sets the enable B.
     *
     * @param isEnable the new enable B
     */
    public static void setEnableB(boolean isEnable) {
        setEnableA(isEnable);
        MUIElement partUIElement = EclipseInjections.getInstance().getMS().find(UIConstants.UI_PART_ID_SQL_ASSISTANT,
                EclipseInjections.getInstance().getApp());

        if (!(null != partUIElement && partUIElement instanceof MPart)) {
            return;
        }

        if (isEnable() && DbAssistantSupportedVersions.isDbAssistantSupported(DBAssistantWindow.getDocRealVersion())) {
            MPart part = (MPart) partUIElement;
            EclipseInjections.getInstance().getPS().activate(part);
            setCurentSQL();
            initBrowserForOpen();
        } else {
            if (getBrowser() != null) {
                MUIElement partStackUIElement = EclipseInjections.getInstance().getMS()
                        .find(UIConstants.UI_PARTSTACK_ID_SQL_ASSISTANT, EclipseInjections.getInstance().getApp());
                if (null != partStackUIElement && partStackUIElement instanceof MPartStack) {
                    MPartStack partS = (MPartStack) partStackUIElement;
                    MPart part = (MPart) partUIElement;
                    EclipseInjections.getInstance().getPS().activate(part);
                    List<String> tags = partS.getTags();
                    if (null != tags) {
                        tags.remove(IPresentationEngine.MINIMIZED);
                    }
                    partS.setVisible(false);
                    part.setToBeRendered(false);
                    DBAssistantWindow.setDocTempVersion("");
                }
            }
        }
    }

    /**
     * Exec SQL.
     *
     * @param sqlStr the sql str
     */
    public static void execSQL(String sqlStr) {
        if (getBrowser() != null) {
            try {
                if (sqlStr.length() > 1024) {
                    String inputStr = sqlStr.substring(sqlStr.length() - 1024);
                    inputStr = inputStr.replaceAll(getSystemLineOperators(), " ").trim();
                    getBrowser().execute("processInput('" + inputStr + "')");
                } else {

                    String inputStr = sqlStr.replaceAll(getSystemLineOperators(), " ").trim();
                    getBrowser().execute("processInput('" + inputStr + "')");
                }
            } catch (Exception e) {
                noNeedToPrint = true;
            }
        }
    }

    /**
     * Exec err.
     *
     * @param errStr the err str
     */
    public static void execErr(String errStr) {
        if (getBrowser() != null) {
            try {
                int pos = errStr.indexOf("GAUSS-");
                if (errStr.length() >= 11 && pos > -1) {
                    String errCode = errStr.substring(pos, pos + 11);
                    getBrowser().execute("processErrorCode('" + errCode + "')");
                }
            } catch (Exception e) {
                noNeedToPrint = true;
            }
        }
    }

    /**
     * Load data.
     */
    public static void loadData() {
        try {
            Browser brwsr = getBrowser();
            if (isDisplayVersionNote()) {
                if (null != brwsr) {

                    //

                    brwsr.execute("displayVersionNote('" + getDocRealVersion() + "')");
                }
                removeMinimizedTagFromDBAssistantPart(false);
            } else {
                if (!"".equals(getDocRealVersion())) {
                    removeMinimizedTagFromDBAssistantPart(false);
                    if (null != brwsr) {
                        getBrowser().execute("setCurrentSQL('"
                                + getCurentSQL().replaceAll(getSystemLineOperators(), " ").trim() + "')");
                        getBrowser().execute("loadSuggestion('" + getDocRealVersion() + '/' + docRealLang + "')");
                        getBrowser().execute("loadErrorcode('" + getDocRealVersion() + '/' + docRealLang + "')");
                    }
                }
            }

        } catch (Exception e) {
            noNeedToPrint = true;
        }
    }

    /**
     * Toggle assitant enable disable.
     *
     * @param viewr the viewr
     * @param db the db
     */
    public static void toggleAssitantEnableDisable(SourceViewer viewr, Database db) {
        if (!isUserClosedWindow()) {
            toggleAssitant(viewr, db);
        }
    }

    /**
     * Toggle assitant.
     * 
     * @param viewr the viewr
     * @param db the db
     */
    public static void toggleAssitant(SourceViewer viewr, Database db) {
        setDbAssistContent(viewr, db);
    }

    private static void removeMinimizedTagFromDBAssistantPart(boolean isMakePartInvisible) {
        EModelService ms = EclipseInjections.getInstance().getMS();
        if (ms instanceof MPartStack) {
            MPartStack partS = (MPartStack) ms.find(UIConstants.UI_PARTSTACK_ID_SQL_ASSISTANT,
                    EclipseInjections.getInstance().getApp());
            List<String> tags = partS.getTags();
            if (null != tags && tags.contains(IPresentationEngine.MINIMIZED)) {
                tags.remove(IPresentationEngine.MINIMIZED);
            }

            if (isMakePartInvisible) {
                partS.setVisible(false);
            }
        }
    }

    private static void setDbAssistContent(SourceViewer viewr, Database db) {
        try {
            if (db != null) {
                DBAssistantWindow.setDocRealVersion(db);
            }
            if (DBAssistantWindow.isEnable()
                    && DbAssistantSupportedVersions.isDbAssistantSupported(DBAssistantWindow.getDocRealVersion())) {
                setContentInDbAssistWindow(viewr, db);
            } else {
                DBAssistantWindow.setSupportedVersion(false);
                if (DBAssistantWindow.getBrowser() != null) {
                    removeMinimizedTagFromDBAssistantPart(true);
                    DBAssistantWindow.setDocTempVersion("");
                    DBAssistantWindow.getBrowser().dispose();
                    DBAssistantWindow.setBrowser(null);
                }
            }
        } catch (Exception e) {
            noNeedToPrint = true;
        }
    }

    private static void setContentInDbAssistWindow(SourceViewer viewr, Database db) {
        DBAssistantWindow.setViewer(viewr);
        DBAssistantWindow.setDatabase(db);
        DBAssistantWindow.setIssueTime("no");

        String realDocVersionPath = DBAssistantWindow.getLocalVersionPath();

        if ("".equals(realDocVersionPath)) {
            DBAssistantWindow.setSupportedVersion(true);
            if (isSupportedVersionEnableDisable()) {
                DBAssistantWindow.setVisible(true);
            } else {
                DBAssistantWindow.setVisible(false);
            }
        } else {
            DBAssistantWindow.setSupportedVersion(false);
            DBAssistantWindow.setVisible(false);
            if (DBAssistantWindow.getBrowser() != null) {
                DBAssistantWindow.getBrowser().dispose();
                DBAssistantWindow.setBrowser(null);
            }
        }
    }

    /**
     * Toggle current assitant.
     *
     * @param isSearchWindow the is search window
     */
    public static void toggleCurrentAssitant(boolean isSearchWindow) {
        if (isDebugInProgress() || isSearchWindow) {
            if (DBAssistantWindow.getBrowser() != null) {
                removeMinimizedTagFromDBAssistantPart(true);
                DBAssistantWindow.setDocTempVersion("");
                DBAssistantWindow.getBrowser().dispose();
                DBAssistantWindow.setBrowser(null);
            }
            return;
        }
        int offset = viewer.getTextWidget().getCaretOffset();
        if (offset == 0) {
            String totalStr = viewer.getDocument().get();
            int posEnd = totalStr.indexOf(';');
            if (posEnd < 0) {
                DBAssistantWindow.execSQL(totalStr);
            } else {
                DBAssistantWindow.execSQL(totalStr.substring(0, posEnd));
            }
        } else {
            String totalStr = viewer.getDocument().get();
            if ("".equals(totalStr)) {
                return;
            }
            String subStr = totalStr.substring(0, offset);
            int posStart = subStr.lastIndexOf(';');
            int posEnd = totalStr.indexOf(';', posStart + 1);
            if (posStart < 0 && posEnd < 0) {
                DBAssistantWindow.execSQL(totalStr);
            } else if (posStart < 0 && posEnd >= 0) {
                DBAssistantWindow.execSQL(totalStr.substring(0, posEnd));
            } else if (posStart >= 0 && posEnd < 0) {
                DBAssistantWindow.execSQL(totalStr.substring(posStart + 1));
            } else {
                DBAssistantWindow.execSQL(totalStr.substring(posStart + 1, posEnd));
            }
        }
    }

    private static boolean isDebugInProgress() {
        return viewer == null || viewer.getTextWidget() == null || viewer.getDocument() == null;
    }

    private static void delFolderA(File folder) {
        try {
            if (folder != null && folder.exists()) {
                boolean tempB = false;
                if (folder.isDirectory()) {
                    File[] files = folder.listFiles();
                    int length = files != null ? files.length : 0;
                    for (int li = 0; li < length; li++) {

                        delFolderA(files[li]);
                    }
                    tempB = folder.delete();
                } else {
                    tempB = folder.delete();
                }
                if (tempB) {
                    noNeedToPrint = true;
                }
            }
        } catch (Exception e) {
            noNeedToPrint = true;
        }
    }
}
