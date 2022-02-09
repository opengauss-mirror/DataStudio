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

package org.opengauss.mppdbide.view.view.createview;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.Document;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class CreateViewExecute.
 *
 * @since 3.0.0
 */
public class CreateViewExecute {
    /**
     * Command service
     */
    public ECommandService commandService;
    /**
     * Handler service
     */
    public EHandlerService handlerService;
    private Database database;

    public CreateViewExecute(Database database) {
        this.database = database;
    }

    /**
     * Execute
     *
     * @param String the language
     */
    @Execute
    public void baseExecute(String language) {
        SQLTerminal terminal = UIElement.getInstance().createNewTerminal(database);
        if (terminal != null) {
            Document doc = new Document(language);
            terminal.getTerminalCore().setDocument(doc, 0);
            terminal.resetSQLTerminalButton();
            terminal.resetAutoCommitButton();
            terminal.setModified(true);
            terminal.setModifiedAfterCreate(true);
            terminal.registerModifyListener();
            Command command = commandService.getCommand(
                    "org.opengauss.mppdbide.command.id.executeobjectbrowseritemfromtoolbar"
                    );
            ParameterizedCommand pCommand = new ParameterizedCommand(command, null);
            handlerService.executeHandler(pCommand);
        }
    }

    /**
     * Can execute.
     *
     * @return boolean true if can execute
     */
    @CanExecute
    protected boolean baseCanExecute() {
        return true;
    }
}
