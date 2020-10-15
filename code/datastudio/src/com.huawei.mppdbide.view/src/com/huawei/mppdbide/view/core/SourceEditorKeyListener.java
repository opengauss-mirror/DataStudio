/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

/**
 * Title: SourceEditorKeyListener
 * 
 * Description:The listener interface for receiving sourceEditorKey events. The
 * class that is interested in processing a sourceEditorKey event implements
 * this interface, and the object created with that class is registered with a
 * component using the component's <code>addSourceEditorKeyListener<code>
 * method. When the sourceEditorKey event occurs, that object's appropriate
 * method is invoked.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 21-May-2019]
 * @since 21-May-2019
 */

public class SourceEditorKeyListener implements KeyListener {
    private TextViewer viewer;
    private boolean disableUndoRedo;

    /**
     * Instantiates a new source editor key listener.
     *
     * @param viewer the viewer
     */
    public SourceEditorKeyListener(TextViewer viewer) {
        this(viewer, false);
    }

    /**
     * Instantiates a new source editor key listener.
     *
     * @param viewer the viewer
     * @param disableUndoRedo the disable undo redo
     */
    public SourceEditorKeyListener(TextViewer viewer, boolean disableUndoRedo) {
        this.viewer = viewer;
        this.disableUndoRedo = disableUndoRedo;
    }

    /**
     * Key pressed.
     *
     * @param event the event
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (isUndoKeyPress(event) && !disableUndoRedo) {
            viewer.doOperation(ITextOperationTarget.UNDO);
        } else if (isRedoKeyPress(event) && !disableUndoRedo) {
            viewer.doOperation(ITextOperationTarget.REDO);
        } else if (isSelectAllKeyPress(event)) {
            viewer.doOperation(ITextOperationTarget.SELECT_ALL);
        }
    }

    /**
     * Key released.
     *
     * @param keyEvent the key event
     */
    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    /**
     * Undo keybinding handler.
     *
     * @param keyEvent the e
     * @return true, if is undo key press
     */
    private boolean isUndoKeyPress(KeyEvent keyEvent) {
        // CTRL + z
        return ((keyEvent.stateMask & SWT.CONTROL) != 0) && ((keyEvent.keyCode == 'z') || (keyEvent.keyCode == 'Z'));
    }

    /**
     * Redo keybinding handler.
     *
     * @param keyEvent the e
     * @return true, if is redo key press
     */
    private boolean isRedoKeyPress(KeyEvent keyEvent) {
        // CTRL + y
        return ((keyEvent.stateMask & SWT.CONTROL) != 0) && ((keyEvent.keyCode == 'y') || (keyEvent.keyCode == 'Y'));
    }

    /**
     * Select all keybinding handler.
     *
     * @param event the e
     * @return true, if is select all key press
     */
    private boolean isSelectAllKeyPress(KeyEvent event) {
        // CTRL + a
        return ((event.stateMask & SWT.CONTROL) != 0) && ((event.keyCode == 'a') || (event.keyCode == 'A'));
    }
}
