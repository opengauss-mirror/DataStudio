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

package org.opengauss.mppdbide.view.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.EditController;
import org.eclipse.nebula.widgets.nattable.edit.command.EditCellCommand;
import org.eclipse.nebula.widgets.nattable.edit.gui.ICellEditDialog;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.component.grid.IEditTableGridStyleLabelFactory;

/**
 * 
 * Title: class
 * 
 * Description: The Class TableCellCommand.
 *
 * @since 3.0.0
 */
public class TableCellCommand extends AbstractLayerCommandHandler<EditCellCommand> {

    /**
     * Gets the command class.
     *
     * @return the command class
     */
    @Override
    public Class<EditCellCommand> getCommandClass() {
        return EditCellCommand.class;
    }

    /**
     * Do command.
     *
     * @param command the command
     * @return true, if successful
     */
    @Override
    public boolean doCommand(EditCellCommand command) {
        ILayerCell cell = command.getCell();
        Composite parent = command.getParent();
        IConfigRegistry configRegistry = command.getConfigRegistry();
        if (cell != null && configRegistry != null) {

            // check if the cell is editable
            IEditableRule rule = configRegistry.getConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
                    DisplayMode.EDIT, cell.getConfigLabels().getLabels());
            Object originalCanonicalValue = cell.getDataValue();
            if (null != originalCanonicalValue
                    && originalCanonicalValue.toString().length() > IEditTableGridStyleLabelFactory.CANONICAL_LIMIT) {
                configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_IN_DIALOG, Boolean.TRUE,
                        DisplayMode.EDIT, ParameterInputDialog.PARAMETER_VALUE_LABEL);
                Display display = Display.getCurrent();
                // Set the multiline text window style
                Map<String, Object> editDialogSettings = new HashMap<>();
                editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_TITLE,
                        MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_VARIABLE_COLUMN_THREE));
                editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_ICON, display.getSystemImage(SWT.ICON_SEARCH));
                editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_RESIZABLE, Boolean.TRUE);
                Point size = new Point(400, 300);
                editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_SIZE, size);

                int screenWidth = display.getBounds().width;
                int screenHeight = display.getBounds().height;
                int monitorsAttached = display.getMonitors().length;
                if (monitorsAttached != 0) {
                    Point location = new Point((screenWidth / (2 * monitorsAttached)) - (size.x / 2),
                            (screenHeight / 2) - (size.y / 2));
                    editDialogSettings.put(ICellEditDialog.DIALOG_SHELL_LOCATION, location);
                }
                // add custum message
                editDialogSettings.put(ICellEditDialog.DIALOG_MESSAGE,
                        MessageConfigLoader.getProperty(IMessagesConstants.WRITE_HERE));

                configRegistry.registerConfigAttribute(EditConfigAttributes.EDIT_DIALOG_SETTINGS, editDialogSettings,
                        DisplayMode.EDIT, ParameterInputDialog.PARAMETER_VALUE_LABEL);

            } else {
                configRegistry.unregisterConfigAttribute(EditConfigAttributes.OPEN_IN_DIALOG, DisplayMode.EDIT,
                        ParameterInputDialog.PARAMETER_VALUE_LABEL);
                configRegistry.unregisterConfigAttribute(EditConfigAttributes.EDIT_DIALOG_SETTINGS, DisplayMode.EDIT,
                        ParameterInputDialog.PARAMETER_VALUE_LABEL);
            }

            if (rule.isEditable(cell, configRegistry)) {
                EditController.editCell(cell, parent, cell.getDataValue(), configRegistry);
            }
        }

        // successful or not
        return true;
    }

}
