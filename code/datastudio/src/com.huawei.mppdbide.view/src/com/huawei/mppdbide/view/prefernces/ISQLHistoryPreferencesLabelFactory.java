/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

/**
 * Title: ISQLHistoryPreferencesLabelFactory
 * 
 * Description:A factory for creating ISQLHistoryPreferencesLabel objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 21-May-2019]
 * @since 21-May-2019
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
