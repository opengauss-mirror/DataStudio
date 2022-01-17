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

package com.huawei.mppdbide.view.ui.terminal;

import java.util.Iterator;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationBarHoverManager;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.view.core.sourceeditor.ErrorAnnotation;
import com.huawei.mppdbide.view.core.sourceeditor.ErrorAnnotationMarkerAccess;
import com.huawei.mppdbide.view.ui.QueryInfo;

/**
 * 
 * Title: class
 * 
 * Description: The Class AnnotationManager.
 *
 * @since 3.0.0
 */
public class AnnotationManager {
    private IAnnotationAccess fAnnotationAccess;
    private AnnotationModel fAnnotationModel;

    private AnnotationRulerColumn annotationRulerColumn;
    private CompositeRuler fCompositeRuler;
    private AnnotationPainter ap;
    private static final int ANNOTATION_RULER_WIDTH = 16;
    private static final int SPACE_BETWEEN_RULER = 1;

    /**
     * Instantiates a new annotation manager.
     */
    public AnnotationManager() {
        fAnnotationModel = new AnnotationModel();
    }

    /**
     * Gets the annotation model.
     *
     * @return the annotation model
     */
    public AnnotationModel getAnnotationModel() {
        return fAnnotationModel;
    }

    /**
     * Gets the annotation access.
     *
     * @return the annotation access
     */
    public IAnnotationAccess getAnnotationAccess() {
        if (null == fAnnotationAccess) {
            AnnotationModel fAnnotnModel = getAnnotationModel();
            fAnnotationAccess = new ErrorAnnotationMarkerAccess(fAnnotnModel);
        }

        return fAnnotationAccess;
    }

    private AnnotationRulerColumn getAnnotationRulerColumn() {
        if (annotationRulerColumn == null) {
            AnnotationModel fAnnotnModel = getAnnotationModel();
            annotationRulerColumn = new AnnotationRulerColumn(fAnnotnModel, ANNOTATION_RULER_WIDTH,
                    getAnnotationAccess());
        }
        return annotationRulerColumn;
    }

    /**
     * Gets the composite ruler.
     *
     * @return the composite ruler
     */
    public CompositeRuler getCompositeRuler() {
        if (fCompositeRuler == null) {
            int annotationRulerColumnIndex = 0;
            int lineNumberColumnIndex = 1;

            fCompositeRuler = new CompositeRuler(SPACE_BETWEEN_RULER);
            AnnotationRulerColumn annotnRulerColumn = getAnnotationRulerColumn();
            fCompositeRuler.addDecorator(annotationRulerColumnIndex, annotnRulerColumn);
            annotnRulerColumn.addAnnotationType(ErrorAnnotation.getStrategyid());

            LineNumberRulerColumn lineNumRulerColumn = new LineNumberRulerColumn();
            lineNumRulerColumn.setForeground(new Color(Display.getDefault(), 104, 99, 94));

            fCompositeRuler.addDecorator(lineNumberColumnIndex, lineNumRulerColumn);

            AnnotationModel fAnnotnModel = getAnnotationModel();
            fCompositeRuler.setModel(fAnnotnModel);
        }
        return fCompositeRuler;
    }

