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

import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLWhiteSpaceDetector.
 *
 * @since 3.0.0
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
