/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021. All rights reserved.
 */

package com.huawei.mppdbide.view.view.createview;

import java.util.List;

/**
 * Title: class
 * Description: The Class CreateViewDataModel.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021.
 *
 * @version [DataStudio 2.1.0, 21 Oct., 2021]
 * @since 21 Oct., 2021
 */
public class CreateViewDataModel {
    private List<ViewBody> viewBodyList;
    private List<TableAlias> tableAliasList;
    private List<WhereCondition> whereConditionList;

    public CreateViewDataModel (List<ViewBody> viewBodyList, List<TableAlias> tableAliasList,
            List<WhereCondition> whereConditionList) {
        this.viewBodyList = viewBodyList;
        this.tableAliasList = tableAliasList;
        this.whereConditionList = whereConditionList;
    }

    /**
     * Gets view body list
     *
     * @return List<ViewBody> the view body list
     */
    public List<ViewBody> getViewBodyList () {
        return viewBodyList;
    }

    /**
     * Gets table alias list
     *
     * @return List<TableAlias> the table list alias
     */
    public List<TableAlias> getTableAliasList () {
        return tableAliasList;
    }

    /**
     * Gets where condition list
     *
     * @return List<WhereCondition> the where condition list
     */
    public List<WhereCondition> getWhereConditionList () {
        return whereConditionList;
    }
}