/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
