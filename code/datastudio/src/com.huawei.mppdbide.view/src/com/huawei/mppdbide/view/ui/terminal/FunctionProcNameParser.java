/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
