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

package org.opengauss.mppdbide.view.ui.saveif;

/**
 * Title: DataModeSave for use
 * Description: the interface DataModeSave
 *
 * @since 3.0.0
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
