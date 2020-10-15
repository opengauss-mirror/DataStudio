/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum ForeignKeyActionType.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
