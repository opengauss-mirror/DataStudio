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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.bl.search.SearchObjectEnum;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class DatabaseListControl.
 *
 * @since 3.0.0
 */
public class DatabaseListControl extends Observable {
    private Combo connectionCombo;
    private HashMap<Integer, ConnectionProfileId> connectionMap;
    private ConnectionProfileId lastSelected;
    private final Object LOCK = new Object();

    /**
     * Instantiates a new database list control.
     */
    public DatabaseListControl() {
        connectionMap = new HashMap<Integer, ConnectionProfileId>(4);
    }

    /**
     * Creates the part control.
     *
     * @param parent the parent
     */
    @PostConstruct
    public void createPartControl(Composite parent) {
        Composite combocomposite = new Composite(parent, SWT.None);
        connectionCombo = new Combo(combocomposite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.FILL);
        connectionCombo.add(MessageConfigLoader.getProperty(IMessagesConstants.SQL_TERMINAL_CONNS));
        connectionCombo.setSize(connectionCombo.computeSize(150, 15));
        connectionCombo.setTextLimit(25);
        connectionCombo.select(0);
        connectionCombo.setBounds(0, 0, 180, 15);
        connectionCombo.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        connectionCombo.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIdx = connectionCombo.getSelectionIndex();
                lastSelected = connectionMap.get(selectionIdx);
                notifyDefaultSchemaWidget();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do
            }
        });
        Observer ob = UIElement.getInstance().getObjectBrowserModel();
        if (null != ob) {
            addObserver(ob);
        }

    }

    private void notifyDefaultSchemaWidget() {
        setChanged();
        notifyObservers(SearchObjectEnum.DATABSEITEM_SELECT);
    }

    /**
     * Gets the selected connection.
     *
     * @return the selected connection
     */
    public Database getSelectedConnection() {
        if (connectionMap.size() > 0 && connectionCombo != null) {
            ConnectionProfileId profile = connectionMap.get(connectionCombo.getSelectionIndex());
            if (null != profile) {
                return profile.getDatabase();
            }
        }
        return null;
    }

    /**
     * Refresh connection combo items.
     */
    public void refreshConnectionComboItems() {
        if (connectionCombo == null) {
            return;
        }

        String currentSelectedItem = getCurrentSelectedItem();
        clearOnNewRefresh();
        Iterator<Server> servers = DBConnProfCache.getInstance().getServers().iterator();
        int selectIdx = updateDatabaseInfo(currentSelectedItem, servers);

        if (-1 < selectIdx) {
            connectionCombo.select(selectIdx);
            notifyDefaultSchemaWidget();
        }
        redrawConnectionCombo();
        addDefaultConnectionComboWhenNoConectionItems();
    }

    private int updateDatabaseInfo(String currentSelectedItem, Iterator<Server> servers) {
        int idx = 0;
        int selectIdx = -1;
        boolean isLastSelected = isLastSelected();
        boolean serverHasNext = servers.hasNext();
        boolean dbHasNext = false;
        Server server = null;
        Iterator<Database> dbs = null;
        Database db = null;
        while (serverHasNext) {
            server = servers.next();
            dbs = server.getAllDatabases().iterator();
            dbHasNext = dbs.hasNext();
            while (dbHasNext) {
                db = dbs.next();
                if (db.isConnected()) {
                    connectionCombo.add(db.getName() + '@' + server.getName());
                    connectionMap.put(idx, db.getProfileId());
                    // This code is to notify Search button on Object browser
                    // toolbar for enabling/disabling on database
                    // connect/disconnect
                    notifyOnDatabaseUpdate();

                    if (currentSelectedItem != null
                            && currentSelectedItem.equals(db.getName() + '@' + server.getName())) {
                        selectIdx = idx;
                        isLastSelected = true;
                        lastSelected = db.getProfileId();
                    } else if (!isLastSelected) {
                        lastSelected = db.getProfileId();
                        isLastSelected = true;
                        selectIdx = idx;
                    } else if (-1 == selectIdx && this.lastSelected.isEquals(db.getProfileId())) {
                        selectIdx = idx;
                    }

                    idx++;
                }
                dbHasNext = dbs.hasNext();
            }
            serverHasNext = servers.hasNext();
        }
        return selectIdx;
    }

    private void notifyOnDatabaseUpdate() {
        setChanged();
        notifyObservers(SearchObjectEnum.DATABASELIST_UPDATE);
    }

    private void clearOnNewRefresh() {
        connectionCombo.removeAll();
        connectionMap.clear();

        notifyOnDatabaseUpdate();

    }

    private void addDefaultConnectionComboWhenNoConectionItems() {
        if (connectionCombo.getItemCount() <= 0) {
            connectionCombo.add(MessageConfigLoader.getProperty(IMessagesConstants.SQL_TERMINAL_CONNS));
            connectionCombo.select(0);
            setChanged();
            notifyObservers(SearchObjectEnum.NO_DB_SELECTED);
        }
    }

    private void redrawConnectionCombo() {
        if (connectionCombo.getParent() != null) {
            connectionCombo.getParent().redraw();
        }
    }

    private boolean isLastSelected() {
        boolean isLastSelected = false;
        if (null != lastSelected && null != lastSelected.getDatabase() && lastSelected.getDatabase().isConnected()) {
            isLastSelected = true;
        }
        return isLastSelected;
    }

    private String getCurrentSelectedItem() {
        int currentSelectedIdx = connectionCombo.getSelectionIndex();
        String currentSelectedItem = null;
        if (currentSelectedIdx > -1) {
            currentSelectedItem = connectionCombo.getItem(currentSelectedIdx);
        }
        return currentSelectedItem;
    }

    /**
     * Sets the selected database.
     *
     * @param db the new selected database
     */
    public void setSelectedDatabase(Database db) {
        if (connectionCombo == null) {
            return;
        }
        for (Entry<Integer, ConnectionProfileId> entry : connectionMap.entrySet()) {
            Database database = entry.getValue().getDatabase();
            if (entry.getValue().getServerId() == db.getServer().getId() && null != database
                    && database.getName().equals(db.getName())) {
                connectionCombo.select(entry.getKey());
                notifyDefaultSchemaWidget();

            }
        }
    }

    /**
     * Gets the connected db count.
     *
     * @return the connected db count
     */
    public int getConnectedDbCount() {
        return connectionMap.size();

    }

    /**
     * Adds the observer.
     *
     * @param on the on
     */
    /*
     * while Overriding the following method in subclass, method should be made
     * thread-safe similar to super class implementation since adding observer
     * needs to be synchronized in order to not cause an exception
     */
    @Override
    public void addObserver(Observer on) {
        synchronized (LOCK) {
            super.addObserver(on);
        }
    }

    /**
     * Delete observer.
     *
     * @param arg0 the arg 0
     */
    /*
     * while Overriding the following method in subclass, method should be made
     * thread-safe similar to super class implementation since observer
     * destruction needs to be synchronized in order to not cause an exception
     */
    @Override
    public void deleteObserver(Observer arg0) {
        synchronized (LOCK) {
            super.deleteObserver(arg0);
        }

    }

}
