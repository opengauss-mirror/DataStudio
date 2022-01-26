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

package com.huawei.mppdbide.gauss.sqlparser.bean.tokendata;

/**
 * Title: SQLTokenFactory
 *
 * @since 3.0.0
 */
public abstract class SQLTokenFactory {
    /**
     * factory method to return the token data
     * 
     * @return the sql token data
     */
    public static ISQLTokenData getTokenData() {
        return new SQLTokenData();
    }
}
