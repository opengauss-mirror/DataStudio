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

package org.opengauss.mppdbide.bl.errorlocator;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractErrorLocator.
 * 
 */

public abstract class AbstractErrorLocator implements IErrorLocator {

    @Override
    public int textStartOffset(String query, String text, int searchOffset) {
        int qryStartOffset;
        String[] qry = query.split(MPPDBIDEConstants.LINE_SEPARATOR);
        qryStartOffset = text.indexOf(qry[0], searchOffset);
        return qryStartOffset;
    }

    @Override
    public int textEndOffset(String query, String text, int searchOffset) {
        int qryStartOffset = textStartOffset(query, text, searchOffset);

        // In some cases the end offset is 1 more than the actual
        // length of the query itself. Added a safety condition
        int qryEndOffset = qryStartOffset + query.length();
        if (qryEndOffset > text.length()) {
            qryEndOffset = text.length();
        }
        return qryEndOffset;
    }

    /**
     * error Line Number.
     *
     * @param qryStartOffset the qry start offset
     * @param errorPos the error pos
     * @return the int
     */
    @Override
    public abstract int errorLineNumber(int qryStartOffset, int errorPos);

    /**
     * error Position.
     *
     * @param query the query
     * @param qryStartOffset the qry start offset
     * @param qryEndOffset the qry end offset
     * @param startLineNo the start line no
     * @param endLineNo the end line no
     * @param exp the exp
     * @return the int
     */
    @Override
    public abstract int errorPosition(String query, int qryStartOffset, int qryEndOffset, int startLineNo,
            int endLineNo, MPPDBIDEException exp);

    /**
     * error Message.
     *
     * @param text the text
     * @param errorPos the error pos
     * @param qryStartOffset the qry start offset
     * @param qryEndOffset the qry end offset
     * @return the string
     */
    @Override
    public abstract String errorMessage(String text, int errorPos, int qryStartOffset, int qryEndOffset);

    /**
     * server Error Message.
     *
     * @param exp the exp
     * @return the string
     */
    @Override
    public abstract String serverErrorMessage(MPPDBIDEException exp);

    /**
     * Identify pos from qry err string.
     *
     * @param qryErrString the qry err string
     * @return the int
     */
    protected int identifyPosFromQryErrString(String qryErrString) {
        int pos = 0;

        if (qryErrString.contains(" ")) {

            pos = qryErrString.indexOf(' ');
        }

        else if (qryErrString.contains(MPPDBIDEConstants.LINE_SEPARATOR)) {

            pos = qryErrString.indexOf(MPPDBIDEConstants.LINE_SEPARATOR);
        }

        else if (qryErrString.contains(";")) {

            pos = qryErrString.indexOf(';');
        } else {
            pos = qryErrString.length();
        }
        return pos;
    }

}
