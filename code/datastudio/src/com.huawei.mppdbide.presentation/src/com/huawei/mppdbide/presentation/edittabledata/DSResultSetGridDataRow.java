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

package com.huawei.mppdbide.presentation.edittabledata;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSResultSetGridDataRow.
 *
 * @since 3.0.0
 */
public class DSResultSetGridDataRow implements IDSGridDataRow {

    /** The values. */
    protected Object[] values;
    private String encoding;
    private boolean isIncludeEncoding;
    private DSResultSetGridDataProvider dsResultSetGridDataProvider;

    /**
     * Instantiates a new DS result set grid data row.
     */
    public DSResultSetGridDataRow(DSResultSetGridDataProvider dsResultSetGridDataProvider) {
        this.encoding = null;
        this.dsResultSetGridDataProvider = dsResultSetGridDataProvider;
    }

    /**
     * Sets the values.
     *
     * @param rowValues the new values
     */
    public void setValues(Object[] rowValues) {
        this.values = rowValues;
    }

    @Override
    public Object[] getValues() {
        return values;
    }

    @Override
    public Object getValue(int columnIndex) {
        if (values != null && columnIndex >= 0 && columnIndex < values.length) {
            if (isIncludeEncoding() && !isUnstructuredDatatype(columnIndex)) {
                return getEncodedValue(values[columnIndex]);
            }
            return values[columnIndex];
        } else {
            return "";
        }

    }

    /**
     * Gets the encoded value.
     *
     * @param value the value
     * @return the encoded value
     */
    protected Object getEncodedValue(Object value) {
        if (value instanceof byte[]) {
            byte[] byteVal = (byte[]) value;

            try {
                if (null != getEncoding() && !getEncoding().isEmpty()) {

                    return new String(byteVal, getEncoding());

                }

                return new String(byteVal, Charset.defaultCharset().name());
            } catch (UnsupportedEncodingException e) {
                // Ignore. nothing can be done here.
                MPPDBIDELoggerUtility.debug("Encoding failed");
            }
        }

        return value;
    }

    @Override
    public Object[] getClonedValues() {
        return getValues().clone();
    }

    /**
     * Gets the encoding.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding.
     *
     * @param encoding the new encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Checks if is include encoding.
     *
     * @return true, if is include encoding
     */
    public boolean isIncludeEncoding() {
        return isIncludeEncoding;
    }

    /**
     * Checks if is blob.
     *
     * @param columnIndex the column index
     * @return true, if is blob
     */
    public boolean isBlob(int columnIndex) {
        return MPPDBIDEConstants.BLOB
                .equals(dsResultSetGridDataProvider.getColumnDataProvider().getColumnDataTypeName(columnIndex));
    }

    /**
     * Checks if is byte A.
     *
     * @param columnIndex the column index
     * @return true, if is byte A
     */
    public boolean isByteA(int columnIndex) {
        return MPPDBIDEConstants.BYTEA
                .equals(dsResultSetGridDataProvider.getColumnDataProvider().getColumnDataTypeName(columnIndex));
    }

    /**
     * Checks if is unstructured datatype.
     *
     * @param columnIndex the column index
     * @return true, if is unstructured datatype
     */
    public boolean isUnstructuredDatatype(int columnIndex) {
        return isBlob(columnIndex) || isByteA(columnIndex);
    }

    /**
     * Sets the include encoding.
     *
     * @param isIncludeDSEncoding the new include encoding
     */
    public void setIncludeEncoding(boolean isIncludeDSEncoding) {
        this.isIncludeEncoding = isIncludeDSEncoding;
    }

}
