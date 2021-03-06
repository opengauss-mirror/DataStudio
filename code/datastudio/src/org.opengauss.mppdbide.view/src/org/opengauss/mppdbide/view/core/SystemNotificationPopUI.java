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

package org.opengauss.mppdbide.view.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class SystemNotificationPopUI.
 *
 * @since 3.0.0
 */
public class SystemNotificationPopUI extends AbstractNotificationPopup {
    private String successLoginMsg;
    private List<String> failureLoginMsg;
    private String dbName;
    private String conName;

    private Label label;
    private boolean isLastLoginSuccess;
    private static final String ELLIPSES = "...";
    private static final int MAXLENGTH = 30;

    /**
     * Instantiates a new system notification pop UI.
     *
     * @param display the display
     * @param isLastLoginSuccess the is last login success
     */
    public SystemNotificationPopUI(Display display, boolean isLastLoginSuccess) {
        super(display);
        successLoginMsg = "";
        failureLoginMsg = new ArrayList<String>(MPPDBIDEConstants.RECORD_ARRAY_SIZE);
        this.isLastLoginSuccess = isLastLoginSuccess;
    }

    /**
     * Creates the content area.
     *
     * @param parent the parent
     */
    @Override
    protected void createContentArea(Composite parent) {

        Composite body = new Composite(parent, SWT.FILL);

        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        GridLayout bodyLayout = new GridLayout(1, true);
        bodyLayout.horizontalSpacing = 8;

        bodyLayout.marginHeight = 10;
        bodyLayout.verticalSpacing = 8;

        body.setLayout(bodyLayout);
        body.setBackgroundMode(SWT.INHERIT_FORCE);

        Display display = Display.getDefault();

        Color black = display.getSystemColor(SWT.COLOR_BLACK);
        label = new Label(body, SWT.BORDER_SOLID);

        FontData fontData = label.getFont().getFontData()[0];

        fontData.setStyle(SWT.BOLD);
        Font font = new Font(Display.getCurrent(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
        label.setFont(font);
        label.setForeground(black);
        label.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_NAME_NOTIFICATION,
                getTruncatedName(dbName)));

        label = new Label(body, SWT.BORDER_SOLID);
        label.setFont(font);
        label.setText(MessageConfigLoader.getProperty(IMessagesConstants.LASTLOGIN_CONNECTION_NAME,
                getTruncatedName(conName)));

        label = new Label(body, SWT.BORDER_SOLID);
        label.setFont(font);

        if (isLastLoginSuccess) {
            label.setText(MessageConfigLoader.getProperty(IMessagesConstants.LAST_SUCCESSFULL_LOGIN_MESSAGE));
            label = new Label(body, SWT.FILL);

            label.setText(this.successLoginMsg);

        } else {
            label.setText(MessageConfigLoader.getProperty(IMessagesConstants.LAST_LOGIN_UNAVAILABE));

        }

        label = new Label(body, SWT.FILL);
        if (!this.failureLoginMsg.isEmpty()) {
            label = new Label(body, SWT.BORDER_SOLID);
            label.setFont(font);

            if (isLastLoginSuccess) {
                label.setText(MessageConfigLoader.getProperty(IMessagesConstants.LAST_FAILURE_LOGIN_MESSAGE));

            } else {
                label.setText(MessageConfigLoader.getProperty(IMessagesConstants.LAST_LOGIN_UNAVAILABE));
            }
            Text text = new Text(body, SWT.MULTI);

            for (int cnt = 0; cnt < failureLoginMsg.size(); cnt++) {
                text.append(failureLoginMsg.get(cnt) + MPPDBIDEConstants.NEW_LINE_SIGN);
            }

        }
    }

    /**
     * Sets the database name.
     *
     * @param databaseName the new database name
     */
    public void setDatabaseName(String databaseName) {
        this.dbName = databaseName;
    }

    /**
     * Sets the con name.
     *
     * @param conName the new con name
     */
    public void setConName(String conName) {
        this.conName = conName;
    }

    /**
     * Gets the truncated name.
     *
     * @param name the name
     * @return the truncated name
     */
    public String getTruncatedName(String name) {
        if (name.length() > MAXLENGTH) {
            String truncatedName = name.substring(0, 30) + ELLIPSES;
            return truncatedName;
        }

        return name;
    }

    /**
     * Gets the popup shell title.
     *
     * @return the popup shell title
     */
    @Override
    protected String getPopupShellTitle() {

        return MessageConfigLoader.getProperty(IMessagesConstants.DATA_STUDIO_NOTIFIFICATIONS);
    }

    /**
     * Sets the success login info.
     *
     * @param msg the new success login info
     */
    public void setSuccessLoginInfo(String msg) {
        this.successLoginMsg = msg;
    }

    /**
     * Sets the failure login info.
     *
     * @param failureLoginTxt the new failure login info
     */
    public void setFailureLoginInfo(List<String> failureLoginTxt) {
        this.failureLoginMsg = failureLoginTxt;
    }
}
