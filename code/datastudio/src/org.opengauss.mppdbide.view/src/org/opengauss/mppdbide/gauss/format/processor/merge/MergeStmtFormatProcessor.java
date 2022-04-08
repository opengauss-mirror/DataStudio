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

package org.opengauss.mppdbide.gauss.format.processor.merge;

import org.opengauss.mppdbide.gauss.format.consts.FormatItemsType;
import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.AbstractProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.dml.TMergeSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: WithStmtFormatProcessor
 *
 * @since 3.0.0
 */
public class MergeStmtFormatProcessor extends AbstractProcessor<TMergeSqlStatement> {
    /**
     * return options of proceesed data
     * 
     * @param selectAstNode selected node to process
     * @param pData options data while process
     * @param options options for format
     * @return returns the options data
     */
    public OptionsProcessData getOptionsProcessData(TMergeSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {
        int maxKeywordWidth = getMaxKeywordWidth(selectAstNode, options);

        OptionsProcessData lOptionsProcessData = super.getNewOptionsDataBean(pData, maxKeywordWidth);

        lOptionsProcessData.setFormatItemsType(FormatItemsType.UNKNOWN);
        return lOptionsProcessData;
    }

    /**
     * return start node
     * 
     * @param selectAstNode node to which startnode to be returned
     * @return return start node
     */
    @Override
    public TParseTreeNode getStartNode(TMergeSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }

    /**
     * return true is data is cloned
     * 
     * @return return true is data is cloned else false
     */
    public boolean isCloneData() {
        return true;
    }
}
