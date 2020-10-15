/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean.pos;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

/**
 * 
 * Title: SQLScriptPosition
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
 */
public class SQLScriptPosition extends Position implements Comparable<SQLScriptPosition> {

    private final ProjectionAnnotation foldingAnnotation;
    private final boolean isMultiline;

    /**
     * Instantiates a new SQL script position.
     *
     * @param offset the offset
     * @param length the length
     * @param isMultiline the is multiline
     * @param foldingAnnotation the folding annotation
     */
    public SQLScriptPosition(int offset, int length, boolean isMultiline, ProjectionAnnotation foldingAnnotation) {
        super(offset, length);
        this.foldingAnnotation = foldingAnnotation;
        this.isMultiline = isMultiline;
    }

    /**
     * Gets the folding annotation.
     *
     * @return the folding annotation
     */
    public ProjectionAnnotation getFoldingAnnotation() {
        return foldingAnnotation;
    }

    /**
     * Checks if is multiline.
     *
     * @return true, if is multiline
     */
    public boolean isMultiline() {
        return isMultiline;
    }

    /**
     * Compare to.
     *
     * @param scriptPosition the o
     * @return the int
     */
    @Override
    public int compareTo(SQLScriptPosition scriptPosition) {
        return offset - scriptPosition.offset;
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
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {

        return super.equals(obj);
    }

}
