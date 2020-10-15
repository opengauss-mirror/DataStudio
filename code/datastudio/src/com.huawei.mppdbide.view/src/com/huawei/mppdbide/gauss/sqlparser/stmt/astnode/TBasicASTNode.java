/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.astnode;

import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: TBasicASTNode
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
