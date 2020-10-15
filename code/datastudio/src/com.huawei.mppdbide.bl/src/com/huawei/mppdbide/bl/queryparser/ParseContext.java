/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.queryparser;

import java.util.HashMap;
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class ParseContext.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
