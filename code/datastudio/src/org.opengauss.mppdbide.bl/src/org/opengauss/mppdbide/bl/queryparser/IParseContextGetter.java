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

package org.opengauss.mppdbide.bl.queryparser;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IParseContextGetter.
 * 
 * @since 3.0.0
 */
public interface IParseContextGetter {

    /**
     * Parses the query.
     *
     * @param query the query
     */
    public void parseQuery(String query);

    /**
     * Gets the parses the context.
     *
     * @return the parses the context
     */
    public ParseContext getParseContext();
}
