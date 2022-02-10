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

package org.opengauss.mppdbide.gauss.format.processor.update;

import org.opengauss.mppdbide.gauss.format.consts.FormatItemsType;
import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.AbstractProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.dml.TUpdateSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: UpdateStmtFormatProcessor
 *
 * @since 3.0.0
 */
public class UpdateStmtFormatProcessor extends AbstractProcessor<TUpdateSqlStatement> {

    /**
     * Gets the options process data.
     *
     * @param selectAstNode the select ast node
     * @param pData the data
     * @param options the options
     * @return the options process data
     */
    public OptionsProcessData getOptionsProcessData(TUpdateSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {

        int maxKeywordWidth = getMaxKeywordWidth(selectAstNode, options);

        OptionsProcessData lOptionsProcessData = getNewOptionsDataBean(pData, maxKeywordWidth);

        lOptionsProcessData.setFormatItemsType(FormatItemsType.UPDATE);

        return lOptionsProcessData;
    }

    /**
     * Gets the start node.
     *
     * @param selectAstNode the select ast node
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode(TUpdateSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }

    /**
     * Checks if is clone data.
     *
     * @return true, if is clone data
     */
    public boolean isCloneData() {
        return true;
    }

}
