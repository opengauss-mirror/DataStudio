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

package org.opengauss.mppdbide.view.ui.visualexplainplan;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum VisualExplainPlanWindowDetailsEnum.
 *
 * @since 3.0.0
 */
public enum VisualExplainPlanWindowDetailsEnum {

    /**
     * The visual explain diagram window.
     */
    VISUAL_EXPLAIN_DIAGRAM_WINDOW(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_WINDOW_LBL), null,
            IiconPath.VIS_EXPLAIN_DETAILED_PLAN_TAB,
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_WINDOW_LBL), false),

    /**
     * The visual explain overall property window.
     */
    VISUAL_EXPLAIN_OVERALL_PROPERTY_WINDOW(
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_WINDOW_ALL_NODES_LBL), null,
            IiconPath.VIS_EXPLAIN_PROPERTIES_TAB,
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_WINDOW_ALL_NODES_LBL), false),

    /**
     * The visual explain text property window.
     */
    VISUAL_EXPLAIN_TEXT_PROPERTY_WINDOW(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_WINDOW_TEXT_LBL),
            null, IiconPath.VIS_EXPLAIN_GENERAL_DETAILS_TAB,
            MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_WINDOW_TEXT_LBL), false),

    /**
     * The visual explain node property window.
     */
    VISUAL_EXPLAIN_NODE_PROPERTY_WINDOW(null, null, IiconPath.VIS_EXPLAIN_PROPERTIES_NODE_TAB, null, true);

    private String windowTitle;
    private String uniqueID;
    private final String icon;
    private String shortTitle;
    private boolean isCloseable;

    private VisualExplainPlanWindowDetailsEnum(String title, String uId, String iconPath, String sTitle,
            Boolean isCloseable) {
        this.windowTitle = title;
        this.uniqueID = uId;
        this.icon = iconPath;
        this.shortTitle = sTitle;
        this.isCloseable = isCloseable;
    }

    /**
     * Gets the vis explain window title.
     *
     * @return the vis explain window title
     */
    public String getVisExplainWindowTitle() {
        return windowTitle;
    }

    /**
     * Sets the window title.
     *
     * @param windowTitle the new window title
     */
    protected void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    /**
     * Gets the unique ID.
     *
     * @return the unique ID
     */
    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * Sets the unique ID.
     *
     * @param uniqueID the new unique ID
     */
    protected void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    /**
     * Gets the icon.
     *
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Gets the short title.
     *
     * @return the short title
     */
    public String getShortTitle() {
        return shortTitle;
    }

    /**
     * Sets the short title.
     *
     * @param shortTitle the new short title
     */
    protected void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    /**
     * Checks if is closeable.
     *
     * @return true, if is closeable
     */
    public boolean isCloseable() {
        return isCloseable;
    }

}
