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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.print.command.TurnViewportOffCommand;
import org.eclipse.nebula.widgets.nattable.print.command.TurnViewportOnCommand;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;

import org.opengauss.mppdbide.presentation.exportdata.ExportCursorExecuteVisitor.ColumnDataType;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridSelectionLayerPortData.
 *
 * @since 3.0.0
 */
public class GridSelectionLayerPortData {
    private SelectionLayer selectionLayer;
    private IDSGridDataProvider dataProvider;
    private PositionCoordinate[] rowSelectedPosition;
    private int[] colIndexSelected;
    private List<Integer> rowCoordinate;
    private Set<Integer> rowSelectCountSet;
    private List<String> headerList;
    private List<String[]> listOfRows;
    private IClientAreaProvider originalClientAreaProvider;
    private int rowSelectCount;
    private int rowCount;
    private String columnName;
    private Boolean isCellPositionSelected;

    /**
     * Instantiates a new grid selection layer port data.
     *
     * @param selectionLayer the selection layer
     * @param dataProvider the data provider
     */
    public GridSelectionLayerPortData(SelectionLayer selectionLayer, IDSGridDataProvider dataProvider) {
        this.selectionLayer = selectionLayer;
        this.dataProvider = dataProvider;
        this.rowSelectCountSet = new HashSet<>();
        this.rowCoordinate = new ArrayList<>();
        this.headerList = new ArrayList<>();
        this.listOfRows = new ArrayList<>();
        this.rowSelectCount = this.selectionLayer.getSelectedRowCount();
        this.rowSelectedPosition = this.selectionLayer.getSelectedCellPositions();
        this.colIndexSelected = this.selectionLayer.getSelectedColumnPositions();
        this.rowCount = this.selectionLayer.getRowCount();
        for (PositionCoordinate row : rowSelectedPosition) {
            rowSelectCountSet.add(row.rowPosition);

        }
        Iterator it = rowSelectCountSet.iterator();
        while (it.hasNext()) {
            rowCoordinate.add((Integer) it.next());

        }
    }

