/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.resultsetif;

import java.util.List;

/**
 * Title: interface Description: The Interface IConsoleResult. Copyright (c)
 * Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IConsoleResult extends List<String> {

    /**
     * get the list of hint messages
     * 
     * @return return the hint message list
     */
    List<String> getHintMessages();

}
