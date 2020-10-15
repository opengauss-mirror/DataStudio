/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.visualexplainplan;

import com.huawei.mppdbide.presentation.IWindowDetail;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainWindowDetailImpl.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
