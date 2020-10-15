/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

/**
 * 
 * Title: class
 * 
 * Description: The Class DummyTerminalExecutionConnectionInfra.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DummyTerminalExecutionConnectionInfra extends TerminalExecutionConnectionInfra {
    @Override
    public boolean isConnected() {
        return true;
    }
}
