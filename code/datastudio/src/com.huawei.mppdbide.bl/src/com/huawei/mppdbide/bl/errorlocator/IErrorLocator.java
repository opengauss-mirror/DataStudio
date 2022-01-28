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

package com.huawei.mppdbide.bl.errorlocator;

import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IErrorLocator.
 */

public interface IErrorLocator {

    /**
     * Text start offset.
     *
     * @param query the query
     * @param text the text
     * @param searchOffset the search offset
     * @return the int
     */
    int textStartOffset(String query, String text, int searchOffset);

    /**
     * Text end offset.
     *
     * @param query the query
     * @param text the text
     * @param qryStartOffset the qry start offset
     * @param qryEndOffset the qry end offset
     * @param searchOffset the search offset
     * @return the int
     */
    int textEndOffset(String query, String text, int searchOffset);

    /**
     * Error line number.
     *
     * @param qryStartOffset the qry start offset
     * @param errorPos the error pos
     * @return the int
     */
    int errorLineNumber(int qryStartOffset, int errorPos);

    /**
     * Error position.
     *
     * @param query the query
     * @param qryStartOffset the qry start offset
     * @param qryEndOffset the qry end offset
     * @param startLineNo the start line no
     * @param endLineNo the end line no
     * @param exp the exp
     * @return the int
     */
    int errorPosition(String query, int qryStartOffset, int qryEndOffset, int startLineNo, int endLineNo,
            MPPDBIDEException exp);

    /**
     * Error message.
     *
     * @param text the text
     * @param errorPos the error pos
     * @param qryStartOffset the qry start offset
     * @param qryEndOffset the qry end offset
     * @return the string
     */
    String errorMessage(String text, int errorPos, int qryStartOffset, int qryEndOffset);

    /**
     * Server error message.
     *
     * @param exp the exp
     * @return the string
     */
    String serverErrorMessage(MPPDBIDEException exp);
}
