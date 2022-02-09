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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.node;

/**
 * 
 * Title: TColumnListWithBracket
 *
 * @since 3.0.0
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
