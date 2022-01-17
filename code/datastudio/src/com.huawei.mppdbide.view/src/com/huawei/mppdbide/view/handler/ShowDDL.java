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
 * @since 3.0.0
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
