/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.saveif;

/**
 * Title: DataModeSave for use
 * Description: the interface DataModeSave
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2021-06-02]
 * @since 2021-06-02
 */
public interface DataModeSave {
    /**
     * description: save dataModel
     *
     * @param String the unique id
     * @param Object the data model
     * @return String the saved string
     */
    String saveData(String id, Object dataModel);

    /**
     * description: load data model by unique id
     *
     * @param String the unique id which return by saveData
     * @param Class<T> the loaded data model class
     * @return <T> T the loaded data model object
     */
    <T> T loadData(String id, Class<T> clz);
}
