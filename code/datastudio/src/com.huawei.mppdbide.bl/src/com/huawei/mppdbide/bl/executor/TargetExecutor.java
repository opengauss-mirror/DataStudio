/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.executor;

import java.util.ArrayList;

import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.bl.adapter.gauss.GaussConnection;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class TargetExecutor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class TargetExecutor {

    private GaussConnection connection;
    private boolean isConnected;
    private final Object targetConnectionLock = new Object();

    /**
     * Connect.
     *
     * @param connectionInfo the connection info
     * @param iConnectionDriver the i connection driver
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void connect(IServerConnectionInfo connectionInfo, IConnectionDriver iConnectionDriver)
            throws MPPDBIDEException {

        MPPDBIDELoggerUtility.debug("TargetExecutor: connecting to server.");
        try {
            synchronized (targetConnectionLock) {
                connection = new GaussConnection(iConnectionDriver);
                connection.connect(connectionInfo);

                isConnected = true;
            }
        } catch (MPPDBIDEException exp) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_CONNECT_FAILED), exp);
            throw new MPPDBIDEException(IMessagesConstants.ERR_BL_CONNECT_FAILED, exp);
        }

    }

    /**
     * Disconnect.
     */
    public void disconnect() {
        MPPDBIDELoggerUtility.debug("TargetExecutor: disconnecting from server.");
        synchronized (targetConnectionLock) {
            if (isConnected) {
                connection.dbDisconnect();
            }

            isConnected = false;
        }

    }

    /**
     * Gets the query exectuion string.
     *
     * @param dbgobj the dbgobj
     * @return the query exectuion string
     * @throws DatabaseOperationException the database operation exception
     */
    public void getQueryExectuionString(IDebugObject dbgobj) throws DatabaseOperationException {
        MPPDBIDELoggerUtility.debug("Get query execution string.");
        String needExecuteSqlString = null;
        synchronized (targetConnectionLock) {
            needExecuteSqlString = getOLAPQueryStatementString(dbgobj);
            dbgobj.setExecutionQuery(needExecuteSqlString);
        }
    }

    /**
     * Gets the OLAP query statement string.
     *
     * @param dbgobj the dbgobj
     * @return the OLAP query statement string
     * @Author: yWX611925
     * @Date: 2019-6-21
     * @Title: getOLAPQueryStatementString
     * @Description: get OLAP Query Statement
     */
    private String getOLAPQueryStatementString(IDebugObject dbgobj) {
        StringBuilder template = new StringBuilder("SELECT");
        ArrayList<ObjectParameter> templateParameters = dbgobj.getTemplateParameters();
        template.append(" ");
        template.append("\"" + dbgobj.getNamespace().getName() + "\"").append(".");
        template.append(ServerObject.getQualifiedObjectName(dbgobj.getName()));

        template.append("(");

        if (null != templateParameters && templateParameters.size() > 0) {
            ObjectParameter param = null;
            boolean isFirstParam = true;
            String argValue = null;
            int paramSize = templateParameters.size();

            for (int i1 = 0; i1 < paramSize; i1++) {
                param = templateParameters.get(i1);

                switch (param.getType()) {
                    case IN:
                    case INOUT: {
                        if (!isFirstParam) {
                            template.append(", ");
                        }

                        argValue = param.getValue();

                        if (MPPDBIDEConstants.UNKNOWN_DATATYPE_STR.equals(param.getDataType())) {
                            template.append(argValue);
                        } else {
                            if (null != argValue) {
                                appendTemplate(template, param, argValue);
                            }
                        }

                        break;
                    }
                    default: {
                        break;
                    }
                }

                isFirstParam = false;
            }
        }

        template.append(")");
        return template.toString();
    }

    private void appendTemplate(StringBuilder template, ObjectParameter param, String argValue) {
        if ("null".equalsIgnoreCase(argValue)) {
            template.append("null");
        } else {
            if (argValue.charAt(0) != '\'') {
                template.append("'").append(argValue).append("'");
            } else {
                template.append(argValue);
            }
            /* Start DTS2013022707389 */
            appendDataTypeForTemplate(param.getDataType(), template);
            /* End DTS2013022707389 */
        }
    }

    /**
     * Append data type for template.
     *
     * @param dataType the param
     * @param template the template
     */
    private void appendDataTypeForTemplate(String dataType, StringBuilder template) {
        if (null != dataType && ("char".equalsIgnoreCase(dataType) || "bpchar".equalsIgnoreCase(dataType))) {
            template.append("::").append("varchar");
        } else {
            template.append("::").append(dataType);
        }
    }

    /**
     * Gets the server version.
     *
     * @return the server version
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */

    public String getServerVersion() throws DatabaseOperationException, DatabaseCriticalException {
        MPPDBIDELoggerUtility.debug("TargetExecutor: Get server version.");
        synchronized (targetConnectionLock) {
            return connection.getVersion();
        }
    }

    /**
     * Fetch server IP.
     *
     * @return the string
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String fetchServerIP() throws DatabaseOperationException, DatabaseCriticalException {
        MPPDBIDELoggerUtility.debug("TargetExecutor: Get server IP.");
        synchronized (targetConnectionLock) {
            return connection.getServerIP1();
        }
    }

}
