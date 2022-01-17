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

package com.huawei.mppdbide.view.view.createview;

/**
 * Title: class
 * Description: The Class WhereCondition.
 *
 * @since 3.0.0
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
