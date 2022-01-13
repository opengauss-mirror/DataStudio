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

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.bl.serverdatacache.groups.TablespaceObjectGroup;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandCopy.
 *
 * @since 3.0.0
 */
public class CommandCopy {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        Control focusControl = Display.getDefault().getFocusControl();
        Clipboard cb = new Clipboard(Display.getDefault());
        if (null != focusControl) {
            if (focusControl instanceof StyledText) {
                Object partObject = UIElement.getInstance().getActivePartObject();
                PLSourceEditorCore core = null;
                if (partObject instanceof PLSourceEditor) {
                    core = ((PLSourceEditor) partObject).getSourceEditorCore();
                    core.copyDocText();

                } else if (partObject instanceof SQLTerminal) {
                    core = ((SQLTerminal) partObject).getTerminalCore();
                    core.copyDocText();
                } else {
                    editorContentCopy();
                }

            }
            if (focusControl instanceof Tree) {
                Tree treeFocusControl = (Tree) Display.getDefault().getFocusControl();
                if (treeFocusControl != null) {
                    TreeItem treeItem = treeFocusControl.getSelection()[0];

                    if (treeItem.getData() instanceof IDebugObject) {
                        IDebugObject debugObject = (IDebugObject) treeItem.getData();
                        String qualifiedName = debugObject.getDisplayName(true);
                        String namespace = debugObject.getDisplayName();
                        String neededStr = namespace.substring(0, namespace.indexOf('.') + 1) + qualifiedName;
                        cb.clearContents();
                        cb.setContents(new Object[] {neededStr}, new Transfer[] {TextTransfer.getInstance()});
                    } else if (treeItem.getData() instanceof ServerObject) {
                        ServerObject servObj = (ServerObject) treeItem.getData();

                        String qualifiedName = servObj.getDisplayName();
                        cb.clearContents();
                        cb.setContents(new Object[] {qualifiedName}, new Transfer[] {TextTransfer.getInstance()});
                    } else if (treeItem.getData() instanceof Tablespace) {
                        cb.clearContents();
                        Object object = treeItem.getText();
                        cb.setContents(new Object[] {object}, new Transfer[] {TextTransfer.getInstance()});
                    }
                }
            }
        }
    }

    /**
     * Editor content copy.
     */
    private void editorContentCopy() {
        StyledText styledText = (StyledText) Display.getDefault().getFocusControl();
        LineBackgroundListener lineBackgroundListener = PLSourceEditorCore.getLineBackgroundColorListener();
        if (null != styledText) {
            // To avoid coping of Blue background color override it with
            // background color listener,
            styledText.addLineBackgroundListener(lineBackgroundListener);
            styledText.copy();
            // Remove background color listener so UI screen will get blue
            // background selection.
            styledText.removeLineBackgroundListener(lineBackgroundListener);
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        if (Display.getDefault().getFocusControl() instanceof StyledText) {
            return ((StyledText) Display.getDefault().getFocusControl()).getSelectionCount() > 0;
        } else if (Display.getDefault().getFocusControl() instanceof Tree) {
            TreeItem treeItem = ((Tree) Display.getDefault().getFocusControl()).getSelection()[0];

            if (!(treeItem.getData() instanceof ServerObject) || treeItem.getData() instanceof TablespaceObjectGroup) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
