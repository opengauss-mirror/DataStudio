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

package org.opengauss.mppdbide.view.handler.trigger;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.SourceCode;
import org.opengauss.mppdbide.bl.serverdatacache.TriggerMetaData;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.search.SearchWindow;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.ui.saveif.DataModeSave;
import org.opengauss.mppdbide.view.ui.saveif.DataModeSaveFactory;
import org.opengauss.mppdbide.view.ui.trigger.CreateTriggerDataModel;
import org.opengauss.mppdbide.view.ui.trigger.CreateTriggerMainDialog;
import org.opengauss.mppdbide.view.ui.trigger.DsCreateTriggerRelyInfo;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class TriggerEditHandler {
    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    /**
     * Execute
     */
    @Execute
    public void execute() {
        MPPDBIDELoggerUtility.info("Start show edit Trigger dialog.");
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        TriggerMetaData triggerMetaData = (TriggerMetaData) obj;
        Namespace namespace = null;
        namespace = triggerMetaData.getNamespace();
        if (namespace == null) {
            return;
        }
        CreateTriggerMainDialog dialog = new CreateTriggerMainDialog(new Shell(), SWT.NONE);
        DsCreateTriggerRelyInfo relyInfo = new DsCreateTriggerRelyInfo();
        DataModeSaveFactory factory = DataModeSaveFactory.instance();
        DataModeSave modeSave = factory.init(
                namespace.getConnectionManager(),
                0);
        relyInfo.setNamespace(namespace);
        dialog.setRelyInfo(relyInfo);
        String uniqueId = getUniqueId(triggerMetaData);
        dialog.setTriggerDataModel(
                triggerMetaData.getName(),
                modeSave.loadData(uniqueId,
                        CreateTriggerDataModel.class));
        if (dialog.open() != 0) {
            factory.clear(modeSave);
            return;
        }
        modeSave.saveData(uniqueId, dialog.getSaveDataModel());
        factory.clear(modeSave);
        executeCreateTrigger(triggerMetaData, namespace, relyInfo);
    }

    private void executeCreateTrigger(TriggerMetaData triggerMetaData, Namespace namespace,
            DsCreateTriggerRelyInfo relyInfo) {
        IDebugObject object = null;
        SourceCode srcCode = new SourceCode();
        final Database db = namespace.getDatabase();
        object = getDebugObject(db);
        srcCode.setCode(relyInfo.getSourceCode());
        object.setNamespace(namespace);
        object.setSourceCode(srcCode);
        PLSourceEditor plSourceEditor = UIElement.getInstance().createEditor(object);
        if (plSourceEditor != null) {
            plSourceEditor.displaySourceForDebugObject(object);
            plSourceEditor.registerModifyListener();
            if (!"".equals(srcCode.getCode())) {
                dropBeforeCreate(triggerMetaData);
                Command command = commandService.getCommand(
                        "org.opengauss.mppdbide.command.id.executeobjectbrowseritemfromtoolbar"
                        );
                ParameterizedCommand pCommand = new ParameterizedCommand(command, null);
                handlerService.executeHandler(pCommand);
            }
        }
    }

    private void dropBeforeCreate(TriggerMetaData metaData) {
        try {
            Database db = metaData.getDatabase();
            db.getConnectionManager().execNonSelectOnObjBrowserConn(metaData.getDropQuery(false));
        } catch (DatabaseCriticalException | DatabaseOperationException exp) {
            MPPDBIDELoggerUtility.error("drop trigger failed!, please check!");
        }
    }

    private static String getUniqueId(TriggerMetaData metaData) {
        return metaData.getNamespace().getName() + "_" + metaData.getName();
    }

    /**
     * Gets the debug object.
     *
     * @param db the db
     * @return the debug object
     */
    protected DebugObjects getDebugObject(final Database db) {
        return new DebugObjects(0, "NewObject", OBJECTTYPE.PLSQLFUNCTION, db);
    }

    /**
     * Can execute
     *
     * @return boolean true if can exacute
     */
    @CanExecute
    public boolean canExecute() {
        Object object = UIElement.getInstance().getActivePartObject();
        if (object instanceof SearchWindow) {
            return false;
        }
        Object obj =  IHandlerUtilities.getObjectBrowserSelectedObject();
        return obj instanceof TriggerMetaData;
    }
}
