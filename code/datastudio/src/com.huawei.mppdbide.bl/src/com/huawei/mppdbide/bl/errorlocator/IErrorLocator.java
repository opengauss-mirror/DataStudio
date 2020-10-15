/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.errorlocator;

import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IErrorLocator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
