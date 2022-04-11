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

package org.opengauss.mppdbide.utils.exceptions;

import java.io.IOException;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class MPPDBIDEException.
 * 
 * @since 3.0.0
 */
public class MPPDBIDEException extends Exception {

    private static final long serialVersionUID = 2835532651963396147L;

    private final String dbErrorMessage;

    private String serverMessage;

    private String serverStackTrace;

    private final int errorCode;

    /**
     * Class of the exception generator. This can be used if no exception is
     * passed to the constructor.
     */
    private final Object clazz;

    /**
     * Copy the server exception to new exception class. This enables the
     * propagation of SQLException error message to UI.
     *
     * @param exception the new server message
     */
    private void setServerMessage(Exception exception) {
        if (exception instanceof SQLException) {
            SQLException sqlException = (SQLException) exception;
            this.serverMessage = (sqlException.getErrorCode() > 0
                    ? "[SQLErrorCode : " + sqlException.getErrorCode() + ']'
                    : "") + sqlException.getMessage();
            this.serverStackTrace = Arrays.toString(sqlException.getStackTrace());
        } else if (exception instanceof IOException) {
            IOException ioException = (IOException) exception;
            this.serverMessage = ioException.getMessage();

            this.serverStackTrace = Arrays.toString(ioException.getStackTrace());
        } else if (exception instanceof MPPDBIDEException) {
            MPPDBIDEException plsqlException = (MPPDBIDEException) exception;
            this.serverMessage = plsqlException.getServerMessage();
            this.serverStackTrace = plsqlException.getServerStackTrace();
        } else {
            this.serverMessage = MessageConfigLoader.getProperty(IMessagesConstants.IDE_INTERNAL_ERR)
                    + exception.getMessage();
        }
    }

    private void setParsedServerMessage(String parsedErrorMessage, Exception exception) {
        if (exception instanceof SQLException) {
            SQLException sqlException = (SQLException) exception;
            this.serverMessage = parsedErrorMessage;
            this.serverStackTrace = Arrays.toString(sqlException.getStackTrace());
        }
    }

    /**
     * Sets the server message.
     *
     * @param serverMsg the new server message
     */
    public void setServerMessage(String serverMsg) {
        StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        msg.append("[SERVER]");
        msg.append(serverMsg);
        this.serverMessage = msg.toString();
    }

    /**
     * Clear server message.
     */
    public void clearServerMessage() {
        this.serverMessage = "";
    }

    /**
     * Gets the server message.
     *
     * @return the server message
     */
    public String getServerMessage() {
        String msg = this.serverMessage;
        if (msg != null) {
            msg = Normalizer.normalize(msg, Form.NFKC);
            return (msg.contains(MPPDBIDEConstants.LINE_SEPARATOR + " "))
                    ? msg.replaceAll(MPPDBIDEConstants.LINE_SEPARATOR + " +", MPPDBIDEConstants.LINE_SEPARATOR)
                    : msg;
        }
        return msg;
    }

    /**
     * Gets the server stack trace.
     *
     * @return the server stack trace
     */
    public String getServerStackTrace() {
        return this.serverStackTrace;
    }

    /**
     * Instantiates a new MPPDBIDE exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param excption the e
     */
    public MPPDBIDEException(String dbErrorMessageCode, Exception excption) {
        super(MessageConfigLoader.getProperty(dbErrorMessageCode), excption);
        dbErrorMessage = MessageConfigLoader.getProperty(dbErrorMessageCode);
        errorCode = 0;
        this.clazz = null;
        setServerMessage(excption);
    }

    /**
     * Instantiates a new MPPDBIDE exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param parsedErrorMessage the parsed error message
     * @param exception the e
     */
    public MPPDBIDEException(String dbErrorMessageCode, String parsedErrorMessage, Exception exception) {
        super(MessageConfigLoader.getProperty(dbErrorMessageCode), exception);
        dbErrorMessage = MessageConfigLoader.getProperty(dbErrorMessageCode);
        errorCode = 0;
        this.clazz = null;
        setParsedServerMessage(parsedErrorMessage, exception);
    }

    /**
     * Instantiates a new MPPDBIDE exception.
     *
     * @param dbErrorMessageCode the db error message code
     */
    public MPPDBIDEException(String dbErrorMessageCode) {
        super(MessageConfigLoader.getProperty(dbErrorMessageCode));
        dbErrorMessage = MessageConfigLoader.getProperty(dbErrorMessageCode);
        errorCode = 0;
        this.clazz = null;
        this.serverMessage = MessageConfigLoader.getProperty(dbErrorMessageCode);
    }

    /**
     * Instantiates a new MPPDBIDE exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param params the params
     */
    public MPPDBIDEException(String dbErrorMessageCode, Object... params) {
        super(MessageConfigLoader.getProperty(dbErrorMessageCode, params));
        dbErrorMessage = MessageConfigLoader.getProperty(dbErrorMessageCode, params);
        errorCode = 0;
        this.clazz = null;
        this.serverMessage = dbErrorMessage;
    }

    /**
     * Instantiates a new MPPDBIDE exception.
     *
     * @param dbErrorMessageCode the db error message code
     * @param throwable the e
     */
    public MPPDBIDEException(String dbErrorMessageCode, Throwable throwable) {
        super(MessageConfigLoader.getProperty(dbErrorMessageCode), throwable);
        dbErrorMessage = MessageConfigLoader.getProperty(dbErrorMessageCode);

        errorCode = 0;
        this.clazz = null;
    }


    /**
     * Gets the DB error message.
     *
     * @return the DB error message
     */
    public String getDBErrorMessage() {
        if (null == clazz) {
            return dbErrorMessage;
        } else {
            return dbErrorMessage + "in the class:" + clazz.getClass().getName();
        }
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public int getErrorCode() {
        return errorCode;
    }
}
