/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.connection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class ClientSSLKeyDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ClientSSLKeyDialog extends UserInputDialog {
    private String keyFileName;
    private Label keySSl;
    private Text clSSLKeyFilePathText;
    private Button sslKeyBrowseBtn;

    /**
     * Instantiates a new client SSL key dialog.
     *
     * @param parent the parent
     * @param serverObject the server object
     */
    public ClientSSLKeyDialog(Shell parent, Object serverObject) {
        super(parent, serverObject);
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite curComposite = (Composite) getBlankDialogArea(parent);
        curComposite.setLayout(new GridLayout(1, false));

        GridData grid2 = new GridData();
        grid2.grabExcessHorizontalSpace = true;
        grid2.horizontalAlignment = GridData.FILL;
        grid2.verticalAlignment = GridData.FILL;
        grid2.horizontalIndent = 5;
        grid2.verticalIndent = 0;
        grid2.minimumWidth = 400;

        curComposite.setLayoutData(grid2);
        GridLayout valueFieldLayout = new GridLayout(2, false);
        valueFieldLayout.horizontalSpacing = 0;
        valueFieldLayout.marginWidth = 0;
        valueFieldLayout.marginHeight = 5;
        valueFieldLayout.verticalSpacing = 2;

        keySSl = new Label(curComposite, SWT.NULL);
        keySSl.setText(MessageConfigLoader.getProperty(IMessagesConstants.ENTER_CLIENT_SSLPVT_KEYFILE));
        Composite clientKeyComposite = new Composite(curComposite, SWT.NULL);
        clientKeyComposite.setLayout(valueFieldLayout);
        clientKeyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        clSSLKeyFilePathText = new Text(clientKeyComposite, SWT.READ_ONLY | SWT.BORDER);
        clSSLKeyFilePathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        clSSLKeyFilePathText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        inputControl = clSSLKeyFilePathText;

        sslKeyBrowseBtn = new Button(clientKeyComposite, SWT.NONE);

        sslKeyBrowseBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_BROWSE));
        sslKeyBrowseBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
                dialog.setFilterNames(
                        new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_CLIENT_PVTKEY)});
                dialog.setFilterExtensions(
                        new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_CLIENT_PVTKEY)});

                String clientKeyPath = dialog.open();
                clSSLKeyFilePathText.setText(clientKeyPath != null ? clientKeyPath : "");
                if (!"".equals(clSSLKeyFilePathText.getText())) {
                    enableButtons();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });

        return curComposite;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
    }

    /**
     * Checks if is connect DB.
     *
     * @return true, if is connect DB
     */
    @Override
    protected boolean isConnectDB() {
        return true;
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException e) {
    }

    /**
     * On presetup failure UI action.
     *
     * @param exception the e
     */
    @Override
    public void onPresetupFailureUIAction(MPPDBIDEException exception) {
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    @Override
    protected String getWindowTitle() {
        return MessageConfigLoader.getProperty(IMessagesConstants.ENTRE_CLIENTSSLKEY);
    }

    /**
     * Gets the header.
     *
     * @return the header
     */
    @Override
    protected String getHeader() {
        return MessageConfigLoader.getProperty(IMessagesConstants.ENTER_CLIENT_SSLPVT_KEYFILE);
    }

    /**
     * Perform ok operation.
     */
    @Override
    protected void performOkOperation() {
        keyFileName = clSSLKeyFilePathText.getText();
        Path sslPath = Paths.get(keyFileName).toAbsolutePath().normalize();
        if (!Files.isReadable(sslPath)) {
            printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.INVALID_SSL_KEY), false);
        } else {
            close();
        }

    }

    /**
     * Gets the key file name.
     *
     * @return the key file name
     */
    public String getKeyFileName() {
        return this.keyFileName;
    }
}
