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

package org.opengauss.mppdbide.view.handler;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.INamespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.SourceCode;
import org.opengauss.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.createfunction.CreateFunctionMainDlg;
import org.opengauss.mppdbide.view.createfunction.DsCreateFunctionRelyInfo;
import org.opengauss.mppdbide.view.search.SearchWindow;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 *
 * Title: class
 *
 * Description: The Class CreateFunction.
 *
 * @since 3.0.0
 */
public class CreateFunctionBase {
    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    /**
     * Execute
     *
     * @param String the language
     */
    public void baseExecute(String language) {
        /*
         * The object will never be null. In case of null, the Menu item will be
         * disabled automatically.
         */

        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();

        ObjectGroup debugObjectGroup = (ObjectGroup) obj;
        INamespace namespace = null;

        if (debugObjectGroup != null) {
            namespace = debugObjectGroup.getNamespace();
            if (namespace == null) {
                return;
            }
            CreateFunctionMainDlg dlg = new CreateFunctionMainDlg(new Shell(), SWT.NONE);
            DsCreateFunctionRelyInfo relyInfo = new DsCreateFunctionRelyInfo(debugObjectGroup);
            relyInfo.setSchameName(namespace.getName());
            dlg.setRelyInfo(relyInfo);
            dlg.setInitLanguage(language);
            if (dlg.open() != 0) {
                return;
            }
            IDebugObject object = null;
            SourceCode srcCode = new SourceCode();
            if (debugObjectGroup instanceof OLAPObjectGroup) {
                final Database db = namespace.getDatabase();
                object = getDebugObject(db);
                srcCode.setCode(relyInfo.getSourceCode());
            }
            if (object == null) {
                return;
            }

            object.setNamespace(namespace);

            object.setSourceCode(srcCode);

            PLSourceEditor plSourceEditor = UIElement.getInstance().createEditor(object);
            if (plSourceEditor != null) {
                plSourceEditor.displaySourceForDebugObject(object);
                plSourceEditor.registerModifyListener();
                if (relyInfo.getAutoCompile() && !"".equals(srcCode.getCode())) {
                    Command command = commandService.getCommand(
                            "org.opengauss.mppdbide.command.id.executeobjectbrowseritemfromtoolbar"
                            );
                    ParameterizedCommand pCommand = new ParameterizedCommand(command, null);
                    handlerService.executeHandler(pCommand);
                }
            }
        }
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
     * Can execute.
     *
     * @return true, if successful
     */
    protected boolean baseCanExecute() {
        Object object = UIElement.getInstance().getActivePartObject();
        if (object instanceof SearchWindow) {
            return false;
        }
        ObjectGroup<?> obj = (ObjectGroup<?>) IHandlerUtilities.getObjectBrowserSelectedObject();
        return (obj instanceof DebugObjectGroup) && (OBJECTTYPE.FUNCTION_GROUP == obj.getObjectGroupType());
    }

}
