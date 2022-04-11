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

package org.opengauss.mppdbide.gauss.format.processor.union;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.AbstractProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.common.TUnionSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: LoopStmtFormatProcessor
 *
 * @since 3.0.0
 */
public class UnionFormatProcessor extends AbstractProcessor<TUnionSqlStatement> {
    /**
     * return options of proceesed data
     * 
     * @param selectAstNode selected node to process
     * @param pData options data while process
     * @param options options for format
     * @return returns the options data
     */
    public OptionsProcessData getOptionsProcessData(TUnionSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {
        pData.setLastFormatType(SQLTokenConstants.T_SQL_KEYWORK_UNION);
        OptionsProcessData lOptionsProcessData = getNewOptionsDataBean(pData, 0);
        return lOptionsProcessData;
    }

    /**
     * return start node
     * 
     * @param selectAstNode custom select statement
     * @return returns the start node
     */
    @Override
    public TParseTreeNode getStartNode(TUnionSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }
}
