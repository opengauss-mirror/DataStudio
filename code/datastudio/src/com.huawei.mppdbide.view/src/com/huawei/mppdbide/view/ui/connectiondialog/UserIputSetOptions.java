/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.connectiondialog;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserIputSetOptions.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class UserIputSetOptions extends Dialog {
    private Label randomePageCost;
    private Label seqPageCost;
    private Text randomeCostInput;
    private Tablespace tablespace;
    private Label comboLblNotice;
    private Text seqcostInput;
    private Button okButton;
    private Button cancelButton;

    /**
     * Instantiates a new user iput set options.
     *
     * @param prnt the prnt
     * @param tablespace the tablespace
     */
    public UserIputSetOptions(Shell prnt, Tablespace tablespace) {
        super(prnt);
        setDefaultImage(getWindowImage());
        this.tablespace = tablespace;
    }

    private Image getWindowImage() {
        return IconUtility.getIconImage(IiconPath.TABLESPACE, getClass());
    }

    /**
     * Instantiates a new user iput set options.
     *
     * @param prnt the prnt
     * @param randomePageCost the randome page cost
     * @param seqPageCost the seq page cost
     * @param comboLblNotice the combo lbl notice
     * @param randomeCostInput the randome cost input
     * @param seqcostInput the seqcost input
     */
    public UserIputSetOptions(Shell prnt, Label randomePageCost, Label seqPageCost, Label comboLblNotice,
            Text randomeCostInput, Text seqcostInput) {
        super(prnt);
        this.randomePageCost = randomePageCost;
        this.seqPageCost = seqPageCost;
        this.comboLblNotice = comboLblNotice;
        this.randomeCostInput = randomeCostInput;
        this.seqcostInput = seqcostInput;
    }

    /**
     * Configure shell.
     *
     * @param newShell the new shell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getWindowTitle());
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite curComposite = (Composite) super.createDialogArea(parent);
        curComposite.setLayout(new GridLayout(2, false));

        GridData grdData2 = new GridData();
        grdData2.grabExcessHorizontalSpace = true;
        grdData2.horizontalAlignment = GridData.FILL;
        grdData2.verticalAlignment = GridData.FILL;
        grdData2.horizontalIndent = 5;
        grdData2.verticalIndent = 0;
        grdData2.minimumWidth = 300;
        grdData2.minimumHeight = 300;

        curComposite.setLayoutData(grdData2);
        HashMap<String, String> map = getRandomCost(tablespace);
        randomePageCost = new Label(curComposite, SWT.NONE);
        randomePageCost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        randomePageCost.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_RANCOST));
        randomeCostInput = userInputControlText(curComposite);
        randomeCostInput.setFocus();
        if (map.get("random_page_cost") != null) {
            randomeCostInput.setText(map.get("random_page_cost"));
        }
        // change
        randomeCostInput.addKeyListener(new KeyHelper());
        randomeCostInput.addVerifyListener(new TablespacePageCostValidator(randomeCostInput));
        seqPageCost = new Label(curComposite, SWT.NONE);
        seqPageCost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        seqPageCost.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_SEQCOST));
        seqcostInput = userInputControlText(curComposite);
        if (map.get("seq_page_cost") != null) {
            seqcostInput.setText(map.get("seq_page_cost"));
        }
        // change
        seqcostInput.addKeyListener(new KeyHelper());
        seqcostInput.addVerifyListener(new TablespacePageCostValidator(seqcostInput));
        comboLblNotice = new Label(curComposite, SWT.WRAP);
        comboLblNotice.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        comboLblNotice.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        comboLblNotice.setText(' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ');
        return curComposite;

    }

    /**
     * Gets the random cost.
     *
     * @param tblespace the tblespace
     * @return the random cost
     */
    public HashMap<String, String> getRandomCost(Tablespace tblespace) {
        String fileOption = null;
        String name = tblespace.getFileOption();

        fileOption = name;
        HashMap<String, String> data = new HashMap<String, String>(10);
        Pattern pattern = Pattern.compile("[\\{\\}\\=\\, ]++");
        String[] split = pattern.split(fileOption);
        for (int index = 0; index + 2 <= split.length; index += 2) {
            data.put(split[index], split[index + 1]);
        }
        return data;

    }

    /**
     * Prints the message.
     *
     * @param msg the msg
     */
    public void printMessage(String msg) {
        comboLblNotice.setText(msg);
        comboLblNotice.redraw();
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    protected abstract String getWindowTitle();

    /**
     * Perform ok operation.
     */
    protected abstract void performOkOperation();

    /**
     * User input control text.
     *
     * @param comp the comp
     * @return the text
     */
    protected Text userInputControlText(Composite comp) {
        int txtProp = SWT.BORDER | SWT.SINGLE;
        Text txtInput = new Text(comp, txtProp);
        GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

        data.widthHint = 300;

        txtInput.setLayoutData(data);
        return txtInput;
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        performOkOperation();
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String okLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";
        final String cancelLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        okButton = createButton(parent, UIConstants.OK_ID, okLabel, true);
        okButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");
        cancelButton = createButton(parent, CANCEL, cancelLabel, false);
        cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");
        okButton.setEnabled(true);
    }

    /**
     * Gets the random inputtext.
     *
     * @return the random inputtext
     */
    protected String getrandomInputtext() {

        Text text = (Text) randomeCostInput;
        if (text.isDisposed()) {
            return "";
        }
        return text.getText().trim();

    }

    /**
     * Gets the seq inputtext.
     *
     * @return the seq inputtext
     */
    protected String getSeqInputtext() {

        Text text = (Text) seqcostInput;
        if (text.isDisposed()) {
            return "";
        }
        return text.getText().trim();

    }

    /**
     * Checks if is dialog complete.
     *
     * @return true, if is dialog complete
     */
    public boolean isDialogComplete() {
        return !((Text) randomeCostInput).getText().isEmpty() || !((Text) seqcostInput).getText().isEmpty();
    }

    /**
     * Validate data.
     */
    public void validateData() {
        if (isDialogComplete()) {
            okButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class KeyHelper.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class KeyHelper implements KeyListener {

        @Override
        public void keyPressed(KeyEvent arg0) {

        }

        @Override
        public void keyReleased(KeyEvent event) {
            String eChar = event.character + "";
            try {
                // Validates the input is long value only.
                if (event.keyCode != 8 && event.keyCode != 127 && event.keyCode != 16777219 && event.keyCode != 16777220
                        && event.character != '.' && Long.parseLong(eChar) < 0) {
                    event.doit = false;
                }
            } catch (final NumberFormatException numberFormatException) {
                event.doit = false;
            }
        }

    }

}
