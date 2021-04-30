/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

/**
 * Title: class
 * Description: The enum TriggerKeyword.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 2021-4-30]
 * @since 2021-4-30
 */
public enum TriggerKeyword {
    BEFORE("BEFORE"),
    AFTER("AFTER"),
    INSTEAD_OF("INSTEAD OF"),
    INSERT("INSERT"),
    DELETE("DELETE"),
    TRUNCATE("TRUNCATE"),
    UPDATE("UPDATE"),
    ROW("ROW"),
    STATEMENT("STATEMENT");

    /**
     * The keyword
     */
    public final String keyword;
    TriggerKeyword(String keyword) {
        this.keyword = keyword;
    }
}
