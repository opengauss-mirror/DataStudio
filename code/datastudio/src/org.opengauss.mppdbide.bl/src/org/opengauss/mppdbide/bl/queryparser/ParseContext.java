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

import java.util.HashMap;
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class ParseContext.
 * 
 */

public class ParseContext {

    private HashMap<String, List<String>> aliasToTableNameMap;

    /**
     * Instantiates a new parses the context.
     */
    public ParseContext() {
        aliasToTableNameMap = new HashMap<String, List<String>>(1);
    }

    /**
     * Gets the alias to table name map.
     *
     * @return the alias to table name map
     */
    public HashMap<String, List<String>> getAliasToTableNameMap() {
        return this.aliasToTableNameMap;
    }
}
