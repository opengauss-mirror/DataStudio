/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import java.util.Optional;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
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
 * Description: The Class ErrorAnnotationMarkerAccess.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ErrorAnnotationMarkerAccess extends DefaultMarkerAnnotationAccess {

    private AnnotationModel fAnnotationModel;
    private IDocument document;

    /**
     * Instantiates a new error annotation marker access.
     *
     * @param fAnnotationModel the f annotation model
     */
    public ErrorAnnotationMarkerAccess(AnnotationModel fAnnotationModel) {
        this.fAnnotationModel = fAnnotationModel;
    }

    /**
     * Instantiates a new error annotation marker access.
     *
     * @param fAnnotationModel the f annotation model
     * @param document the document
     */
    public ErrorAnnotationMarkerAccess(AnnotationModel fAnnotationModel, IDocument document) {
        this.fAnnotationModel = fAnnotationModel;
        this.document = document;
    }

    /**
     * Gets the type label.
     *
     * @param annotation the annotation
     * @return the type label
     */
    @Override
    public String getTypeLabel(Annotation annotation) {
        if (annotation instanceof AnnotationWithLineNumber) {
            return ((AnnotationWithLineNumber) annotation)
                    .getAnnotationType()
                    .getTypeLabel();
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
        if (annotation instanceof AnnotationWithLineNumber) {
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
        if (annotation instanceof ErrorAnnotation) {
            Optional<Image> paintImg = ((ErrorAnnotation) annotation).getImage();
            if (paintImg.isPresent()) {
                ImageUtilities.drawImage(paintImg.get(), gc, canvas, bounds, SWT.CENTER, SWT.TOP);
            }

            // Update the line number of the modified ErrorAnnotation.
            Position position = fAnnotationModel.getPosition(annotation);
            if (position != null) {
                int offset = position.getOffset();
                int lineOfOffset = -1;
                try {
                    if (document != null) {
                        lineOfOffset = document.getLineOfOffset(offset);
                    }
                } catch (BadLocationException e) {
                    lineOfOffset = -1;
                }
                if (lineOfOffset != -1) {
                    // Line number starts from 0. Increment to get the actual
                    // number.
                    lineOfOffset++;
                    ((ErrorAnnotation) annotation).setLine(lineOfOffset);
                }
            }
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
        if (annotation instanceof ErrorAnnotation) {
            return ((ErrorAnnotation) annotation).getImage().isPresent();
        } else {
            return super.isPaintable(annotation);
        }

    }

    /**
     * Checks if is multi line.
     *
     * @param annotation the annotation
     * @return true, if is multi line
     */
    @Override
    public boolean isMultiLine(Annotation annotation) {
        return true;
    }

    /**
     * Sets the document.
     *
     * @param document the new document
     */
    public void setDocument(IDocument document) {
        this.document = document;
    }

}
