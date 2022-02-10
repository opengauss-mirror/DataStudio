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

package org.opengauss.mppdbide.view.core.sourceeditor.templates;

import org.eclipse.core.runtime.IStatus;

/**
 * 
 * Title: class
 * 
 * Description: The Class StatusInfo.
 *
 * @since 3.0.0
 */
public class StatusInfo implements IStatus {

    private String statusMessage;
    private int severity;

    /**
     * Instantiates a new status info.
     */
    public StatusInfo() {
        this(OK, null);
    }

    /**
     * Instantiates a new status info.
     *
     * @param severity the severity
     * @param message the message
     */
    public StatusInfo(int severity, String message) {
        this.statusMessage = message;
        this.severity = severity;
    }

    /**
     * Checks if is ok.
     *
     * @return true, if is ok
     */
    public boolean isOK() {
        return severity == IStatus.OK;
    }

    /**
     * Checks if is warning.
     *
     * @return true, if is warning
     */
    public boolean isWarning() {
        return severity == IStatus.WARNING;
    }

    /**
     * Checks if is info.
     *
     * @return true, if is info
     */
    public boolean isInfo() {
        return severity == IStatus.INFO;
    }

    /**
     * Checks if is error.
     *
     * @return true, if is error
     */
    public boolean isError() {
        return severity == IStatus.ERROR;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return statusMessage;
    }

    /**
     * Sets the error.
     *
     * @param errorMessage the new error
     */
    public void setError(String errorMessage) {
        if (null != errorMessage) {
            statusMessage = errorMessage;
            severity = IStatus.ERROR;
        }
    }

    /**
     * Sets the warning.
     *
     * @param warningMessage the new warning
     */
    public void setWarning(String warningMessage) {
        if (null != warningMessage) {
            statusMessage = warningMessage;
            severity = IStatus.WARNING;
        }
    }

    /**
     * Sets the info.
     *
     * @param infoMessage the new info
     */
    public void setInfo(String infoMessage) {
        if (null != infoMessage) {
            statusMessage = infoMessage;
            severity = IStatus.INFO;
        }
    }

    /**
     * Sets the OK.
     */
    public void setOK() {
        statusMessage = null;
        severity = IStatus.OK;
    }

    /**
     * Matches.
     *
     * @param severityMask the severity mask
     * @return true, if successful
     */
    public boolean matches(int severityMask) {
        return (severity & severityMask) != 0;
    }

    /**
     * Checks if is multi status.
     *
     * @return true, if is multi status
     */
    public boolean isMultiStatus() {
        return false;
    }

    /**
     * Gets the severity.
     *
     * @return the severity
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * Gets the plugin.
     *
     * @return the plugin
     */
    public String getPlugin() {
        return null;
    }

    /**
     * Gets the exception.
     *
     * @return the exception
     */
    public Throwable getException() {
        return null;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public int getCode() {
        return severity;
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public IStatus[] getChildren() {
        return new IStatus[0];
    }

}
