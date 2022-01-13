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

package com.huawei.mppdbide.gauss.format.processor.ifstmt.ast;

import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.IfStmtConditionListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineWithProcessFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.ifstmt.TIfElseASTNode;

/**
 * 
 * Title: IfStmtASTNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class IfStmtASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        TIfElseASTNode selectAstNode = (TIfElseASTNode) node;

        // add the listener for the if condition

        addFormatProcessListener(selectAstNode.getThen(), getThenProcessorListener());

        addFormatProcessListener(selectAstNode.getItemList(), new IfStmtConditionListFormatProcessorListener());

        addFormatProcessListener(selectAstNode.getEnd(), new NewlineWithProcessFormatProcessorListener());

        addFormatProcessListener(selectAstNode.getEndNode(), new AddEmptyPreTextFormatProcessorListener());

    }

    private IFormarProcessorListener getThenProcessorListener() {
        return this.getOptions().isThenOnNewLine() ? new NewlineWithProcessFormatProcessorListener()
                : new AddEmptyPreTextFormatProcessorListener();
    }

}
