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

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.WithColumnlistCommaProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TResultColumnList;

/**
 * 
 * Title: WithResultListFormatProcessorListener
 *
 * @since 3.0.0
 */
public class WithResultListFormatProcessorListener extends ResultListFormatProcessorListener {

    @Override
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {

        TResultColumnList expressionList = (TResultColumnList) nextNode;

        // add the expressionList

        WithColumnlistCommaProcessor lColumnlistCommaProcessor = new WithColumnlistCommaProcessor();
        lColumnlistCommaProcessor.setOptions(options);
        lColumnlistCommaProcessor.setAddPreSpace(isAddPreSpace());
        lColumnlistCommaProcessor.process(expressionList, options, pData);

    }

}
