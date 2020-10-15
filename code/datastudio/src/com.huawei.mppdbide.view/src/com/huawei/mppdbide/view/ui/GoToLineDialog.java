/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class GoToLineDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GoToLineDialog extends Dialog {
    private IDocument doc;
    private int linenumber;
    private SourceViewer viewer;
    private Label lebelNotice;
    private StyledText txtInput = null;

    private int finalRange = 0;
    private int initialValue = 1;
    private Integer text = 0;
    private String initialTextBoxValue = null;
    private int goToLineTextLimit = 10;
    private Button okButton = null;
    private Button cancelButton = null;

    /**
     * Instantiates a new go to line dialog.
     *
     * @param parentShell the parent shell
     * @param viewer the viewer
     */
    public GoToLineDialog(Shell parentShell, SourceViewer viewer) {
        super(parentShell);
        this.viewer = viewer;
        this.doc = viewer.getDocument();
        this.finalRange = doc.getNumberOfLines();

    }

    /**
     * Configure shell.
     *
     * @param shell the shell
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.GO_TO_LINE_POPUP_TITLE));
        shell.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_WND_GO_To_LINE_DIALOG_001");
        shell.setImage(IconUtility.getIconImage(IiconPath.ICO_FIND, this.getClass()));
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        Composite curComposite = (Composite) super.createDialogArea(parent);
        curComposite.setLayout(new GridLayout(1, false));
        int txtProp = SWT.BORDER | SWT.SINGLE;

        GridData gridData2 = new GridData();
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.verticalAlignment = GridData.FILL;
        gridData2.horizontalIndent = 5;
        gridData2.verticalIndent = 0;
        gridData2.minimumWidth = 400;

        curComposite.setLayoutData(gridData2);

        Label lblTextVal = new Label(curComposite, SWT.NONE);
        lblTextVal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        lblTextVal.setText(MessageConfigLoader.getProperty(IMessagesConstants.ENTER_LINE_NUMBER) + '(' + initialValue
                + '-' + finalRange + ')');

        txtInput = new StyledText(curComposite, txtProp);

        txtInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        ITextSelection textSelection = (ITextSelection) viewer.getSelection();
        text = textSelection.getStartLine() + 1;
        initialTextBoxValue = text.toString();
        txtInput.setText(" ");
        txtInput.setText(initialTextBoxValue);
        txtInput.selectAll();
        lebelNotice = new Label(curComposite, SWT.WRAP);
        lebelNotice.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
        lebelNotice.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        lebelNotice.setText(' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ');
        txtInput.setTextLimit(goToLineTextLimit);

        txtInput.addKeyListener(addTxtInputKeyListener());

        return curComposite;
    }

    private KeyListener addTxtInputKeyListener() {
        return new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

                String regex = "^[0-9]*$";

                if (!(txtInput.getText().matches(regex)) || txtInput.getText().length() > 10) {
                    invalidText();

                } else if (txtInput.getText().isEmpty()) {

                    emptyText();

                }

                else if (txtInput.getText().matches(regex)) {
                    linenumber = Integer.parseInt(txtInput.getText());

                    if (linenumber > finalRange || linenumber < initialValue) {
                        invalidText();
                    } else {
                        validText();
                    }

                }

            }

        };
    }

    /**
     * Gets the viewer linenumber.
     *
     * @return the viewer linenumber
     */
    public int getViewerLinenumber() {
        return linenumber;
    }

    /**
     * Sets the viewer linenumber.
     *
     * @param lineno the new viewer linenumber
     */
    private void setViewerLinenumber(int lineno) {
        this.linenumber = lineno;
    }

    /**
     * Invalid text.
     */
    public void invalidText() {

        lebelNotice.setText(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_MESSAGE_FOR_GO_TO_LINE));

        lebelNotice.redraw();
        okButton.setEnabled(false);
        cancelButton.setEnabled(true);

    }

    /**
     * Empty text.
     */
    public void emptyText() {
        okButton.setEnabled(false);
        cancelButton.setEnabled(true);

        lebelNotice.setText(" ");
        lebelNotice.redraw();

    }

    /**
     * Valid text.
     */
    public void validText() {

        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
        lebelNotice.setText(" ");
        lebelNotice.redraw();

    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {

        if (buttonId == UIConstants.OK_ID) {
            linenumber = Integer.parseInt(txtInput.getText());
            setViewerLinenumber(linenumber);
        }
        setReturnCode(buttonId);
        close();
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        final String okLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK) + "     ";

        final String cancelLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.BTN_CANCEL) + "     ";

        okButton = createButton(parent, UIConstants.OK_ID, okLabel, true);
        okButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CONTINUE_001");

        cancelButton = createButton(parent, UIConstants.CANCEL_ID, cancelLabel, false);
        cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CANCEL_001");

    }

}
