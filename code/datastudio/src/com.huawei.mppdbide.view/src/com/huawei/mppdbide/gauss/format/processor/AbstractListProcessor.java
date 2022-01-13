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
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;

/**
 * Title: AbstractListProcessor
 *
 * @since 3.0.0
 */
public class AbstractListProcessor extends AbstractProcessor<TParseTreeNodeList<?>> {

    /**
     * process
     */
    public void process(TParseTreeNodeList<?> columns, FmtOptionsIf options, OptionsProcessData pData) {
        // in one line if possible need to find the
        OptionsProcessData listParentData = pData.clone();

        listParentData.setParentData(pData);

        // for single line
        int itemsSize = columns.getResultList().size();

        // for fit layout need to maintain running count

        for (int itemIndex = 0; itemIndex < itemsSize; itemIndex++) {

            beforeItemProcess(pData, listParentData);

            TAbstractListItem tAbstractListItem = (TAbstractListItem) columns.getResultList().get(itemIndex);

            addListItemListener(tAbstractListItem);

            TParseTreeNode startNode = tAbstractListItem.getStartNode();

            iterateAndParseNodes(options, listParentData, startNode);

        }

        // set the current run data to parent from child
        pData.setOffSet(listParentData.getOffSet());

    }

    /**
     * process before item
     * 
     * @param pData options data to process
     * @param currentData current options data to process
     */
    protected void beforeItemProcess(OptionsProcessData pData, OptionsProcessData currentData) {

    }

    /**
     * add listener
     * 
     * @param tAbstractListItem list item to which listeners to be added
     */
    protected void addListItemListener(TAbstractListItem tAbstractListItem) {

    }

    @Override
    public TParseTreeNode getStartNode(TParseTreeNodeList<?> selectAstNode) {
        return null;
    }

}
