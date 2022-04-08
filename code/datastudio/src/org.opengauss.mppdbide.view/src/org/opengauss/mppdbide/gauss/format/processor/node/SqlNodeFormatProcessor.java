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

package org.opengauss.mppdbide.gauss.format.processor.node;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.AbstractProcessor;
import org.opengauss.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: SqlNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class SqlNodeFormatProcessor extends AbstractProcessor<TSqlNode> {

    @Override
    public void process(TSqlNode selectAstNode, FmtOptionsIf options, OptionsProcessData pData, boolean addPreSpace) {
        if (addPreSpace) {
            ProcessorUtils.addPreEmptyText(selectAstNode, pData);
        }
        pData.addOffSet(selectAstNode.getNodeText().length());
    }

    @Override
    public TParseTreeNode getStartNode(TSqlNode selectAstNode) {
        return selectAstNode.getStartNode();
    }

}
