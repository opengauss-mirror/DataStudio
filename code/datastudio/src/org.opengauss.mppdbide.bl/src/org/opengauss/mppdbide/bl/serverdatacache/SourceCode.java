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

package org.opengauss.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SourceCode.
 * 
 */

public class SourceCode implements ISourceCode {

    private String code;
    private long versionNumber1;
    private long versionNumber2;
    private int headerLength;

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the version number 1.
     *
     * @return the version number 1
     */
    public long getVersionNumber1() {
        return versionNumber1;
    }

    /**
     * Sets the version number 1.
     *
     * @param versionNumber1 the new version number 1
     */
    public void setVersionNumber1(long versionNumber1) {
        this.versionNumber1 = versionNumber1;
    }

    /**
     * Gets the version number 2.
     *
     * @return the version number 2
     */
    public long getVersionNumber2() {
        return versionNumber2;
    }

    /**
     * Sets the version number 2.
     *
     * @param versionNumber2 the new version number 2
     */
    public void setVersionNumber2(long versionNumber2) {
        this.versionNumber2 = versionNumber2;
    }

    /**
     * Gets the header length.
     *
     * @return the header length
     */
    public int getHeaderLength() {
        return headerLength;
    }

    /**
     * Sets the header length.
     *
     * @param headerLength the new header length
     */
    public void setHeaderLength(int headerLength) {
        this.headerLength = headerLength;
    }

    /**
     * Update code.
     *
     * @param rs the rs
     * @throws SQLException the SQL exception
     */
    public void updateCode(ResultSet rs) throws SQLException {
        StringBuilder fnsSrc = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        this.setHeaderLength(rs.getInt(1));
        String src = rs.getString(2);
        if (null != src) {
            fnsSrc.append(src.replaceAll("\\$function\\$", "\\$\\$"));
            fnsSrc.append(MPPDBIDEConstants.ESCAPE_FORWARDSLASH);
            this.setCode(fnsSrc.toString());
        }
    }

    /**
     * Update version number.
     *
     * @param rs the rs
     * @throws SQLException the SQL exception
     */
    public void updateVersionNumber(ResultSet rs) throws SQLException {
        if (null != rs) {
            this.setVersionNumber1(rs.getLong(1));
            this.setVersionNumber2(rs.getLong(2));
        }
    }

    /**
     * Gets the source line num from server equivalent.
     *
     * @param serverEqualentLineNumber the server equalent line number
     * @return the source line num from server equivalent
     */
    public int getSourceLineNumFromServerEquivalent(int serverEqualentLineNumber) {
        return serverEqualentLineNumber - getHeaderLength();
    }

    /**
     * Checks if is changed.
     *
     * @param latCode the lat code
     * @return true, if is changed
     */
    public boolean isChanged(String latCode) {
        String latestCode = latCode;
        if (code == null) {
            return false;
        }
        if (code.equals(latestCode)) {
            return false;
        }
        return true;
    }

}