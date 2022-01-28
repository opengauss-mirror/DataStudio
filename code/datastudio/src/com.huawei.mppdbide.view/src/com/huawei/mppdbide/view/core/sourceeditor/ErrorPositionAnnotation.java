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

package com.huawei.mppdbide.view.core.sourceeditor;

import java.util.Optional;

import org.eclipse.swt.graphics.Image;

import com.huawei.mppdbide.view.core.sourceeditor.AnnotationHelper.AnnotationType;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ErrorPositionAnnotation.
 *
 * @since 3.0.0
 */
public class ErrorPositionAnnotation extends AnnotationWithLineNumber {
    private static final AnnotationType ANNOTATION_TYPE = AnnotationType.ERROR_POSITION;

    /**
     * Instantiates a new debug position annotation.
     *
     * @param errorMessage the line
     */
    public ErrorPositionAnnotation(String errorMessage, int line) {
        super(ANNOTATION_TYPE.getStrategy(), false, errorMessage, line);
    }

    /**
     * Gets the layer.
     *
     * @return the layer
     */
    public static int getLayer() {
        return ANNOTATION_TYPE.getLayer();
    }

    /**
     * Gets the image.
     *
     * @return the image
     */
    public Optional<Image> getImage() {
        return Optional.of(loadImage(IiconPath.ICO_ERROR));
    }

    /**
     * Gets the strategyid.
     *
     * @return the strategyid
     */
    public static String getStrategyid() {
        return ANNOTATION_TYPE.getStrategy();
    }

    /**
     * Gets the typelabel.
     *
     * @return the typelabel
     */
    public static String getTypelabel() {
        return ANNOTATION_TYPE.getTypeLabel();
    }

    @Override
    public AnnotationType getAnnotationType() {
        return ANNOTATION_TYPE;
    }
}
