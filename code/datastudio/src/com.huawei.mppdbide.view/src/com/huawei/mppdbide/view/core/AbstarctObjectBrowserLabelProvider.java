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
 * @since 3.0.0
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
