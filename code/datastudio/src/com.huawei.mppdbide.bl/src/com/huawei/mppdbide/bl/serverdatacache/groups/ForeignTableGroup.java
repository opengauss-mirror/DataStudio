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

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ForeignTableGroup.
 * 
 */

public class ForeignTableGroup extends TableObjectGroup {

    /**
     * Instantiates a new foreign table group.
     *
     * @param type the type
     * @param nm the nm
     */
    public ForeignTableGroup(OBJECTTYPE type, Namespace nm) {
        super(type, nm);
    }

    /**
     * Gets the display label.
     *
     * @return the display label
     */
    public String getDisplayLabel() {
        return super.getDisplayLabel();
    }
}
