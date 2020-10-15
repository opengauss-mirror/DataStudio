/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.view.ui.DatabaseListControl;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class OpenNewTerminal.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
