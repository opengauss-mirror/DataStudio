/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import com.huawei.mppdbide.view.core.sourceeditor.AnnotationHelper.AnnotationType;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class BreakpointAnnotation.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class BreakpointAnnotation extends AnnotationWithLineNumber {
    private static final AnnotationHelper.AnnotationType annotationType = AnnotationType.BREAKPOINT;
    private static final RGB BREAKPOINT_RGB = new RGB(188, 188, 222);
    private int breakpointId;
    private boolean isDisabled;

    /**
     * Instantiates a new breakpoint annotation.
     *
     * @param info the info
     * @param line the line
     */
    public BreakpointAnnotation(String info, int line) {
        super(annotationType.getStrategy(), false, info, line);
    }
    
    @Override
    public AnnotationHelper.AnnotationType getAnnotationType() {
        return annotationType;
    }

    /**
     * Gets the layer.
     *
     * @return the layer
     */
    public static int getLayer() {
        return annotationType.getLayer();
    }

    /**
     * Gets the image.
     *
     * @return the image
     */
    @Override
    public Optional<Image> getImage() {
        String imgPath = isDisabled ? IiconPath.ICO_BREAKPOINT_DISABLED :
                IiconPath.ICO_BREAKPOINT_ENABLED;
        return Optional.ofNullable(loadImage(imgPath));
    }
    
    /**
     * Gets the breakpoint rgb.
     *
     * @return the breakpoint rgb
     */
    public static RGB getBreakpointRgb() {
        return BREAKPOINT_RGB;
    }

    /**
     * Gets the strategyid.
     *
     * @return the strategyid
     */
    public static String getStrategyid() {
        return annotationType.getStrategy();
    }

    /**
     * Gets the breakpoint id.
     *
     * @return the breakpoint id
     */
    public int getBreakpointId() {
        return breakpointId;
    }

    /**
     * Sets the breakpoint id.
     *
     * @param breakpointId the new breakpoint id
     */
    public void setBreakpointId(int breakpointId) {
        this.breakpointId = breakpointId;
    }

    /**
     * Gets the typelabel.
     *
     * @return the typelabel
     */
    public static String getTypelabel() {
        return annotationType.getTypeLabel();
    }

    /**
     * Sets the disabled.
     *
     * @param isDisabld the new disabled
     */
    public void setDisabled(boolean isDisabld) {
        this.isDisabled = isDisabld;
    }

    /**
     * Sets the disabled.
     *
     * @param isDisabld the new disabled
     */
    public boolean getDisabled() {
        return this.isDisabled;
    }
}
