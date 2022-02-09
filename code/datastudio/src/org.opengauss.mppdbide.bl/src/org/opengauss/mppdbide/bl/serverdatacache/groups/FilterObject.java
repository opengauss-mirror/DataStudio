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

package org.opengauss.mppdbide.bl.serverdatacache.groups;

/**
 * Title: ObjectBrowserFilter
 * 
 * Description:ObjectBrowserFilter
 * 
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
