/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.view.ui.trigger;

import java.util.Arrays;
import java.util.List;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: Class
 * Description: the class CreateFunctionParamsTitle
 *
 * @since 3.0.0
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
