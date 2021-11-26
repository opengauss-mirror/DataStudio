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
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.saveif.DataModeSave;
import com.huawei.mppdbide.view.ui.saveif.DataModeSaveFactory;
import com.huawei.mppdbide.view.view.createview.CreateViewDataModel;
import com.huawei.mppdbide.view.view.createview.CreateViewExecute;
import com.huawei.mppdbide.view.view.createview.CreateViewMainDialog;
import com.huawei.mppdbide.view.view.createview.CreateViewRelyInfo;
import com.huawei.mppdbide.view.view.createview.ICreateViewRelyInfo;

/**
 *
 * Title: class
 * Description: The Class EditViewHandler.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021.
 *
 * @version [DataStudio 2.1.0, 21 Oct., 2021]
 * @since 21 Oct., 2021
 */
public class EditViewHandler {
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
        ViewMetaData view = IHandlerUtilities.getSelectedViewObject();
        Database db = view.getDatabase();
        if (view != null) {
            CreateViewMainDialog dialog = new CreateViewMainDialog(new Shell(), 1);
            ICreateViewRelyInfo createViewRelyInfo = new CreateViewRelyInfo(db);
            createViewRelyInfo.setIsEditView(true);
            createViewRelyInfo.setFixedViewName(view.getName());
            createViewRelyInfo.setFixedSchemaName(view.getNameSpaceName());
            dialog.setCreateViewRelyInfo(createViewRelyInfo);
            DataModeSaveFactory factory = DataModeSaveFactory.instance();
            DataModeSave modeSave = factory.init(view.getParent().getConnectionManager(), 1);
            String id = view.getNameSpaceName() + "." + view.getName();
            CreateViewDataModel createViewDataModel = modeSave.loadData(id, CreateViewDataModel.class);
            dialog.setSaveDataModel(createViewDataModel);
            if (dialog.open() != 0) {
                return;
            }
            CreateViewDataModel datamodel = dialog.getSaveDataModel();
            modeSave.saveData(dialog.getViewNameFullName(), datamodel);
            dropBeforeCreate(view);
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
     * @return boolean if can execute
     */
    @CanExecute
    public boolean canExecute() {
        ViewMetaData view = IHandlerUtilities.getSelectedViewObject();
        if (view != null) {
            Namespace ns = (Namespace) view.getNamespace();
            if (ns != null && ns.getDatabase().isConnected() && "v".equals(view.getRelKind())) {
                return true;
            }
        }
        return false;
    }

    private void dropBeforeCreate (ViewMetaData metaData) {
        try {
            Database db = metaData.getDatabase();
            db.getConnectionManager().execNonSelectOnObjBrowserConn(metaData.getDropQuery(false));
        } catch (DatabaseCriticalException | DatabaseOperationException exp) {
            MPPDBIDELoggerUtility.error("Drop view failed!, please check!");
        }
    }
}
