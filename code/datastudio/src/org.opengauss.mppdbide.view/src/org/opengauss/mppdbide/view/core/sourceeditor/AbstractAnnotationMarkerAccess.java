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

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractAnnotationMarkerAccess.
 *
 * @since 3.0.0
 */
public abstract class AbstractAnnotationMarkerAccess implements IAnnotationAccess, IAnnotationAccessExtension {

    /**
     * Gets the type label.
     *
     * @param annotation the annotation
     * @return the type label
     */
    @Override
    public abstract String getTypeLabel(Annotation annotation);

    /**
     * Gets the layer.
     *
     * @param annotation the annotation
     * @return the layer
     */
    @Override
    public abstract int getLayer(Annotation annotation);

    /**
     * Paint.
     *
     * @param annotation the annotation
     * @param gc the gc
     * @param canvas the canvas
     * @param bounds the bounds
     */
    @Override
    public abstract void paint(Annotation annotation, GC gc, Canvas canvas, Rectangle bounds);

    /**
     * Checks if is paintable.
     *
     * @param annotation the annotation
     * @return true, if is paintable
     */
    @Override
    public boolean isPaintable(Annotation annotation) {
        return false;
    }

    /**
     * Checks if is subtype.
     *
     * @param annotationType the annotation type
     * @param potentialSupertype the potential supertype
     * @return true, if is subtype
     */
    @Override
    public boolean isSubtype(Object annotationType, Object potentialSupertype) {
        if (annotationType.equals(potentialSupertype)) {
            return true;
        }

        return false;
    }

    /**
     * Gets the supertypes.
     *
     * @param annotationType the annotation type
     * @return the supertypes
     */
    @Override
    public Object[] getSupertypes(Object annotationType) {
        return new Object[0];
    }

    /**
     * Gets the type.
     *
     * @param annotation the annotation
     * @return the type
     */
    @Override
    public Object getType(Annotation annotation) {
        return annotation.getType();
    }

    /**
     * Checks if is multi line.
     *
     * @param annotation the annotation
     * @return true, if is multi line
     */
    @Override
    public abstract boolean isMultiLine(Annotation annotation);

    /**
     * Checks if is temporary.
     *
     * @param annotation the annotation
     * @return true, if is temporary
     */
    @Override
    public boolean isTemporary(Annotation annotation) {
        return !annotation.isPersistent();
    }

}
