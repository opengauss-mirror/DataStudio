/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.resultset;

import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;

/**
 * Title: class Description: The Class ConsoleDataWrapper. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ConsoleDataWrapper extends ArrayList<String> implements IConsoleResult {

    private static final long serialVersionUID = 1L;

    private List<String> hintMessage = new ArrayList<>(1);

    /**
     * Instantiates a new console data wrapper.
     */
    public ConsoleDataWrapper() {
        super();
    }

    /**
     * get the list of hint messages
     * 
     * @return return the hint message list
     */
    @Override
    public List<String> getHintMessages() {
        return hintMessage;
    }

}
