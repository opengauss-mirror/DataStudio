/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ErrorAnnotation.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ErrorAnnotation extends AnnotationWithLineNumber {
    private static final int RED_VAL = 255;
    private static final int GREEN_VAL = 0;
    private static final int BLUE_VAL = 0;

    /**
     * The Constant STRATEGY_ID.
     */
    public static final String STRATEGY_ID = "error.type";
    private static final int LAYER = 3;

    private static final Object LOCK = new Object();
    private static volatile RGB errorRGB;
    private int line;
    private static String annotationTypeLabel;

    /**
     * Instantiates a new error annotation.
     *
     * @param line the line
     * @param info the info
     */
    public ErrorAnnotation(int line, String info) {
        super(STRATEGY_ID, false, info);
        this.line = line;
    }

    /**
     * Gets the layer.
     *
     * @return the layer
     */
    public static int getLayer() {
        return LAYER;
    }

    /**
     * Gets the image.
     *
     * @return the image
     */
    public Image getImage() {
        return IconUtility.getIconImage(IiconPath.ICO_ERROR, this.getClass());
    }

    /**
     * Gets the error rgb.
     *
     * @return the error rgb
     */
    public static RGB getErrorRgb() {
        if (null == errorRGB) {
            synchronized (LOCK) {
                if (null == errorRGB) {
                    errorRGB = new RGB(RED_VAL, GREEN_VAL, BLUE_VAL);
                }
            }
        }
        return errorRGB;
    }

    /**
     * Gets the strategyid.
     *
     * @return the strategyid
     */
    public static String getStrategyid() {
        return STRATEGY_ID;
    }

    /**
     * Gets the line.
     *
     * @return the line
     */
    @Override
    public int getLine() {
        return line;
    }

    /**
     * Sets the line.
     *
     * @param line the new line
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Gets the typelabel.
     *
     * @return the typelabel
     */
    public static String getTypelabel() {
        if (null == annotationTypeLabel) {
            annotationTypeLabel = MessageConfigLoader.getProperty(IMessagesConstants.ERROR_ANNOTATION_LABEL);
        }
        return annotationTypeLabel;
    }

}
