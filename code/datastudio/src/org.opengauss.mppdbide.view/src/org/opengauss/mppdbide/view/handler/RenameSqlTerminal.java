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

package org.opengauss.mppdbide.view.handler;

import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class RenameSqlTerminal.
 *
 * @since 3.0.0
 */
public class RenameSqlTerminal {
    @Inject
    private EPartService partService;

    @Inject
    private static EModelService modelService;

    @Inject
    private static MApplication application;

    /**
     * Execute.
     *
     * @param shell the shell
     * @param sqlTerminalPartElemId the sql terminal part elem id
     */
    @Execute
    public void execute(final Shell shell,
            @Named("org.opengauss.mppdbide.view.commandparameter.sqlTerminalPart") String sqlTerminalPartElemId) {
        MPart sqlTerminalPart = partService.findPart(sqlTerminalPartElemId);
        if (null != sqlTerminalPart) {
            SqlTerminalRenameDialog renameDialog = new SqlTerminalRenameDialog(shell, sqlTerminalPart);
            renameDialog.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return true;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SqlTerminalRenameDialog.
     */
    private static final class SqlTerminalRenameDialog extends UserInputDialog {
        private MPart part;
        private String oldTerminalTitle;

        /**
         * Instantiates a new sql terminal rename dialog.
         *
         * @param parent the parent
         * @param serverObject the server object
         */
        private SqlTerminalRenameDialog(Shell parent, Object serverObject) {
            super(parent, serverObject);
            part = (MPart) serverObject;
            oldTerminalTitle = part.getLabel();
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TERMINAL_TITLE);
        }

        @Override
        protected String getHeader() {
            return MessageConfigLoader.getProperty(IMessagesConstants.RENAME_TERMINAL_MSG, part.getLabel());
        }

        @Override
        protected void performOkOperation() {
            String userInput = ((StyledText) inputControl).getText();

            List<MPartStack> stacks = modelService.findElements(application, "org.opengauss.mppdbide.partstack.id.editor",
                    MPartStack.class, null);
            List<MStackElement> eles = stacks.get(0).getChildren();
            if (null != eles) {
                for (MStackElement ele : eles) {
                    if (ele instanceof MPart) {
                        MPart mPart = (MPart) ele;
                        if (userInput.equalsIgnoreCase(mPart.getLabel())) {
                            printErrorMessage(MessageConfigLoader.getProperty(
                                    IMessagesConstants.RENAME_TERMINAL_DUPLICATE_NAME_ERROR, oldTerminalTitle,
                                    userInput), false);
                            return;
                        }

                    }
                }
            }

            part.setLabel(userInput);
            SQLTerminal sqlt = (SQLTerminal) part.getObject();
            sqlt.setPartLabel(userInput);

            String toolTip = sqlt.getUpdatedToolTip();
            sqlt.setTabToolTip(toolTip);
            part.setTooltip(toolTip);
            close();
        }

        /**
         * User input control.
         *
         * @param comp the comp
         * @return the object
         */
        protected Object userInputControl(Composite comp) {
            int txtProp = SWT.BORDER | SWT.SINGLE;
            StyledText txtInput = new StyledText(comp, txtProp);
            txtInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            txtInput.forceFocus();
            txtInput.setText(getInitialText());
            final ControlDecoration deco = new ControlDecoration(txtInput, SWT.TOP | SWT.LEFT);

            // use an existing image
            Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

            // set description and image
            deco.setImage(image);

            // always show decoration
            deco.setShowOnlyOnFocus(false);
            txtInput.setTextLimit(MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH);
            txtInput.addVerifyListener(new InputControlListener());
            return txtInput;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            return;
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            return;
        }
        
        /**
         * The listener interface for receiving inputControl events. The class
         * that is interested in processing a inputControl event implements this
         * interface, and the object created with that class is registered with
         * a component using the component's <code>addInputControlListener<code>
         * method. When the inputControl event occurs, that object's appropriate
         * method is invoked.
         *
         * InputControlEvent
         */
        private static class InputControlListener implements VerifyListener {
            @Override
            public void verifyText(VerifyEvent event) {
                String text = ((StyledText) event.widget).getText() + event.text;
                try {

                    String qualifiedTitle = text;
                    Pattern pattern = Pattern.compile(".*[\\\\/:*?\"<>|].*");

                    if (pattern.matcher(text).matches()) {
                        qualifiedTitle = text.replaceAll("[\\\\/:*?\"<>|]", "");
                    }
                    if (!qualifiedTitle.equals(text)) {
                        event.doit = false;
                    }
                } catch (NumberFormatException e) {
                    event.doit = false;
                }
            }
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            return;
        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {
            return;
        }

        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_SQL_TERMINAL, this.getClass());
        }
    }

}
