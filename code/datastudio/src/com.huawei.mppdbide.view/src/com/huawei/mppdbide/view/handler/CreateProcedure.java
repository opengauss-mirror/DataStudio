/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.SourceCode;
import com.huawei.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
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
 * Description: The Class CreateProcedure.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CreateProcedure {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        MPPDBIDELoggerUtility.info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_CREATE_PROCEDURE));

        /*
         * The object will never be null. In case of null, the Menu item will be
         * disabled automatically.
         */

        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj == null) {
            return;
        }
        ObjectGroup debugObjectGroup = (ObjectGroup) obj;
        IDebugObject object = null;
        SourceCode srcCode = new SourceCode();
        INamespace namespace = debugObjectGroup.getNamespace();
        if (namespace == null) {
            return;
        }
        if (debugObjectGroup instanceof OLAPObjectGroup) {
            final Database db = namespace.getDatabase();
            object = getDebugObject(db);
            srcCode.setCode(IHandlerUtilities.getNewPLSQLProcedureTemplate(namespace));
        }
        if (object == null) {
            return;
        }
        object.setNamespace(namespace);

        object.setSourceCode(srcCode);

        PLSourceEditor plSourceEditor = UIElement.getInstance().createEditor(object);
        if (null != plSourceEditor) {
            plSourceEditor.displaySourceForDebugObject(object);
            plSourceEditor.registerModifyListener();
        }

    }

    /**
     * Gets the debug object.
     *
     * @param db the db
     * @return the debug object
     */
    private DebugObjects getDebugObject(final Database db) {
        return new DebugObjects(0, "NewObject", OBJECTTYPE.PROCEDURE, db);
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
        ObjectGroup<?> obj = (ObjectGroup<?>) IHandlerUtilities.getObjectBrowserSelectedObject();
        return (obj instanceof DebugObjectGroup) && (OBJECTTYPE.FUNCTION_GROUP == obj.getObjectGroupType());
    }
}
