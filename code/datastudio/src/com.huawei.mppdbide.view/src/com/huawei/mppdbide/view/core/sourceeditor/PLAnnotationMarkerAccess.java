/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;

/**
 * 
 * Title: class
 * 
 * Description: The Class PLAnnotationMarkerAccess.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class PLAnnotationMarkerAccess extends DefaultMarkerAnnotationAccess {

    /**
     * Gets the type label.
     *
     * @param annotation the annotation
     * @return the type label
     */
    @Override
    public String getTypeLabel(Annotation annotation) {
        if (annotation instanceof ErrorPositionAnnotation) {
            return ErrorPositionAnnotation.getTypelabel();
        } else if (annotation instanceof ErrorAnnotation) {
            return ErrorAnnotation.getTypelabel();
        } else {
            return super.getTypeLabel(annotation);
        }
    }

    /**
     * Gets the layer.
     *
     * @param annotation the annotation
     * @return the layer
     */
    @Override
    public int getLayer(Annotation annotation) {
        if (annotation instanceof ErrorPositionAnnotation) {
            return ErrorPositionAnnotation.getLayer();
        } else if (annotation instanceof ErrorAnnotation) {
            return ErrorAnnotation.getLayer();
        }

        return super.getLayer(annotation);
    }

    /**
     * Paint.
     *
     * @param annotation the annotation
     * @param gc the gc
     * @param canvas the canvas
     * @param bounds the bounds
     */
    @Override
    public void paint(Annotation annotation, GC gc, Canvas canvas, Rectangle bounds) {
        Image paintImage = null;
        if (annotation instanceof ErrorPositionAnnotation) {
            paintImage = ((ErrorPositionAnnotation) annotation).getImage();
        } else if (annotation instanceof ErrorAnnotation) {
            paintImage = ((ErrorAnnotation) annotation).getImage();
        }

        if (null != paintImage) {
            ImageUtilities.drawImage(paintImage, gc, canvas, bounds, SWT.CENTER, SWT.CENTER);
        } else {
            super.paint(annotation, gc, canvas, bounds);
        }

    }

    /**
     * Checks if is paintable.
     *
     * @param annotation the annotation
     * @return true, if is paintable
     */
    @Override
    public boolean isPaintable(Annotation annotation) {
        if (annotation instanceof ErrorPositionAnnotation) {
            return ((ErrorPositionAnnotation) annotation).getImage() != null;
        } else if (annotation instanceof ErrorAnnotation) {
            return ((ErrorAnnotation) annotation).getImage() != null;
        }

        return super.isPaintable(annotation);
    }

    /**
     * Checks if is multi line.
     *
     * @param annotation the annotation
     * @return true, if is multi line
     */
    @Override
    public boolean isMultiLine(Annotation annotation) {
        return false;
    }

}
