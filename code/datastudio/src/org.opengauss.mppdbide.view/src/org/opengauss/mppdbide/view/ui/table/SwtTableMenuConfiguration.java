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

package org.opengauss.mppdbide.view.ui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SwtTableMenuConfiguration.
 *
 * @since 3.0.0
 */
public class SwtTableMenuConfiguration {

    private static final String MENUITEM_COPY = "Copy";
    private static final String SINGLE_SPACE = " ";
    private MenuItem copyItem;

    /**
     * Adds the copy menu item.
     *
     * @param table the table
     */
    public void addCopyMenuItem(Table table) {
        Menu contextMenu = new Menu(table);
        table.setMenu(contextMenu);
        copyItem = new MenuItem(contextMenu, SWT.NONE);
        copyItem.setText(MENUITEM_COPY);
        table.addListener(SWT.MenuDetect, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (table.getSelectionCount() <= 0) {
                    event.doit = false;
                    return;
                }
                TableItem[] selection = table.getSelection();
                if (selection.length != 0 && (event.button == 3)) {
                    contextMenu.setVisible(true);
                }
            }
        });
        copyItem.addListener(SWT.Selection, new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                StringBuilder str = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
                str.append(table.getSelection()[0].getText(0));
                str.append(SINGLE_SPACE);
                str.append(table.getSelection()[0].getText(1));
                final Clipboard clipboard = new Clipboard(table.getDisplay());
                clipboard.setContents(new String[] {str.toString()}, new TextTransfer[] {TextTransfer.getInstance()});
                clipboard.dispose();
            }
        });
    }
}
