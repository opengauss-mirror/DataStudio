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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.UserRoleManager;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.IObjectPropertyData;
import org.opengauss.mppdbide.presentation.objectproperties.IServerObjectProperties;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesConstants;
import org.opengauss.mppdbide.presentation.objectproperties.PropertiesUserRoleImpl;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.observer.IDSGridUIListenable;
import org.opengauss.mppdbide.view.component.TabGridUIPreference;
import org.opengauss.mppdbide.view.component.grid.DSGridComponent;
import org.opengauss.mppdbide.view.component.grid.core.DataGrid;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewObjectPropertyTab.
 *
 * @since 3.0.0
 */
public class ViewObjectPropertyTab extends CTabItem {

    /**
     * The grid component.
     */
    protected DSGridComponent gridComponent;

    /**
     * The composite.
     */
    protected Composite composite;

    /**
     * The result grid UI pref.
     */
    protected ObjectPropertyGridUIPreference resultGridUIPref;

    /**
     * The resultset displaydata.
     */
    protected IDSGridDataProvider resultsetDisplaydata;

    /**
     * The view object properties result display UI manager.
     */
    protected ViewObjectPropertiesResultDisplayUIManager viewObjectPropertiesResultDisplayUIManager;

    /**
     * The is tab edited.
     */
    protected boolean isTabEdited;

    /**
     * The Constant NULL_VALUE.
     */
    protected static final String NULL_VALUE = "[NULL]";

    /**
     * The tab folder.
     */
    protected CTabFolder tabFolder;

    /**
     * Instantiates a new view object property tab.
     *
     * @param parent the parent
     * @param style the style
     * @param composite the composite
     * @param resultsetDisplaydata the resultset displaydata
     * @param viewObjectPropertyTabManager the view object property tab manager
     */
    public ViewObjectPropertyTab(CTabFolder parent, int style, Composite composite,
            IDSGridDataProvider resultsetDisplaydata, ViewObjectPropertyTabManager viewObjectPropertyTabManager) {
        super(parent, style);
        tabFolder = parent;
        this.resultsetDisplaydata = resultsetDisplaydata;
        setControl(composite);
        this.resultGridUIPref = new ObjectPropertyGridUIPreference(resultsetDisplaydata);
        this.gridComponent = new DSGridComponent(resultGridUIPref, resultsetDisplaydata);
        this.composite = composite;
    }

