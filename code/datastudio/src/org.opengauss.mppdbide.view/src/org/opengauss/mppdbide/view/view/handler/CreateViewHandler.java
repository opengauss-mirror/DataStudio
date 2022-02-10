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

package org.opengauss.mppdbide.view.view.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.INamespace;
import org.opengauss.mppdbide.bl.serverdatacache.IViewObjectGroups;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.search.SearchWindow;
import org.opengauss.mppdbide.view.ui.saveif.DataModeSave;
import org.opengauss.mppdbide.view.ui.saveif.DataModeSaveFactory;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.view.createview.CreateViewDataModel;
import org.opengauss.mppdbide.view.view.createview.CreateViewExecute;
import org.opengauss.mppdbide.view.view.createview.CreateViewMainDialog;
import org.opengauss.mppdbide.view.view.createview.CreateViewRelyInfo;
import org.opengauss.mppdbide.view.view.createview.ICreateViewRelyInfo;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateViewHandler.
 *
 * @since 3.0.0
 */
public class CreateViewHandler {

    /**
     * Command service
     */
    @Inject
    protected ECommandService commandService;

    /**
     * Handler service
     */
    @Inject
    protected EHandlerService handlerService;

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        IViewObjectGroups view = IHandlerUtilities.getSelectedIViewObjectGroup();
        if (view != null) {
            Database db = view.getDatabase();
            ObjectGroup viewObjectGroup = (ObjectGroup) view;
            INamespace namespace = (INamespace) viewObjectGroup.getParent();
            CreateViewMainDialog dialog = new CreateViewMainDialog(new Shell(), 1);
            ICreateViewRelyInfo createViewRelyInfo = new CreateViewRelyInfo(db);
            createViewRelyInfo.setFixedSchemaName(namespace.getName());
            dialog.setCreateViewRelyInfo(createViewRelyInfo);
            if (dialog.open() != 0) {
                return;
            }
            DataModeSaveFactory factory = DataModeSaveFactory.instance();
            DataModeSave modeSave = factory.init(namespace.getDatabase().getConnectionManager(), 1);
            CreateViewDataModel datamodel = dialog.getSaveDataModel();
            modeSave.saveData(dialog.getViewNameFullName(), datamodel);

            String ddlSentence = createViewRelyInfo.getDdlSentence();
            CreateViewExecute createViewExecute = new CreateViewExecute(view.getDatabase());
            createViewExecute.commandService = commandService;
            createViewExecute.handlerService = handlerService;
            createViewExecute.baseExecute(ddlSentence);
        }
    }

    /**
     * Can execute.
     *
     * @return boolean true if can execute
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
