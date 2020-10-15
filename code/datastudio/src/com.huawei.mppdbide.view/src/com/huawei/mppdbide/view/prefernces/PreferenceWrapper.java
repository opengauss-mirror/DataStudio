/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.autosave.AutoSaveManager;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSyntaxColorProvider;
import com.huawei.mppdbide.view.data.DSViewDataManager;
import com.huawei.mppdbide.view.erd.contextmenu.ERPreferencePage;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;

/**
 * 
 * Title: class
 * 
 * Description: The Class PreferenceWrapper.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class PreferenceWrapper {

    private static final int PREF_FILE_MAX_SIZE = 5;

    private static PreferenceWrapper wrapper;

    private PreferenceStore preferenceStore = null;

    private boolean isPreferenceApply = false;

    private boolean needRestart = false;
    private boolean isChangeDone = false;
    private boolean isDefaultStore = false;
    private boolean isPreferenceValid = false;
    private boolean isRestartSkipped = false;

    /**
     * Instantiates a new preference wrapper.
     */
    private PreferenceWrapper() {

    }

    static {
        wrapper = new PreferenceWrapper();
    }

    /**
     * Gets the single instance of PreferenceWrapper.
     *
     * @return single instance of PreferenceWrapper
     */
    public static PreferenceWrapper getInstance() {
        return wrapper;

    }

    /**
     * Gets the preference store.
     *
     * @return the preference store
     */
    public PreferenceStore getPreferenceStore() {
        return preferenceStore;
    }

    /**
     * Load preference store.
     *
     * @return the preference store
     * @throws MPPDBIDEException
     */
    public PreferenceStore loadPreferenceStore() throws MPPDBIDEException {
        StringBuilder packagePath = new StringBuilder(DSViewDataManager.getInstance().getCurrentOsUserPath());
        ISetFilePermission withPermission = FilePermissionFactory.getFilePermissionInstance();

        File file = new File(packagePath.toString());

        String filepath = null;

        // Check if folder has write permission
        boolean canWrite = Files.isWritable(file.toPath());
        file = null;
        if (canWrite) {
            packagePath.append(File.separator + MPPDBIDEConstants.PREFERENCES_FOLDER);
        }

        try {
            // Create a connection profile related folder with appropriate
            // security permission for the file
            withPermission.createFileWithPermission(packagePath.toString(), true, null, true);
            filepath = packagePath.toString() + File.separator + MPPDBIDEConstants.PREFERENCES_FILE;

            file = new File(filepath);
            if (!file.exists()) {
                withPermission.createFileWithPermission(filepath, false, null, true);
            }
            double fileSizeInMB = FileUtils.sizeOf(file) / (double) (1024 * 1024);
            if (fileSizeInMB != 0 && fileSizeInMB > PREF_FILE_MAX_SIZE) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_WARNING_MESSAGE));
                throw new MPPDBIDEException(IMessagesConstants.FILE_LIMIT_WARNING_MESSAGE);
            }
            preferenceStore = new PreferenceStore(filepath);
            setAllDefaultPreferences();

            preferenceStore.load();
            verifyPreferenceValues();
        } catch (IOException e) {
            MPPDBIDEDialogs.generateErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_LOAD_FAIL_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_LOAD_FAIL_MSG), e);

        } catch (DatabaseOperationException e) {
            MPPDBIDEDialogs.generateErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_LOAD_FAIL_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_LOAD_FAIL_MSG), e);
        }
        return preferenceStore;

    }

    /**
     * Sets the all default preferences.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void setAllDefaultPreferences() throws IOException {
        SQLSyntaxColorProvider.setDefaultPreferences(preferenceStore);
        DSTemplatePreferencePage.setDefaultPreferences(preferenceStore);
        DSFoldingPreferencePage.setDefaultPreferences(preferenceStore);
        KeyBindingWrapper.getInstance().setDefaultKeyMapperPreferences(preferenceStore);
        SecurityOptionProviderForPreferences.setDefaultSecurityPreferences(preferenceStore);
        UserEncodingOption.setDefaultSecurityPreferences(preferenceStore);
        DBAssistantOption.setDefaultDBAssistantPreferences(preferenceStore);
        AutoSaveManager.setDefaultPreferences(preferenceStore);
        ResultManagementViewDataPreferencePage.setDefaultPreferences(preferenceStore);
        ExportDDLPreferencePage.setDefaultPreferences(preferenceStore);
        EditTableOptionProviderForPreferences.setDefaultEditTablePreferences(preferenceStore);
        SQLHistoryPreferences.setDefaultForSQLPreferences(preferenceStore);
        AutoCompletePreference.setDefaultForAutoCompletePreferences(preferenceStore);
        DSFormatterPreferencePage.setDefaultPreferencesForFormatter(preferenceStore);
        DSTransactionPreferencePage.setDefaultPreferences(preferenceStore);
        DsDataLimitPreferencePage.setDefaultPreferences(preferenceStore);
        DSFontPreferencePage.setDefaultPreferences(preferenceStore);
        ERPreferencePage.setDefaultPreferences(preferenceStore);
        DateTimePreferencePage.setDefaultPreferences(preferenceStore);
        ObjectBrowserPreferncePage.setDefaultPreferences(preferenceStore);
    }

    /**
     * Checks if is preference apply.
     *
     * @return true, if is preference apply
     */
    public boolean isPreferenceApply() {
        return isPreferenceApply;
    }

    /**
     * Sets the preference apply.
     *
     * @param isPrefernceApply the new preference apply
     */
    public void setPreferenceApply(boolean isPrefernceApply) {
        this.isPreferenceApply = isPrefernceApply;
    }

    /**
     * Checks if is need restart.
     *
     * @return true, if is need restart
     */
    public boolean isNeedRestart() {
        return needRestart;
    }

    /**
     * Sets the need restart.
     *
     * @param needRestart the new need restart
     */
    public void setNeedRestart(boolean needRestart) {
        this.needRestart = needRestart;
    }

    /**
     * Checks if is change done.
     *
     * @return true, if is change done
     */
    public boolean isChangeDone() {
        return isChangeDone;
    }

    /**
     * Sets the change done.
     *
     * @param isChngeDone the new change done
     */
    public void setChangeDone(boolean isChngeDone) {
        this.isChangeDone = isChngeDone;
    }

    /**
     * Checks if is default store.
     *
     * @return true, if is default store
     */
    public boolean isDefaultStore() {
        return isDefaultStore;
    }

    /**
     * Sets the default store.
     *
     * @param isDefultStore the new default store
     */
    public void setDefaultStore(boolean isDefultStore) {
        this.isDefaultStore = isDefultStore;
    }

    /**
     * Checks if is preference valid.
     *
     * @return true, if is preference valid
     */
    public boolean isPreferenceValid() {
        return isPreferenceValid;
    }

    /**
     * Sets the preference valid.
     *
     * @param isPrefernceValid the new preference valid
     */
    public void setPreferenceValid(boolean isPrefernceValid) {
        this.isPreferenceValid = isPrefernceValid;
    }

    /**
     * Checks if is restart skipped.
     *
     * @return true, if is restart skipped
     */
    public boolean isRestartSkipped() {
        return isRestartSkipped;
    }

    /**
     * Sets the restart skipped.
     *
     * @param isRestrtSkipped the new restart skipped
     */
    public void setRestartSkipped(boolean isRestrtSkipped) {
        this.isRestartSkipped = isRestrtSkipped;
    }

    /**
     * 
     * @Author: lijialiang(l00448174)
     * @Date: Sep 11, 2019
     * @Title: verifyPreferenceValues
     * @Description: validate the preference value, reset to default if it is
     * invalid.
     *
     */
    private void verifyPreferenceValues() {
        int fontSize = preferenceStore.getInt(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE);
        if (fontSize > DSFontPreferencePage.FONT_SIZE_MAX || fontSize < DSFontPreferencePage.FONT_SIZE_MIN) {
            preferenceStore.setToDefault(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE);
        }
        ResultManagementViewDataPreferencePage.validateFetchCount(preferenceStore);
        SQLHistoryAndQueryPreference.validateSQLHistoryInfo(preferenceStore);
        ExportDDLPreferencePage.validateImportExportData(preferenceStore);
        ObjectBrowserPreferncePage.validateObjectBrowserPref(preferenceStore);
    }
}
