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

package org.opengauss.mppdbide.gauss.format.processor.forloop;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.AbstractProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.loop.TForLoopSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: IfStmtFormatProcessor
 *
 * @since 3.0.0
 */
public class ForLoopFormatProcessor extends AbstractProcessor<TForLoopSqlStatement> {
    /**
     * Gets the options process data.
     *
     * @param selectAstNode the select ast node
     * @param pData the data
     * @param options the options
     * @return the options process data
     */
    public OptionsProcessData getOptionsProcessData(TForLoopSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {
        OptionsProcessData lOptionsProcessData = getNewOptionsDataBean(pData, 0);
        return lOptionsProcessData;
    }

    @Override
    public TParseTreeNode getStartNode(TForLoopSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }
}
