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

package com.huawei.mppdbide.view.view.createview;

import java.util.List;

/**
 * Title: class
 * Description: The Class CreateViewDataModel.
 *
 * @since 3.0.0
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