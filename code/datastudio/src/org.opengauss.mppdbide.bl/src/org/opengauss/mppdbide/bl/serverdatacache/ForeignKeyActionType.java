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
 * Title: enum
 * 
 * Description: The Enum ForeignKeyActionType.
 */

public enum ForeignKeyActionType {
    FK_NO_ACTION(" ON UPDATE NO ACTION", " ON DELETE NO ACTION"),
    FK_RESTRICT(" ON UPDATE RESTRICT", " ON DELETE RESTRICT"), FK_CASCADE(" ON UPDATE CASCADE", " ON DELETE CASCADE"),
    FK_SET_NULL(" ON UPDATE SET NULL", " ON DELETE SET NULL"),
    FK_SET_DEFAULT(" ON UPDATE SET DEFAULT", " ON DELETE SET DEFAULT");

    private String updateLabel = "";
    private String deleteLabel = "";

    /**
     * Instantiates a new foreign key action type.
     *
     * @param updateLabel the update label
     * @param deleteLabel the delete label
     */
    private ForeignKeyActionType(String updateLabel, String deleteLabel) {
        this.updateLabel = updateLabel;
        this.deleteLabel = deleteLabel;
    }

    /**
     * Gets the update label.
     *
     * @return the update label
     */
    public String getUpdateLabel() {
        return this.updateLabel;
    }

    /**
     * Gets the delete label.
     *
     * @return the delete label
     */
    public String getDeleteLabel() {
        return this.deleteLabel;
    }
}
