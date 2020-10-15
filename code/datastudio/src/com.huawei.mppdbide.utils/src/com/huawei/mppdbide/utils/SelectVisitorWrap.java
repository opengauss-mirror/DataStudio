/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;

/**
 * 
 * Title: class
 * 
 * Description: The Class SelectVisitorWrap.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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

}
