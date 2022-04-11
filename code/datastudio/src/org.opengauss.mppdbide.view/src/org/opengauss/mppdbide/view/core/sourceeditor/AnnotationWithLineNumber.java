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

package org.opengauss.mppdbide.view.core.sourceeditor;

import java.util.Optional;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;

import org.opengauss.mppdbide.view.utils.icon.IconUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class AnnotationWithLineNumber.
 *
 * @since 3.0.0
 */
public abstract class AnnotationWithLineNumber extends Annotation {
    /**
     *  the annotation line
     */
    protected int line;

    public AnnotationWithLineNumber(String strategyId, boolean isPersistent, String info, int line) {
        super(strategyId, isPersistent, info);
        this.line = line;
    }

    public AnnotationWithLineNumber(int line) {
        super();
        this.line = line;
    }

    /**
     * description: get annotationType
     *
     * @return AnnotationType the type
     */
    public abstract AnnotationHelper.AnnotationType getAnnotationType();

    /**
     * Gets the image.
     *
     * @return Optional<Image> the image
     */
    public Optional<Image> getImage() {
        return Optional.empty();
    }

    /**
     * Gets the line.
     *
     * @return int the line
     */
    public int getLine() {
        return this.line;
    }

    /**
     * set line
     *
     * @param line line to set
     * @return void
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return boolean true if successful
     */
    @Override
    public boolean equals(Object obj) {
        return this.getClass().isInstance(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * description: load image from path
     *
     * @param imagePath the image path
     * @return Image image instance
     */
    public static Image loadImage(String imagePath) {
        return IconUtility.getIconImage(imagePath, AnnotationWithLineNumber.class);
    }
}
