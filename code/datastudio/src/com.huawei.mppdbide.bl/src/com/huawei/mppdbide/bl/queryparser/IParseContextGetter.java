/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.queryparser;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IParseContextGetter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
