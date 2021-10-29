/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

import java.util.Arrays;
import java.util.List;

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
        return Arrays.asList("", "\u5217\u540d", "\u6570\u636e\u7c7b\u578b");
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
