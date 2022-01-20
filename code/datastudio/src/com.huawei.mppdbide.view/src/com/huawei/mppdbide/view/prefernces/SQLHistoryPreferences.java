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

package com.huawei.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferenceStore;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLHistoryPreferences.
 *
 * @since 3.0.0
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
