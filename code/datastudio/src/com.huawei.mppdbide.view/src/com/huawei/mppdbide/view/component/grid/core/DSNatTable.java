/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
