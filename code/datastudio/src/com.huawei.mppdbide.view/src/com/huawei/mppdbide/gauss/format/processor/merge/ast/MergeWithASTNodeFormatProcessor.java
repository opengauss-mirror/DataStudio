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

package com.huawei.mppdbide.gauss.format.processor.merge.ast;

import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineWithIndentFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.listimpl.NewlineWithProcessFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.merge.TMergeWhenASTNode;

/**
 * Title: WithASTNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class MergeWithASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {
    /**
     * adds listeners to the node before process
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode

        TMergeWhenASTNode mergeNode = getMergeWhenASTNode(node);

        addFormatProcessListener(mergeNode.getWhenMatch(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(mergeNode.getMatchDML(), new NewlineWithIndentFormatProcessorListener() {
            /**
             * options data to change
             */
            protected void changeOptionsData(OptionsProcessData pData) {
                pData.setOffSet(pData.getParentOffSet());
            }
        });

        addFormatProcessListener(mergeNode.getWhenNotMatch(), new NewlineWithProcessFormatProcessorListener());

        addFormatProcessListener(mergeNode.getInsertDML(), new NewlineWithIndentFormatProcessorListener() {
            /**
             * options data to change
             */
            protected void changeOptionsData(OptionsProcessData pData) {
                pData.setOffSet(pData.getParentOffSet());
            }
        });
    }

    private TMergeWhenASTNode getMergeWhenASTNode(TBasicASTNode node) {
        if (node instanceof TMergeWhenASTNode) {
            return (TMergeWhenASTNode) node;
        }
        throw new GaussDBSQLParserException("Unable to typecase the statement");
    }
}