    /**
     * Configure annotation.
     *
     * @param sourceViewer the source viewer
     * @param compositeRuler the composite ruler
     */
    public void configureAnnotation(SourceViewer sourceViewer, CompositeRuler compositeRuler) {
        ((ErrorAnnotationMarkerAccess) getAnnotationAccess()).setDocument(sourceViewer.getDocument());
        AnnotationBarHoverManager fAnnotationHoverManager = new AnnotationBarHoverManager(compositeRuler, sourceViewer,
                new AnnotationHover(), new AnnotationConfiguration());
        fAnnotationHoverManager.install(getAnnotationRulerColumn().getControl());

        // hover manager that shows text when we hover

        ap = new AnnotationPainter(sourceViewer, getAnnotationAccess());
        ap.addAnnotationType(ErrorAnnotation.getStrategyid());
        ap.setAnnotationTypeColor(ErrorAnnotation.getStrategyid(),
                new Color(Display.getDefault(), ErrorAnnotation.getErrorRgb()));
        sourceViewer.addPainter(ap);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AnnotationHover.
     */
    // annotation hover manager
    private class AnnotationHover implements IAnnotationHover, ITextHover {

        /**
         * Gets the hover info.
         *
         * @param sourceViewer the source viewer
         * @param lineNumber the line number
         * @return the hover info
         */
        public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
            String hoverStr = null;

            AnnotationModel fAnnotnModel = getAnnotationModel();
            Iterator<?> ite = fAnnotnModel.getAnnotationIterator();

            Annotation annotatn = null;
            int index = 0;

            while (ite.hasNext()) {
                annotatn = (Annotation) ite.next();
                if (annotatn instanceof ErrorAnnotation) {
                    index = ((ErrorAnnotation) annotatn).getLine();

                    if (lineNumber + 1 == index) {
                        hoverStr = ((ErrorAnnotation) annotatn).getText();
                    }
                }
            }
            return hoverStr;
        }

        @Override
        public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
            return null;

        }

        @Override
        public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
            return null;
        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AnnotationConfiguration.
     */
    private static final class AnnotationConfiguration implements IInformationControlCreator {

        /**
         * Creates the information control.
         *
         * @param shell the shell
         * @return the i information control
         */
        public IInformationControl createInformationControl(Shell shell) {
            return new DefaultInformationControl(shell);
        }
    }

    /**
     * Removes the annotation at line.
     *
     * @param lineAtOffset the line at offset
     */
    public void removeAnnotationAtLine(int lineAtOffset) {
        AnnotationModel fAnnotnModel = getAnnotationModel();
        Iterator<Annotation> iter = fAnnotnModel.getAnnotationIterator();
        while (iter.hasNext()) {
            Annotation annotation = iter.next();
            if (annotation instanceof ErrorAnnotation) {
                ErrorAnnotation errorAnnotation = (ErrorAnnotation) annotation;
                if (errorAnnotation.getLine() == lineAtOffset) {
                    fAnnotnModel.removeAnnotation(errorAnnotation);
                }
            }
        }
    }

    /**
     * Adds the annotation.
     *
     * @param errorDetails the error details
     */
    public void addAnnotation(final QueryInfo errorDetails) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {

                ErrorAnnotation errorAnnotation = new ErrorAnnotation(errorDetails.getErrLineNo(),
                        errorDetails.getServerMessageString());

                int position = errorDetails.getStartOffset() + errorDetails.getErrorPosition();
                AnnotationModel fAnnotnModel = getAnnotationModel();
                fAnnotnModel.addAnnotation(errorAnnotation,
                        new Position(position - 1, errorDetails.getErrorMsgString().length()));
            }
        });
    }

    /**
     * Removes the all annotations.
     */
    public void removeAllAnnotations() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                AnnotationModel fAnnotnModel = getAnnotationModel();
                fAnnotnModel.removeAllAnnotations();
            }
        });
    }

    /**
     * Removes the annotations in selected range.
     *
     * @param off the off
     * @param length the length
     * @param startsBefore the starts before
     * @param endsAfter the ends after
     */
    public void removeAnnotationsInSelectedRange(int off, int length, boolean startsBefore, boolean endsAfter) {

        AnnotationModel fAnnotnModel = getAnnotationModel();
        @SuppressWarnings("unchecked")
        Iterator<Annotation> annotationIterator = fAnnotnModel.getAnnotationIterator(off, length, startsBefore,
                endsAfter);
        Annotation anno = null;

        while (annotationIterator.hasNext()) {
            anno = annotationIterator.next();
            fAnnotnModel.removeAnnotation(anno);
        }

    }

}
