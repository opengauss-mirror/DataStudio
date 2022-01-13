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

package com.huawei.mppdbide.view.handler.trigger;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.SourceCode;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TriggerObjectGroup;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.saveif.DataModeSave;
import com.huawei.mppdbide.view.ui.saveif.DataModeSaveFactory;
import com.huawei.mppdbide.view.ui.trigger.CreateTriggerDataModel;
import com.huawei.mppdbide.view.ui.trigger.CreateTriggerMainDialog;
import com.huawei.mppdbide.view.ui.trigger.DsCreateTriggerRelyInfo;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class TriggerCreateHandler.
 *
 * @since 3.0.0
 */
public class TriggerCreateHandler {
    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        MPPDBIDELoggerUtility.info("Start show createTrigger dialog.");
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        TriggerObjectGroup debugObjectGroup = (TriggerObjectGroup) obj;
        Namespace namespace = null;
        namespace = debugObjectGroup.getNamespace();
        if (namespace == null) {
            return;
        }
        CreateTriggerMainDialog dialog = new CreateTriggerMainDialog(new Shell(), SWT.NONE);
        DsCreateTriggerRelyInfo relyInfo = new DsCreateTriggerRelyInfo();
        relyInfo.setNamespace(namespace);
        dialog.setRelyInfo(relyInfo);
        if (dialog.open() != 0) {
            return;
        }
        DataModeSaveFactory factory = DataModeSaveFactory.instance();
        DataModeSave modeSave = factory.init(
                namespace.getDatabase().getConnectionManager(),
                0);
        CreateTriggerDataModel dataModel = dialog.getSaveDataModel();
        modeSave.saveData(dataModel.getUniqueId(), dataModel);
        factory.clear(modeSave);
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
                Command command = commandService.getCommand(
                        "com.huawei.mppdbide.command.id.executeobjectbrowseritemfromtoolbar"
                        );
                ParameterizedCommand pCommand = new ParameterizedCommand(command, null);
                handlerService.executeHandler(pCommand);
            }
        }
    }

    /**
     * Gets the debug object.
     *
     * @param Database the database
     * @return DebugObjects the debug object
     */
    protected DebugObjects getDebugObject(final Database db) {
        return new DebugObjects(0, "NewObject", OBJECTTYPE.PLSQLFUNCTION, db);
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
        Object selectedObject = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (!(selectedObject instanceof ObjectGroup<?>)) {
            return false;
        }
        ObjectGroup<?> obj = (ObjectGroup<?>) selectedObject;
        return (obj instanceof TriggerObjectGroup) && (OBJECTTYPE.TRIGGER_GROUP == obj.getObjectGroupType());
    }
}
