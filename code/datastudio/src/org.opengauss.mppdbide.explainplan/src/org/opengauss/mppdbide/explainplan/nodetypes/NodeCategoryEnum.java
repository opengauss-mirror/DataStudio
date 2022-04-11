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

package org.opengauss.mppdbide.explainplan.nodetypes;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum NodeCategoryEnum.
 *
 * @since 3.0.0
 */
public enum NodeCategoryEnum {
    AGGREGATE(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_AGGREGATE)),

    FUNCTION(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_FUNCTION)),

    GROUPAGGREGATE(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_GROUPAGGREGATE)),

    HASH(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_HASH)),

    HASHJOIN(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_HASHJOIN)),

    MODIFYTABLE(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_MODIFYTABLE)),

    NESTLOOPJOIN(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_NESTLOOPJOIN)),

    NESTEDLOOP(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_NESTEDLOOP)),

    PARTITIONITERATOR(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_PARTITIONITERATOR)),

    ROWADAPTER(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_ROWADAPTER)),

    SCAN(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_SCAN)),

    SETOP(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_SETOP)),

    SORT(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_SORT)),

    STREAM(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_STREAM)),

    UNKNOWN(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_UNKNOWN)),

    UNION(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_NODCATEGORY_UNION));

    private final String categoryName;

    private NodeCategoryEnum(String name) {
        this.categoryName = name;
    }

    /**
     * Gets the category name.
     *
     * @return the category name
     */
    public String getCategoryName() {
        return this.categoryName;
    }

}
