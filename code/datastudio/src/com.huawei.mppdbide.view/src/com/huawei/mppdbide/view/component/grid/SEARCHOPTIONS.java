/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
