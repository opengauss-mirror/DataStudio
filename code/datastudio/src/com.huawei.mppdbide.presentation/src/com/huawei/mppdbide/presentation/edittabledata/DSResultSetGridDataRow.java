/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSResultSetGridDataRow implements IDSGridDataRow {

    /** The values. */
    protected Object[] values;
    protected Object[] originalValues;
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
     * Sets the originalValues.
     *
     * @param rowValues the new values
     */
    public void setOriginalValues(Object[] rowValues) {
        this.originalValues = rowValues;
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

	public Object[] getOriginalValues() {
		return originalValues;
	}

    public Object getOriginalValue(int columnIndex) {
        if (originalValues != null && columnIndex >= 0 && columnIndex < originalValues.length) {
            if (isIncludeEncoding() && !isUnstructuredDatatype(columnIndex)) {
                return getEncodedValue(originalValues[columnIndex]);
            }
            return originalValues[columnIndex];
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
