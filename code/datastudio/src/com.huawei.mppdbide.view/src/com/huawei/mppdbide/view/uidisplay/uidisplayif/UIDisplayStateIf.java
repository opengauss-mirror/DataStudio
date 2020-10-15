/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.uidisplay.uidisplayif;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.messaging.StatusMessage;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface UIDisplayStateIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface UIDisplayStateIf {
    /**
     * Clear sql object.
     */
    void clearSqlObject();

    /**
     * Checks if is version compatibile.
     *
     * @return true, if is version compatibile
     */
    public boolean isVersionCompatibile();

    /**
     * Sets the version compatibile.
     *
     * @param modelService the model service
     * @param application the application
     */
    public void setVersionCompatibile(EModelService modelService, MApplication application);

    /**
     * Gets the checks if is return from server.
     *
     * @return the checks if is return from server
     */
    public boolean getIsReturnFromServer();

    /**
     * Sets the return from server.
     *
     * @param returnFromServer the new return from server
     */
    public void setReturnFromServer(boolean returnFromServer);

    /**
     * /** Handle DB critical error.
     *
     * @param exception the exception
     * @param db the db
     */
    public void handleDBCriticalError(final MPPDBIDEException exception, final Database db);

    /**
     * handleServerCriticalError
     * 
     * @param mppException exception
     * @param server server
     */
    public void handleServerCriticalError(final MPPDBIDEException mppException, final Server server);

    /**
     * Cleanup on database disconnect.
     *
     * @param database the database
     * @param server the server
     * @return true, if successful
     */
    public void cleanupOnDatabaseDisconnect(final Database database, Server server);

    /**
     * Cleanup on server removal.
     *
     * @param server the server
     * @return true, if successful
     */
    public boolean cleanupOnServerRemoval(Server server);

    /**
     * Cleanup UI items.
     */
    public void cleanupUIItems();

    /**
     * Cleanup debug items.
     */
    public void cleanupDebugItems();

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage();

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage);

    /**
     * Sets the connected profile id.
     *
     * @param profileId the new connected profile id
     */
    public void setConnectedProfileId(ConnectionProfileId profileId);

    /**
     * Reset connected profile id.
     */
    public void resetConnectedProfileId();

    /**
     * Gets the connected profile id.
     *
     * @return the connected profile id
     */
    public ConnectionProfileId getConnectedProfileId();

    /**
     * Checks if is disclaimer req.
     *
     * @return true, if is disclaimer req
     */
    public boolean isDisclaimerReq();

    /**
     * Sets the disclaimer req.
     *
     * @param isDisclaimrReq the new disclaimer req
     */
    public void setDisclaimerReq(boolean isDisclaimrReq);

    /**
     * Sets the SS loff.
     *
     * @param isSSLon the new SS loff
     */
    public void setSSLoff(boolean isSSLon);

    /**
     * Gets the SS loff.
     *
     * @return the SS loff
     */
    public boolean getSSLoff();

    /**
     * Reconnect on critical exception for debug.
     *
     * @param debugObject the debug object
     */
    public void reconnectOnCriticalExceptionForDebug(final IDebugObject debugObject);

    /**
     * Reset connection on critical error.
     *
     * @param debugObj the debug obj
     */
    public void resetConnectionOnCriticalError(final IDebugObject debugObj);

    /**
     * Gets the reconnect pop up on critical error.
     *
     * @param debugObj the debug obj
     * @return the reconnect pop up on critical error
     */
    public int getReconnectPopUpOnCriticalError(IDebugObject debugObj);

    /**
     * On cancel click disconnect DB.
     *
     * @param debugObj the debug obj
     */
    public void onCancelClickDisconnectDB(final IDebugObject debugObj);

    /**
     * Handle critical exception for reconnect.
     *
     * @param dbgObj the dbg obj
     * @return true, if successful
     */
    public boolean handleCriticalExceptionForReconnect(IDebugObject dbgObj);

    /**
     * Clean upon window close.
     */
    public void cleanUponWindowClose();

    /**
     * Clean up security on window close.
     */
    void cleanUpSecurityOnWindowClose();

    /**
     * Need prompt password error.
     *
     * @param errMsg the err msg
     * @return true, if successful
     */
    boolean needPromptPasswordError(String errMsg);

    /**
     * deleteSecurityFolderFromProfile
     * 
     * @param server server
     */
    void deleteSecurityFolderFromProfile(Server server);

}
