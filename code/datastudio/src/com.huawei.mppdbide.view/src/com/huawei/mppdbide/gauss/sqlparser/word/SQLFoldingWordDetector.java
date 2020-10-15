/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.word;

/**
 * 
 * Title: SQLFoldingWordDetector
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
public class SQLFoldingWordDetector {

    /**
     * Checks if is word start.
     *
     * @param charValue the c
     * @return true, if is word start
     */
    public boolean isWordStart(char charValue) {
        return Character.isUnicodeIdentifierStart(charValue);
    }

    /**
     * Checks if is word part.
     *
     * @param charValue the c
     * @return true, if is word part
     */
    public boolean isWordPart(char charValue) {
        return Character.isUnicodeIdentifierPart(charValue) || charValue == '$';
    }
}
