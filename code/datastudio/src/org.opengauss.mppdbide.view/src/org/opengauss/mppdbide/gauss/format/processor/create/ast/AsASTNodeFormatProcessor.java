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

package org.opengauss.mppdbide.gauss.format.processor.create.ast;

import org.opengauss.mppdbide.gauss.format.processor.listimpl.DeclareFieldsNewlineWithIndentFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.NewlineFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import org.opengauss.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.create.TAsASTNode;

/**
 * Title: LoopASTNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class AsASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        TAsASTNode lTAsASTNode = getAstNode(node);
        // add listener to the startnode

        if (ProcessorUtils.isNodeListAvailable(node.getItemList())) {
            addFormatProcessListener(node.getItemList(), new DeclareFieldsNewlineWithIndentFormatProcessorListener());
        }

        addFormatProcessListener(lTAsASTNode.getEndAs(), new NewlineFormatProcessorListener());

    }

    private TAsASTNode getAstNode(TBasicASTNode node) {
        if (!(node instanceof TAsASTNode)) {
            throw new GaussDBSQLParserException("Unable to Cast the AST Node");
        }
        return (TAsASTNode) node;
    }

}