    /**
     * Gets the row coordinate.
     *
     * @return the row coordinate
     */
    public List<Integer> getRowCoordinate() {
        return rowCoordinate;
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
     * Finalize layer.
     */
    public void finalizeLayer() {
        this.selectionLayer.setClientAreaProvider(originalClientAreaProvider);
        this.selectionLayer.doCommand(new TurnViewportOnCommand());
    }

    /**
     * Initialize layer.
     */
    public void initializeLayer() {
        originalClientAreaProvider = this.selectionLayer.getClientAreaProvider();
        this.selectionLayer.doCommand(new TurnViewportOffCommand());

    }

    /**
     * Gets the cell data.
     *
     * @return the cell data
     */
    public String getCellData() {
        String cellDataValue = null;
        int selectRowSize = this.rowCoordinate.size();
        if (selectRowSize > 0) {
            for (int j = 0; j < selectRowSize; j++) {
                for (int i = 0; i < colIndexSelected.length; i++) {
                    cellDataValue = getObjectString(
                            selectionLayer.getDataValueByPosition(colIndexSelected[i], rowCoordinate.get(j)));
                }
            }
        }
        return cellDataValue;
    }

    /**
     * Gets the header list.
     *
     * @return the header list
     */
    public List<String> getHeaderList() {
        headerList.clear();
        headerList.add(null);
        for (int i : colIndexSelected) {
            columnName = dataProvider.getColumnDataProvider().getColumnName(selectionLayer.getColumnIndexByPosition(i));
            headerList.add(columnName);
        }
        return headerList;
    }

    /**
     * Gets the select row.
     *
     * @param i the i
     * @param j the j
     * @return the select row
     */
    public Object getSelectRow(int i, int j) {
        Object cellDataValue = getObjectString(selectionLayer.getDataValueByPosition(j, i));
        return cellDataValue;
    }

    /**
     * Gets the row.
     *
     * @return the row
     */
    public List<String[]> getRow() {
        String[] cellDataValue = null;
        for (int j = 0; j < this.rowCoordinate.size(); j++) {
            cellDataValue = new String[this.colIndexSelected.length + 1];
            cellDataValue[0] = getObjectString(j + 1);
            isCellPositionSelected = false;
            for (int i = 0; i < colIndexSelected.length; i++) {
                isCellPositionSelected = selectionLayer.isCellPositionSelected(colIndexSelected[i],
                        rowCoordinate.get(j));
                // get actual col position even after reorder
                int col = selectionLayer.getColumnIndexByPosition(colIndexSelected[i]);
                String colDataypeName = dataProvider.getColumnDataProvider().getColumnDataTypeName(col);
                Object dataValueByPos = selectionLayer.getDataValueByPosition(colIndexSelected[i],
                        rowCoordinate.get(j));

                // convert to water mark for unstructured data
                if (isCellPositionSelected && colDataypeName != null && dataValueByPos != null) {
                    switch (colDataypeName) {
                        case MPPDBIDEConstants.BLOB: {
                            cellDataValue[i + 1] = MPPDBIDEConstants.BLOB_WATERMARK;
                            continue;
                        }
                        case MPPDBIDEConstants.TINYBLOB: {
                            cellDataValue[i + 1] = MPPDBIDEConstants.TINYBLOB_WATERMARK;
                            continue;
                        }
                        case MPPDBIDEConstants.MEDIUMBLOB: {
                            cellDataValue[i + 1] = MPPDBIDEConstants.MEDIUMBLOB_WATERMARK;
                            continue;
                        }
                        case MPPDBIDEConstants.LONGBLOB: {
                            cellDataValue[i + 1] = MPPDBIDEConstants.LONGBLOB_WATERMARK;
                            continue;
                        }
                        case MPPDBIDEConstants.BYTEA: {
                            cellDataValue[i + 1] = MPPDBIDEConstants.BYTEA_WATERMARK;
                            continue;
                        }
                        default: {
                            break;
                        }
                    }
                }
                cellDataValue[i + 1] = getObjectString(
                        selectionLayer.getDataValueByPosition(colIndexSelected[i], rowCoordinate.get(j)));
            }
            listOfRows.add(cellDataValue);
        }
        return listOfRows;
    }

    /**
     * Gets the excel data type.
     *
     * @return the excel data type
     */
    public List<ColumnDataType> getExcelDataType() {

        List<ColumnDataType> columnList = new ArrayList<ColumnDataType>();
        Object data = null;
        columnList.add(ColumnDataType.STRING);
        for (int j = 0; j < this.rowCoordinate.size(); j++) {
            for (int i = 0; i < colIndexSelected.length; i++) {
                data = selectionLayer.getDataValueByPosition(colIndexSelected[i], rowCoordinate.get(j));

                if (data instanceof Float) {
                    columnList.add(ColumnDataType.DOUBLE);
                } else if (data instanceof Double) {
                    columnList.add(ColumnDataType.DOUBLE);
                } else if (data instanceof Date) {
                    columnList.add(ColumnDataType.DATE);
                } else if (data instanceof Timestamp) {
                    columnList.add(ColumnDataType.DATE);
                } else if (data instanceof Boolean) {
                    columnList.add(ColumnDataType.BOOLEAN);
                } else {
                    columnList.add(ColumnDataType.STRING);
                }
            }
        }
        return columnList;

    }

    /**
     * Gets the object string.
     *
     * @param data1 the data
     * @return the object string
     */
    private String getObjectString(Object data1) {
        if (data1 instanceof Byte) {
            return Byte.toString((Byte) data1);
        } else if (data1 instanceof Short) {
            return Short.toString((Short) data1);
        } else if (data1 instanceof Integer) {
            return Integer.toString((Integer) data1);
        } else if (data1 instanceof Long) {
            return Long.toString((Long) data1);
        } else if (data1 instanceof Float) {
            return Float.toString((Float) data1);
        } else if (data1 instanceof Double) {
            return Double.toString((Double) data1);
        } else if (data1 instanceof Boolean) {
            return Boolean.toString((Boolean) data1);
        } else if (data1 instanceof Date) {
            return ((Date) data1).toString();
        } else if (data1 instanceof Time) {
            return ((Time) data1).toString();
        } else if (data1 instanceof Timestamp) {
            return ((Timestamp) data1).toString();
        } else if (data1 instanceof List) {
            return MPPDBIDEConstants.CURSOR_WATERMARK;
        } else {
            return null == data1 ? "" : data1.toString();
        }
    }

    /**
     * Gets the row select count.
     *
     * @return the row select count
     */
    public int getRowSelectCount() {
        return rowSelectCount;
    }

}
