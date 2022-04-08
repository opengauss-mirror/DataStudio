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

package org.opengauss.mppdbide.view.objectpropertywiew;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.presentation.edittabledata.CommitStatus;
import org.opengauss.mppdbide.presentation.objectproperties.IObjectPropertyData;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesConstants;
import org.opengauss.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.component.grid.DSGridComponent;
import org.opengauss.mppdbide.view.component.grid.core.DataGrid;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewObjectPropertyTabManager.
 *
 * @since 3.0.0
 */
public class ViewObjectPropertyTabManager {

    /**
     * The parent.
     */
    protected Composite parent;

    /**
     * The tab foldr.
     */
    protected CTabFolder tabFoldr = null;

    /**
     * The view object properties result display UI manager.
     */
    protected ViewObjectPropertiesResultDisplayUIManager viewObjectPropertiesResultDisplayUIManager;

    /**
     * Instantiates a new view object property tab manager.
     *
     * @param parent the parent
     * @param viewObjectPropertiesResultDisplayUIManager the view object
     * properties result display UI manager
     */
    public ViewObjectPropertyTabManager(Composite parent,
            ViewObjectPropertiesResultDisplayUIManager viewObjectPropertiesResultDisplayUIManager) {
        this.parent = parent;
        this.viewObjectPropertiesResultDisplayUIManager = viewObjectPropertiesResultDisplayUIManager;
        createTabFolder();
    }

