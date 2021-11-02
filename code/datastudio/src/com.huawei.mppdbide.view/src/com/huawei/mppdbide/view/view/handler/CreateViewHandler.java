/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.view.handler;

import javax.inject.Inject;

import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.IViewObjectGroups;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.ui.saveif.DataModeSave;
import com.huawei.mppdbide.view.ui.saveif.DataModeSaveFactory;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.view.createview.CreateViewDataModel;
import com.huawei.mppdbide.view.view.createview.CreateViewExecute;
import com.huawei.mppdbide.view.view.createview.CreateViewMainDialog;
import com.huawei.mppdbide.view.view.createview.CreateViewRelyInfo;
import com.huawei.mppdbide.view.view.createview.ICreateViewRelyInfo;

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
