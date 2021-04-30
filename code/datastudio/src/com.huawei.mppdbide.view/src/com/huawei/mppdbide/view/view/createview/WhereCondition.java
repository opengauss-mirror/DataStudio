/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021. All rights reserved.
 */

package com.huawei.mppdbide.view.view.createview;

/**
 * Title: class
 * Description: The Class WhereCondition.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021.
 *
 * @version [DataStudio 2.1.0, 21 Oct., 2021]
 * @since 21 Oct., 2021
 */
public class WhereCondition {
    private String leftExpression;
    private String rightExpression;

    public WhereCondition (String leftExpression, String rightExpression) {
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    /**
     * Gets left expression
     *
     * @return String the left expression
     */
    public String getLeftExpression () {
        return leftExpression;
    }

    /**
     * Gets right expression
     *
     * @return String the right expression
     */
    public String getRightExcepssion () {
        return rightExpression;
    }
}
