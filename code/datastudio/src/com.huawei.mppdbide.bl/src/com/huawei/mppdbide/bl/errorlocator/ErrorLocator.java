/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.errorlocator;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ErrorLocator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
