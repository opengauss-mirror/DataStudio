/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.view.handler.debug;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 30,11,2020]
 * @since 30,11,2020
 */
public class DebugHandlerUtils {
    private static DebugHandlerUtils debugUtils = new DebugHandlerUtils();
    private boolean isDebugStart = false;
    private DebugHandlerUtils() {
        
    }
    
    public static DebugHandlerUtils getInstance() {
        return debugUtils;
    }
    
    public boolean canStartDebug() {
        if (isDebugStart) {
            return false;
        }
        
        IDebugObject debugObject = IHandlerUtilities.getSelectedDebugObject();
        if (debugObject != null) {
            return debugObject.getDatabase().isConnected();
        }

        PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        if (plSourceEditor != null) {
            debugObject = plSourceEditor.getDebugObject();
            if (debugObject != null) {
                return debugObject.getDatabase().isConnected();
            }
        }
        return false;
    }
    
    public boolean canTerminateDebug() {
        return isDebugStart;
    }
    
    public void setDebugStart(boolean isDebugStart) {
        this.isDebugStart = isDebugStart;
    }

    /**
     * this use to show debug view partstack
     * 
     * @param isShow show or not
     * @return null
     * */
    public void showAllDebugView(boolean isShow) {
        String partId = "com.huawei.mppdbide.partsashcontainer.id.sub.alldebug";
        MUIElement allDebugPart = UIElement.getInstance().getModelService().find(partId, 
                UIElement.getInstance().getApplication());
        allDebugPart.setVisible(isShow);
    }
    
    @SuppressWarnings("restriction")
    public void initDebugSourceView() {
        String viewCommand = "com.huawei.mppdbide.command.id.viewsourceobjectbrowseritem";
        ECommandService commandService = EclipseInjections.getInstance().getCommandService();
        EHandlerService handlerService = EclipseInjections.getInstance().getHandlerService();
        Command command = commandService.getCommand(viewCommand);
        ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, null);
        handlerService.executeHandler(parameterizedCommand);
    }
}
