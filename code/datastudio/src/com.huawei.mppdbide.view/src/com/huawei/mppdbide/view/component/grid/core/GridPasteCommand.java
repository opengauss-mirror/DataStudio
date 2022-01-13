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
 * @since 3.0.0
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
