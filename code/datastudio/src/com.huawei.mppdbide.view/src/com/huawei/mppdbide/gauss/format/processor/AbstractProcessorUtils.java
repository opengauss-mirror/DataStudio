/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: ProcessorUtils Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class AbstractProcessorUtils {

    /**
     * process parseTree node.
     *
     * @param nextNode the next node
     * @param options the options
     * @param clonedOptData the cloned opt data
     */
    public static void processParseTreeNode(TParseTreeNode nextNode, FmtOptionsIf options,
            OptionsProcessData clonedOptData) {
        processParseTreeNode(nextNode, options, clonedOptData, true);
    }

    /**
     * process parseTree node.
     *
     * @param nextNode the next node
     * @param options the options
     * @param clonedOptData the cloned opt data
     * @param addPreSpace the add pre space
     */
    public static void processParseTreeNode(TParseTreeNode nextNode, FmtOptionsIf options,
            OptionsProcessData clonedOptData, boolean addPreSpace) {
        AbstractProcessor<TParseTreeNode> processor = ProcessorFactory.getProcessor(nextNode);
        if (null != processor) {
            processor.setOptions(options);
            processor.setOptionsProcessData(clonedOptData);
            processor.beforeProcess(nextNode);
            processor.process(nextNode, options, clonedOptData, addPreSpace);
            processor.afterProcess(nextNode, clonedOptData);
        }
    }
}
