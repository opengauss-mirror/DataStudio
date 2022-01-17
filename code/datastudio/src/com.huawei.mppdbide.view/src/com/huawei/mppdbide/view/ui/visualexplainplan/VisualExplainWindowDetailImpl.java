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

package com.huawei.mppdbide.view.ui.visualexplainplan;

import com.huawei.mppdbide.presentation.IWindowDetail;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainWindowDetailImpl.
 *
 * @since 3.0.0
 */
public class VisualExplainWindowDetailImpl implements IWindowDetail {
    private VisualExplainPlanWindowDetailsEnum windowType;

    /**
     * Instantiates a new visual explain window detail impl.
     *
     * @param winType the win type
     */
    public VisualExplainWindowDetailImpl(VisualExplainPlanWindowDetailsEnum winType) {
        this.windowType = winType;
    }

    /**
     * Instantiates a new visual explain window detail impl.
     *
     * @param winType the win type
     * @param windowDetails the window details
     */
    public VisualExplainWindowDetailImpl(VisualExplainPlanWindowDetailsEnum winType, IWindowDetail windowDetails) {
        this.windowType = winType;
        this.windowType.setWindowTitle(windowDetails.getTitle());
        this.windowType.setUniqueID(windowDetails.getUniqueID());
        this.windowType.setShortTitle(windowDetails.getShortTitle());

        // icon and is Closeable information is already present in the Enum.
        // Overwrite remaining properties.
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    @Override
    public String getTitle() {
        return this.windowType.getVisExplainWindowTitle();
    }

    /**
     * Gets the short title.
     *
     * @return the short title
     */
    @Override
    public String getShortTitle() {
        return this.windowType.getShortTitle();
    }

    /**
     * Gets the unique ID.
     *
     * @return the unique ID
     */
    @Override
    public String getUniqueID() {
        return this.windowType.getUniqueID();
    }

    /**
     * Gets the icon.
     *
     * @return the icon
     */
    @Override
    public String getIcon() {
        return this.windowType.getIcon();
    }

    /**
     * Checks if is closeable.
     *
     * @return true, if is closeable
     */
    @Override
    public boolean isCloseable() {
        return this.windowType.isCloseable();
    }

}
