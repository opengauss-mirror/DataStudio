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

package org.opengauss.mppdbide.view.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class FindAndReplaceDialog.
 *
 * @since 3.0.0
 */
public class FindAndReplaceDialog extends Dialog {
    private TextViewer textViewer;
    private int lastSearchReturnIndex;
    private boolean atleastOneMatchFound;

    /* Elements */
    private Text txtFind;
    private Text txtReplace;
    private Button btnIsBackward;
    private Button btnIsMatchCase;
    private Button btnIsWholeWord;
    private Button btnIsWrapAround;
    private Button btnReplace;

    private FindAndReplaceOptions findAndReplaceOptions;
    private PLSourceEditorCore core = null;

    /**
     * Instantiates a new find and replace dialog.
     *
     * @param parentShell the parent shell
     * @param textViewer the text viewer
     */
    public FindAndReplaceDialog(Shell parentShell, TextViewer textViewer) {
        super(parentShell);

        this.textViewer = textViewer;

        setDefaultImage(IconUtility.getIconImage(IiconPath.ICO_FIND, this.getClass()));

        setBlockOnOpen(false);

    }

    /**
     * Configure shell.
     *
     * @param shell the shell
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_TITLE));
        shell.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_WND_FIND_AND_REPLACE_DIALOG_001");

        setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
        setBlockOnOpen(false);
        core = getViewerForFindingText();

    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        parent.setLayout(new FillLayout());

        /* Parent most composite */
        Composite findComposite = new Composite(parent, SWT.NONE);
        findComposite.setLayout(new GridLayout(2, false));
        findComposite.setData(new GridData(SWT.FILL, SWT.FILL, true, true));

        /* Find/Replace textbox & checkbox left panel. -> will be left side */
        Composite inputComp = new Composite(findComposite, SWT.NONE | SWT.FILL);
        inputComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        inputComp.setLayout(new GridLayout());
        inputComp.setData(new GridData(SWT.FILL, SWT.FILL, true, true));

        /* Text box composite inside input composite. Top of inputComp. */
        Composite textboxComp = new Composite(inputComp, SWT.NONE | SWT.FILL);
        textboxComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        textboxComp.setLayout(new GridLayout(2, false));
        textboxComp.setData(new GridData(SWT.FILL, SWT.FILL, true, true));

        /* Text box controls */
        addFindAndReplaceText(textboxComp);
        /* Check box controls */
        addCheckBoxForTextOptions(inputComp);

        /* Buttons composite -> will be on right side */
        addButtons(findComposite);

