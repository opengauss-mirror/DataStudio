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
import com.huawei.mppdbide.gauss.format.processor.ColumnlistCommaProcessor;
import com.huawei.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.from.TFromItemList;

/**
 * 
 * Title: FromListFormatProcessorListener
 *
 * @since 3.0.0
 */
public class FromListFormatProcessorListener implements IFormarProcessorListener {

    @Override
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {

        TFromItemList fromItemList = (TFromItemList) nextNode;

        // add the expressionList

        if (fromItemList.isJoinStmt()) {
            FromJoinListProcessorNew lFromJoinListProcessor = new FromJoinListProcessorNew();
            lFromJoinListProcessor.setOptions(options);
            lFromJoinListProcessor.process(fromItemList, options, pData);
        } else {
            ColumnlistCommaProcessor lColumnlistCommaProcessor = new ColumnlistCommaProcessor();
            lColumnlistCommaProcessor.setOptions(options);
            lColumnlistCommaProcessor.process((TParseTreeNodeList) fromItemList, options, pData);
        }

    }

}
