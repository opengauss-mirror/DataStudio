/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import com.huawei.mppdbide.presentation.edittabledata.DSEditTableDataGridRow;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataRow;
import com.huawei.mppdbide.presentation.edittabledata.EditTableCellState;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordStates;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataRow;
import com.huawei.mppdbide.presentation.objectproperties.IObjectPropertyData;
import com.huawei.mppdbide.presentation.util.DataTypeUtility;
import com.huawei.mppdbide.utils.ConvertTimeStampValues;
import com.huawei.mppdbide.utils.ConvertTimeValues;
import com.huawei.mppdbide.utils.DateTimeFormatValidator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.component.grid.GridUIUtils;
import com.huawei.mppdbide.view.component.grid.ITableGridStyleLabelFactory;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridColumnValueAccessor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GridColumnValueAccessor implements IColumnPropertyAccessor<IDSGridDataRow> {
    private static final String GENERAL = "General";
    private static final String INDEX = "Index";
    private static final String CONSTRAINTS = "Constraints";
    private static final String COLUMNS = "Columns";

    /**
     * The data provider.
     */
    protected IDSGridDataProvider dataProvider;
    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";
    private static final String BIT_ONE = "1";
    private static final String BIT_ZERO = "0";
    private static final String NULL_VALUE = "";
    private String objectName;

    /**
     * Instantiates a new grid column value accessor.
     *
     * @param dataProvider the data provider
     */
    public GridColumnValueAccessor(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    /**
     * Gets the data value.
     *
     * @param rowObject the row object
     * @param columnIndex the column index
     * @return the data value
     */
    @Override
    public Object getDataValue(IDSGridDataRow rowObject, int columnIndex) {
        int columnDataType = dataProvider.getColumnDataProvider().getColumnDatatype(columnIndex);
        Object value = null;
        switch (columnDataType) {
            case Types.TIMESTAMP: {
                value = getTimestampTypeDataValue(rowObject, columnIndex);
                break;
            }
            case Types.TIME: {
                value = getTimeTypeDataValue(rowObject, columnIndex);
                break;
            }
            case Types.DATE: {
                value = getDateTypeDataValue(rowObject, columnIndex);
                break;
            }
            default: {
                value = rowObject.getValue(columnIndex);
                break;
            }
        }
        return value;
    }

    private Object getTimestampTypeDataValue (IDSGridDataRow rowObject, int columnIndex) {
        String columnDataTypeName = dataProvider.getColumnDataProvider().getColumnDataTypeName(columnIndex);
        if ("timestamptz".equals(columnDataTypeName)) {
            return rowObject.getValue(columnIndex);
        }
        Object objVal = rowObject.getValue(columnIndex);
        Timestamp timestamp = null;
        if (objVal instanceof String) {
            try {
                timestamp = Timestamp.valueOf(objVal.toString());
            } catch (IllegalArgumentException exp) {
                return null;
            }
        } else if (objVal instanceof Timestamp) {
            timestamp = (Timestamp) objVal;
        } else {
            return timestamp;
        }
        ConvertTimeStampValues value = null;
        String dateFormat = PreferenceWrapper.getInstance().getPreferenceStore()
                .getString(MPPDBIDEConstants.DATE_FORMAT_VALUE);
        String timeFormat = PreferenceWrapper.getInstance().getPreferenceStore()
                .getString(MPPDBIDEConstants.TIME_FORMAT_VALUE);
        if (null != timestamp) {
            value = new ConvertTimeStampValues(timestamp.getTime(),
                    DateTimeFormatValidator.getDatePlusTimeFormat(dateFormat, timeFormat));
        }
        return value;
    }

    private Object getTimeTypeDataValue (IDSGridDataRow rowObject, int columnIndex) {
        String columnDataTypeName = dataProvider.getColumnDataProvider().getColumnDataTypeName(columnIndex);
        if ("timetz".equals(columnDataTypeName)) {
            return rowObject.getValue(columnIndex);
        }
        Object obj = rowObject.getValue(columnIndex);
        Timestamp timestamp = null;
        if (obj instanceof Timestamp) {
            timestamp = (Timestamp) obj;
        }
        ConvertTimeValues value = null;
        String timeFormat = PreferenceWrapper.getInstance().getPreferenceStore()
                .getString(MPPDBIDEConstants.TIME_FORMAT_VALUE);
        if (null != timestamp) {
            value = new ConvertTimeValues(timestamp.getTime(), timeFormat);
        }
        return value;
    }

    private Object getDateTypeDataValue (IDSGridDataRow rowObject, int columnIndex) {
        Object obj = rowObject.getValue(columnIndex);
        Date date = null;
        if (obj instanceof Date) {
            date = (Date) obj;
        }
        ConvertTimeStampValues value = null;
        String dateFormat = PreferenceWrapper.getInstance().getPreferenceStore()
                .getString(MPPDBIDEConstants.DATE_FORMAT_VALUE);
        if (null != date) {
            value = new ConvertTimeStampValues(date.getTime(), dateFormat);
        }
        return value;
    }

    /**
     * Sets the data value.
     *
     * @param rowObject the row object
     * @param columnIndex the column index
     * @param newValue the new value
     */
    @Override
    public void setDataValue(IDSGridDataRow rowObject, int columnIndex, Object newValue) {
        if (rowObject.getClass() == DSObjectPropertiesGridDataRow.class) {
            if ((((IObjectPropertyData) dataProvider).getObjectPropertyName()).equals(COLUMNS) && (columnIndex == 2)) {
                try {

                    if (newValue instanceof String) {
                        convertToBoolean(newValue.toString());
                    }
                } catch (IllegalArgumentException e) {
                    invalidBooleanValueErrDialog(MessageConfigLoader.getProperty(
                            IMessagesConstants.TABLE_PROPERTIES_INVALID_BOOLEAN_DATATYPE), IiconPath.ICO_TABLE);
                    return;
                }
            }

            performSetValueForObjProps(rowObject, columnIndex, newValue);
        } else {
            performSetValueForEditTableRow(rowObject, columnIndex, newValue);
        }
    }

    private void invalidBooleanValueErrDialog(String errorMessage, String iconPath) {
        // Executes in Async Thread
        // Removing will lead to two popups
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                IconUtility.getIconImage(iconPath, this.getClass()),
                MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE), errorMessage);
    }

    private void performSetValueForObjProps(IDSGridDataRow rowObject, int columnIndex, Object newValue) {

        if (!((IDSEditGridDataProvider) dataProvider).isEditSupported()) {
            return;
        }

        Object oldValue = rowObject.getValue(columnIndex);
        if (!isEditablePropertiesColumn(columnIndex, rowObject)) {
            return;
        }

        if (dataProvider instanceof IObjectPropertyData) {
            String errorMessage = ((DSObjectPropertiesGridDataProvider) dataProvider).isValidObjectName(columnIndex,
                    newValue, rowObject);

            if (errorMessage != null) {

                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                        IconUtility.getIconImage(IiconPath.ICO_TABLE, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE),
                        errorMessage);
                return;
            }
        }
        Comparator<Object> comparator = dataProvider.getColumnDataProvider().getComparator(columnIndex);
        if (!isNullValue(columnIndex, newValue)) {
            if (isValueChanged(newValue, oldValue, comparator)) {

                ((DSObjectPropertiesGridDataRow) rowObject).setValue(columnIndex, newValue);
            }
        } else {

            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                    IconUtility.getIconImage(IiconPath.ICO_TABLE, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE),
                    objectName);
        }
        return;
    }

    private boolean isEditablePropertiesColumn(int columnIndex, IDSGridDataRow rowObject) {
        String objName = ((IObjectPropertyData) dataProvider).getObjectPropertyName();
        String colName = dataProvider.getColumnDataProvider().getColumnName(columnIndex);
        String rowData = null;
        if (objName.equals(GENERAL)) {
            rowData = rowObject.getValue(0).toString();
        }
        if (null != colName && GridUIUtils.isEditablePropertiesAttributes(objName, colName, rowData)) {
            return true;
        }
        return false;
    }

    private boolean isNullValue(int columnIndex, Object newValue) {
        String propertyName = ((IObjectPropertyData) this.dataProvider).getObjectPropertyName();
        boolean flag = false;
        switch (propertyName) {
            case GENERAL: {
                return false;
            }
            case COLUMNS: {
                objectName = MessageConfigLoader.getProperty(IMessagesConstants.ERR_COLUMN_NAME_EMPTY);
                flag = true;
                break;
            }
            case INDEX: {
                objectName = MessageConfigLoader.getProperty(IMessagesConstants.ERR_INDEX_NAME_EMPTY);
                flag = true;
                break;
            }
            case CONSTRAINTS: {
                objectName = MessageConfigLoader.getProperty(IMessagesConstants.ERR_CONSTRAINT_NAME_EMPTY);
                flag = true;
                break;
            }

            default: {
                break;
            }
        }

        if (flag && columnIndex == 0 && newValue == null) {
            return true;
        }
        return false;
    }

    private void performSetValueForEditTableRow(IDSGridDataRow rowObject, int columnIndex, Object newValueParam) {
        Object newValue = newValueParam;
        Object oldValue = rowObject.getValue(columnIndex);
        EditTableRecordStates updatedState = ((IDSGridEditDataRow) rowObject).getUpdatedState();
        List<String> distributedCols = ((IDSEditGridDataProvider) dataProvider).getDistributedColumnList();

        String columnDataTypeName = dataProvider.getColumnDataProvider().getColumnDataTypeName(columnIndex);
        int colPrecisionVal = dataProvider.getColumnDataProvider().getPrecision(columnIndex);
        if (!GridUIUtils.isDatatypeEditSupported(columnDataTypeName, colPrecisionVal)) {
            return;
        }
        if (distributedCols != null
                && distributedCols.contains(dataProvider.getColumnDataProvider().getColumnName(columnIndex))
                && updatedState != EditTableRecordStates.INSERT) {
            return;
        }
        try {

            newValue = convertValueAsPerDataType(rowObject, columnIndex, newValue);

            oldValue = convertValueAsPerDataType(rowObject, columnIndex, oldValue);

            Comparator<Object> comparator = dataProvider.getColumnDataProvider().getComparator(columnIndex);
            updateCellStatusOnValueChange(rowObject, columnIndex, newValue, oldValue, comparator);
        } catch (NumberFormatException e) {
            // Executes in Async Thread
            // Removing will lead to two popups
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                    IconUtility.getIconImage(IiconPath.ICO_EDIT_EDIT, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_INVALID_NUMBER_DATATYPE));

        } catch (IllegalArgumentException e) {
            invalidBooleanValueErrDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_INVALID_BOOLEAN_DATATYPE),
                    IiconPath.ICO_EDIT_EDIT);
        } catch (ParseException e) {
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                    IconUtility.getIconImage(IiconPath.ICO_EDIT_EDIT, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_INVALID_DATE_DATATYPE));

        }
    }

    private void updateCellStatusOnValueChange(IDSGridDataRow rowObject, int columnIndex, Object newValue,
            Object oldValue, Comparator<Object> comparator) {
        if (isValueChanged(newValue, oldValue, comparator)) {
            ((DSEditTableDataGridRow) rowObject).setValue(columnIndex, newValue);
            EditTableRecordExecutionStatus executionStatus = ((IDSGridEditDataRow) rowObject).getExecutionStatus();
            // FAILED_AND_MODIFIED is checked when the row has more than one
            // failed cell value
            if (executionStatus == EditTableRecordExecutionStatus.FAILED
                    || executionStatus == EditTableRecordExecutionStatus.FAILED_AND_MODIFIED) {
                ((IDSGridEditDataRow) rowObject).setExecutionStatus(EditTableRecordExecutionStatus.FAILED_AND_MODIFIED);
                ((IDSGridEditDataRow) rowObject).setCellSatus(EditTableCellState.MODIFIED, columnIndex);
            }
        }
    }

    private Object convertValueAsPerDataType(IDSGridDataRow rowObject, int columnIndex, Object newValue)
            throws ParseException {
        if (newValue instanceof String) {
            newValue = convertStringToObject(rowObject, (String) newValue, columnIndex);

        } else if (newValue instanceof Date) {
            newValue = convertDateToTimeStamp((Date) newValue, columnIndex);
        }
        return newValue;
    }

    private boolean isValueChanged(Object newValue, Object oldValue, Comparator<Object> comparator) {
        if (null == newValue && null == oldValue) {
            return false;
        }

        return (null == newValue) || (null == oldValue)
                || (comparator != null && comparator.compare(oldValue, newValue) != 0);
    }

    /**
     * Gets the data provider.
     *
     * @return the data provider
     */
    public IDSGridDataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Gets the column count.
     *
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        IDSGridColumnProvider columnDataProvider = getColumnDataProvider();
        if (null != columnDataProvider) {
            return columnDataProvider.getColumnCount();
        }
        return 0;
    }

    /**
     * Gets the column property.
     *
     * @param columnIndex the column index
     * @return the column property
     */
    @Override
    public String getColumnProperty(int columnIndex) {
        IDSGridColumnProvider columnDataProvider = getColumnDataProvider();
        if (null != columnDataProvider) {
            return columnDataProvider.getColumnName(columnIndex);
        }
        return "";
    }

    /**
     * Gets the column data provider.
     *
     * @return the column data provider
     */
    protected IDSGridColumnProvider getColumnDataProvider() {
        if (null != this.dataProvider) {
            return this.dataProvider.getColumnDataProvider();
        }
        return null;
    }

    /**
     * Gets the column index.
     *
     * @param propertyName the property name
     * @return the column index
     */
    @Override
    public int getColumnIndex(String propertyName) {
        IDSGridColumnProvider columnDataProvider = getColumnDataProvider();
        if (null != columnDataProvider) {
            return columnDataProvider.getColumnIndex(propertyName);
        }
        return 0;
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        this.dataProvider = null;
    }

    /**
     * Convert string to object.
     *
     * @param rowObject the row object
     * @param value the value
     * @param columIdx the colum idx
     * @return the object
     * @throws ParseException the parse exception
     */
    public Object convertStringToObject(IDSGridDataRow rowObject, String value, int columIdx) throws ParseException {
        Date dateValue = null;
        // set null if the value is empty

        if (StringUtils.isEmpty(value)) {
            return null;
        }

        switch (dataProvider.getColumnDataProvider().getColumnDatatype(columIdx)) {
            case Types.TINYINT:
            case Types.SMALLINT: {
                return Short.parseShort(value);
            }
            case Types.INTEGER: {
                return Integer.parseInt(value);
            }
            case Types.BIGINT: {
                return Long.parseLong(value);
            }
            case Types.REAL: {
                return Float.parseFloat(value);
            }
            case Types.FLOAT:
            case Types.DOUBLE: {
                return Double.parseDouble(value);
            }
            case Types.NUMERIC: {
                BigDecimal decimal = new BigDecimal(value);
                return decimal;
            }
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE: {
                dateValue = DataTypeUtility.convertToDateObj(value,
                        ITableGridStyleLabelFactory.COMMON_GRID_DATE_FORMAT);
                return new Timestamp(dateValue.getTime());
            }
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE: {
                dateValue = DataTypeUtility.convertToTimeObj(value,
                        ITableGridStyleLabelFactory.COMMON_GRID_TIME_FORMAT);
                return new Timestamp(dateValue.getTime());
            }
            case Types.BIT:
            case Types.BOOLEAN: {
                return convertToBoolean(value);
            }
            default: {
                return getDefaultValue(rowObject, value);
            }
        }

    }

    private Object getDefaultValue(IDSGridDataRow rowObject, String value) {
        if (rowObject instanceof DSResultSetGridDataRow && ((DSResultSetGridDataRow) rowObject).isIncludeEncoding()) {
            DSResultSetGridDataRow row = (DSResultSetGridDataRow) rowObject;
            String encoding = row.getEncoding();
            if (null != encoding && !encoding.isEmpty()) {
                try {
                    return value.getBytes(encoding);
                } catch (UnsupportedEncodingException e) {
                    // Ignore. Nothing can be done.
                    MPPDBIDELoggerUtility.debug("Unable to convert input by encoding.");
                }
            }
        }
        return value;
    }

    private Object convertToBoolean(String value) {
        if (NULL_VALUE.equals(value)) {
            return false;
        }
        if (TRUE_VALUE.equalsIgnoreCase(value) || value.equals(BIT_ONE)) {
            return true;
        }
        if (FALSE_VALUE.equalsIgnoreCase(value) || value.equals(BIT_ZERO)) {
            return false;
        }
        throw new IllegalArgumentException();

    }

    private Object convertDateToTimeStamp(Date dateObj, int columnIndex) throws ParseException {
        SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat(ITableGridStyleLabelFactory.COMMON_GRID_DATE_FORMAT);
        Date trimmedDate = datetimeFormatter1.parse(datetimeFormatter1.format(dateObj));

        return new Timestamp(trimmedDate.getTime());
    }
}
