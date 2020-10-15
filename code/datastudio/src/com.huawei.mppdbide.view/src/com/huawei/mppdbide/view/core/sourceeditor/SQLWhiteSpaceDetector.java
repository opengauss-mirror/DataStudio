/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLWhiteSpaceDetector.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SQLWhiteSpaceDetector implements IWhitespaceDetector {

    /**
     * Checks if is whitespace.
     *
     * @param chr the chr
     * @return true, if is whitespace
     */
    public boolean isWhitespace(char chr) {
        return Character.isWhitespace(chr);
    }

}
