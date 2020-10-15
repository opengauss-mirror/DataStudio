/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShowDDL.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ShowDDL {

    /**
     * Execute.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Execute
    public void execute() throws DatabaseOperationException, DatabaseCriticalException {
        SQLTerminal terminal = null;

        Tablespace tablespace = IHandlerUtilities.getSelectedTablespace();
        if (null != tablespace) {
            terminal = UIElement.getInstance().createNewTerminal(tablespace);
            String query = tablespace.getDDL();
            if (null != terminal) {
                terminal.setDocumentContent(query);
                terminal.setModified(true);
                terminal.setModifiedAfterCreate(true);
            }

        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     * @throws DatabaseCriticalException 
     * @throws DatabaseOperationException 
     */
    @CanExecute
    public boolean canExecute() throws DatabaseOperationException, DatabaseCriticalException {
        Tablespace selectedTablespace = IHandlerUtilities.getSelectedTablespace();
        if (selectedTablespace == null || !selectedTablespace.getPrivilege()) {
            return false;
        }

        if (!IHandlerUtilities.getActiveDB(selectedTablespace.getServer())) {
            return false;
        }
        return true;
    }
}
