/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.source.ISourceViewer;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.sourceeditor.PLSQLFormatter;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandFormatter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CommandFormatter {
    private PLSourceEditor plSourceEditor;

    /**
     * Execute.
     *
     * @param registry the registry
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Execute
    public void execute(IExtensionRegistry registry) throws MPPDBIDEException {
        MPPDBIDELoggerUtility.info(MessageConfigLoader
                .getProperty(IMessagesConstants.GUI_FORMATTING_SQL_QUERY_STATEMENTS_FROM_FORMAT_MENU));
        Object partObject = UIElement.getInstance().getActivePartObject();
        PLSourceEditorCore sourceViewer = null;

        if (partObject instanceof SQLTerminal) {
            SQLTerminal terminal = (SQLTerminal) partObject;
            sourceViewer = terminal.getTerminalCore();
        } else if (partObject instanceof PLSourceEditor) {
            plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
            if (plSourceEditor == null) {
                Object object = UIElement.getInstance().getActivePartObject();
                if (object instanceof PLSourceEditor) {
                    plSourceEditor = (PLSourceEditor) object;
                } else {
                    return;
                }
            }
            sourceViewer = plSourceEditor.getTerminalCore();
        } else {
            return;
        }
        if (sourceViewer != null) {
            sourceViewer.doFormattingOfContents();
        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object partObj = UIElement.getInstance().getActivePartObject();
        if (partObj instanceof SQLTerminal) {
            SQLTerminal terminal = (SQLTerminal) partObj;
            return terminal.getTerminalCore().getSourceViewer().isEditable();
        } else if (partObj instanceof PLSourceEditor) {
            PLSourceEditor sourceEditor = (PLSourceEditor) partObj;
            return sourceEditor.getSourceEditorCore() != null
                    && sourceEditor.getSourceEditorCore().getSourceViewer().isEditable();
        }
        return false;
    }
}
