/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IUserPreference.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IUserPreference {

    /**
     * The max resultset fetch count.
     */
    int MAX_RESULTSET_FETCH_COUNT = 5000;

    /**
     * The max console line count.
     */
    int MAX_CONSOLE_LINE_COUNT = 5000;

    /**
     * The edit max resultset fetch count.
     */
    int EDIT_MAX_RESULTSET_FETCH_COUNT = 5000;

    /**
     * The min console line count.
     */
    int MIN_CONSOLE_LINE_COUNT = 1000;

    /**
     * The current line visibility.
     */
    String CURRENT_LINE_VISIBILITY = "CURRENT_LINE_VISIBILITY";

    /**
     * The currentline color.
     */
    String CURRENTLINE_COLOR = "CURRENTLINE_COLOR";

    /**
     * The breakpoint enable key.
     */
    String BREAKPOINT_ENABLE_KEY = "BREAKPOINT_TEXT_KEY";

    /**
     * The breakpoint overview key.
     */
    String BREAKPOINT_OVERVIEW_KEY = "BREAKPOINT_OVERVIEW_KEY";

    /**
     * The breakpoint color.
     */
    String BREAKPOINT_COLOR = "BREAKPOINT_COLOR";

    /**
     * The highlight preference.
     */
    String HIGHLIGHT_PREFERENCE = "HIGHLIGHT_PREFERENCE";

    /**
     * The debugposition enable key.
     */
    String DEBUGPOSITION_ENABLE_KEY = "DEBUGPOSITION_TEXT_KEY";

    /**
     * The debugposition overview key.
     */
    String DEBUGPOSITION_OVERVIEW_KEY = "DEBUGPOSITION_OVERVIEW_KEY";

    /**
     * The debugposition color.
     */
    String DEBUGPOSITION_COLOR = "DEBUGPOSITION_COLOR";

    /**
     * Gets the prefernce store.
     *
     * @return the prefernce store
     */
    IPreferenceStore getPrefernceStore();

    /**
     * Gets the edits the result set fetch count.
     *
     * @return the edits the result set fetch count
     */
    int getEditResultSetFetchCount();

    /**
     * Sets the edits the result set fetch count.
     *
     * @param editResultSetFetchCount the new edits the result set fetch count
     */
    void setEditResultSetFetchCount(int editResultSetFetchCount);

    /**
     * Gets the result data fetch count.
     *
     * @return the result data fetch count
     */
    int getResultDataFetchCount();

    /**
     * Gets the pref time out.
     *
     * @return the pref time out
     */
    int getPrefTimeOut();

    /**
     * Checks if is generate new result window.
     *
     * @return true, if is generate new result window
     */
    boolean isGenerateNewResultWindow();

    /**
     * Gets the console line count.
     *
     * @return the console line count
     */
    int getConsoleLineCount();

    /**
     * Checks if is ssl enabled
     *
     * @return true, if is ssl enabled
     */
    boolean isSslEnable();

    /**
     * Gets the DefaultDatabaseType
     *
     * @return the DefaultDatabaseType
     */
    String getDefaultDatabaseType();

    /**
     * Gets the enable permanent password save option.
     *
     * @return the enable permanent password save option
     */
    boolean getEnablePermanentPasswordSaveOption();

    /**
     * Gets the enable security warning option.
     *
     * @return the enable security warning option
     */
    boolean getEnableSecurityWarningOption();

    /**
     * Checks if is focus on first result.
     *
     * @return true, if is focus on first result
     */
    boolean isFocusOnFirstResult();

    /**
     * Gets the batch msg size.
     *
     * @return the batch msg size
     */
    int getBatchMsgSize();

    /**
     * Gets the file encoding.
     *
     * @return the file encoding
     */
    String getFileEncoding();

    /**
     * Checks if is login allowed on password expiry.
     *
     * @return true, if is login allowed on password expiry
     */
    boolean isLoginAllowedOnPasswordExpiry();

    /**
     * Sets the login allowed on password expiry.
     *
     * @param isloginAllowedOnPasswordExpiry the islogin allowed on password
     * expiry
     * @return true, if successful
     */
    boolean setLoginAllowedOnPasswordExpiry(boolean isloginAllowedOnPasswordExpiry);

    /**
     * Checks if is commit valid rows.
     *
     * @return true, if is commit valid rows
     */
    boolean isCommitValidRows();

    /**
     * Sets the commit valid rows.
     *
     * @param isCommitValidRows the is commit valid rows
     * @return true, if successful
     */
    boolean setCommitValidRows(boolean isCommitValidRows);
}
