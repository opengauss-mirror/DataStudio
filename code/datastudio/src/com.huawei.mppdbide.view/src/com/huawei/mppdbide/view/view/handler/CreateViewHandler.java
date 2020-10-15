/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.Document;

import com.huawei.mppdbide.bl.serverdatacache.IViewObjectGroups;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateViewHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CreateViewHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        IViewObjectGroups view = IHandlerUtilities.getSelectedIViewObjectGroup();

        if (null != view) {
            SQLTerminal terminal = UIElement.getInstance().createNewTerminal(view.getDatabase());
            if (null != terminal) {
                Document doc = new Document(view.getTemplateCode());
                terminal.getTerminalCore().setDocument(doc, 0);
                terminal.resetSQLTerminalButton();
                terminal.resetAutoCommitButton();
                terminal.setModified(true);
                terminal.setModifiedAfterCreate(true);
                terminal.registerModifyListener();
            }
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object object = UIElement.getInstance().getActivePartObject();
        if (object instanceof SearchWindow) {
            return false;
        }
        IViewObjectGroups viewgroup = IHandlerUtilities.getSelectedIViewObjectGroup();
        if (null != viewgroup) {
            return viewgroup.isDbConnected();
        }

        return false;
    }

}
