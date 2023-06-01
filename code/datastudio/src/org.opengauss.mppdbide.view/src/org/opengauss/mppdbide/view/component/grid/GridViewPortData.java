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

package org.opengauss.mppdbide.view.component.grid;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.print.command.TurnViewportOffCommand;
import org.eclipse.nebula.widgets.nattable.print.command.TurnViewportOnCommand;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;

import org.opengauss.mppdbide.presentation.exportdata.ExportCursorExecuteVisitor.ColumnDataType;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridViewPortData.
 *
 * @since 3.0.0
 */
public class GridViewPortData implements Iterable<String[]> {
    private ILayer viewport;
    private int dataReadIndex;
    private int rowCount;

    private int colCount;

    /**
     * Gets the col count.
     *
     * @return the col count
     */
    public int getColCount() {
        return colCount;
    }

    /**
     * Sets the col count.
     *
     * @param colCount the new col count
     */
    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    private IClientAreaProvider originalClientAreaProvider;

    /**
     * Instantiates a new grid view port data.
     *
     * @param layer the layer
     */
    public GridViewPortData(ILayer layer) {
        this.viewport = layer;
        this.dataReadIndex = 0;
    }

    /**
     * Gets the row count.
     *
     * @return the row count
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Sets the row count.
     *
     * @param rowCount the new row count
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * Iterator.
     *
     * @return the iterator
     */
    @Override
    public Iterator<String[]> iterator() {
        return new Iterator<String[]>() {

            @Override
            public boolean hasNext() {
                return dataReadIndex < rowCount;
            }

            @Override
            public String[] next() {
                if (dataReadIndex >= rowCount) {
                    throw new NoSuchElementException();
                }
                return getRow();
            }

        };
    }

    /**
     * Finalize layer.
     */
    public void finalizeLayer() {
        this.viewport.setClientAreaProvider(originalClientAreaProvider);
        this.viewport.doCommand(new TurnViewportOnCommand());
    }

    /**
     * Initialize layer.
     */
    public void initializeLayer() {
        originalClientAreaProvider = this.viewport.getClientAreaProvider();
        this.viewport.doCommand(new TurnViewportOffCommand());
        setRowCount(this.viewport.getRowCount());
        setColCount(this.viewport.getColumnCount());
    }

    /**
     * Gets the row.
     *
     * @return the row
     */
    public String[] getRow() {
        String[] rowData = new String[this.colCount - 1];

        // As we operate on Grid Layer, Skip the row number column
        for (int i = 1; i < this.colCount; i++) {
            Object dataValue = this.viewport.getDataValueByPosition(i, this.dataReadIndex);
            if (dataValue != null) {
                LabelStack stack = this.viewport.getConfigLabelsByPosition(i, this.dataReadIndex);
                
                // If the datatype is Blob, [BLOB] watermark is added
                // instead of the content since it can be huge
                if (stack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_BLOB_TYPE_CELL)) {
                    rowData[i - 1] = MPPDBIDEConstants.BLOB_WATERMARK;
                    continue;
                }

                if (stack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_TINYBLOB_TYPE_CELL)) {
                    rowData[i - 1] = MPPDBIDEConstants.TINYBLOB_WATERMARK;
                    continue;
                }

                if (stack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_MEDIUMBLOB_TYPE_CELL)) {
                    rowData[i - 1] = MPPDBIDEConstants.MEDIUMBLOB_WATERMARK;
                    continue;
                }

                if (stack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_LONGBLOB_TYPE_CELL)) {
                    rowData[i - 1] = MPPDBIDEConstants.LONGBLOB_WATERMARK;
                    continue;
                }

                if (stack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_CURSOR_TYPE_CELL)
                        && dataValue instanceof List) {
                    rowData[i - 1] = MPPDBIDEConstants.CURSOR_WATERMARK;
                    continue;
                }

                if (stack.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_BYTEA_TYPE_CELL)) {
                    rowData[i - 1] = MPPDBIDEConstants.BYTEA_WATERMARK;
                    continue;
                }
            }
            rowData[i - 1] = getObjectString(dataValue);
        }
        this.dataReadIndex++;
        return rowData;
    }

    /**
     * Gets the excel data type.
     *
     * @return the excel data type
     */
    public List<ColumnDataType> getExcelDataType() {

        List<ColumnDataType> columnList = new ArrayList<ColumnDataType>();
        for (int i = 1; i < colCount; i++) {
            Object data1 = this.viewport.getDataValueByPosition(i, 1);
            if (data1 instanceof Float) {
                columnList.add(ColumnDataType.DOUBLE);
            } else if (data1 instanceof Double) {
                columnList.add(ColumnDataType.DOUBLE);
            } else if (data1 instanceof Date) {
                columnList.add(ColumnDataType.DATE);
            } else if (data1 instanceof Timestamp) {
                columnList.add(ColumnDataType.DATE);
            } else if (data1 instanceof Boolean) {
                columnList.add(ColumnDataType.BOOLEAN);
            } else {
                columnList.add(ColumnDataType.STRING);
            }
        }
        return columnList;

    }

    /**
     * Converts known type of object to string for export operation. Limitation:
     * To be extended for format like Date time format, because What use see on
     * UI and exported file may be different.
     *
     * @param data the data
     * @return the object string
     */
    private String getObjectString(Object data) {
        if (data instanceof Byte) {
            return Byte.toString((Byte) data);
        } else if (data instanceof Short) {
            return Short.toString((Short) data);
        } else if (data instanceof Integer) {
            return Integer.toString((Integer) data);
        } else if (data instanceof Long) {
            return Long.toString((Long) data);
        } else if (data instanceof Float) {
            return Float.toString((Float) data);
        } else if (data instanceof Double) {
            return Double.toString((Double) data);
        } else if (data instanceof Boolean) {
            return Boolean.toString((Boolean) data);
        } else if (data instanceof Date) {
            return ((Date) data).toString();
        } else if (data instanceof Time) {
            return ((Time) data).toString();
        } else if (data instanceof Timestamp) {
            return ((Timestamp) data).toString();
        } else {
            return null == data ? "" : data.toString();
        }
    }

}
