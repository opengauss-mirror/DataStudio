/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import java.util.Optional;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;

import com.huawei.mppdbide.view.utils.icon.IconUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class AnnotationWithLineNumber.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author s00428892
 * @version [DataStudio 8.0.2, 04 Apr, 2020]
 * @since 04 Apr, 2020
 */
public abstract class AnnotationWithLineNumber extends Annotation {
    // the annotation line
    protected int line;
    public AnnotationWithLineNumber(String strategyId, boolean isPersistent, String info, int line) {
        super(strategyId, isPersistent, info);
        this.line = line;
    }

    public AnnotationWithLineNumber(int line) {
        super();
        this.line = line;
    }
    
    public abstract AnnotationHelper.AnnotationType getAnnotationType();

    /**
     * Gets the image.
     *
     * @return the line
     */
    public Optional<Image> getImage() {
        return Optional.empty();
    }
    
    /**
     * Gets the line.
     *
     * @return the line
     */
    public int getLine() {
        return this.line;
    }
    
    /**
     * set line
     * 
     * @param line line to set
     * @return the line
     */
    public void setLine(int line) {
        this.line = line;
    }
    
    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.getClass().isInstance(obj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public static Image loadImage(String imagePath) {
        return IconUtility.getIconImage(imagePath, AnnotationWithLineNumber.class);
    }
}
