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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class AnnotationHover.
 *
 * @since 3.0.0
 */
public class AnnotationHover implements IAnnotationHover, ITextHover {
    private AnnotationModel fAnnotationModel;

    /**
     * Instantiates a new annotation hover.
     *
     * @param annotationModel the annotation model
     */
    public AnnotationHover(AnnotationModel annotationModel) {
        super();
        this.fAnnotationModel = annotationModel;
    }

    /**
     * Gets the hover info.
     *
     * @param textViewer the text viewer
     * @param hoverRegion the hover region
     * @return the hover info
     */
    @Override
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        // Skip this
        return null;
    }

    /**
     * Gets the hover region.
     *
     * @param textViewer the text viewer
     * @param offset the offset
     * @return the hover region
     */
    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        // Skip this
        return null;
    }

    /**
     * Gets the hover info.
     *
     * @param sourceViewer the source viewer
     * @param lineNumber the line number
     * @return the hover info
     */
    @Override
    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
        List<String> hoverStr = new ArrayList<String>();
        Iterator<Annotation> iterator = fAnnotationModel.getAnnotationIterator();
        Annotation annotation = null;
        boolean doNotOverride = false;
        boolean iteratorHasNext = iterator.hasNext();
        while (iteratorHasNext) {
            annotation = iterator.next();
            if (annotation instanceof AnnotationWithLineNumber) {
                AnnotationWithLineNumber annotationWithLineNumber = (AnnotationWithLineNumber) annotation;
                if (annotation instanceof BreakpointAnnotation) {
                    if (!doNotOverride && lineNumber == annotationWithLineNumber.getLine()) {
                        hoverStr.add(annotation.getText());
                    }
                } else {
                    if (lineNumber + 1 == annotationWithLineNumber.getLine()) {
                        hoverStr.add(annotation.getText());
                    }
                    if (annotationWithLineNumber instanceof ErrorPositionAnnotation) {
                        doNotOverride = true;
                    }
                }
            }
            iteratorHasNext = iterator.hasNext();
        }
        StringBuilder hoverInfo = new StringBuilder();
        if (hoverStr.size() > 1) {
            hoverInfo.append(MessageConfigLoader.getProperty(IMessagesConstants.MUTIPLE_MARKERS))
                    .append(MPPDBIDEConstants.LINE_SEPARATOR);
        }
        for (int i = 0; i < hoverStr.size(); i++) {
            hoverInfo.append("-").append(hoverStr.get(i)).append(MPPDBIDEConstants.LINE_SEPARATOR);
        }
        return hoverInfo.toString();
    }

}
