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

package org.opengauss.mppdbide.gauss.sqlparser.word;

/**
 * 
 * Title: SQLFoldingWordDetector
 *
 * @since 3.0.0
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
