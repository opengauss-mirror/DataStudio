/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.connectiondialog.UserComboDialog;
import com.huawei.mppdbide.view.ui.table.UIUtils;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.view.handler.ViewWorkerJob.VIEWOPTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetViewSchemaHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
