/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;
/**
 * Title: class Description: The Class ErrorPositionAnnotation. Copyright (c)
 * Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author g00408002
 * @version [DataStudio 8.0.1, 17 Jan, 2020]
 * @since 17 Jan, 2020
 */

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
 * Description: The Class ErrorPositionAnnotation.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author s00428892
 * @version [DataStudio 8.0.2, 04 Apr, 2020]
 * @since 04 Apr, 2020
 */
public class ErrorPositionAnnotation extends AnnotationWithLineNumber {
    private static int layer = 2;
    private static final String ANNOTATION_TYPE_LABEL = MessageConfigLoader
            .getProperty(IMessagesConstants.ERROR_POSITION_LABEL);
    private static final RGB DEBUGPOSITION_RGB = new RGB(255, 255, 0);
    private static final String STRATEGY_ID = "PLSQL_ERROR_POSITION";
    private int line;

    /**
     * Instantiates a new debug position annotation.
     *
     * @param errorMessage the line
     */
    public ErrorPositionAnnotation(String errorMessage, int line) {
        super(STRATEGY_ID, false, errorMessage);
        this.line = line;
    }

    /**
     * Gets the layer.
     *
     * @return the layer
     */
    public static int getLayer() {
        return layer;
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
     * Gets the strategyid.
     *
     * @return the strategyid
     */
    public static String getStrategyid() {
        return STRATEGY_ID;
    }

    /**
     * Gets the typelabel.
     *
     * @return the typelabel
     */
    public static String getTypelabel() {
        return ANNOTATION_TYPE_LABEL;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ErrorPositionAnnotation) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * returns the line number of annotation
     *
     * @return the int line number
     */
    @Override
    public int getLine() {
        return line;
    }

}
