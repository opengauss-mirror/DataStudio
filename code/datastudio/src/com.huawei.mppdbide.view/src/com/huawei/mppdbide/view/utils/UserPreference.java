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

package com.huawei.mppdbide.view.utils;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.prefernces.UserEncodingOption;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserPreference.
 *
 * @since 3.0.0
 */
public final class UserPreference implements IUserPreference {
    private PreferenceStore fPreferenceStore;
    private static volatile IUserPreference instance = null;
    private int editResultSetFetchCount = 1000;
    private int consoleLineCount = -1;
    private boolean enablePermanentPasswordSaveOption = false;
    private boolean enableSecurityWarningOption = true;
    private boolean focusOnFirstResult = false;
    private boolean sslEnable = true;
    private int batchMsgSize = 1000;
    private String fileEncoding = "UTF-8";
    private String defaultDatabaseType = "openGauss";
    private boolean isLogingAllowedOnPasswordExpiry = true;
    private boolean isCommitValidRows = true;
    private boolean isenableTestability = false;
    private static final Object LOCK = new Object();

    /**
     * Instantiates a new user preference.
     */
    private UserPreference() {
        fPreferenceStore = new PreferenceStore();
        setCurrentLinePrefs();
        setHighlightPref();
    }

    /**
     * Gets the single instance of UserPreference.
     *
     * @return single instance of UserPreference
     */
    public static IUserPreference getInstance() {
        if (null == instance) {
            synchronized (LOCK) {
                if (null == instance) {
                    instance = new UserPreference();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the prefernce store.
     *
     * @return the prefernce store
     */
    @Override
    public IPreferenceStore getPrefernceStore() {
        return fPreferenceStore;
    }

    /**
     * Sets the current line prefs.
     */
    private void setCurrentLinePrefs() {
        int red = 200;
        int green = 214;
        int blue = 245;

        StringBuilder colorSB = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        colorSB.append(red).append(",");
        colorSB.append(green).append(",");
        colorSB.append(blue);

        fPreferenceStore.setDefault(CURRENT_LINE_VISIBILITY, true);
        fPreferenceStore.setDefault(CURRENTLINE_COLOR, colorSB.toString());
    }

    /**
     * Sets the highlight pref.
     */
    private void setHighlightPref() {
        fPreferenceStore.setDefault(HIGHLIGHT_PREFERENCE, true);
    }

    /**
     * Gets the edits the result set fetch count.
     *
     * @return the edits the result set fetch count
     */
    @Override
    public int getEditResultSetFetchCount() {
        return editResultSetFetchCount;
    }

    /**
     * Sets the edits the result set fetch count.
     *
     * @param editResultSetFetchCount the new edits the result set fetch count
     */
    @Override
    public void setEditResultSetFetchCount(int editResultSetFetchCount) {
        this.editResultSetFetchCount = editResultSetFetchCount;
    }

    /**
     * Gets the result data fetch count.
     *
     * @return the result data fetch count
     */
    public int getResultDataFetchCount() {
        PreferenceStore preferenceStore = PreferenceWrapper.getInstance().getPreferenceStore();
        boolean isFetchAll = preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_RECORD_FETCH_ALL);

        return isFetchAll ? -1 : preferenceStore.getInt(MPPDBIDEConstants.PREF_RESULT_RECORD_FETCH_COUNT);
    }

    /**
     * Gets the pref time out.
     *
     * @return the pref time out
     */
    public int getPrefTimeOut() {
        PreferenceStore preferenceStore = PreferenceWrapper.getInstance().getPreferenceStore();
        boolean isDefault = preferenceStore.getBoolean(MPPDBIDEConstants.IS_DEFAULT_TIMEOUT);

        return isDefault ? MPPDBIDEConstants.PROCESS_TIMEOUT : preferenceStore.getInt(MPPDBIDEConstants.TIMEOUT_VALUE);
    }

    /**
     * Checks if is generate new result window.
     *
     * @return true, if is generate new result window
     */
    public boolean isGenerateNewResultWindow() {
        PreferenceStore preferenceStore = PreferenceWrapper.getInstance().getPreferenceStore();
        return preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_WINDOW_GENERATE);
    }

    /**
     * Gets the console line count.
     *
     * @return the console line count
     */
    public int getConsoleLineCount() {
        return validateConsoleLineCount(consoleLineCount);
    }

    /**
     * Sets the console line count.
     *
     * @param consoleLineCount the new console line count
     */
    public void setConsoleLineCount(int consoleLineCount) {
        this.consoleLineCount = consoleLineCount;
    }

    /**
     * gets the DefaultDatabaseType
     * 
     * @return defaultDatabaseType the default db type
     */
    public String getDefaultDatabaseType() {
        return defaultDatabaseType;
    }

    /**
     * sets the DefaultDatabaseType
     * 
     * @param defaultDatabaseType the default db type
     */
    public void setDefaultDatabaseType(String defaultDatabaseType) {
        this.defaultDatabaseType = defaultDatabaseType;
    }

    /**
     * Gets the enable permanent password save option.
     *
     * @return the enable permanent password save option
     */
    public boolean getEnablePermanentPasswordSaveOption() {
        return enablePermanentPasswordSaveOption;
    }

    /**
     * Sets the enable permanent password save option.
     *
     * @param enablePermanentPasswordSaveOption the new enable permanent
     * password save option
     */
    public void setEnablePermanentPasswordSaveOption(boolean enablePermanentPasswordSaveOption) {
        this.enablePermanentPasswordSaveOption = enablePermanentPasswordSaveOption;
    }

    /**
     * Gets the enable security warning option.
     *
     * @return the enable security warning option
     */
    public boolean getEnableSecurityWarningOption() {
        return enableSecurityWarningOption;
    }

    /**
     * Sets the enable security warning option.
     *
     * @param enableSecurityWarningOption the new enable security warning option
     */
    public void setEnableSecurityWarningOption(boolean enableSecurityWarningOption) {
        this.enableSecurityWarningOption = enableSecurityWarningOption;
    }

    /**
     * Validate console line count.
     *
     * @param lineCount the line count
     * @return the int
     */
    private int validateConsoleLineCount(int lineCount) {

        if (lineCount > MAX_CONSOLE_LINE_COUNT) {
            return MAX_CONSOLE_LINE_COUNT;
        } else if (lineCount <= 0) {
            return MIN_CONSOLE_LINE_COUNT;
        }
        return lineCount;
    }

    /**
     * Gets the batch msg size.
     *
     * @return the batch msg size
     */
    public int getBatchMsgSize() {
        return batchMsgSize;
    }

    /**
     * Checks if is focus on first result.
     *
     * @return true, if is focus on first result
     */
    public boolean isFocusOnFirstResult() {
        return focusOnFirstResult;
    }

    /**
     * Sets the focus on first result.
     *
     * @param focusOnFirstResult the new focus on first result
     */
    public void setFocusOnFirstResult(boolean focusOnFirstResult) {
        this.focusOnFirstResult = focusOnFirstResult;
    }

    /**
     * Checks if SSL is enabled
     *
     * @return true, if SSL is enabled
     */
    public boolean isSslEnable() {
        return sslEnable;
    }

    /**
     * Sets the SSL enable pref
     *
     * @param sslEnable the flag
     */
    public void setSslEnable(boolean sslEnable) {
        this.sslEnable = sslEnable;
    }

    /**
     * Gets the file encoding.
     *
     * @return the file encoding
     */
    public String getFileEncoding() {
        String fileEncodingPref = PreferenceWrapper.getInstance().getPreferenceStore()
                .getString(UserEncodingOption.FILE_ENCODING);
        if (!fileEncodingPref.isEmpty()) {
            fileEncoding = fileEncodingPref;
        }
        return fileEncoding;
    }

    /**
     * Checks if is login allowed on password expiry.
     *
     * @return true, if is login allowed on password expiry
     */
    @Override
    public boolean isLoginAllowedOnPasswordExpiry() {
        return isLogingAllowedOnPasswordExpiry;
    }

    /**
     * Sets the login allowed on password expiry.
     *
     * @param isLogginaAllowedOnPasswordExpiry the is loggina allowed on
     * password expiry
     * @return true, if successful
     */
    @Override
    public boolean setLoginAllowedOnPasswordExpiry(boolean isLogginaAllowedOnPasswordExpiry) {
        this.isLogingAllowedOnPasswordExpiry = isLogginaAllowedOnPasswordExpiry;
        return isLogginaAllowedOnPasswordExpiry;
    }

    /**
     * Checks if is commit valid rows.
     *
     * @return true, if is commit valid rows
     */
    @Override
    public boolean isCommitValidRows() {
        return isCommitValidRows;
    }

    /**
     * Sets the commit valid rows.
     *
     * @param isComitValidRows the is comit valid rows
     * @return true, if successful
     */
    @Override
    public boolean setCommitValidRows(boolean isComitValidRows) {
        this.isCommitValidRows = isComitValidRows;
        return isComitValidRows;
    }

    /**
     * Checks if is isenable testability.
     *
     * @return true, if is isenable testability
     */
    public boolean isIsenableTestability() {
        return isenableTestability;
    }

    /**
     * Sets the isenable testability.
     *
     * @param isenableTestability the new isenable testability
     */
    public void setIsenableTestability(boolean isenableTestability) {
        this.isenableTestability = isenableTestability;
    }

    /**
     * gets date and time format based on user pref
     * 
     * @return dateFormat the date format
     */
    public String getDateTimeFormat() {
        StringBuilder dateFormat = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        dateFormat.append(
                PreferenceWrapper.getInstance().getPreferenceStore().getString(MPPDBIDEConstants.DATE_FORMAT_VALUE));
        dateFormat.append(" "
                + PreferenceWrapper.getInstance().getPreferenceStore().getString(MPPDBIDEConstants.TIME_FORMAT_VALUE));
        return dateFormat.toString();
    }

}
