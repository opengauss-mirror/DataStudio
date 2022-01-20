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

package com.huawei.mppdbide.gauss.format.processor.begin;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.AbstractProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.block.TDeclareSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * 
 * Title: LoopStmtFormatProcessor
 *
 * @since 3.0.0
 */
public class DeclareFormatProcessor extends AbstractProcessor<TDeclareSqlStatement> {

    /**
     * Gets the options process data.
     *
     * @param selectAstNode the select ast node
     * @param pData the data
     * @param options the options
     * @return the options process data
     */
    public OptionsProcessData getOptionsProcessData(TDeclareSqlStatement selectAstNode, OptionsProcessData pData,
            FmtOptionsIf options) {
        OptionsProcessData lOptionsProcessData = getNewOptionsDataBean(pData, 0);
        return lOptionsProcessData;
    }

    /**
     * Gets the start node.
     *
     * @param selectAstNode the select ast node
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode(TDeclareSqlStatement selectAstNode) {
        return selectAstNode.getStartNode();
    }

}
