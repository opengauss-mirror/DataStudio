/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferenceStore;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLHistoryPreferences.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SQLHistoryPreferences implements ISQLHistoryPreferencesLabelFactory {

    /**
     * Sets the default for SQL preferences.
     *
     * @param ps the new default for SQL preferences
     */
    public static void setDefaultForSQLPreferences(PreferenceStore ps) {
        ps.setDefault(SQL_HISTORY_SIZE, DEFAULT_SQL_HISTORY_SIZE);
        ps.setDefault(SQL_QUERY_LENGTH, DEFAULT_SQL_QUERY_LENGTH);

    }
}
