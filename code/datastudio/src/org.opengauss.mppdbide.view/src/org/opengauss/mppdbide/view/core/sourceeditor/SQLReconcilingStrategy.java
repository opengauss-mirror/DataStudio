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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

import org.opengauss.mppdbide.gauss.sqlparser.bean.pos.SQLScriptElement;
import org.opengauss.mppdbide.gauss.sqlparser.bean.pos.SQLScriptPosition;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: SQLReconcilingStrategy
 *
 * @since 3.0.0
 */
public class SQLReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

    private SQLEditorParser editor;
    private IDocument document;

    private int regionOffset;

    private final Object lock = new Object();

    /**
     * Gets the editor.
     *
     * @return the editor
     */
    public SQLEditorParser getEditor() {
        return editor;
    }

    /**
     * Sets the editor.
     *
     * @param editor the new editor
     */
    public void setEditor(SQLEditorParser editor) {
        this.editor = editor;
    }

    /**
     * Sets the document.
     *
     * @param document the new document
     */
    @Override
    public void setDocument(IDocument document) {
        this.document = document;
    }

    /**
     * Reconcile.
     *
     * @param dirtyRegion the dirty region
     * @param subRegion the sub region
     */
    @Override
    public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
        synchronized (lock) {
            calculatePositions(dirtyRegion);
        }
    }

    /**
     * Reconcile.
     *
     * @param partition the partition
     */
    @Override
    public void reconcile(IRegion partition) {
        synchronized (lock) {
            calculatePositions(partition);
        }
    }

    /**
     * Sets the progress monitor.
     *
     * @param monitor the new progress monitor
     */
    @Override
    public void setProgressMonitor(IProgressMonitor monitor) {
    }

    /**
     * Initial reconcile.
     */
    @Override
    public void initialReconcile() {
        synchronized (lock) {
            calculatePositions(null);
        }
    }

    private List<SQLScriptPosition> parsedPositions = new ArrayList<>();

    /**
     * Calculate positions.
     *
     * @param partition the partition
     */
    protected void calculatePositions(IRegion partition) {
        if (null == editor || null == document) {
            return;
        }

        List<Annotation> removedAnnotations = null;

        regionOffset = 0;

        ProjectionAnnotationModel annotationModel = editor.getProjectionAnnotationModel();
        if (annotationModel == null) {
            return;
        }
        List<SQLScriptElement> queries;
        try {
            queries = editor.extractScriptQueries(regionOffset, document.getLength() - regionOffset, false, true,
                    false);
        } catch (Exception exception) {
            MPPDBIDELoggerUtility.error("Exception while Parsing the sql for SQL Folding", exception);
            return;
        }

        List<SQLScriptPosition> removedPositions = new ArrayList<>();
        for (SQLScriptPosition sp : parsedPositions) {
            if (sp.getOffset() >= regionOffset) {
                removedPositions.add(sp);
            }
        }
        if (!removedPositions.isEmpty()) {
            parsedPositions.removeAll(removedPositions);
            removedAnnotations = new ArrayList<>();
            for (SQLScriptPosition removedPosition : removedPositions) {
                if (removedPosition.isMultiline()) {
                    removedAnnotations.add(removedPosition.getFoldingAnnotation());
                }
            }
        }

        Map<Annotation, Position> addedAnnotations = handleAddedModifiedProjections(queries);
        if (removedAnnotations != null || !isEmpty(addedAnnotations)) {

            annotationModel.modifyAnnotations(
                    removedAnnotations == null ? null
                            : removedAnnotations.toArray(new Annotation[removedAnnotations.size()]),
                    addedAnnotations, null);
        }
    }

    /**
     * Handle added modified projections.
     *
     * @param queries the queries
     * @return the map
     */
    public Map<Annotation, Position> handleAddedModifiedProjections(List<SQLScriptElement> queries) {

        Map<Annotation, Position> addedAnnotations = new HashMap<>();

        if (null == document) {
            return addedAnnotations;
        }

        try {
            List<SQLScriptPosition> addedPositions = new ArrayList<>();
            int documentLength = document.getLength();
            addPosition(queries, addedPositions, documentLength);
            if (!addedPositions.isEmpty()) {
                final int firstQueryPos = addedPositions.get(0).getOffset();
                int posBeforeFirst = 0;
                for (int i = 0; i < parsedPositions.size(); i++) {
                    SQLScriptPosition sp = parsedPositions.get(i);
                    if (sp.getOffset() >= firstQueryPos) {
                        break;
                    }
                    posBeforeFirst = i;
                }
                parsedPositions.addAll(posBeforeFirst, addedPositions);

                for (SQLScriptPosition pos : addedPositions) {
                    if (pos.isMultiline()) {
                        addedAnnotations.put(pos.getFoldingAnnotation(), pos);
                    }
                }
            }
        } catch (Exception exception) {
            MPPDBIDELoggerUtility.error("Exception while Parsing the sql for SQL Folding", exception);
        }
        return addedAnnotations;
    }

    private void addPosition(List<SQLScriptElement> queries, List<SQLScriptPosition> addedPositions, int documentLength)
            throws BadLocationException {
        for (SQLScriptElement se : queries) {
            int queryOffset = se.getOffset();
            int queryLength = se.getLength();

            if (queryOffset == 0 && queryLength == 0) {
                MPPDBIDELoggerUtility.error("queryOffset & queryLength is null, Some script present with 0 length");
                continue;
            }

            boolean isMultiline = document.getLineOfOffset(queryOffset) != document
                    .getLineOfOffset(queryOffset + queryLength);

            // Expand query to the end of line
            for (int i = queryOffset + queryLength; i < documentLength; i++) {
                char ch = document.getChar(i);
                if (Character.isWhitespace(ch)) {
                    queryLength++;
                }
                if (ch == '\n') {
                    break;
                }
            }
            addedPositions
                    .add(new SQLScriptPosition(queryOffset, queryLength, isMultiline, new ProjectionAnnotation()));
        }
    }

    /**
     * Checks if is empty.
     *
     * @param value the value
     * @return true, if is empty
     */
    public boolean isEmpty(Map<?, ?> value) {
        return value == null || value.isEmpty();
    }

}
