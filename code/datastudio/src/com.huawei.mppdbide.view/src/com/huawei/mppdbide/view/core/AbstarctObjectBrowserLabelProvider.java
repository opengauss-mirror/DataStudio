/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstarctObjectBrowserLabelProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstarctObjectBrowserLabelProvider extends ColumnLabelProvider {

    /**
     * Gets the tool tip text helper.
     *
     * @param serv the serv
     * @return the tool tip text helper
     */
    protected String getToolTipTextHelper(Server serv) {
        String serverVersion = CustomStringUtility.parseServerVersion(serv.getServerVersion(true));
        String serverIp = serv.getServerIP2();
        String tooltipText = tooltipCheck(serverVersion, serverIp);
        return tooltipText != null ? tooltipText
                : MessageConfigLoader.getProperty(IMessagesConstants.SERVERIP_TOOLTIP, serverIp)
                        + MPPDBIDEConstants.LINE_SEPARATOR
                        + MessageConfigLoader.getProperty(IMessagesConstants.SERVERVERSION_TOOLTIP, serverVersion);
    }

    /**
     * Tooltip check.
     *
     * @param serverVersion the server version
     * @param serverIp the server ip
     * @return the string
     */
    private String tooltipCheck(String serverVersion, String serverIp) {
        if (serverVersion.isEmpty() && serverIp.isEmpty()) {
            return MessageConfigLoader.getProperty(IMessagesConstants.SERVERIP_TOOLTIP,
                    MessageConfigLoader.getProperty(IMessagesConstants.SERVERIP_TOOLTIP_FAIL))
                    + MPPDBIDEConstants.LINE_SEPARATOR
                    + MessageConfigLoader.getProperty(IMessagesConstants.SERVERVERSION_TOOLTIP,
                            MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_VERSION_NUMBER_FAILED_MSG));
        } else if (serverVersion.isEmpty()) {
            return MessageConfigLoader.getProperty(IMessagesConstants.SERVERIP_TOOLTIP, serverIp)
                    + MPPDBIDEConstants.LINE_SEPARATOR
                    + MessageConfigLoader.getProperty(IMessagesConstants.SERVERVERSION_TOOLTIP,
                            MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_VERSION_NUMBER_FAILED_MSG));
        } else if (serverIp.isEmpty()) {
            return MessageConfigLoader.getProperty(IMessagesConstants.SERVERIP_TOOLTIP,
                    MessageConfigLoader.getProperty(IMessagesConstants.SERVERIP_TOOLTIP_FAIL))
                    + MPPDBIDEConstants.LINE_SEPARATOR
                    + MessageConfigLoader.getProperty(IMessagesConstants.SERVERVERSION_TOOLTIP, serverVersion);
        }
        return null;
    }
}
