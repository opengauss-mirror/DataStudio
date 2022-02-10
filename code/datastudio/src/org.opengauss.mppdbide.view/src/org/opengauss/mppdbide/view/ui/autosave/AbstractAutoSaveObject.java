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

package org.opengauss.mppdbide.view.ui.autosave;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import org.opengauss.mppdbide.view.ui.terminal.resulttab.ResultTabManager;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractAutoSaveObject.
 *
 * @since 3.0.0
 */
public abstract class AbstractAutoSaveObject implements IAutoSaveObject {
    
    /** 
     * The database name. 
     */
    private String databaseName;
    
    /** 
     * The connection name. 
     */
    private String connectionName;

    /**
     * The is modified. 
     */
    private boolean isModified;
    
    /** 
     * The is modified after create. 
     */
    private boolean isModifiedAfterCreate;
    
    /** 
     * The status. 
     */
    private AutoSaveTerminalStatus status;

    /**
     * Sets the connection name.
     *
     * @param connName the new connection name
     */
    @Override
    public void setConnectionName(String connName) {
        this.connectionName = connName;
    }

    /**
     * Gets the connection name.
     *
     * @return the connection name
     */
    @Override
    public String getConnectionName() {
        return connectionName;
    }

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Sets the database name.
     *
     * @param dbName the new database name
     */
    @Override
    public void setDatabaseName(String dbName) {
        this.databaseName = dbName;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    @Override
    public AutoSaveTerminalStatus getStatus() {
        return status;
    }

    /**
     * Update status.
     *
     * @param status1 the status 1
     */
    @Override
    public void updateStatus(AutoSaveTerminalStatus status1) {
        this.status = status1;
    }

    /**
     * Sets the modified after create.
     *
     * @param value the new modified after create
     */
    @Override
    public void setModifiedAfterCreate(boolean value) {
        this.isModifiedAfterCreate = value;
    }

    /**
     * Checks if is modified after create.
     *
     * @return true, if is modified after create
     */
    @Override
    public boolean isModifiedAfterCreate() {
        return isModifiedAfterCreate;
    }

    /**
     * Checks if is modified.
     *
     * @return true, if is modified
     */
    @Override
    public boolean isModified() {
        return isModified;
    }

    /**
     * Sets the modified.
     *
     * @param isModified1 the new modified
     */
    @Override
    public void setModified(final boolean isModified1) {
        this.isModified = isModified1;
    }

    /**
     * Reset buttons.
     */
    @Override
    public void resetButtons() {
    }

    /**
     * Reset conn buttons.
     *
     * @param isProfileExists the is profile exists
     */
    @Override
    public void resetConnButtons(boolean isProfileExists) {
    }

    /**
     * Sets the tab status msg.
     *
     * @param status1 the new tab status msg
     */
    @Override
    public void setTabStatusMsg(String status1) {

    }

    /**
     * Register modify listener.
     */
    public abstract void registerModifyListener();

    /**
     * Gets the auto save modify listener.
     *
     * @return the auto save modify listener
     */
    public IDocumentListener getAutoSaveModifyListener() {
        IDocumentListener listener = new IDocumentListener() {

            @Override
            public void documentAboutToBeChanged(DocumentEvent event) {

            }

            @Override
            public void documentChanged(DocumentEvent event) {
                setModified(true);
                setModifiedAfterCreate(true);
            }
        };

        return listener;
    }

    /**
     * Gets the source editor core.
     *
     * @return the source editor core
     */
    public abstract PLSourceEditorCore getSourceEditorCore();

    /**
     * Gets the result manager.
     *
     * @return the result manager
     */
    public abstract ResultTabManager getResultManager();

    /**
     * Gets the database.
     *
     * @return the database
     */
    public abstract Database getDatabase();
}
