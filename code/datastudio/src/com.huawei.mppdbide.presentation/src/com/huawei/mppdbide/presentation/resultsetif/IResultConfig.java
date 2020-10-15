/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.resultsetif;

import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IResultConfig.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
