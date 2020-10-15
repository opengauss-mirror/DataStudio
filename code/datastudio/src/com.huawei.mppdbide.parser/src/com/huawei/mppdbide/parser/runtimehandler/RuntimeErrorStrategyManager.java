/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.parser.runtimehandler;

import org.antlr.v4.runtime.ANTLRErrorStrategy;

/**
 * Title: class Description: The Class RuntimeErrorStrategyManager. Copyright
 * (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class RuntimeErrorStrategyManager {

    /**
     * Gets the runtime errorhandler.
     *
     * @return the runtime errorhandler
     */
    public static ANTLRErrorStrategy getRuntimeErrorhandler() {
        return new RuntimeErrorStrategyImpl().getRuntimeErrorhandler();
    }
}
