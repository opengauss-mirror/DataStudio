/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
