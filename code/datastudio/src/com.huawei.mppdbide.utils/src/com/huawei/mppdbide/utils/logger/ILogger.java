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

package com.huawei.mppdbide.utils.logger;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ILogger.
 *
 * @since 3.0.0
 */
public interface ILogger {

    String PERF_CON = "CON";
    String PERF_DISCON = "DISCON";
    String PERF_OBJBRWSR_POPULATE = "PERF_OBJBRWSR_POPULATE";
    String PERF_SYNC_ACK = "SYNC_ACK";
    String PERF_EXECUTE_DBOBJECT = "EXEC_DBOBJECT";
    String PERF_DISPLAY_RESULTS = "DISPLAY_RESULTS";
    String PERF_EXECUTE_SQLTERMINAL_QUERY = "EXEC_SQLTRMINLQUERY";
    String PERF_FETCH_SRC_CODE = "FETCH_SRC_CODE";
    String PERF_REFRESH_CONNPROF = "REFRESH_CONNPROF";
    String PERF_REFRESH_NAMESPACE = "REFRESH_NAMESPACE";
    String PERF_REFRESH_OBJECT_GROUP = "REFRESH_OBJECT_GROUP";
    String PERF_REFRESH_OBJECT = "REFRESH_OBJECT";
    String PERF_ADVANCED_SEARCH = "ADVANCED_SEARCH";
    String PERF_EXECUTE_STMT = "EXECUTE_STATEMENT";

}
