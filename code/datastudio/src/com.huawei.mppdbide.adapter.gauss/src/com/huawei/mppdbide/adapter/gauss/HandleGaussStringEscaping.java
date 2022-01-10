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

package com.huawei.mppdbide.adapter.gauss;

import java.io.IOException;

import javax.print.attribute.standard.MediaSize.NA;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class HandleGaussStringEscaping.
 * 
 */
public class HandleGaussStringEscaping {

    /**
     * Escape literal.
     *
     * @param sbufParam the sbuf param
     * @param valueParam the value param
     * @return the string builder
     */
    public static StringBuilder escapeLiteral(StringBuilder sbufParam, String valueParam) {
        StringBuilder sbuf = sbufParam;
        String value = valueParam;
        if (value == null) {
            return new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        }
        if (sbuf == null) {
            sbuf = new StringBuilder(value.length() * 11 / 10); // Add 10% for
                                                                // escaping.
        }
        try {
            doAppendEscapedLiteral(sbuf, value);
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.warn(" Error while escaping literal. ");
        }
        return sbuf;
    }

    /**
     * Do append escaped literal.With standard_conforming_strings on, escape
     * only single-quotes.
     *
     * @param sbuf the sbuf
     * @param value the value
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private static void doAppendEscapedLiteral(Appendable sbuf, String value) throws MPPDBIDEException {
        try {

            char ch = '\'';
            sbuf.append(ch);

            int length = value.length();
            for (int index = 0; index < length; ++index) {

                ch = value.charAt(index);

                if (ch == '\0') {
                    MPPDBIDELoggerUtility.error("Zero bytes may not occur in string parameters.");
                    throw new MPPDBIDEException("Zero bytes may not occur in string parameters.");
                }

                if (ch == '\'') {
                    sbuf.append('\'');
                }

                sbuf.append(ch);
            }

            sbuf.append('\'');
        } catch (IOException e) {
            throw new MPPDBIDEException("No IOException expected from StringBuffer or StringBuilder");
        }
    }

    /**
     * Escape identifier.
     *
     * @param value the value
     * @return the string builder
     */
    public static StringBuilder escapeIdentifier(String value) {
        StringBuilder sbuf = null;
        if (value == null) {
            return new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        }
        if (sbuf == null) {
            // Add 10% for escaping
            sbuf = new StringBuilder(2 + value.length() * 11 / 10);

        }
        try {
            doAppendEscapedIdentifier(sbuf, value);
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.warn(" Error while escaping identifier name.");
        }
        return sbuf;
    }

    /**
     * Common part for appendEscapedIdentifier.
     *
     * @param sbuf Either StringBuffer or StringBuilder as we do not expect any
     * IOException to be thrown.
     * @param value value to append
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private static void doAppendEscapedIdentifier(Appendable sbuf, String value) throws MPPDBIDEException {
        try {

            char ch = '"';
            sbuf.append(ch);

            int length = value.length();

            for (int index = 0; index < length; ++index) {
                ch = value.charAt(index);
                if (ch == '\0') {
                    MPPDBIDELoggerUtility.error("Zero bytes may not occur in string parameters.");
                    throw new MPPDBIDEException("Zero bytes may not occur in string parameters.");
                }
                if (ch == '"') {
                    sbuf.append(ch);
                }
                sbuf.append(ch);
            }

            sbuf.append('"');
        } catch (IOException e) {
            throw new MPPDBIDEException("No IOException expected from StringBuffer or StringBuilder");
        }
    }
}
