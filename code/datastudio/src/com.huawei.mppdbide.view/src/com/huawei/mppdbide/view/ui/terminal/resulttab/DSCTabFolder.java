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

package com.huawei.mppdbide.view.ui.terminal.resulttab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MemoryCleaner;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.ui.ResultSetWindow;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSCTabFolder.
 *
 * @since 3.0.0
 */
public final class DSCTabFolder {

    private DSCTabFolder() {
    }

    private static int cleanupTabFolder(final CTabFolder tabFolder, int toBeDisposed) {
        CTabItem tab = tabFolder.getItem(toBeDisposed);
        if (tab instanceof IResultTab) {
            IResultTab rTab = (IResultTab) tab;
            manipulateResultTabCount(tab, rTab);
            if (rTab.isResultTabDirty() && !ResultSetWindow.isDiscardAllModified()) {
                if (!ResultSetWindow.isCancelForAllModified() && rTab.preDestroy()) {
                    performCleanUpActivity(tabFolder, toBeDisposed);
                } else {
                    toBeDisposed++;
                }
            } else {
                modifyDirtyTabCntOnResultDirty(rTab);
                if (rTab.preDestroy()) {
                    performCleanUpActivity(tabFolder, toBeDisposed);
                }
            }

        }
        return toBeDisposed;
    }

    private static void modifyDirtyTabCntOnResultDirty(IResultTab rTab) {
        if (rTab.isResultTabDirty()) {
            rTab.getParentTabManager().modifyDirtyTabCount(false);
        }
    }

    /**
     * Creates the right clk menu.
     *
     * @param tabFolder the tab folder
     */
    public static void createRightClkMenu(final CTabFolder tabFolder) {

        if (tabFolder != null) {

            Menu popupMenu = new Menu(tabFolder);
            final MenuItem closeItem = new MenuItem(popupMenu, SWT.CASCADE);
            closeItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.CLOSE_TAB_MENU));
            closeItem.addSelectionListener(closeItemSelectionListener(tabFolder));

            final MenuItem closeOthers = new MenuItem(popupMenu, SWT.NONE);
            closeOthers.setText(MessageConfigLoader.getProperty(IMessagesConstants.CLOSE_OTHER_TABS_MENU));
            closeOthers.addSelectionListener(closeOthersSelectionListener(tabFolder));

