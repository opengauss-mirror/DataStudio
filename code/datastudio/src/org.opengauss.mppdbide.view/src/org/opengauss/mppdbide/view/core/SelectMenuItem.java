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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SelectMenuItem.
 *
 * @since 3.0.0
 */
public abstract class SelectMenuItem {

    /**
     * The menu select all.
     */
    protected MenuItem menuSelectAll;

    /**
     * The menu copy.
     */
    protected MenuItem menuCopy;

    /**
     * Gets the menu select all.
     *
     * @return the menu select all
     */
    public MenuItem getMenuSelectAll() {
        return menuSelectAll;
    }

    /**
     * Gets the menu copy.
     *
     * @return the menu copy
     */
    public MenuItem getMenuCopy() {
        return menuCopy;
    }

    /**
     * Adds the select all menu item.
     *
     * @param menu the menu
     */
    protected void addSelectAllMenuItem(Menu menu) {
        menuSelectAll = new MenuItem(menu, SWT.PUSH);
        menuSelectAll.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_SELECTALL));
        menuSelectAll.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                selectAllDocText();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    /**
     * Adds the copy menu item.
     *
     * @param menu the menu
     */
    protected void addCopyMenuItem(Menu menu) {
        menuCopy = new MenuItem(menu, SWT.PUSH);
        menuCopy.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_COPY));
        menuCopy.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                copyDocText();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
        menuCopy.setImage(IconUtility.getIconImage(IiconPath.ICO_COPY, this.getClass()));
    }

    /**
     * Select all doc text.
     */
    protected abstract void selectAllDocText();

    /**
     * Gets the control.
     *
     * @return the control
     */
    protected abstract Control getControl();

    /**
     * Copy doc text.
     */
    protected abstract void copyDocText();
}
