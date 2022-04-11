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

package org.opengauss.mppdbide.presentation.resultsetif;

import org.opengauss.mppdbide.presentation.resultset.ActionAfterResultFetch;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IResultConfig.
 * 
 * @since 3.0.0
 */
public interface IResultConfig {

    /**
     * Gets the fetch count.
     *
     * @return the fetch count
     */
    int getFetchCount();

    /**
     * Gets the action after fetch.
     *
     * @return the action after fetch
     */
    ActionAfterResultFetch getActionAfterFetch();
}
