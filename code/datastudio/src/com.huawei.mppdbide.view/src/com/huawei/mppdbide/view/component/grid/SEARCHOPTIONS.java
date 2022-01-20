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

package com.huawei.mppdbide.view.component.grid;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum SEARCHOPTIONS.
 *
 * @since 3.0.0
 */
public enum SEARCHOPTIONS {

    /**
     * The srch contains.
     */
    SRCH_CONTAINS(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OPT_CONTAINS)),

    /**
     * The srch equals.
     */
    SRCH_EQUALS(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OPT_EQUALS)),

    /**
     * The srch starts with.
     */
    SRCH_STARTS_WITH(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OPT_STARTS_WITH)),

    /**
     * The srch regex.
     */
    SRCH_REGEX(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OPT_REGEX)),

    /**
     * The srch null.
     */
    SRCH_NULL(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OPT_NULL));

    private final String displayName;

    private SEARCHOPTIONS(String name) {
        this.displayName = name;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return this.displayName;
    }
}
