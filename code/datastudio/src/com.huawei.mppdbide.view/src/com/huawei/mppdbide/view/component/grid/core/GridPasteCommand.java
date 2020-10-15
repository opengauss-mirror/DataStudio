/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.copy.command.PasteDataCommand;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridPasteCommand.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GridPasteCommand extends PasteDataCommand {

    /**
     * Instantiates a new grid paste command.
     *
     * @param configRegistry the config registry
     */
    public GridPasteCommand(IConfigRegistry configRegistry) {
        super(configRegistry);
    }

    /**
     * Convert to target layer.
     *
     * @param targetLayer the target layer
     * @return true, if successful
     */
    @Override
    public boolean convertToTargetLayer(ILayer targetLayer) {
        return super.convertToTargetLayer(targetLayer);
    }

    /**
     * Clone command.
     *
     * @return the abstract context free command
     */
    @Override
    public AbstractContextFreeCommand cloneCommand() {
        return super.cloneCommand();
    }

}
