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

package org.opengauss.mppdbide.view.prefernces;

/**
 * Title: ISQLHistoryPreferencesLabelFactory
 * 
 * Description:A factory for creating ISQLHistoryPreferencesLabel objects.
 * 
 * @since 3.0.0
 */
public interface ISQLHistoryPreferencesLabelFactory {

    /**
     * The sql history size.
     */
    String SQL_HISTORY_SIZE = "sqlterminal.historysize";

    /**
     * The sql query length.
     */
    String SQL_QUERY_LENGTH = "sqlterminal.querylength";

    /**
     * The default sql query length.
     */
    int DEFAULT_SQL_QUERY_LENGTH = 1000;

    /**
     * The default sql history size.
     */
    int DEFAULT_SQL_HISTORY_SIZE = 50;

}
