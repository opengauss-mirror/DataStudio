/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TColumnListWithBracket
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public class TColumnListWithBracket extends TListItem {

    private TSqlNode openColumnListBracket = null;
    private TResultColumnList tableColumns = null;
    private TSqlNode closeColumnListBracket = null;

    public TSqlNode getOpenColumnListBracket() {
        return openColumnListBracket;
    }

    public void setOpenColumnListBracket(TSqlNode openBracket) {
        this.openColumnListBracket = openBracket;
    }

    public TResultColumnList getTableColumns() {
        return tableColumns;
    }

    public void setTableColumns(TResultColumnList tableColumns) {
        this.tableColumns = tableColumns;
    }

    public TSqlNode getCloseColumnListBracket() {
        return closeColumnListBracket;
    }

    public void setCloseColumnListBracket(TSqlNode closeBracket) {
        this.closeColumnListBracket = closeBracket;
    }

    @Override
    public TParseTreeNode getStartNode() {
        return openColumnListBracket;
    }

    @Override
    public TSqlNode getSeperator() {
        return null;
    }

    @Override
    public TSqlNode getEndNode() {
        return null;
    }

    @Override
    public TSqlNode getAs() {
        return null;
    }

    @Override
    public TParseTreeNode getItemListNode() {
        return null;
    }
}
