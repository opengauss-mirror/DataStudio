/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.jface.text.source.Annotation;

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
public class AnnotationWithLineNumber extends Annotation {
    public AnnotationWithLineNumber(String strategyId, boolean boolValue, String info) {
        super(strategyId, boolValue, info);
    }

    public AnnotationWithLineNumber() {
        super();
    }

    /**
     * Gets the line.
     *
     * @return the line
     */
    public int getLine() {
        return 0;
    }

}
