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

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.presentation.grid.IDSEditGridDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import org.opengauss.mppdbide.presentation.objectproperties.IObjectPropertyData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.core.sourceeditor.templates.StatusInfo;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesTabRefreshWorker.
 *
 * @since 3.0.0
 */
public class PropertiesTabRefreshWorker extends AbstarctPropertiesWorker {
    private StatusMessage refreshStatusMessage;
    private BottomStatusBar refreshBottomStatusBar;
    private DBConnection connection;
    private ViewObjectPropertyTabManager tabManager;
    private StatusInfo statusInfo;

    /**
     * Instantiates a new properties tab refresh worker.
     *
     * @param name the name
     * @param family the family
     * @param dataProvider the data provider
     * @param refreshStatusMessage the refresh status message
     * @param refreshBottomStatusBar the refresh bottom status bar
     * @param connection the connection
     * @param tabManager the tab manager
     * @param statusInfo the status info
     */
    public PropertiesTabRefreshWorker(String name, Object family, IDSEditGridDataProvider dataProvider,
            StatusMessage refreshStatusMessage, BottomStatusBar refreshBottomStatusBar, DBConnection connection,
            ViewObjectPropertyTabManager tabManager, StatusInfo statusInfo) {
        super(name, family, IMessagesConstants.PROP_HANDLER_PROPERTIES_ERROR, dataProvider);
        this.refreshStatusMessage = refreshStatusMessage;
        this.refreshBottomStatusBar = refreshBottomStatusBar;
        this.connection = connection;
        this.tabManager = tabManager;
        this.statusInfo = statusInfo;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    protected Database getDatabase() {
        TableMetaData table = (TableMetaData) dataProvider.getTable();
        if (null != table) {

            return table.getDatabase();
        }
        return null;
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        Database db = getDatabase();
        if (this.dataProvider instanceof DSObjectPropertiesGridDataProvider) {
            setServerPwd(null != db && db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
            ((TableMetaData) dataProvider.getTable()).refresh(connection);

            ((DSObjectPropertiesGridDataProvider) dataProvider).refresh();
        }

        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        statusInfo.setOK();
        tabManager.resetTabData((IObjectPropertyData) this.dataProvider);

    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        super.finalCleanup();
        ((DSObjectPropertiesGridDataProvider) dataProvider).releaseConnection();
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        refreshBottomStatusBar.hideStatusbar(refreshStatusMessage);
    }

    /**
     * Exception event call.
     *
     * @param exception the e
     */
    protected void exceptionEventCall(MPPDBIDEException exception) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE),
                exception.getServerMessage());
    }

}
