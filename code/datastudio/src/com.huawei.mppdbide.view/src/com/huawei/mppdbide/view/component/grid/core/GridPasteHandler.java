/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.copy.command.PasteDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridPasteHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GridPasteHandler extends AbstractLayerCommandHandler<PasteDataCommand> {
    private SelectionLayer selectionLayer;
    private PositionCoordinate[] posCoordinate;
    private LabelStack configLabelsByPosition;

    /**
     * Instantiates a new grid paste handler.
     *
     * @param selectionLayer the selection layer
     */
    public GridPasteHandler(SelectionLayer selectionLayer) {
        this.selectionLayer = selectionLayer;
    }

    /**
     * Gets the command class.
     *
     * @return the command class
     */
    @Override
    public Class<PasteDataCommand> getCommandClass() {
        return PasteDataCommand.class;
    }

    /**
     * Do command.
     *
     * @param command the command
     * @return true, if successful
     */
    @Override
    protected boolean doCommand(PasteDataCommand command) {

        String[][] pasteableString = convertToPastable();
        posCoordinate = getPostionCoordinates();
        PositionCoordinate positionCoordinate = null;

        // check when nothing is selected from the external source to copy in
        // the nattable
        if (null == pasteableString) {

            return false;
        }
        int counter = noOfElementsInString(pasteableString);

        if ((posCoordinate.length) == counter) {

            if (validateCells(pasteableString)) {
                int row = pasteableString.length;
                int col = 0;
                int cnt = 0;
                if (row > 0) {
                    col = pasteableString[0].length;
                }
                for (int colIndex = 0; colIndex < col; colIndex++) {
                    for (int rowIndex = 0; rowIndex < row; rowIndex++) {

                        positionCoordinate = posCoordinate[colIndex + rowIndex + cnt];
                        configLabelsByPosition = selectionLayer.getConfigLabelsByPosition(
                                positionCoordinate.columnPosition, positionCoordinate.rowPosition);
                        /*
                         * Handling of paste for complex datatype in properties
                         * window
                         */
                        if (configLabelsByPosition.hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_CUSTOM_DIALOG)
                                || configLabelsByPosition
                                        .hasLabel(IEditTableGridStyleLabelFactory.COL_LABEL_READONLY_CELL)) {
                            continue;
                        }

                        this.selectionLayer
                                .doCommand(new UpdateDataCommand(this.selectionLayer, positionCoordinate.columnPosition,
                                        positionCoordinate.rowPosition, pasteableString[rowIndex][colIndex]));
                    }
                    cnt = cnt + pasteableString.length - 1;
                }
                return true;
            }

        } else {
            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.PASTE_INVALID_SELECTION_DIALOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PASTE_INVALID_SELECTION_DIALOG_MSG),
                    MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));

        }
        return false;
    }

    private int noOfElementsInString(String[][] pasteableString) {
        int counter = 0;

        for (int index = 0; index < pasteableString.length; index++) {
            counter = counter + pasteableString[index].length;
        }
        return counter;
    }

    private PositionCoordinate[] getPostionCoordinates() {
        return this.selectionLayer.getSelectedCellPositions();

    }

    private String[][] convertToPastable() {
        String trstring = null;
        Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();

        try {
            if (system != null) {
                trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));
            }

        } catch (UnsupportedFlavorException exp) {
            MPPDBIDELoggerUtility.error("UnsupportedFlavorException in GridPasteHandler.", exp);
        } catch (IOException ex) {
            MPPDBIDELoggerUtility.error("IOException in GridPasteHandler while converting data from clipboard.", ex);
        }
        if (null == trstring) {
            return new String[0][0];
        }
        return parseTSV(new StringReader(trstring));
    }

    private boolean validateCells(String[][] pastedValue) {
        return true;
    }

    private String[][] parseTSV(StringReader stringReader) {
        ArrayList<String[]> recBuffer = new ArrayList<String[]>(5);
        String[] row = null;
        int index = 0;
        try {
            for (CSVRecord record : CSVFormat.TDF.withQuote(null).parse(stringReader)) {
                row = new String[record.size()];
                index = 0;

                for (String field : record) {
                    row[index++] = field;
                }
                recBuffer.add(row);
            }
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Error while TSV parsing.", exception);
        }

        int recordCount = recBuffer.size();
        String[][] retArray = new String[recordCount][];
        return recBuffer.toArray(retArray);
    }
    
    /**
     * the onPreDestroy
     */
    public void onPreDestroy() {
        this.selectionLayer = null;
    }
}
