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

package com.huawei.mppdbide.utils;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.values.ValuesStatement;

/**
 * 
 * Title: class
 * 
 * Description: The Class SelectVisitorWrap.
 *
 * @since 3.0.0
 */
public class SelectVisitorWrap implements SelectVisitor {

    private boolean hasSetOperations = false;

    /**
     * Checks for set operations.
     *
     * @return true, if successful
     */
    public boolean hasSetOperations() {
        return this.hasSetOperations;
    }

    @Override
    public void visit(PlainSelect arg0) {
    }

    @Override
    public void visit(SetOperationList arg0) {
        this.hasSetOperations = true;
    }

    @Override
    public void visit(WithItem arg0) {
    }

    @Override
    public void visit(ValuesStatement aThis) {
    }
}
