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

package com.huawei.mppdbide.utils;

import java.util.ArrayList;

import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IQuerrySplitter.
 *
 * @since 3.0.0
 */
public interface IQuerrySplitter {

    /**
     * 
     * splitQuerries.
     *
     * @param queryArray the query array
     * @param query the query
     * @param isOLAP the is OLAP
     * @throws DatabaseOperationException the database operation exception
     */
    void splitQuerries(ArrayList<String> queryArray, String query, boolean isOLAP) throws DatabaseOperationException;

}
