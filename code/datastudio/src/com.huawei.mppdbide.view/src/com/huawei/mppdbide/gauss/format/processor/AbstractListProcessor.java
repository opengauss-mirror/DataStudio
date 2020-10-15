/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;

/**
 * Title: AbstractListProcessor Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
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
