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

import com.huawei.mppdbide.gauss.format.processor.AbstractListProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.update.TUpdateSetValuesNodeItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;

/**
 * 
 * Title: UpdateSetValuesItemListProcessor
 *
 * @since 3.0.0
 */
public class UpdateSetValuesItemListProcessor extends AbstractListProcessor {

    /**
     * Adds the list item listener.
     *
     * @param tAbstractListItem the t abstract list item
     */
    protected void addListItemListener(TAbstractListItem tAbstractListItem) {

        TUpdateSetValuesNodeItem fromItem = (TUpdateSetValuesNodeItem) tAbstractListItem;

        addFormatProcessListener(fromItem.getLeftNodeItem(), new UpdateSetValueBeanFormatProcessorListener());

        addFormatProcessListener(fromItem.getRightNodeItem(), new UpdateSetValueBeanFormatProcessorListener());

        addFormatProcessListener(fromItem.getSeperator(), new NoPreTextFormatProcessorListener());

    }

}