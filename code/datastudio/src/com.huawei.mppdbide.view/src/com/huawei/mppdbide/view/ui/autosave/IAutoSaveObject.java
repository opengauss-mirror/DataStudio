/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.autosave;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IAutoSaveObject.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IAutoSaveObject {

    /**
     * Checks if is modified.
     *
     * @return true, if is modified
     */
    boolean isModified();

    /**
     * Sets the modified.
     *
     * @param isModified the new modified
     */
    void setModified(boolean isModified);

    /**
     * Checks if is modified after create.
     *
     * @return true, if is modified after create
     */
    boolean isModifiedAfterCreate();

    /**
     * Sets the modified after create.
     *
     * @param isModifiedAfterCreate the new modified after create
     */
    void setModifiedAfterCreate(boolean isModifiedAfterCreate);

    /**
     * Gets the text.
     *
     * @return the text
     */
    String getText();

    /**
     * Sets the text.
     *
     * @param data the new text
     */
    void setText(String data);

    /**
     * Update status.
     *
     * @param status the status
     */
    void updateStatus(AutoSaveTerminalStatus status);

    /**
     * Gets the status.
     *
     * @return the status
     */
    AutoSaveTerminalStatus getStatus();

    /**
     * Gets the connection name.
     *
     * @return the connection name
     */
    String getConnectionName();

    /**
     * Sets the connection name.
     *
     * @param connName the new connection name
     */
    void setConnectionName(String connName);

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    String getDatabaseName();

    /**
     * Sets the database name.
     *
     * @param dbName the new database name
     */
    void setDatabaseName(String dbName);

    /**
     * Gets the element ID.
     *
     * @return the element ID
     */
    String getElementID();

    /**
     * Sets the element ID.
     *
     * @param id the new element ID
     */
    void setElementID(String id);

    /**
     * Gets the tab label.
     *
     * @return the tab label
     */
    String getTabLabel();

    /**
     * Sets the tab label.
     *
     * @param label the new tab label
     */
    void setTabLabel(String label);

    /**
     * Gets the tab tool tip.
     *
     * @return the tab tool tip
     */
    String getTabToolTip();

    /**
     * Sets the tab tool tip.
     *
     * @param toolTip the new tab tool tip
     */
    void setTabToolTip(String toolTip);

    /**
     * Update editor window.
     *
     * @param dbObject the db object
     * @return true, if successful
     */
    boolean updateEditorWindow(Object dbObject);

    /**
     * Sets the editable.
     *
     * @param isEditable the new editable
     */
    void setEditable(boolean isEditable);

    /**
     * Reset conn buttons.for buttons which are connection dependent
     *
     * @param isConnectionPresent the is connection present
     */
    void resetConnButtons(boolean isConnectionPresent);

    /**
     * Reset buttons.for buttons which are database connectivity dependent
     */
    void resetButtons();

    /**
     * Gets the type.
     *
     * @return the type
     */
    String getType();

    /**
     * Sets the tab status msg.
     *
     * @param status the new tab status msg
     */
    void setTabStatusMsg(String status);

    /**
     * Gets the def label id.
     *
     * @return the def label id
     */
    String getDefLabelId();

}
