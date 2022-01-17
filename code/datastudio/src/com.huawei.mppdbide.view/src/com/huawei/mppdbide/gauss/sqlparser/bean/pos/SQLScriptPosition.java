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

package com.huawei.mppdbide.gauss.sqlparser.bean.pos;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

/**
 * 
 * Title: SQLScriptPosition
 *
 * @since 3.0.0
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
