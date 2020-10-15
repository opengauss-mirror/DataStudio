/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.nodetypes;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum NodeCategoryEnum.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
