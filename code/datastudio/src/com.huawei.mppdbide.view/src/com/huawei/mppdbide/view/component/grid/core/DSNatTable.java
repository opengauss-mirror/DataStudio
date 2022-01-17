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

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.command.ClientAreaResizeCommand;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSNatTable.
 *
 * @since 3.0.0
 */
public class DSNatTable extends NatTable {

    /**
     * Instantiates a new DS nat table.
     *
     * @param parent the parent
     * @param gridLayer the grid layer
     * @param boolValue the b
     */
    public DSNatTable(Composite parent, GridLayer gridLayer, boolean boolValue) {
        super(parent, gridLayer, boolValue);
    }

    /**
     * Clear configurations.
     */
    public void clearConfigurations() {

        this.configurations.clear();
    }

    /**
     * Sets the layer.
     *
     * @param layer the new layer
     */
    public void setLayer(GridLayer layer) {
        super.setLayer(layer);
        // done to enable scrollbar listeners
        doCommand(new ClientAreaResizeCommand(this));
    }

    /**
     * Handle layer event.
     *
     * @param event the event
     */
    @Override
    public void handleLayerEvent(ILayerEvent event) {
        if (!this.isDisposed()) {
            super.handleLayerEvent(event);
        }
    }

    /**
     *  clear Layers : to clear mem leak
     */
    public void clearLayers() {
        super.setLayerPainter(null);
    }

}
