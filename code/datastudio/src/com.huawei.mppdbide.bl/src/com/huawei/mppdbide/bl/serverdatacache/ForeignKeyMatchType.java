/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum ForeignKeyMatchType.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public enum ForeignKeyMatchType {
    FK_MATCH_DEFAULT(""), FK_MATCH_FULL(" MATCH FULL"), FK_MATCH_PARTIAL(" MATCH PARTIAL"),
    FK_MATCH_SIMPLE(" MATCH SIMPLE");

    private String label = "";

    /**
     * Instantiates a new foreign key match type.
     *
     * @param label the label
     */
    private ForeignKeyMatchType(String label) {
        this.label = label;
    }

    /**
     * Gets the label.
     *
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }
}
