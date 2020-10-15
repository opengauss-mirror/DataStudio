/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Title: WithASTNodeFormatProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
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
