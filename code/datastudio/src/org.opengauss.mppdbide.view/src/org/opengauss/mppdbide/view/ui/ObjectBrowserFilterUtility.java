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

import java.util.HashSet;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: ObjectBrowserFilterUtility
 *
 * @since 3.0.0
 */
public final class ObjectBrowserFilterUtility extends Observable {
    private static final Object LOCK = new Object();
    private static volatile ObjectBrowserFilterUtility instance;
    Set<String> listOfFileteredServer;

    private ObjectBrowserFilterUtility() {
        listOfFileteredServer = new HashSet<>();
    }

    /**
     * instance of ObjectBrowserFilterUtility
     * 
     * @return ObjectBrowserFilterUtility object browser utility
     */
    public static ObjectBrowserFilterUtility getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ObjectBrowserFilterUtility();
                }
            }
        }
        return instance;
    }

    /**
     * addFilteredServer add filtered server
     * 
     * @param serverName server name
     */
    public void addFilteredServer(String serverName) {
        listOfFileteredServer.add(serverName);
        setChanged();
        notifyObservers();
    }

    /**
     * removeRefreshedServerFromList remove
     * 
     * @param server server
     */
    public void removeRefreshedServerFromList(String server) {
        listOfFileteredServer.remove(server);
        setChanged();
        notifyObservers();
    }

    /**
     * getErrorTooltip tooltip
     * 
     * @return string string
     */
    public String getErrorTooltip() {
        String tooltipMessage = "";
        String listOfServer = listOfFileteredServer.toString();
        if (!listOfFileteredServer.isEmpty()) {
            listOfServer = listOfServer.substring(1, listOfServer.length() - 1);
            tooltipMessage = String.format(Locale.ENGLISH,
                    MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER_FILTER_TIMEOUT_TOOLTIP_MESSAGE),
                    listOfServer);
        }
        return tooltipMessage;
    }

    /**
     * isAllServerRefreshed method
     * 
     * @return boolean flag
     */
    public boolean isAllServerRefreshed() {
        if (listOfFileteredServer.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * clearServerList list
     */
    public void clearServerList() {
        listOfFileteredServer.clear();
        setChanged();
        notifyObservers();
    }

    @Override
    public void addObserver(Observer on) {
        synchronized (LOCK) {
            super.addObserver(on);
        }
    }
    
}
