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

package com.huawei.mppdbide.view.component.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSGridMenuManager.
 *
 * @since 3.0.0
 */
public class DSGridMenuManager {

    /**
     * Adds the context menu with ID.
     *
     * @param editControl the edit control
     * @param isCellEditable the is cell editable
     */
    public static void addContextMenuWithID(final Text editControl, boolean isCellEditable) {
        Menu menu = new Menu(editControl);
        MenuItem copyItem = new MenuItem(menu, SWT.PUSH);
        copyItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_COPY));
        copyItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                editControl.copy();

            }
        });
        if (isCellEditable) {
            MenuItem pasteItem = new MenuItem(menu, SWT.PUSH);
            pasteItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_PASTE));
            pasteItem.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    editControl.paste();
                }
            });
        }
        MenuItem selectAllItem = new MenuItem(menu, SWT.PUSH);
        selectAllItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_SELECTALL));
        selectAllItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                editControl.selectAll();
            }
        });
        editControl.setMenu(menu);
    }
}
