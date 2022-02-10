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

package org.opengauss.mppdbide.bl.serverdatacache;

import org.opengauss.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: ShowMoreObject
 * 
 * Description:
 * 
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
