/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

import java.util.Arrays;
import java.util.List;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: Class
 * Description: the class CreateFunctionParamsTitle
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [DataStudio for openGauss 2021-04-25]
 * @since 2021-04-25
 */
public class CreateTriggerParamsTitle {
    private static final int[] WIDTHS = new int[] {30, 200, 200};

    /**
     * Gets titles
     *
     * @return List<String> the title string list
     */
    public List<String> getTitles() {
        return Arrays.asList("",
                MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_COLUMN_NAME),
                MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_DATA_TYPE));
    }

    /**
     * Gets the width
     *
     * @param int the index
     * @return int the width
     */
    public int getWidth(int index) {
        return WIDTHS[index];
    }

    /**
     * Gets the scales
     *
     * @return List<Integer> the scale list
     */
    public List<Integer> getScales() {
        return Arrays.asList(1, 5, 5);
    }
}