        setParentLocation(parent);
        return parent;
    }

    private void setParentLocation(Composite parent) {
        parent.setSize(375, 170);
        Rectangle screenSize = Display.getDefault().getPrimaryMonitor().getBounds();
        parent.setLocation((screenSize.width - parent.getBounds().width) / 2,
                (screenSize.height - parent.getBounds().height) / 2);
    }

    private void addButtons(Composite findComposite) {
        Composite btnComp = new Composite(findComposite, SWT.NONE | SWT.TOP);
        btnComp.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
        btnComp.setLayout(new GridLayout());
        btnComp.setData(new GridData(SWT.FILL, SWT.TOP | SWT.FILL, true, true));

        /* Button controls */
        final Button btnFind = new Button(btnComp, SWT.NONE);
        btnFind.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
        btnFind.setText(MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_NEXT));
        btnFind.addSelectionListener(new FindSelection());

        btnReplace = new Button(btnComp, SWT.NONE);
        btnReplace.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
        btnReplace.setText(MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE));
        btnReplace.addSelectionListener(new ReplaceSelection());

        final Button btnReplaceAll = new Button(btnComp, SWT.NONE);
        btnReplaceAll.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
        btnReplaceAll.setText(MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_ALL));
        btnReplaceAll.addSelectionListener(new ReplaceAllSelection());

        Button btnClose = new Button(btnComp, SWT.NONE);
        btnClose.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
        btnClose.setText(MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CLOSE));
        btnClose.addSelectionListener(new CloseSelection());

        txtFind.addModifyListener(new FindModifyListener(btnFind, btnReplaceAll));

        txtReplace.addModifyListener(new ReplaceModifyListener(btnFind, btnReplaceAll));
    }

    private void addCheckBoxForTextOptions(Composite inputComp) {
        /* Text box composite inside input compsite. Bottom of inputComp. */
        Composite checkboxComp = new Composite(inputComp, SWT.NONE | SWT.FILL);
        checkboxComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCheckboxComp = new GridLayout();
        glCheckboxComp.numColumns = 2;
        checkboxComp.setLayout(glCheckboxComp);
        checkboxComp.setData(new GridData(SWT.FILL, SWT.FILL, true, true));

        btnIsBackward = new Button(checkboxComp, SWT.CHECK);
        btnIsBackward.setText(MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_BACK));

        btnIsMatchCase = new Button(checkboxComp, SWT.CHECK);
        btnIsMatchCase.setText(MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_MATCH));

        btnIsWholeWord = new Button(checkboxComp, SWT.CHECK);
        btnIsWholeWord.setText(MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_WHOLE));

        btnIsWrapAround = new Button(checkboxComp, SWT.CHECK);
        btnIsWrapAround.setText(MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_WRAP));
        btnIsWrapAround.setSelection(true);
    }

    private void addFindAndReplaceText(Composite textboxComp) {
        Label lblFind = new Label(textboxComp, SWT.NONE);
        lblFind.setText(MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLCAE_WHAT));
        txtFind = new Text(textboxComp, SWT.BORDER | SWT.SINGLE);
        txtFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        ITextSelection textSel = (ITextSelection) textViewer.getSelectionProvider().getSelection();
        txtFind.setText(textSel.getText());

        Label lblReplace = new Label(textboxComp, SWT.NONE);
        lblReplace.setText(MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_WITH));
        txtReplace = new Text(textboxComp, SWT.BORDER | SWT.SINGLE);
        txtReplace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }

    /**
     * The listener interface for receiving replaceModify events. The class that
     * is interested in processing a replaceModify event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addReplaceModifyListener<code>
     * method. When the replaceModify event occurs, that object's appropriate
     * method is invoked.
     *
     * ReplaceModifyEvent
     */
    private class ReplaceModifyListener implements ModifyListener {
        private Button btnFind;
        private Button btnReplaceAll;

        public ReplaceModifyListener(Button btnFind, Button btnReplaceAll) {
            this.btnFind = btnFind;
            this.btnReplaceAll = btnReplaceAll;
        }

        @Override
        public void modifyText(ModifyEvent e) {
            boolean isFindTxtEmpty = txtFind.getText().isEmpty();
            boolean isReplaceTxtEmpty = txtReplace.getText().isEmpty();

            btnFind.setEnabled(!isFindTxtEmpty);
            btnReplace.setEnabled(!isFindTxtEmpty && !isReplaceTxtEmpty);
            btnReplaceAll.setEnabled(!isFindTxtEmpty && !isReplaceTxtEmpty);
        }
    }

    /**
     * The listener interface for receiving findModify events. The class that is
     * interested in processing a findModify event implements this interface,
     * and the object created with that class is registered with a component
     * using the component's <code>addFindModifyListener<code> method. When the
     * findModify event occurs, that object's appropriate method is invoked.
     *
     * FindModifyEvent
     */
    private class FindModifyListener implements ModifyListener {
        private Button btnFind;
        private Button btnReplaceAll;

        public FindModifyListener(Button btnFind, Button btnReplaceAll) {
            this.btnFind = btnFind;
            this.btnReplaceAll = btnReplaceAll;
        }

        @Override
        public void modifyText(ModifyEvent e) {
            boolean isFindTxtEmpty = txtFind.getText().isEmpty();
            boolean isReplaceTxtEmpty = txtReplace.getText().isEmpty();

            btnFind.setEnabled(!isFindTxtEmpty);
            btnReplace.setEnabled(!isFindTxtEmpty && !isReplaceTxtEmpty);
            btnReplaceAll.setEnabled(!isFindTxtEmpty && !isReplaceTxtEmpty);
        }
    }

    /**
     * Close.
     *
     * @return true, if successful
     */
    @Override
    public boolean close() {

        if (core != null) {
            findAndReplaceOptions = core.getFindAndReplaceoptions();

            if (findAndReplaceOptions != null) {
                findAndReplaceOptions.setBackwardSearch(false);
            } else {
                return super.close();
            }

        }

        Object partObject = UIElement.getInstance().getActivePartObject();
        if (partObject instanceof PLSourceEditor) {
            ((PLSourceEditor) partObject).getSourceEditorCore().setFindAndReplaceoptions(findAndReplaceOptions);
        } else if (partObject instanceof SQLTerminal) {
            ((SQLTerminal) partObject).getTerminalCore().setFindAndReplaceoptions(findAndReplaceOptions);
        }

        return super.close();
    }

    private void replaceAllText(String findText, String replaceText) {
        IFindReplaceTarget findReplaceTarget = textViewer.getFindReplaceTarget();
        boolean isFwdSearch = !btnIsBackward.getSelection();
        boolean isCaseSensitive = btnIsMatchCase.getSelection();
        boolean isWholeWord = btnIsWholeWord.getSelection();

        lastSearchReturnIndex = 0;
        atleastOneMatchFound = false;
        int searchStartPosition = 0;

        while (true) {
            lastSearchReturnIndex = findReplaceTarget.findAndSelect(searchStartPosition, findText, isFwdSearch,
                    isCaseSensitive, isWholeWord);

            if (lastSearchReturnIndex > -1 && !atleastOneMatchFound) {
                atleastOneMatchFound = true;
            }

            if (lastSearchReturnIndex == -1) {
                break;
            }

            findReplaceTarget.replaceSelection(replaceText);
            searchStartPosition = core.getSearchStartPosition(isFwdSearch);

        }

        if (!atleastOneMatchFound) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_SEARCH_NOT_FOUND),
                    MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_SEARCH_NOT_FOUND) + '!');
        }
    }

    /**
     * Gets the find and replace input options.
     *
     * @return the find and replace input options
     */
    public FindAndReplaceOptions getFindAndReplaceInputOptions() {
        FindAndReplaceOptions options = new FindAndReplaceOptions();

        options.setBackwardSearch(btnIsBackward.getSelection());

        options.setCaseSensitive(btnIsMatchCase.getSelection());

        options.setWholeWord(btnIsWholeWord.getSelection());

        options.setWrapAround(btnIsWrapAround.getSelection());

        options.setSearchText(txtFind.getText());

        if (btnReplace.isEnabled()) {
            options.setReplaceText(txtReplace.getText());
        }

        return options;

    }

    /**
     * Reset search start index.
     */
    public void resetSearchStartIndex() {
        this.lastSearchReturnIndex = 0;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CloseSelection.
     */
    private class CloseSelection extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {

            close();

        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ReplaceAllSelection.
     */
    private class ReplaceAllSelection extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            replaceAllText(txtFind.getText(), txtReplace.getText());
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ReplaceSelection.
     */
    private class ReplaceSelection extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            core.setFindAndReplaceoptions(getFindAndReplaceInputOptions());
            core.findText(true);

        }
    }

    /**
     * Gets the viewer for finding text.
     *
     * @return the viewer for finding text
     */
    public PLSourceEditorCore getViewerForFindingText() {

        Object partObject = UIElement.getInstance().getActivePartObject();
        if (partObject instanceof PLSourceEditor) {
            core = ((PLSourceEditor) partObject).getSourceEditorCore();
        } else if (partObject instanceof SQLTerminal) {
            core = ((SQLTerminal) partObject).getTerminalCore();
        }

        return core;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class FindSelection.
     */
    private class FindSelection extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            core.setFindAndReplaceoptions(getFindAndReplaceInputOptions());
            core.findText(false);

        }
    }

}
