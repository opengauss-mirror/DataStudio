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

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;

/**
 * Title: ProcessorUtils
 *
 * @since 3.0.0
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
