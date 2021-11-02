/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021. All rights reserved.
 */

package com.huawei.mppdbide.view.view.createview;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.Document;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class CreateViewExecute.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021.
 *
 * @version [DataStudio 2.1.0, 21 Oct., 2021]
 * @since 21 Oct., 2021
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
                    "com.huawei.mppdbide.command.id.executeobjectbrowseritemfromtoolbar"
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
