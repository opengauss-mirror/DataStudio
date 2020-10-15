/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.Map;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.presentation.edittabledata.DSGridEditDataRow;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordStates;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSObjectPropertiesGridDataRow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSObjectPropertiesGridDataRow extends DSGridEditDataRow {

    private ServerObject serverObject;

    /**
     * Instantiates a new DS object properties grid data row.
     *
     * @param row the row
     */
    public DSObjectPropertiesGridDataRow(Object[] row) {
        this.rows = row.clone();
        this.rowstate = EditTableRecordStates.NOT_EDITED;
        this.executionStatus = EditTableRecordExecutionStatus.NOT_EXECUTED;
    }

    @Override
    public Object[] getValues() {
        Object[] values = rows;
        prepareModifiedRow(values);
        return values;
    }

    /**
     * Prepare modified row.
     *
     * @param values the values
     */
    private void prepareModifiedRow(Object[] values) {
        for (Map.Entry<Integer, Object> modifiedEntry : modifiedData.entrySet()) {

            values[modifiedEntry.getKey()] = modifiedEntry.getValue() != null ? modifiedEntry.getValue().toString()
                    : modifiedEntry.getValue();
        }
    }

    @Override
    public Object[] getClonedValues() {
        Object[] values = rows.clone();
        prepareModifiedRow(values);

        return values;
    }

    @Override
    public void setValue(int columnIndex, Object newValue) {
        modifiedData.put(columnIndex, newValue);
        if (modifiedData.containsKey(columnIndex)) {
            if (serverObject != null && newValue != null) {
                updateServerObject(columnIndex, newValue);
            }
        }
        if (this.rowstate == EditTableRecordStates.NOT_EDITED) {
            rowstate = EditTableRecordStates.UPDATE;
        }
        getEventTable().sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, null));
    }

    /**
     * Update server object.
     *
     * @param columnIndex the column index
     * @param newValue the new value
     */
    private void updateServerObject(int columnIndex, Object newValue) {
        switch (serverObject.getType()) {
            case COLUMN_METADATA: {
                performUpdateforColumn(columnIndex, newValue);
                break;
            }
            case CONSTRAINT: {
                ((ConstraintMetaData) serverObject).setName(newValue.toString());
                break;
            }
            case INDEX_METADATA: {
                ((IndexMetaData) serverObject).setName(newValue.toString());
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Perform updatefor column.
     *
     * @param columnIndex the column index
     * @param value the value
     */
    private void performUpdateforColumn(int columnIndex, Object value) {
        switch (columnIndex) {
            case 0: {
                ((ColumnMetaData) serverObject).setName(value.toString());
                break;
            }
            case 1: {
                ((ColumnMetaData) serverObject).setDataType(((ObjectPropColumnTabInfo) value).getColDatatype());
                ((ColumnMetaData) serverObject).setScale(((ObjectPropColumnTabInfo) value).getScale());
                ((ColumnMetaData) serverObject).setLenOrPrecision(((ObjectPropColumnTabInfo) value).getPrecision());
                break;
            }
            case 2: {
                ((ColumnMetaData) serverObject).setNotNull((boolean) value);
                break;
            }
            case 3: {
                ((ColumnMetaData) serverObject).setColDescription(value.toString());
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Sets the server object.
     *
     * @param serverObject the new server object
     */
    public void setServerObject(ServerObject serverObject) {
        this.serverObject = serverObject;
    }

    /**
     * Gets the server object.
     *
     * @return the server object
     */
    public ServerObject getServerObject() {
        return serverObject;
    }

}