/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.aliasparser;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum AliasRequestResponsePacketState. State machine for
 * Alias parser packet INIT : Packet initialized REQUEST : Packet in alias
 * parser job queue RESPONSE : Parsing request processed
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public enum AliasRequestResponsePacketState {

    /**
     * The init.
     */
    INIT,
    /**
     * The request.
     */
    REQUEST,
    /**
     * The response.
     */
    RESPONSE
}
