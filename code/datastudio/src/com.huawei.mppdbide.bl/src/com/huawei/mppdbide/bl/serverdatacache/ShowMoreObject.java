/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: ShowMoreObject
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 25-Oct-2019]
 * @since 25-Oct-2019
 */

public class ShowMoreObject extends ServerObject {

    private ObjectGroup<?> group;

    /**
     * Instantiates a new show more object.
     *
     * @param type the type
     * @param group the group
     */
    public ShowMoreObject(OBJECTTYPE type, ObjectGroup<?> group) {
        super(type);
        this.group = group;
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    @Override
    public Object[] getChildren() {
        return new Object[0];
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    @Override
    public Object getParent() {
        return this.group;
    }

    /**
     * Gets the display label.
     *
     * @return the display label
     */
    @Override
    public String getDisplayLabel() {
        return MessageConfigLoader.getProperty(IMessagesConstants.SHOW_MORE_OBJECTS);
    }

    /**
     * Render next batch of objects onto object browser
     */
    public void showNextBatch() {
        group.incrementShowObjectCount();
    }
}