            final MenuItem closeAll = new MenuItem(popupMenu, SWT.NONE);
            closeAll.setText(MessageConfigLoader.getProperty(IMessagesConstants.CLOSE_ALL_TAB_MENU));
            closeAll.addSelectionListener(closeAllSelectionListener(tabFolder));
            tabFolder.setMenu(popupMenu);
            popupMenu.addMenuListener(popupMenuSelectionListener(tabFolder, closeItem, closeOthers, closeAll));
        }
    }

    private static MenuListener popupMenuSelectionListener(final CTabFolder tabFolder, final MenuItem closeItem,
            final MenuItem closeOthers, final MenuItem closeAll) {
        return new MenuListener() {

            @Override
            public void menuShown(MenuEvent e) {

                if (null != tabFolder.getSelection()) {
                    enabledisableBtnsOnTabSelection(tabFolder, closeItem, closeOthers, closeAll);
                }

            }

            @Override
            public void menuHidden(MenuEvent e) {
            }
        };
    }

    private static void enabledisableBtnsOnTabSelection(final CTabFolder tabFolder, final MenuItem closeItem,
            final MenuItem closeOthers, final MenuItem closeAll) {
        String itemData = null;
        if (tabFolder.getSelection() != null) {
            itemData = (String) tabFolder.getSelection().getData();
        }
        
        if ("console".equalsIgnoreCase(itemData)) {
            enableDisableCloseOthersItem(tabFolder, closeOthers);
            closeItem.setEnabled(false);
            closeAll.setEnabled(false);
        } else {
            closeItem.setEnabled(true);
            closeAll.setEnabled(true);
            closeOthers.setEnabled(true);
        }
    }

    private static void enableDisableCloseOthersItem(final CTabFolder tabFolder, final MenuItem closeOthers) {
        if (tabFolder.getItems().length == 1) {
            closeOthers.setEnabled(false);
        } else {
            closeOthers.setEnabled(true);
        }
    }

    private static SelectionListener closeAllSelectionListener(final CTabFolder tabFolder) {
        return new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                closeAllCleanUp(tabFolder);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    /**
     * Close all clean up.
     *
     * @param tabFolder the tab folder
     */
    public static void closeAllCleanUp(final CTabFolder tabFolder) {
        int toBeDisposed = 0;

        if (null != tabFolder.getSelection()) {
            String itemData = (String) tabFolder.getItem(0).getData();
            if ("console".equalsIgnoreCase(itemData)) {
                toBeDisposed = 1;
            }
            while (tabFolder.getItems().length > toBeDisposed) {
                toBeDisposed = cleanupTabFolder(tabFolder, toBeDisposed);
            }

        }
        MemoryCleaner.cleanUpMemory();
        ResultSetWindow.setDiscardAllModified(false);
        ResultSetWindow.setCancelForAllModified(false);
    }

    private static SelectionListener closeOthersSelectionListener(final CTabFolder tabFolder) {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performActionOnCloseOther(tabFolder);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    private static SelectionListener closeItemSelectionListener(final CTabFolder tabFolder) {
        return new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (null != tabFolder.getSelection()) {

                    CTabItem tab = tabFolder.getSelection();
                    if (tab instanceof IResultTab) {
                        updateResultWindowCount(tab);
                    }
                    MemoryCleaner.cleanUpMemory();
                    ResultSetWindow.setDiscardAllModified(false);
                    ResultSetWindow.setCancelForAllModified(false);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    private static void updateResultWindowCount(CTabItem tab) {
        if (tab instanceof IResultTab) {
            IResultTab rTab = (IResultTab) tab;
            manipulateResultTabCount(tab, rTab);
            if (rTab.preDestroy()) {
                rTab.dispose();
                UIElement.getInstance().updateResultWindowCounterOnClose();
            }  
        }
    }

    private static void performCleanUpActivity(final CTabFolder tabFolder, int toBeDisposed) {
        if (!tabFolder.getItem(toBeDisposed).isDisposed() && tabFolder.getItem(toBeDisposed).getControl() != null
                && !tabFolder.getItem(toBeDisposed).getControl().isDisposed()) {
            CTabItem item = tabFolder.getItem(toBeDisposed);
            Control ctrl = tabFolder.getItem(toBeDisposed).getControl();
            item.addDisposeListener(new DisposeListener() {  
                /**
                 * widget disposed
                 * 
                 * @param event the event
                 */
                public void widgetDisposed(DisposeEvent event) {
                    if (event.widget instanceof CTabItem) {
                        if (((CTabItem) event.widget).getControl() != null) {
                            ((CTabItem) event.widget).getControl().dispose();
                        }
                    }
                }
            });
            ctrl = null;
            tabFolder.getItem(toBeDisposed).dispose();
        }
        UIElement.getInstance().updateResultWindowCounterOnClose();
    }

    private static void performActionOnCloseOther(final CTabFolder tabFolder) {
        if (null != tabFolder.getSelection()) {
            int selectedTabIndex = tabFolder.getSelectionIndex();
            int toBeDisposed = 0;
            int counter = 0;
            String itemData = (String) tabFolder.getItem(0).getData();
            if ("console".equalsIgnoreCase(itemData)) {
                toBeDisposed = 1;
                counter = 1;
            }

            while (tabFolder.getItems().length > toBeDisposed) {
                if (counter == selectedTabIndex) {
                    toBeDisposed++;
                    counter++;
                    continue;
                } else {
                    toBeDisposed = cleanupTabFolder(tabFolder, toBeDisposed);
                }
                counter++;
            }
            MemoryCleaner.cleanUpMemory();
            ResultSetWindow.setDiscardAllModified(false);
            ResultSetWindow.setCancelForAllModified(false);
        }
    }

    private static void manipulateResultTabCount(CTabItem tab, IResultTab rTab) {
        if (tab.getText().startsWith(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB))) {
            rTab.getParentTabManager().getResultTabCountMap().put(
                    MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB),
                    rTab.getParentTabManager().getResultTabCountMap()
                            .get(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB)) - 1);
        } else if (tab.getText().startsWith(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB))) {
            rTab.getParentTabManager().getResultTabCountMap().put(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB),
                    rTab.getParentTabManager().getResultTabCountMap()
                            .get(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB)) - 1);
        }
    }
}
