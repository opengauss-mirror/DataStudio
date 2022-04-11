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

package org.opengauss.mppdbide.gauss.format.processor.select;

import org.opengauss.mppdbide.gauss.format.processor.listimpl.ResultListFormatProcessorListener;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;

/**
 * 
 * Title: GroupByASTNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class GroupByASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        node.getItemList().setFormatListener(new ResultListFormatProcessorListener());

    }

}
