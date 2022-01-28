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

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum ForeignKeyMatchType.
 * 
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
