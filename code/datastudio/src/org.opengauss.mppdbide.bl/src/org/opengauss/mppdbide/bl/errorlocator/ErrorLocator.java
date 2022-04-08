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
 * Description: The Class ErrorLocator.
 * 
 */

public class ErrorLocator extends AbstractErrorLocator {

    private static final String ERROR_POSITION_IDENTIFIER = "Position:";

    @Override
    public int errorPosition(String query, int qryStartOffset, int qryEndOffset, int startLineNo, int endLineNo,
            MPPDBIDEException exp) {
        String findPos = exp.getServerMessage();
        int errorPos = qryEndOffset - qryStartOffset;
        errorPos++;
        if (null != findPos) {

            int idx = findPos.lastIndexOf(ERROR_POSITION_IDENTIFIER);

            if (idx != -1) {
                String newStr = findPos.substring(idx + ERROR_POSITION_IDENTIFIER.length());

                if (newStr.contains(MPPDBIDEConstants.LINE_SEPARATOR)) {
                    newStr = newStr.substring(0, newStr.indexOf(MPPDBIDEConstants.LINE_SEPARATOR));
                }

                try {
                    errorPos = Integer.parseInt(newStr.trim());
                } catch (NumberFormatException ex) {
                    errorPos = qryEndOffset - qryStartOffset;
                    errorPos++;
                }
            }
        }

        if (errorPos > query.length()) {
            errorPos = query.length();
        }

        return errorPos + qryStartOffset;
    }

    @Override
    public String serverErrorMessage(MPPDBIDEException exp) {
        return exp.getServerMessage();
    }

    @Override
    public int errorLineNumber(int qryStartOffset, int errorPos) {
        int errorLineNo = errorPos;
        return errorLineNo;
    }

    @Override
    public String errorMessage(String text, int errorPos, int qryStartOffset, int qryEndOffset) {
        String qryStr = text.substring(qryStartOffset, qryEndOffset);

        String qryErrString = qryStr.substring((errorPos - qryStartOffset) - 1);
        int pos = identifyPosFromQryErrString(qryErrString);
        String errorString = qryErrString.substring(0, pos);

        return errorString;
    }

}
