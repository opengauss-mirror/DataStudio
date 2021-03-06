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

package org.opengauss.mppdbide.view.ui.debug;

import java.util.List;

/**
 * Title: DebugTableEventHandler for use
 *
 * @since 3.0.0
 */
public interface DebugTableEventHandler {
    /**
     * description: if item clicked, this func will be called!
     *
     * @param selectItems the select item which is instanceof IDebugSourceData,
     * if no value, selectItems is empty not null
     * @param event the event
     */
    void selectHandler(List<IDebugSourceData> selectItems, DebugCheckboxEvent event);
}
