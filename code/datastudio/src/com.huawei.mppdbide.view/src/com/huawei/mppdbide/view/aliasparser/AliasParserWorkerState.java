/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.aliasparser;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum AliasParserWorkerState. State machine for Alias parser
 * worker IDLE : Serving no job BUSY : Processing a parsing request
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public enum AliasParserWorkerState {

    /**
     * The idle.
     */
    IDLE,
    /**
     * The busy.
     */
    BUSY
}
