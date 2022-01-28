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

package com.huawei.mppdbide.view.functionchange;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.core.SourceEditorKeyListener;
import com.huawei.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerDecorationSupport;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class FunctionChangeNotifyDialog.
 *
 * @since 3.0.0
 */
public class FunctionChangeNotifyDialog extends Dialog {
    private String popupMessage;
    private String cancelButtonText;
    private String serverCode;
    private String dialogTitle;
    private String previewMsg;
    private String processButtonText;
    private SQLSyntax syntax;

    /**
     * Instantiates a new function change notify dialog.
     *
     * @param parent the parent
     * @param message the message
     * @param cancelButtonText the cancel button text
     * @param serverCode the server code
     * @param dialogTitle the dialog title
     * @param processButtonText the process button text
     * @param previewMsg the preview msg
     */
    public FunctionChangeNotifyDialog(Shell parent, String message, String cancelButtonText, String serverCode,
            String dialogTitle, String processButtonText, String previewMsg) {
        super(parent);
        setShellStyle(SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL);
        this.popupMessage = message;
        this.cancelButtonText = cancelButtonText;
        this.serverCode = serverCode;
        this.dialogTitle = dialogTitle;
        this.previewMsg = previewMsg;
        this.processButtonText = processButtonText;
    }

    private Image getSWTImage(final int imageID) {
        final Image[] image = new Image[1];
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                image[0] = Display.getDefault().getSystemImage(imageID);
            }
        });

        return image[0];
    }

    private Image getQuestionImage() {
        return getSWTImage(SWT.ICON_QUESTION);
    }

    private Control createMessageArea(Composite composite) {
        // create image
        Image image = getQuestionImage();
        Label imageLabel = new Label(composite, SWT.NULL);
        image.setBackground(imageLabel.getBackground());
        imageLabel.setImage(image);
        GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(imageLabel);

        // create message
        Label messageLabel = new Label(composite, SWT.WRAP);

        messageLabel.setText(MessageConfigLoader.getProperty(popupMessage));
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
                .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
                .applyTo(messageLabel);

        return composite;
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite comp = new Composite(composite, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        comp.setLayout(layout);
        createMessageArea(comp);

        // Creating Label for Space
        new Label(comp, SWT.NONE);
        new Label(comp, SWT.NONE);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 2;

        Label label = new Label(comp, SWT.NONE);
        label.setText(MessageConfigLoader.getProperty(this.previewMsg));
        label.setLayoutData(data);

        SourceViewer viewer = createViewer(comp);
        viewer.setEditable(false);
        Cursor arrowCursor = viewer.getTextWidget().getDisplay().getSystemCursor(SWT.CURSOR_ARROW);
        viewer.getTextWidget().setCursor(arrowCursor);
        viewer.getTextWidget().addKeyListener(new SourceEditorKeyListener(viewer, true));
        viewer.getDocument().set(serverCode);

        return comp;
    }

    /**
     * Configure shell.
     *
     * @param newShell the new shell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(MessageConfigLoader.getProperty(this.dialogTitle));
        newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_FUNCTION_FOLDER, this.getClass()));
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, MessageConfigLoader.getProperty(this.processButtonText), true);
        createButton(parent, IDialogConstants.PROCEED_ID, MessageConfigLoader.getProperty(cancelButtonText), false);
    }

    private SourceViewer createViewer(Composite parent) {
        SourceViewer viewer = new SourceViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        StyledText styledText = viewer.getTextWidget();
        GridData gdStyledText = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdStyledText.widthHint = convertWidthInCharsToPixels(80);
        gdStyledText.heightHint = convertHeightInCharsToPixels(20);
        gdStyledText.horizontalSpan = 2;
        styledText.setLayoutData(gdStyledText);
        viewer.configure(new SQLSourceViewerConfig(getSyntax()));
        IDocument document = new Document();
        viewer.setDocument(document);
        setDecoration(viewer);
        SQLDocumentPartitioner.connectDocument(document, 0);
        return viewer;
    }

    @SuppressWarnings("restriction")
    private static void setDecoration(SourceViewer viewer) {
        ISharedTextColors sharedColors = EditorsPlugin.getDefault().getSharedTextColors();
        SQLSourceViewerDecorationSupport sourceViewerDecorationSupport = new SQLSourceViewerDecorationSupport(viewer,
                null, null, sharedColors);
        sourceViewerDecorationSupport.installDecorations();
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        setReturnCode(buttonId);
        close();
    }

    /**
     * Close.
     *
     * @return true, if successful
     */
    @Override
    public boolean close() {
        syntax = null;
        return super.close();

    }

    /**
     * Gets the syntax.
     *
     * @return the syntax
     */
    public SQLSyntax getSyntax() {
        return syntax;
    }

    /**
     * Sets the syntax.
     *
     * @param syntax the new syntax
     */
    public void setSyntax(SQLSyntax syntax) {
        this.syntax = syntax;
    }

}
