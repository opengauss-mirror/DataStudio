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

package com.huawei.mppdbide.view.component.grid;

import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDataGridContext.
 *
 * @since 3.0.0
 */
public interface IDataGridContext {

    /**
     * Gets the data provider.
     *
     * @return the data provider
     */
    IDSGridDataProvider getDataProvider();

    /**
     * Gets the column meta data list.
     *
     * @return the column meta data list
     */
    List<ColumnMetaData> getColumnMetaDataList();

    /**
     * Sets the data provider.
     *
     * @param dataprovider the new data provider
     */
    void setDataProvider(IDSGridDataProvider dataprovider);

    /**
     *  the onPreDestroy
     */
    void onPreDestroy();
}
