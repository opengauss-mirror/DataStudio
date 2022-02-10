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

package org.opengauss.mppdbide.view.component.grid.core;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
import org.eclipse.nebula.widgets.nattable.copy.serializing.CopyDataToClipboardSerializer;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.presentation.edittabledata.DSResultSetGridDataRow;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSCopyDataToClipboardSerializer.
 *
 * @since 3.0.0
 */
public class DSCopyDataToClipboardSerializer extends CopyDataToClipboardSerializer {

    private final ILayerCell[][] copiedCells;
    private final CopyDataToClipboardCommand command;

    private IDSGridDataProvider dataProvider;
    private static final String BIT_ONE = "1";
    private static final String BIT_ZERO = "0";
    private static final String BIT_DATATYPE = "bit";
    private static final String NULL = "null";

    /**
     * Instantiates a new DS copy data to clipboard serializer.
     *
     * @param copiedCells the copied cells
     * @param command the command
     * @param dataProvider the data provider
     */
    public DSCopyDataToClipboardSerializer(ILayerCell[][] copiedCells, CopyDataToClipboardCommand command,
            IDSGridDataProvider dataProvider) {
        super(copiedCells, command);
        this.copiedCells = copiedCells.clone();
        this.command = command;
        this.dataProvider = dataProvider;
    }

    /**
     * Serialize.
     */
    @Override
    public void serialize() {
        final String cellDelim = this.command.getCellDelimeter();
        final String rowDelim = this.command.getRowDelimeter();

        final TextTransfer textTransfer = TextTransfer.getInstance();
        final StringBuilder txtData = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        int currentRow = 0;
        for (ILayerCell[] cells : this.copiedCells) {
            int currentCell = 0;
            for (ILayerCell cell : cells) {
                final String delimeter = ++currentCell < cells.length ? cellDelim : ""; // $NON-NLS-1$
                if (cell != null) {
                    txtData.append(getTextForCell(cell) + delimeter);
                } else {
                    txtData.append(delimeter);
                }
            }
            if (++currentRow < this.copiedCells.length) {
                txtData.append(rowDelim);
            }
        }
        if (txtData.length() > 0) {
            final Clipboard clipBoard = new Clipboard(Display.getDefault());
            try {
                clipBoard.setContents(new Object[] {txtData.toString()}, new Transfer[] {textTransfer});
            } finally {
                clipBoard.dispose();
            }
        } else {
            final Clipboard clipboard = new Clipboard(Display.getDefault());
            try {
                clipboard.setContents(new Object[] {NULL}, new Transfer[] {textTransfer});
                clipboard.clearContents();
            } finally {
                clipboard.dispose();
            }
        }
    }

    /**
     * Gets the text for cell.
     *
     * @param cell the cell
     * @return the text for cell
     */
    @Override
    protected String getTextForCell(ILayerCell cell) {
        if (cell.getDataValue() == null) {
            return "";
        }
        String dataValue = super.getTextForCell(cell);
        if (!(cell.getLayer().getUnderlyingLayerByPosition(cell.getColumnPosition(),
                cell.getRowPosition()) instanceof DefaultColumnHeaderDataLayer
                || cell.getLayer() instanceof ColumnHeaderLayer)) {
            String columnDatatypeName = dataProvider.getColumnDataProvider()
                    .getColumnDataTypeName(cell.getColumnIndex());
            if (columnDatatypeName != null) {
                switch (columnDatatypeName) {
                    case MPPDBIDEConstants.BLOB: {
                        return MPPDBIDEConstants.BLOB_WATERMARK;
                    }
                    case MPPDBIDEConstants.BYTEA: {
                        return MPPDBIDEConstants.BYTEA_WATERMARK;
                    }
                    default: {
                        break;
                    }
                }
            }
        }

        if (cell.getRowPosition() > 0 && !dataProvider.getAllFetchedRows().isEmpty()
                && cell.getRowPosition() < dataProvider.getAllFetchedRows().size()
                && dataProvider.getAllFetchedRows().get(cell.getRowPosition()) != null) {
            Object[] row = dataProvider.getAllFetchedRows().get(cell.getRowPosition()).getValues();
            if (row != null && cell.getColumnPosition() > 1 && cell.getColumnPosition() < row.length
                    && row[cell.getColumnPosition()] instanceof List<?>) {
                List<Object> dataGridobj = (List<Object>) row[cell.getColumnPosition()];
                if (dataGridobj.get(0) instanceof DSResultSetGridDataRow) {
                    return MPPDBIDEConstants.CURSOR_WATERMARK;
                }
            }
        }

        String columnDataType = this.dataProvider.getColumnDataProvider().getColumnDataTypeName(cell.getColumnIndex());
        int precision = this.dataProvider.getColumnDataProvider().getPrecision(cell.getColumnIndex());
        return getConvertedColumnData(dataValue, columnDataType, precision);
    }

    private String getConvertedColumnData(String dataValue, String columnDataType, int precisionValue) {
        if (BIT_DATATYPE.equals(columnDataType) && precisionValue <= 1) {
            if (Boolean.toString(true).equalsIgnoreCase(dataValue)) {
                return BIT_ONE;
            } else {
                return BIT_ZERO;
            }
        }
        return dataValue;
    }

}
