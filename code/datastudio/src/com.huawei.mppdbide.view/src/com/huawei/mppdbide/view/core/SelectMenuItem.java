/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SelectMenuItem.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
        // DTS2016011900019 Starts
        menuSelectAll.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_SELECTALL));
        // DTS2016011900019 Ends
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
        // DTS2016011900019 Starts
        menuCopy.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_COPY));
        // DTS2016011900019 Ends
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
