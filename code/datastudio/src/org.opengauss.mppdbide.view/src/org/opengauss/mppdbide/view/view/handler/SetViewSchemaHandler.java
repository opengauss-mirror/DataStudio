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

package org.opengauss.mppdbide.view.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserComboDialog;
import org.opengauss.mppdbide.view.ui.table.UIUtils;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.view.handler.ViewWorkerJob.VIEWOPTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetViewSchemaHandler.
 *
 * @since 3.0.0
 */
public class SetViewSchemaHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        ViewMetaData view = IHandlerUtilities.getSelectedViewObject();
        SetSchemaDialog dialog = new SetSchemaDialog(Display.getDefault().getActiveShell(), view);
        dialog.open();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ViewMetaData view = IHandlerUtilities.getSelectedViewObject();
        if (null != view) {
            Namespace ns = (Namespace) view.getNamespace();
            if (null != ns && ns.getDatabase().isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SetSchemaDialog.
     */
    private static class SetSchemaDialog extends UserComboDialog {

        /**
         * Instantiates a new sets the schema dialog.
         *
         * @param prnt the prnt
         * @param serverObject the server object
         */
        protected SetSchemaDialog(Shell prnt, Object serverObject) {
            super(prnt, serverObject);
        }

        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_VIEW, this.getClass()));
        }




        @Override
        protected String getHeader() {
            ViewMetaData view = (ViewMetaData) getObject();
            return MessageConfigLoader.getProperty(IMessagesConstants.SET_VIEW_MESSAGE,
                    view.getNamespace().getQualifiedObjectName(), view.getQualifiedObjectName());
        }

        @Override
        protected void comboDisplayValues(final Combo inputCombo) {
            ViewMetaData selectedView = (ViewMetaData) getObject();
            UIUtils.displayNamespaceList(selectedView.getNamespace().getDatabase(),
                    selectedView.getNamespace().getName(), inputCombo, false);
            setOkButtonEnabled(false);
            if (inputCombo.getSelectionIndex() >= 0) {
                inputCombo.remove(inputCombo.getSelectionIndex());
            }

            inputCombo.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    boolean isComboSelected = inputCombo.getSelectionIndex() >= 0;
                    setOkButtonEnabled(isComboSelected);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }
            });
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.SET_VIEW_SCHEMA);
        }

        @Override
        protected void performOkOperation() {
            String userInput = getUserInput();

            if (!userInput.isEmpty()) {
                ViewMetaData view = (ViewMetaData) getObject();

                String oldSchemaName = view.getNamespace().getQualifiedObjectName() + '.'
                        + view.getQualifiedObjectName();

                printMessage(MessageConfigLoader.getProperty(IMessagesConstants.SET_VIEW_SCHEMA_MOVING, oldSchemaName,
                        userInput));
                ViewWorkerJob job = new ViewWorkerJob("Set View Schema", VIEWOPTYPE.SET_SCHEMA, "", view, userInput,
                        this);

                job.schedule();
            }
        }
    }
}
