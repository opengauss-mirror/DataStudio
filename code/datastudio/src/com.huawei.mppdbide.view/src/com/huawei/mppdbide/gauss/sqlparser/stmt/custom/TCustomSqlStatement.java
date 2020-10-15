/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.custom;

import java.util.LinkedHashMap;
import java.util.Map;

import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TCustomASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmtbeanif.StatementBeanIf;

/**
 * 
 * Title: TCustomSqlStatement
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
public abstract class TCustomSqlStatement extends TParseTreeNode implements StatementBeanIf {
    /**
     *  The ast node map. 
     */
    protected Map<String, TCustomASTNode> astNodeMap = new LinkedHashMap<String, TCustomASTNode>();

    /**
     * Adds the ast node.
     *
     * @param tokenKeyword the token keyword
     * @param astNode the ast node
     */
    public void addAstNode(String tokenKeyword, TCustomASTNode astNode) {
        astNodeMap.put(tokenKeyword, astNode);
    }

    /**
     * Gets the custom ast node.
     *
     * @param key the key
     * @return the custom ast node
     */
    public TCustomASTNode getCustomAstNode(String key) {
        return astNodeMap.get(key);
    }

    /**
     * Gets the start node.
     *
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode() {
        if (astNodeMap.isEmpty()) {
            return null;
        }
        return astNodeMap.entrySet().iterator().next().getValue();
    }

}