    /**
     * Inits the.
     *
     * @param viewObjPropsResultDisplayUIManager the view obj props result
     * display UI manager
     */
    public void init(ViewObjectPropertiesResultDisplayUIManager viewObjPropsResultDisplayUIManager) {
        this.viewObjectPropertiesResultDisplayUIManager = viewObjPropsResultDisplayUIManager;
        this.gridComponent.createComponents(this.composite);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_PROPERITES_COMMIT_DATA,
                viewObjPropsResultDisplayUIManager);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED,
                viewObjPropsResultDisplayUIManager);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED,
                gridComponent.getDataEditListener());
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_REFRESH_QUERY,
                viewObjPropsResultDisplayUIManager);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS,
                viewObjectPropertiesResultDisplayUIManager);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_CANCEL_PASSWORD,
                viewObjectPropertiesResultDisplayUIManager);
        registerDataProviderEditListener();
    }

    /**
     * Register data provider edit listener.
     */
    protected void registerDataProviderEditListener() {
        if (null != resultsetDisplaydata && resultsetDisplaydata instanceof DSObjectPropertiesGridDataProvider) {
            DSObjectPropertiesGridDataProvider editDataProvider = (DSObjectPropertiesGridDataProvider) resultsetDisplaydata;
            editDataProvider.addListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED,
                    gridComponent.getDataEditListener());
            editDataProvider.addListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED,
                    viewObjectPropertiesResultDisplayUIManager);
        }
    }

    /**
     * Enable disable tab icons.
     *
     * @param isConnected the is connected
     */
    public void enableDisableTabIcons(boolean isConnected) {

        gridComponent.getToolbar().setDataProvider(this.resultsetDisplaydata);
        if (resultGridUIPref.isEnableEdit()) {
            gridComponent.getToolbar().handleDataEditEvent(isConnected);
        }
    }

    /**
     * Reset data.
     *
     * @param result the result
     */
    public void resetData(IDSGridDataProvider result) {
        resultsetDisplaydata = result;
        this.gridComponent.setDataProvider(result);
        registerDataProviderEditListener();
    }

    /**
     * Checks if is tab edited.
     *
     * @return true, if is tab edited
     */
    public boolean isTabEdited() {
        if (null != resultsetDisplaydata && resultsetDisplaydata instanceof DSObjectPropertiesGridDataProvider) {
            return ((DSObjectPropertiesGridDataProvider) resultsetDisplaydata).isGridDataEdited();
        }
        return false;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ObjectPropertyGridUIPreference.
     */
    private static final class ObjectPropertyGridUIPreference extends TabGridUIPreference {
        private IDSGridDataProvider resultsetDisplaydata;

        private ObjectPropertyGridUIPreference(IDSGridDataProvider resultsetDisplaydata) {
            super();
            this.resultsetDisplaydata = resultsetDisplaydata;
        }

        @Override
        public boolean isShowQueryArea() {
            return false;
        }

        @Override
        public boolean isRefreshSupported() {
            return true;
        }

        @Override
        public boolean isEnableEdit() {
            return true;
        }

        @Override
        public String getNULLValueText() {
            return NULL_VALUE;
        }

        @Override
        public Map<String, IComboBoxDataProvider> getComboBoxDataProviders() {
            Map<String, IComboBoxDataProvider> comboBoxDataProviderMap = new HashMap<>();

            if (resultsetDisplaydata instanceof DSObjectPropertiesGridDataProvider) {
                IServerObjectProperties objecjProperties = ((DSObjectPropertiesGridDataProvider) resultsetDisplaydata)
                        .getObjectPropertyObject();
                if (objecjProperties instanceof PropertiesUserRoleImpl) {

                    Database dataBase = resultsetDisplaydata.getDatabse();

                    comboBoxDataProviderMap.put(PropertiesConstants.RESOURCE_POOL_COMBO_BOX_DATA_PROVIDER,
                            new IComboBoxDataProvider() {
                                @Override
                                public List<?> getValues(int columnIndex, int rowIndex) {
                                    List<String> resourcePoolNames = new ArrayList<>();
                                    try {
                                        if (dataBase != null) {
                                            resourcePoolNames.addAll(UserRoleManager.fetchResourcePool(
                                                    dataBase.getConnectionManager().getObjBrowserConn()));
                                        }
                                    } catch (MPPDBIDEException exception) {
                                        MPPDBIDELoggerUtility.error("Failed to fetch UserRole resource pool",
                                                exception);
                                    }
                                    return resourcePoolNames;
                                }
                            });

                    comboBoxDataProviderMap.put(PropertiesConstants.MEMBER_SHIP_COMBO_BOX_DATA_PROVIDER,
                            new IComboBoxDataProvider() {
                                @Override
                                public List<?> getValues(int columnIndex, int rowIndex) {
                                    List<String> userRoleNames = new ArrayList<>();
                                    try {
                                        userRoleNames.addAll(UserRoleManager
                                                .fetchAllUserRole(dataBase.getServer(),
                                                        dataBase.getConnectionManager().getObjBrowserConn())
                                                .stream().map(userRole -> userRole.getName())
                                                .collect(Collectors.toList()));
                                        userRoleNames.remove(
                                                ((PropertiesUserRoleImpl) objecjProperties).getUserRole().getName());
                                    } catch (MPPDBIDEException exception) {
                                        MPPDBIDELoggerUtility.error("Failed to fetch all user roles", exception);
                                    }
                                    return userRoleNames;
                                }
                            });
                }
            }
            return comboBoxDataProviderMap;
        }

    }

    /**
     * Handle focus.
     */
    public void handleFocus() {
        this.gridComponent.focus();
    }

    /**
     * Sets the edited status.
     *
     * @param isEdited the new edited status
     */
    public void setEditedStatus(boolean isEdited) {
        if (isDisposed()) {
            return;
        }

        if (isEdited && !isTabEdited) {
            setText(getText() + "*");
            this.isTabEdited = isEdited;
        } else if (!isEdited && isTabEdited) {
            String label = getText();
            setText(label.substring(0, label.length() - 1));
            this.isTabEdited = isEdited;
        }

    }

    /**
     * Reset tab buttons.
     *
     * @param tabData the tab data
     */
    public void resetTabButtons(IObjectPropertyData tabData) {
        this.gridComponent.setLoadingStatus(false);
    }

    /**
     * Gets the data grid.
     *
     * @return the data grid
     */
    public DataGrid getDataGrid() {
        return this.gridComponent.getDataGrid();
    }

}
