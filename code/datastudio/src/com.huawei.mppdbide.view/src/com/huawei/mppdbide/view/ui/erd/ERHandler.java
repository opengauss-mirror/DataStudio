/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.erd;

import java.sql.SQLException;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.presentation.erd.AbstractERPresentation;
import com.huawei.mppdbide.presentation.erd.ERDiagramPresentation;
import com.huawei.mppdbide.presentation.erd.EREntityPresentation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * The Class ERHandler.
 *
 * @ClassName: ERHandler
 * @Description: The Class ERHandler. Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 * @author: f00512995
 * @version:
 * @since: Sep 28, 2019
 */
public class ERHandler {
    
    /**
     * Handler to view ER diagram
     */
    @Execute
    public void execute() {
        Object obj = (Object) IHandlerUtilities.getObjectBrowserSelectedObject();
        String jobName = MessageConfigLoader.getProperty(IMessagesConstants.ER_JOB_DETAILS);

        FillERPresentation job = new FillERPresentation(jobName, MPPDBIDEConstants.CANCELABLEJOB, obj);
        job.schedule();
    }
    
    /**
     * can execute 
     * 
     * @return true if can be executed
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (null == obj) {
            return false;
        }
        return true;
    }

    /**
     * The Class FillERPresentation.
     *
     * @ClassName: FillERPresentation
     * @Description: The Class FillERPresentation. Copyright (c) Huawei
     * Technologies Co., Ltd. 2012-2019.
     * @author: f00512995
     * @version:
     * @since: Sep 28, 2019
     */
    public static class FillERPresentation extends PromptPasswordUIWorkerJob {
        private Database db;

        private DBConnection conn;

        private Object obj;

        private String severObjectName;

        private AbstractERPresentation presentation;

        public FillERPresentation(String name, Object family, Object obj) {
            super(name, family, IMessagesConstants.ER_ERROR_POPUP_HEADER);
            this.conn = null;
            this.obj = obj;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            setServerPwd(getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
            this.conn = getDatabase().getConnectionManager().getFreeConnection();
            if (obj instanceof TableMetaData) {
                presentation = new EREntityPresentation((TableMetaData) obj, conn);
                severObjectName = ((TableMetaData) obj).getNameSpaceName() + "." + ((TableMetaData) obj).getName();
            } else {
                presentation = new ERDiagramPresentation((TableObjectGroup) obj, conn);
                severObjectName = ((TableObjectGroup) obj).getNamespace().getName();
            }
            presentation.initERPresentation();
            return presentation;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            if (null == this.presentation) {
                return;
            }
            UIElement.getInstance().createERPart(presentation);
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
            showErrorPopupMsg(dbCriticalException);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            showErrorPopupMsg(dbOperationException);
        }

        /**
         * Show error popup msg.
         *
         * @param e the e
         */
        private void showErrorPopupMsg(MPPDBIDEException exception) {
            if (null != exception.getCause() && exception.getCause() instanceof SQLException) {
                if (((SQLException) exception.getCause()).getErrorCode() == 1001) {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.ER_VIEW_FAILED),
                            MessageConfigLoader.getProperty(IMessagesConstants.ER_VIEW_INSUFFICIENT_PRIVILEGES_MSG));
                    MPPDBIDELoggerUtility.error("View ER Diagram beacuse of INSUFFICIENT_PRIVILEGES", exception);
                    return;
                }
            }
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ER_VIEW_FAILED),
                    MessageConfigLoader.getProperty(IMessagesConstants.ER_VIEW_FAILED_MSG, severObjectName));
            MPPDBIDELoggerUtility.error("View ER Diagram Failed", exception);
        }

        @Override
        public void finalCleanup() {
            super.finalCleanup();
            presentation = null;
            getDatabase().getConnectionManager().releaseConnection(this.conn);
        }

        @Override
        protected void canceling() {
            super.canceling();
            try {
                if (null != this.conn) {
                    this.conn.cancelQuery();
                }
            } catch (MPPDBIDEException mppdbIdeException) {
                MPPDBIDELoggerUtility.error("failed to cancel query", mppdbIdeException);
            }
        }

        /**
         * @return getDatabase
         */
        @Override
        protected Database getDatabase() {
            if (this.db != null) {
                return this.db;
            }

            try {
                if (getServer(obj) != null) {
                    this.db = getServer(obj).findOneActiveDb();
                }
            } catch (DatabaseOperationException databaseOperationException) {
                MPPDBIDELoggerUtility.error("View ER Diagram : failed to get database", databaseOperationException);
            }
            return this.db;
        }

        private Server getServer(Object obj) {
            Server server = null;
            if (obj instanceof TableMetaData) {
                server = ((TableMetaData) obj).getServer();
            } else {
                if (((TableObjectGroup) obj).getNamespace() != null) {
                    server = ((TableObjectGroup) obj).getNamespace().getServer();
                }
            }

            return server;
        }
    }
}
