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

package org.opengauss.mppdbide.view.ui.terminal;

import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.sourceeditor.ErrorAnnotation;
import org.opengauss.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLTerminalUtility.
 *
 * @since 3.0.0
 */
public class SQLTerminalUtility {

    /**
     * Gets the server version.
     *
     * @return the server version
     */
    public String getServerVersion(Database db) {
        String version = "";
        try {
            version = db.getServerVersion();
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("getting server version failed", exception);
            return version;
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("getting server version failed", exception);
            return version;
        }
        return version;
    }

    /**
     * Enable auto commit.
     *
     * @return true, if successful
     */
    public boolean enableAutoCommit() {
        SQLTerminal visibleTerminal = UIElement.getInstance().getVisibleTerminal();
        Database selectedDatabase = null;
        if (null != visibleTerminal && null != visibleTerminal.getSelectedDatabase()) {
            selectedDatabase = visibleTerminal.getSelectedDatabase();
        } else {
            return false;
        }

        boolean isConnected = selectedDatabase != null ? selectedDatabase.isConnected() : false;
        boolean isexecution = visibleTerminal.isExecuteInProgress();
        return isConnected && !isexecution;
    }

    /**
     * Checks if is button enabled.
     *
     * @return true, if is button enabled
     */
    public boolean isButtonEnabled(boolean isActivated, PLSourceEditorCore sourceEditor) {
        if (!isActivated) {
            return false;
        }

        boolean isSrcEditorEmpty = sourceEditor.getSourceViewer().getDocument().get().trim().isEmpty();
        SQLTerminal visibleTerminal = UIElement.getInstance().getVisibleTerminal();
        Database selectedDatabase = null;
        if (visibleTerminal != null) {
            selectedDatabase = visibleTerminal.getSelectedDatabase();
        } else {
            return false;
        }

        boolean isConnected = selectedDatabase != null ? selectedDatabase.isConnected() : false;
        boolean isexecution = visibleTerminal.isExecuteInProgress();
        return isConnected && !isSrcEditorEmpty && !isexecution;
    }

    /**
     * Creates the annotation painter.
     *
     * @param sv the sv
     * @param access the access
     * @return the annotation painter
     */
    public static AnnotationPainter createAnnotationPainter(ISourceViewer sv, IAnnotationAccess access) {
        AnnotationPainter annotationPainter = new AnnotationPainter(sv, access);
        annotationPainter.addAnnotationType(ErrorAnnotation.getStrategyid());
        annotationPainter.setAnnotationTypeColor(ErrorAnnotation.getStrategyid(),
                new Color(Display.getDefault(), ErrorAnnotation.getErrorRgb()));
        return annotationPainter;
    }

}
