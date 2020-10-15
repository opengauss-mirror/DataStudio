/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
