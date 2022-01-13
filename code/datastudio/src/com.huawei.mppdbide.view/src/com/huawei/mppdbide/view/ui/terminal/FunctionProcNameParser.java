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

package com.huawei.mppdbide.view.ui.terminal;

import java.util.List;

import com.huawei.mppdbide.editor.extension.nameparser.ParseObjectNameMain;

/**
 * 
 * Title: class
 * 
 * Description: The Class FunctionProcNameParser.
 *
 * @since 3.0.0
 */
public class FunctionProcNameParser {
    private String query;

    /**
     * The parser.
     */
    ParseObjectNameMain parser = new ParseObjectNameMain();

    /**
     * Instantiates a new function proc name parser.
     *
     * @param query the query
     */
    public FunctionProcNameParser(String query) {
        this.query = query;
    }

    /**
     * Do parse.
     */
    public void doParse() {
        parser.parsename(query);
    }

    /**
     * Gets the object type.
     *
     * @return the object type
     */
    public String getObjectType() {

        return parser.getObjectType();
    }

    /**
     * Gets the object name.
     *
     * @return the object name
     */
    public String getObjectName() {

        return parser.getObjectName();
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name
     */
    public String getSchemaName() {

        return parser.getSchemaName();
    }

    /**
     * Gets the func name.
     *
     * @return the func name
     */
    public String getFuncName() {

        return parser.getFuncName();
    }

    /**
     * Gets the args.
     *
     * @return the args
     */
    public List<String[]> getArgs() {

        return parser.getArgs();
    }

    /**
     * Gets the ret type.
     *
     * @return the ret type
     */
    public String getRetType() {

        return parser.getRetType();
    }
}
