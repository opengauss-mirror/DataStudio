/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
import org.eclipse.nebula.widgets.nattable.copy.serializing.CopyFormattedTextToClipboardSerializer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.serializing.ISerializer;

import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSCopyDataCommandHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSCopyDataCommandHandler extends CopyDataCommandHandler {

    private boolean copyFormattedText;
    private IDSGridDataProvider dataProvider;

    /**
     * Instantiates a new DS copy data command handler.
     *
     * @param selectionLayer the selection layer
     * @param columnHeaderLayer the column header layer
     * @param rowNumberLayer the row number layer
     * @param dataProvider the data provider
     */
    public DSCopyDataCommandHandler(SelectionLayer selectionLayer, ILayer columnHeaderLayer, ILayer rowNumberLayer,
            IDSGridDataProvider dataProvider) {
        super(selectionLayer, columnHeaderLayer, rowNumberLayer);
        this.dataProvider = dataProvider;
    }

    /**
     * Do command.
     *
     * @param command the command
     * @return true, if successful
     */
    @Override
    public boolean doCommand(CopyDataToClipboardCommand command) {
        ILayerCell[][] selectedCells = assembleCopiedDataStructure();
        if (null != selectedCells) {
            ISerializer serializer = this.copyFormattedText
                    ? new CopyFormattedTextToClipboardSerializer(selectedCells, command)
                    : new DSCopyDataToClipboardSerializer(selectedCells, command, dataProvider);
            serializer.serialize();
        }
        return true;
    }

    /**
     * Sets the copy formatted text.
     *
     * @param copyFormattedText the new copy formatted text
     */
    public void setCopyFormattedText(boolean copyFormattedText) {
        // Calling super to ensure the private copyFormattedText of parent class
        // is updated
        super.setCopyFormattedText(copyFormattedText);
        this.copyFormattedText = copyFormattedText;
    }

    /**
     * Sets the data provider.
     *
     * @param dataProvider the new data provider
     */
    public void setDataProvider(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        dataProvider = null;
    }
}
