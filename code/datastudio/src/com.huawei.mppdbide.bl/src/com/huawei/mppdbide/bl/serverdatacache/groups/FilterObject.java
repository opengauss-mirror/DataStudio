/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

/**
 * Title: ObjectBrowserFilter
 * 
 * Description:ObjectBrowserFilter
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author pWX759367
 * @version [DataStudio 6.5.1, 12-Sep-2019]
 * @since 12-Sep-2019
 */

public class FilterObject {

    private static FilterObject filter_instance = new FilterObject();
    private String filterText = null;

    /**
     * Instantiates a new filter object.
     */
    private FilterObject() {
    }

    /**
     * Gets the single instance of FilterObject.
     *
     * @return single instance of FilterObject
     */
    public static FilterObject getInstance() {
        return filter_instance;
    }

    /**
     * Sets the filter text.
     *
     * @param filterText the new filter text
     */
    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }

    /**
     * Gets the filter text.
     *
     * @return the filter text
     */
    public String getFilterText() {
        return filterText;
    }
}
