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

import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
import org.eclipse.nebula.widgets.nattable.copy.serializing.CopyFormattedTextToClipboardSerializer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.serializing.ISerializer;

import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSCopyDataCommandHandler.
 *
 * @since 3.0.0
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
