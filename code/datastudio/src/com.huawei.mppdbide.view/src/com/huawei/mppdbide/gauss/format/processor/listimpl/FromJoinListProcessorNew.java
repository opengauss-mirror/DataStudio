/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.listimpl;

import com.huawei.mppdbide.gauss.format.processor.AbstractListProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.from.TFromItem;

/**
 * 
 * Title: FromJoinListProcessorNew
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class FromJoinListProcessorNew extends AbstractListProcessor {

    /**
     * Adds the list item listener.
     *
     * @param tAbstractListItem the t abstract list item
     */
    protected void addListItemListener(TAbstractListItem tAbstractListItem) {

        TFromItem fromItem = (TFromItem) tAbstractListItem;

        addFormatProcessListener(fromItem.getTable(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(fromItem.getEndNode(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(fromItem.getJoinType(), new NewlineWithParentOffsetFormatProcessorListener());

        addFormatProcessListener(fromItem.getJoinCondition(), new OnConditionListFormatProcessorListener());

    }

}
