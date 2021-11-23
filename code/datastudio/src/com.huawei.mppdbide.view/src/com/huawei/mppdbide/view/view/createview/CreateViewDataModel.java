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
    private boolean isMaterview;
    private boolean isCaseSensitive;
    private List<ViewBody> viewBodyList;
    private List<TableAlias> tableAliasList;
    private List<WhereCondition> whereConditionList;

    public CreateViewDataModel (boolean isMaterview,
            boolean isCaseSensitive,
            List<ViewBody> viewBodyList,
            List<TableAlias> tableAliasList,
            List<WhereCondition> whereConditionList) {
        this.isMaterview = isMaterview;
        this.isCaseSensitive = isCaseSensitive;
        this.viewBodyList = viewBodyList;
        this.tableAliasList = tableAliasList;
        this.whereConditionList = whereConditionList;
    }

    /**
     * Gets if is materview
     *
     * @return boolean if is materview
     */
    public boolean getIsMaterview() {
        return isMaterview;
    }

    /**
     * Gets if is case sensitive
     *
     * @return boolean if is sensitive
     */
    public boolean getIsCaseSensitive() {
        return isCaseSensitive;
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