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

package com.huawei.mppdbide.view.objectpropertywiew;

import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectPropertyResultConfig.
 *
 * @since 3.0.0
 */
public class ObjectPropertyResultConfig implements IResultConfig {

    /**
     * Gets the fetch count.
     *
     * @return the fetch count
     */
    @Override
    public int getFetchCount() {
        // need to fetch all
        return 0;
    }

    /**
     * Gets the action after fetch.
     *
     * @return the action after fetch
     */
    @Override
    public ActionAfterResultFetch getActionAfterFetch() {
        return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
    }
}
