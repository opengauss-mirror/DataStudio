/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class Alias.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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