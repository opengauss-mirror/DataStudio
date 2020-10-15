/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminal;

import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.sourceeditor.ErrorAnnotation;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLTerminalUtility.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author s00428892
 * @version [DataStudio 8.0.2, 04 Apr, 2020]
 * @since 04 Apr, 2020
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
