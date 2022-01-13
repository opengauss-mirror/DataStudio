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

package com.huawei.mppdbide.view.core;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.dto.version.UIVersionDO;
import com.huawei.mppdbide.view.init.LifeCycleManager;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class AboutMPPDBIDEDialog.
 *
 * @since 3.0.0
 */
public class AboutMPPDBIDEDialog extends Dialog {
    private static final String ALIGN_SPACES = "      ";

    /**
     * Instantiates a new about MPPDBIDE dialog.
     *
     * @param parentShell the parent shell
     */
    public AboutMPPDBIDEDialog(Shell parentShell) {
        super(parentShell);
        setBlockOnOpen(true);
    }

    /**
     * Configure shell.
     *
     * @param shell the shell
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setImage(IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, getClass()));
        shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.ABOUT_DATA_STUDIO_MSG));
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button button = createButton(parent, IDialogConstants.OK_ID,
                MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CLOSE), true);
        button.setFocus();
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        int commonBorderStyle = SWT.NONE;
        int intend = 15;

        addBannerLabel(parent, commonBorderStyle);

        Composite container = getComposite(parent, commonBorderStyle);
        // Print empty space
        Color fontColor = addEmptySpace(commonBorderStyle, intend, container);
        // Print DS version
        addDsVersion(commonBorderStyle, intend, container, fontColor);

        // Print Java version
        addJavaVersion(commonBorderStyle, intend, container, fontColor);
        // Print Java Home Information
        addJavaHomeInformation(commonBorderStyle, intend, container, fontColor);

        // Print Build Time
        addBuildTime(commonBorderStyle, intend, container, fontColor);
        return container;
    }

    /**
     * Adds the build time.
     *
     * @param commonBorderStyle the common border style
     * @param intend the intend
     * @param container the container
     * @param fontColor the font color
     */
    private void addBuildTime(int commonBorderStyle, int intend, Composite container, Color fontColor) {
        GridData gridData;
        Label buildTimeLabel = new Label(container, commonBorderStyle);
        buildTimeLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.BUILD_TIME));
        buildTimeLabel.setForeground(fontColor);
        gridData = new GridData();
        gridData.horizontalIndent = intend;
        buildTimeLabel.setLayoutData(gridData);

        String buildTime = getBuildTime();
        Label buildTimeValue = new Label(container, commonBorderStyle);
        buildTimeValue.setText(ALIGN_SPACES
                + (null == buildTime ? MessageConfigLoader.getProperty(IMessagesConstants.BUILD_TIME_UNKNOWN)
                        : buildTime));
    }

    /**
     * Adds the java home information.
     *
     * @param commonBorderStyle the common border style
     * @param intend the intend
     * @param container the container
     * @param fontColor the font color
     */
    private void addJavaHomeInformation(int commonBorderStyle, int intend, Composite container, Color fontColor) {
        GridData gridData;
        Label javaHomeLabel = new Label(container, commonBorderStyle);
        javaHomeLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.JAVA_HOME_MSG));
        javaHomeLabel.setForeground(fontColor);
        gridData = new GridData();
        gridData.horizontalIndent = intend;
        javaHomeLabel.setLayoutData(gridData);

        String javaHomeStr = EnvirnmentVariableValidator.validateAndGetJavaPath();
        Label javaHomeValue = new Label(container, commonBorderStyle);
        if (null == javaHomeStr) {
            javaHomeValue.setText(ALIGN_SPACES + MessageConfigLoader.getProperty(IMessagesConstants.JAVA_HOME_NOT_SET));
        } else {
            javaHomeValue.setText(ALIGN_SPACES + javaHomeStr);
        }
        javaHomeValue.setForeground(fontColor);
    }

    /**
     * Adds the java version.
     *
     * @param commonBorderStyle the common border style
     * @param intend the intend
     * @param container the container
     * @param fontColor the font color
     */
    private void addJavaVersion(int commonBorderStyle, int intend, Composite container, Color fontColor) {
        GridData gridData;
        Label javaVersionLabel = new Label(container, commonBorderStyle);
        javaVersionLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.JAVA_VERSION_NO_MSG));
        javaVersionLabel.setForeground(fontColor);
        gridData = new GridData();
        gridData.horizontalIndent = intend;
        javaVersionLabel.setLayoutData(gridData);

        Label javaVersionValue = new Label(container, commonBorderStyle);
        javaVersionValue.setText(ALIGN_SPACES + EnvirnmentVariableValidator.validateJavaVersion());
        javaVersionValue.setForeground(fontColor);
    }

    /**
     * Adds the ds version.
     *
     * @param commonBorderStyle the common border style
     * @param intend the intend
     * @param container the container
     * @param fontColor the font color
     */
    private void addDsVersion(int commonBorderStyle, int intend, Composite container, Color fontColor) {
        GridData gridData;
        Label versionLabel = new Label(container, commonBorderStyle);
        versionLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.VERSION_NO_MSG));
        versionLabel.setForeground(fontColor);
        gridData = new GridData();
        gridData.horizontalIndent = intend;
        versionLabel.setLayoutData(gridData);

        Label versionValue = new Label(container, commonBorderStyle);
        versionValue.setText(ALIGN_SPACES + UIVersionDO.getUIVersion());
        versionValue.setForeground(fontColor);
    }

    /**
     * Adds the empty space.
     *
     * @param commonBorderStyle the common border style
     * @param intend the intend
     * @param container the container
     * @return the color
     */
    private Color addEmptySpace(int commonBorderStyle, int intend, Composite container) {
        GridData gridData;
        Label emptyLabel = new Label(container, commonBorderStyle);
        gridData = new GridData();
        gridData.horizontalIndent = intend;
        emptyLabel.setLayoutData(gridData);
        Label emptyLabel1 = new Label(container, commonBorderStyle);
        emptyLabel1.setText("");
        Color fontColor = new Color(Display.getDefault(), 17, 17, 17);
        return fontColor;
    }

    /**
     * Gets the composite.
     *
     * @param parent the parent
     * @param commonBorderStyle the common border style
     * @return the composite
     */
    private Composite getComposite(Composite parent, int commonBorderStyle) {
        Composite container = new Composite(parent, commonBorderStyle);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.marginRight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginTop = 0;
        container.setLayout(gridLayout);
        return container;
    }

    /**
     * Adds the banner label.
     *
     * @param parent the parent
     * @param commonBorderStyle the common border style
     */
    private void addBannerLabel(Composite parent, int commonBorderStyle) {
        Label banner = new Label(parent, commonBorderStyle);
        banner.setImage(IconUtility.getIconImage(IiconPath.ICO_ABOUT_DS, this.getClass()));
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.verticalSpan = 4;
        gridData.horizontalSpan = 2;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        banner.setLayoutData(gridData);
    }

    /**
     * The listener interface for receiving linkSelection events. The class that
     * is interested in processing a linkSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addLinkSelectionListener<code>
     * method. When the linkSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * LinkSelectionEvent
     */
    private static class LinkSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent exception) {
            Program.launch(MessageConfigLoader.getProperty(IMessagesConstants.WEB_HUAWEI_LINK));
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent exception) {

        }

    }

    /**
     * Gets the builds the time.
     *
     * @return the builds the time
     */
    private String getBuildTime() {
        URL path = getVersionFilePath();
        if (null == path) {
            return null;
        }

        Properties prop = new Properties();
        try {
            prop.load(path.openStream());
            Object buildTime = prop.get("data.studio.buildTime");
            return (null == buildTime) ? null : buildTime.toString();
        } catch (IOException exception) {
            return null;
        }
    }

    /**
     * Gets the version file path.
     *
     * @return the version file path
     */
    private URL getVersionFilePath() {
        URL propertiesURL = null;
        ClassLoader classLoader = LifeCycleManager.class.getClassLoader();
        if (null != classLoader) {
            propertiesURL = classLoader.getResource("version.txt");
            return propertiesURL;
        }
        return propertiesURL;
    }
}
