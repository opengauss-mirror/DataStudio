/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.renderers.swt.StackRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;

/**
 * 
 * Title: class
 * 
 * Description: The Class DsStackRenderer.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DsStackRenderer extends StackRenderer {
    private static final int TAB_MIN_CHARS = 15;
    @Inject
    private EHandlerService handlerService;

    @Inject
    private ECommandService commandService;

    /**
     * Preference change.
     *
     * @param event the event
     */
    @Override
    public void preferenceChange(PreferenceChangeEvent event) {
        super.preferenceChange(event);
    }

    /**
     * Show tab.
     *
     * @param element the element
     */
    @Override
    protected void showTab(MUIElement element) {
        ((CTabFolder) getParentWidget(element)).setMinimumCharacters(TAB_MIN_CHARS);
        super.showTab(element);
    }

    private void i10nlizeTabContextMenuText(Menu menu) {

        /**
         * Default Eclipse text that needs to be translated to Chinese:
         * 
         * choosePartsToSaveTitle=Save Parts choosePartsToSave=Select the parts
         * to save: menuClose = &Close menuCloseOthers = Close &Others
         * menuCloseAll = Close &All menuCloseRight = Close Tabs to the &Right
         * menuCloseLeft = Close Tabs to the &Left viewMenu = View Menu
         * tabScrollingLeft = Scroll list left tabScrollingRight = Scroll list
         * right
         */

        String itemText;
        String newItemText = "";
        List<MenuItem> listToRemove = new ArrayList<MenuItem>(1);
        addContexMenuText(menu, listToRemove);

        if (listToRemove.size() >= 1) {
            listToRemove.stream().forEach(item -> {
                item.dispose();
            });
        }
    }

    private void addContexMenuText(Menu menu, List<MenuItem> listToRemove) {
        String itemText;
        String newItemText = null;
        for (MenuItem item : menu.getItems()) {
            itemText = item.getText();

            switch (itemText) {
                case "&Close": {
                    newItemText = MessageConfigLoader.getProperty(IMessagesConstants.CLOSE_TAB_MENU);
                    break;
                }
                case "Close &Others": {
                    newItemText = MessageConfigLoader.getProperty(IMessagesConstants.CLOSE_OTHER_TABS_MENU);
                    break;
                }
                case "Close &All": {
                    newItemText = MessageConfigLoader.getProperty(IMessagesConstants.CLOSE_ALL_TAB_MENU);
                    break;
                }
                case "Close Tabs to the &Right": {
                    newItemText = MessageConfigLoader.getProperty(IMessagesConstants.CLOSE_RIGHT_TABS_MENU);
                    break;
                }
                case "Close Tabs to the &Left": {
                    newItemText = MessageConfigLoader.getProperty(IMessagesConstants.CLOSE_LEFT_TABS_MENU);
                    break;
                }
                // Below are dead code as we donot use dirty flags and save all
                // SQL terminal Windows (except for Edit table data window).
                case "Save Parts": {
                    newItemText = MessageConfigLoader.getProperty(IMessagesConstants.SAVE_PARTS);
                    break;
                }
                case "Select the parts to save:": {
                    newItemText = MessageConfigLoader.getProperty(IMessagesConstants.SELECT_PARTS_FOR_SAVE);
                    break;
                }
                case "&Detach":
                case "": {
                    listToRemove.add(item);
                    break;
                }
                default: {
                    newItemText = itemText;
                    break;
                }
            }
            if (newItemText != null) {
                item.setText(newItemText);
            }
        }
    }

    /**
     * Populate tab menu.
     *
     * @param menu the menu
     * @param part the part
     */
    @Override
    protected void populateTabMenu(final Menu menu, final MPart part) {
        if (part.getObject() instanceof SQLTerminal) {
            MenuItem menuItem = new MenuItem(menu, SWT.NONE);
            new MenuItem(menu, SWT.SEPARATOR);
            menuItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TERMINAL_NAME));
            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String sqlTerminalPart = "com.huawei.mppdbide.view.commandparameter.sqlTerminalPart";
                    Map<String, Object> params = new HashMap<String, Object>(5);
                    params.put(sqlTerminalPart, part.getElementId());
                    ParameterizedCommand cmd = commandService
                            .createCommand("com.huawei.mppdbide.view.command.renameSqlTerminal", params);
                    if (handlerService.canExecute(cmd)) {
                        handlerService.executeHandler(cmd);
                    }
                }
            });

            if (((SQLTerminal) (part.getObject())).isFileTerminalFlag()) {
                menuItem.setEnabled(false);
            }
        }
        super.populateTabMenu(menu, part);
        i10nlizeTabContextMenuText(menu);
    }

}
