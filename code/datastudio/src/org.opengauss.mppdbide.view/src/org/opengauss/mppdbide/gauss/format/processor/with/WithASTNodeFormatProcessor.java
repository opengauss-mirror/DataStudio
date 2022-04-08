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

package org.opengauss.mppdbide.gauss.format.processor.with;

import org.opengauss.mppdbide.gauss.format.processor.listimpl.ASTStartNodeNoNewlineFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.WithResultListFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;

/**
 * 
 * Title: WithASTNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class WithASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        addFormatProcessListener(node.getKeywordNode(), new ASTStartNodeNoNewlineFormatProcessorListener());

        addFormatProcessListener(node.getItemList(), new WithResultListFormatProcessorListener());

    }

}