    private void createTabFolder() {
        if (this.parent.isDisposed()) {
            return;
        }
        tabFoldr = new CTabFolder(this.parent, SWT.BORDER | SWT.NONE);
        tabFoldr.setLayout(new GridLayout());

        tabFoldr.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tabFoldr.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent selectEvent) {

                if (selectEvent.item instanceof ViewObjectPropertyTab) {
                    ((ViewObjectPropertyTab) selectEvent.item).handleFocus();
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    /**
     * Gets the tab folder definite.
     *
     * @return the tab folder definite
     */
    protected CTabFolder getTabFolderDefinite() {
        if (tabFoldr == null) {
            createTabFolder();
        }

        return tabFoldr;
    }

    /**
     * Creates the result.
     *
     * @param propDetails the prop details
     */
    public void createResult(IPropertyDetail propDetails) {
        getTabFolderDefinite();

        tabFoldr.setLayoutData(new GridData(GridData.FILL_BOTH));

        for (IObjectPropertyData oneTabData : propDetails.objectproperties()) {
            Composite composite = new Composite(tabFoldr, SWT.NONE);

            ViewObjectPropertyTab tab = new ViewObjectPropertyTab(tabFoldr, SWT.NONE, composite, oneTabData, this);
            tab.init(viewObjectPropertiesResultDisplayUIManager);
            setTabProperties(tab, oneTabData);
        }

        tabFoldr.setSelection(0);

    }

    /**
     * Reset result.
     *
     * @param propDetails the prop details
     */
    public void resetResult(IPropertyDetail propDetails) {
        int cnt = 0;
        for (IObjectPropertyData oneTabData : propDetails.objectproperties()) {
            ViewObjectPropertyTab tab = (ViewObjectPropertyTab) tabFoldr.getItem(cnt);
            tab.resetData(oneTabData);
            cnt++;
        }
    }

    /*
     * reset a particular tab data after refresh
     */

    /**
     * Reset tab data.
     *
     * @param tabData the tab data
     */
    public void resetTabData(IObjectPropertyData tabData) {
        String objectPropertyName = tabData.getObjectPropertyName();
        CTabItem[] items = tabFoldr.getItems();

        for (int i = 0; i < items.length; i++) {
            if (!items[i].isDisposed() && items[i].getData().equals(getTabHeader(objectPropertyName))) {
                ViewObjectPropertyTab tab = (ViewObjectPropertyTab) tabFoldr.getItem(i);
                tab.resetData(tabData);
                break;
            }

        }
    }

    /**
     * Sets the tab properties.
     *
     * @param tab the tab
     * @param oneTabData the one tab data
     */
    protected void setTabProperties(ViewObjectPropertyTab tab, IObjectPropertyData oneTabData) {
        tab.setText(getTabHeader(oneTabData.getObjectPropertyName()));
        tab.setData(getTabHeader(oneTabData.getObjectPropertyName()));
        tab.setToolTipText(getTabHeader(oneTabData.getObjectPropertyName()));
    }

    /**
     * Gets the tab header.
     *
     * @param tabName the tab name
     * @return the tab header
     */
    protected String getTabHeader(String tabName) {
        switch (tabName) {
            case PropertiesConstants.GENERAL: {
                return MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_GENERAL_TAB);
            }
            case PropertiesConstants.COLUMNS: {
                return MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_COLUMNS_TAB);
            }
            case PropertiesConstants.CONSTRAINTS: {
                return MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_CONSTRAINTS_TAB);
            }
            case PropertiesConstants.INDEX: {
                return MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_INDEX_TAB);
            }
            case PropertiesConstants.PARTITION: {
                return MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_PARTITIONS_TAB);
            }
            case PropertiesConstants.USER_ROLE_PROPERTY_TAB_GENERAL: {
                return MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_PROPERTY_TAB_GENERAL);
            }
            case PropertiesConstants.USER_ROLE_PROPERTY_TAB_PRIVILEGE: {
                return MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_PROPERTY_TAB_PRIVILEGE);
            }
            case PropertiesConstants.USER_ROLE_PROPERTY_TAB_MEMBERSHIP: {
                return MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_PROPERTY_TAB_MEMBERSHIP);
            }
            case PropertiesConstants.CHECKS: {
                return MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_CHECKS_TAB);
            }
            case PropertiesConstants.DISTRIBUTION: {
                return MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_DISTRIBUTION_TAB);
            }
            case PropertiesConstants.KEYS: {
                return MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_KEYS_TAB);
            }
            default: {
                break;
            }
        }
        return tabName;
    }

    /**
     * Handle toolbar icons.
     *
     * @param isDatabaseConnected the is database connected
     */
    public void handleToolbarIcons(boolean isDatabaseConnected) {
        CTabItem[] items = tabFoldr.getItems();
        for (int i = 0; i < items.length; i++) {
            ViewObjectPropertyTab tab = (ViewObjectPropertyTab) tabFoldr.getItem(i);
            tab.enableDisableTabIcons(isDatabaseConnected);

        }
    }

    /**
     * Checks if is any tab edited.
     *
     * @return true, if is any tab edited
     */
    public boolean isAnyTabEdited() {
        boolean isEdited = false;
        for (CTabItem tab : tabFoldr.getItems()) {
            if (tab instanceof ViewObjectPropertyTab && ((ViewObjectPropertyTab) tab).isTabEdited()) {
                isEdited = isEdited || true;
                final ViewObjectPropertyTab propTab = (ViewObjectPropertyTab) tab;
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        propTab.setEditedStatus(true);
                    }
                });
            } else {
                final ViewObjectPropertyTab propTab = (ViewObjectPropertyTab) tab;
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        propTab.setEditedStatus(false);
                    }
                });
            }
        }
        return isEdited;
    }

    /**
     * Reset tab buttons.
     *
     * @param tabData the tab data
     */
    public void resetTabButtons(IObjectPropertyData tabData) {

    }

    /**
     * Gets the all tab grids.
     *
     * @return the all tab grids
     */
    public List<DataGrid> getAllTabGrids() {
        ArrayList<DataGrid> grids = new ArrayList<DataGrid>(this.tabFoldr.getItemCount());

        for (int t = 0; t < this.tabFoldr.getItemCount(); t++) {
            ViewObjectPropertyTab tab = (ViewObjectPropertyTab) this.tabFoldr.getItem(t);
            if (!this.tabFoldr.getItem(t).getData()
                    .equals(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_DDL_TAB))) {
                grids.add(tab.getDataGrid());
            }
        }
        return grids;
    }

    /**
     * Gets the all tab grid components.
     *
     * @return the all tab grid components
     */
    public List<DSGridComponent> getAllTabGridComponents() {
        ArrayList<DSGridComponent> grids = new ArrayList<DSGridComponent>(this.tabFoldr.getItemCount());

        for (int t = 0; t < this.tabFoldr.getItemCount(); t++) {
            ViewObjectPropertyTab tab = (ViewObjectPropertyTab) this.tabFoldr.getItem(t);
            // the grids having data should be added to the list
            if (!this.tabFoldr.isDisposed() && !this.tabFoldr.getItem(t).getData()
                    .equals(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_DDL_TAB))) {
                grids.add(tab.gridComponent);
            }
        }
        return grids;
    }

    /**
     * Update tab dirty label.
     *
     * @param tabId the tab id
     * @param isDirtyParam the is dirty param
     */
    public void updateTabDirtyLabel(int tabId, boolean isDirtyParam) {
        boolean isDirty = isDirtyParam;
        if (tabId < this.tabFoldr.getItemCount()) {
            ViewObjectPropertyTab tab = (ViewObjectPropertyTab) this.tabFoldr.getItem(tabId);
            isDirty = tab.isTabEdited && isDirty;
            tab.setEditedStatus(isDirty);
        }
    }

    /**
     * Sets the commit status.
     *
     * @param dataObj the new commit status
     */
    public void setCommitStatus(List<CommitStatus> dataObj) {
        int cnt = 0;
        if (dataObj.size() != 0) {
            for (DSGridComponent gridComponent : getAllTabGridComponents()) {
                if (dataObj.size() > cnt) {
                    gridComponent.setCommitStatus(dataObj.get(cnt));
                }
                cnt++;
            }
        }
    }

}
