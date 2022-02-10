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

package org.opengauss.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class Alias.
 * 
 */

public class Alias extends ServerObject {

    /**
     * Instantiates a new alias.
     *
     * @param name the name
     * @param type the type
     */
    public Alias(String name, OBJECTTYPE type) {
        super(-1, name, type, false);
    }

    @Override
    public String getAutoSuggestionName(boolean isAutoSuggest) {
        return super.getQualifiedObjectNameHandleQuotes(this.getName());
    }

}