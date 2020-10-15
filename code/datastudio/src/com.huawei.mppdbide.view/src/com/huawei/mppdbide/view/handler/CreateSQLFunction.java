/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.SourceCode;
import com.huawei.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import com.huawei.mppdbide.bl.util.DebugObjectGauss200Utils;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateSQLFunction.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CreateSQLFunction {

    /**
     * Execute.
     *
     * @param command the command
     */
    @Execute
    public void execute(@Optional @Named("function.command") String command) {
        MPPDBIDELoggerUtility.info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_CREATE_FUNCTION));

        /*
         * The object will never be null. In case of null, the Menu item will be
         * disabled automatically.
         */

        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        DebugObjectGroup debugObjectGroup = null;
        debugObjectGroup = (DebugObjectGroup) obj;
        if (null != debugObjectGroup) {

            final Database db = debugObjectGroup.getNamespace().getDatabase();

            final DebugObjects object = new DebugObjects(0, "NewObject",
                    "pl/sql".equals(command) ? OBJECTTYPE.SQLFUNCTION : OBJECTTYPE.CFUNCTION, db);
            object.setNamespace(debugObjectGroup.getNamespace());

            SourceCode sourceCode = new SourceCode();
            Namespace ns = debugObjectGroup.getNamespace();
            String namespacename = "";
            if (null != ns) {
                namespacename = ns.getQualifiedObjectName();
            }
            sourceCode.setCode(
                    DebugObjectGauss200Utils.getNewFunctionObjectTemplate("return_datatype", namespacename, command));
            object.setSourceCode(sourceCode);

            PLSourceEditor plSourceEditor = UIElement.getInstance().createEditor(object);
            if (null != plSourceEditor) {
                plSourceEditor.displaySourceForDebugObject(object);
                plSourceEditor.registerModifyListener();
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

        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        return obj instanceof DebugObjectGroup
                && OBJECTTYPE.FUNCTION_GROUP == ((DebugObjectGroup) obj).getObjectGroupType();
    }

}
