/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import java.util.HashSet;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: ObjectBrowserFilterUtility
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 05-Jun-2020]
 * @since 05-Jun-2020
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
