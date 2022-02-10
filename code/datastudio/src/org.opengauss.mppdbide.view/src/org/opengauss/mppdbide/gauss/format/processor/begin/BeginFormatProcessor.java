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

package org.opengauss.mppdbide.gauss.format.processor.begin;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.AbstractProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.block.TBeginSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: LoopStmtFormatProcessor
 *
 * @since 3.0.0
 */
public class BeginFormatProcessor extends AbstractProcessor<TBeginSqlStatement> {

    /**
     * return option processData
     */
    public OptionsProcessData getOptionsProcessData(TBeginSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {
        OptionsProcessData lOptionsProcessData = getNewOptionsDataBean(pData, 0);
        return lOptionsProcessData;
    }

    /**
     * return start node
     */
    @Override
    public TParseTreeNode getStartNode(TBeginSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }

    /**
     * return true if data is cloned
     */
    public boolean isCloneData() {
        return true;
    }

}
