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
 * @since 3.0.0
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
