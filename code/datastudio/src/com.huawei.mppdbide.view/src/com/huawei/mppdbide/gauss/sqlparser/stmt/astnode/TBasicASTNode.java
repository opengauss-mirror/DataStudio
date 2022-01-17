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

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TBasicASTNode
 *
 * @since 3.0.0
 */
public abstract class TBasicASTNode extends TCustomASTNode {
    private TSqlNode keywordNode = null;

    private TParseTreeNodeList<?> itemList = null;

    public TSqlNode getKeywordNode() {
        return keywordNode;
    }

    public void setKeywordNode(TSqlNode keywordNode) {
        this.keywordNode = keywordNode;
        setPreviousObject(this.keywordNode);
    }

    @Override
    public TParseTreeNode getStartNode() {
        return keywordNode;
    }

    public TParseTreeNodeList<?> getItemList() {
        return itemList;
    }

    public void setItemList(TParseTreeNodeList<?> itemList) {
        this.itemList = itemList;
        setPreviousObject(this.itemList);
    }

}
