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

package org.opengauss.mppdbide.view.utils;

import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
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
 * Description: The Class CommonSqlPreview.
 *
 * @since 3.0.0
 */
public class CommonSqlPreview {
    /** 
     * The menu copy. 
     */
    private MenuItem menuCopy = null;
    
    public CommonSqlPreview() {     
    }
    
    /**
     * Adds the menu items to viewer.
     */
    public void addMenuItemsToViewer(SourceViewer sqlPreviewSourceViewer) {
        Menu menu = new Menu(getControl(sqlPreviewSourceViewer));
        sqlPreviewSourceViewer.getTextWidget().setMenu(menu);
        addSelectAllMenuItem(menu, sqlPreviewSourceViewer);
        addCopyMenuItem(menu, sqlPreviewSourceViewer);
        menu.addMenuListener(addMenuListenerOnMenu(sqlPreviewSourceViewer));
    }

    /**
     * Context menu about to show.
     */
    private void contextMenuAboutToShow(SourceViewer sqlPreviewSourceViewer) {
        if (sqlPreviewSourceViewer.getTextWidget().getSelectionText().isEmpty()) {
            menuCopy.setEnabled(false);
        } else {
            menuCopy.setEnabled(true);
        }
    }

    /**
     * Adds the menu listener on menu.
     *
     * @return the menu listener
     */
    private MenuListener addMenuListenerOnMenu(SourceViewer sqlPreviewSourceViewer) {
        return new MenuListener() {
            @Override
            public void menuShown(MenuEvent event) {
                contextMenuAboutToShow(sqlPreviewSourceViewer);
            }

            @Override
            public void menuHidden(MenuEvent event) {
            }
        };
    }

    /**
     * Adds the copy menu item.
     *
     * @param menu the menu
     */
    private void addCopyMenuItem(Menu menu, SourceViewer sqlPreviewSourceViewer) {
        menuCopy = new MenuItem(menu, SWT.PUSH);
        menuCopy.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_COPY));
        menuCopy.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                sqlPreviewSourceViewer.doOperation(ITextOperationTarget.COPY);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });
        menuCopy.setImage(IconUtility.getIconImage(IiconPath.ICO_COPY, this.getClass()));
    }

    /**
     * Adds the select all menu item.
     *
     * @param menu the menu
     */
    private void addSelectAllMenuItem(Menu menu, SourceViewer sqlPreviewSourceViewer) {
        MenuItem sysnonymMenuSelectAll = new MenuItem(menu, SWT.PUSH);
        sysnonymMenuSelectAll.setText(MessageConfigLoader
                                        .getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_SELECTALL));
        sysnonymMenuSelectAll.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                sqlPreviewSourceViewer.doOperation(ITextOperationTarget.SELECT_ALL);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });
    }
    
    private Control getControl(SourceViewer sqlPreviewSourceViewer) {
        return ControlUtils.getControl(sqlPreviewSourceViewer);
    }
}
