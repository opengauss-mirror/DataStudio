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

package org.opengauss.mppdbide.view.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.view.ui.DatabaseListControl;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class OpenNewTerminal.
 *
 * @since 3.0.0
 */
public class OpenNewTerminal {

    /**
     * Execute.
     *
     * @param openTerminal the open terminal
     * @throws DataStudioSecurityException the data studio security exception
     */
    @Execute
    public void execute(@Optional @Named("open.terminal") String openTerminal) throws DataStudioSecurityException {
        Database db = null;
        if ("objectbrowser".equalsIgnoreCase(openTerminal)) {
            db = getObjectBrowserDatabase();
        } else if ("toolbar".equalsIgnoreCase(openTerminal)) {
            db = getToolbarDatabase();
        } else {
            db = getObjectBrowserDatabase();
            if (db == null || !db.isConnected()) {
                db = getToolbarDatabase();
            }
        }

        if (db != null) {
            UIElement.getInstance().createNewTerminal(db);
        }

    }

    /**
     * Gets the toolbar database.
     *
     * @return the toolbar database
     */
    private Database getToolbarDatabase() {
        Database db = null;
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            db = databaseListControl.getSelectedConnection();
        }

        return db;
    }

    /**
     * Gets the object browser database.
     *
     * @return the object browser database
     */
    private Database getObjectBrowserDatabase() {
        Database db = null;
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof Database) {
            db = (Database) obj;
        }
        return db;
    }

    /**
     * Can execute.
     *
     * @param openTerminal the open terminal
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute(@Optional @Named("open.terminal") String openTerminal) {
        if ("objectbrowser".equalsIgnoreCase(openTerminal)) {
            Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
            if (obj == null) {
                return false;
            }
            if (obj instanceof Database) {
                Database db = (Database) obj;
                return db.isConnected();
            }
        } else {
            Database db = null;
            DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
            if (null != databaseListControl) {
                db = databaseListControl.getSelectedConnection();
                return db != null && db.isConnected();
            }
        }

        return false;
    }

}
