/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.search.SearchObjectEnum;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DefaultSchemaControl.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DefaultSchemaControl extends Observable implements Observer {
    private Combo schemaCombo;
    private HashMap<Integer, Long> schemaMap;
    private Long lastSelected;
    private int defaultSchemaIndex;
    private String defaultSchema;
    private final Object LOCK = new Object();

    /**
     * Instantiates a new default schema control.
     */
    public DefaultSchemaControl() {
        schemaMap = new HashMap<Integer, Long>(4);
    }

    /**
     * Creates the part control.
     *
     * @param parent the parent
     */
    @PostConstruct
    public void createPartControl(Composite parent) {
        Composite combocomposite = new Composite(parent, SWT.None);
        schemaCombo = new Combo(combocomposite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.FILL);
        schemaCombo.add(MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_INITIAL_MSG));
        schemaCombo.setSize(schemaCombo.computeSize(150, 15));
        schemaCombo.setTextLimit(25);
        schemaCombo.select(0);
        schemaCombo.setBounds(0, 0, 180, 15);
        schemaCombo.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        schemaCombo.addSelectionListener(createAndReturnDefaultSchemaSelectionListener());
        addObservers();

    }

    private SelectionListener createAndReturnDefaultSchemaSelectionListener() {
        return new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = schemaCombo.getSelectionIndex();
                if (-1 == selectionIndex || !schemaMap.containsKey(selectionIndex)) {
                    /* Invalid schema selected */
                    return;
                }

                long schemaId = schemaMap.get(selectionIndex);

                if (null != lastSelected && lastSelected == schemaId) {
                    /* Selected value is same as last value. Hence do nothing */
                    return;
                } else {
                    lastSelected = schemaId;
                }

                Namespace schema = getSchemaFromId(schemaId);
                if (null != schema) {
                    SetDefaultSchemaWorker setDefaultSchemaWorker = new SetDefaultSchemaWorker(schema.getDisplayName());
                    setDefaultSchemaWorker.schedule();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do
            }
        };
    }

    private void addObservers() {
        /* Add this class as observer for DBList Combo widget */
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (databaseListControl != null) {
            databaseListControl.addObserver(this);
        }
    }

    private Database getSelectedDatabaseInDBList() {
        Database db = null;
        if (UIElement.getInstance().getDatabaseListControl() != null) {
            db = UIElement.getInstance().getDatabaseListControl().getSelectedConnection();
        }
        return db;
    }

    private Namespace getSchemaFromId(Long schemaId) {
        Database db = getSelectedDatabaseInDBList();
        Namespace getNSById = null;
        try {
            if (db != null) {
                getNSById = db.getNameSpaceById(schemaId);
            }
            return getNSById;
        } catch (DatabaseOperationException e1) {
            MPPDBIDELoggerUtility.error("Failed to get namespace from db", e1);
            return null;
        }
    }

    /**
     * Refresh default schema combo items.
     */
    public void refreshDefaultSchemaComboItems() {
        Database selectedDb = getSelectedDatabaseInDBList();
        if (null == schemaCombo || null == selectedDb) {
            return;
        }
        schemaCombo.setEnabled(true);
        boolean isSuccess = getDefaultSchemaName();
        if (!isSuccess) {
            clearSelection();
            return;
        }
        schemaCombo.removeAll();
        schemaMap.clear();
        Iterator<Namespace> namespaces = null;
        if ( getSelectedDatabaseInDBList() != null) {
            namespaces = getSelectedDatabaseInDBList().getAllNameSpaces().iterator();
        }
        int idx = 0;
        boolean nsHasNext = false;
        if (namespaces != null) {
            nsHasNext = namespaces.hasNext();
            Namespace ns = null;
            while (nsHasNext) {
                ns = namespaces.next();

                if (null != this.defaultSchema && this.defaultSchema.equals(ns.getDisplayName())) {
                    this.defaultSchemaIndex = idx;
                    this.lastSelected = ns.getOid();
                }

                String composedSchemaName = null;
                if (ns instanceof UserNamespace) {
                    composedSchemaName = "U" + " | " + ns.getName();
                } else {
                    composedSchemaName = "S" + " | " + ns.getName();
                }
                schemaCombo.add(composedSchemaName);

                schemaMap.put(idx, ns.getOid());

                idx++;
                nsHasNext = namespaces.hasNext();
            }
        }
        redrawConnectionCombo();
        setDefaultSchemaInCombo();
        addDefaultConnectionComboWhenNoConectionItems();
    }

    private boolean getDefaultSchemaName() {
        /* Clear old values */
        this.defaultSchema = null;
        this.defaultSchemaIndex = -1;
        this.lastSelected = null;

        ResultSet rsForQuerySearchPath = null;
        DBConnection sqlTerminalConn = null;
        boolean status = false;
        Database selectedDatabaseInDBList = getSelectedDatabaseInDBList();
        
        if (selectedDatabaseInDBList != null) {
            sqlTerminalConn = selectedDatabaseInDBList.getConnectionManager().getSqlTerminalConn();
        }
        if (sqlTerminalConn != null) {
            try {
                rsForQuerySearchPath = sqlTerminalConn.execSelectAndReturnRs("SHOW SEARCH_PATH ;");
                if (rsForQuerySearchPath != null) {
                    status = getDefaultSchemaNameStatus(sqlTerminalConn, rsForQuerySearchPath);
                }   
            } catch (DatabaseCriticalException | DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("Failed to find ResultSet for Query Search path", exception);
            }
        }
        
        return status;
    }

    private boolean getDefaultSchemaNameStatus(DBConnection sqlTerminalConn, ResultSet rsForQuerySearchPath) {
        ResultSet rsForSelectUser = null;
        try {
            if (null != rsForQuerySearchPath && rsForQuerySearchPath.next()) {
                /* Ignore 1st value as it will always be "$user" */
                String[] schemas = rsForQuerySearchPath.getString(1).split(",");
                if (schemas.length > 0) {
                    this.defaultSchema = schemas[0].trim();
                }

                /* Resolve parameter value Eg: $user */
                if ("\"$user\"".equals(this.defaultSchema)) {
                    rsForSelectUser = sqlTerminalConn.execSelectAndReturnRs("SELECT user;");
                    if (null != rsForSelectUser && rsForSelectUser.next()) {
                        String resolvedVarName = rsForSelectUser.getString(1);
                        this.defaultSchema = resolvedVarName.trim();
                    }
                }
                return true;
            }
            return false;
        } catch (DatabaseOperationException | DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_MSG),
                    exception);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_MSG)
                            + MPPDBIDEConstants.LINE_SEPARATOR + exception.getServerMessage());
            return false;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_MSG),
                    exception);
            String extractedMsg = sqlTerminalConn.extractErrorCodeAndErrorMsgFromServerError(exception);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_MSG)
                            + MPPDBIDEConstants.LINE_SEPARATOR + extractedMsg);
            return false;
        } finally {
            closeResultSet(rsForQuerySearchPath);
            closeResultSet(rsForSelectUser);
        }
    }

    private void closeResultSet(ResultSet rs) {
        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Failed to close ResultSet, Nothing to be done", exception);
            }
        }
    }

    private void setDefaultSchemaInCombo() {
        /* To be called after getUserSchemaName() */
        if (-1 != this.defaultSchemaIndex) {
            schemaCombo.select(this.defaultSchemaIndex);
        } else {
            clearSelection();
        }
    }

    /**
     * Clear selection.
     */
    public void clearSelection() {
        schemaCombo.deselectAll();
        lastSelected = null;
    }

    private void addDefaultConnectionComboWhenNoConectionItems() {
        if (schemaCombo.getItemCount() <= 0) {
            schemaCombo.add(MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_INITIAL_MSG));
            schemaCombo.select(0);
        }
    }

    private void addDefaultConnectionTextToCombo() {
        schemaCombo.removeAll();
        schemaMap.clear();
        schemaCombo.add(MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_INITIAL_MSG));
        schemaCombo.select(0);
    }

    private void redrawConnectionCombo() {
        if (schemaCombo.getParent() != null) {
            schemaCombo.getParent().redraw();
        }
    }

    /**
     * Adds the observer.
     *
     * @param on the on
     */
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
    @Override
    public void deleteObserver(Observer arg0) {
        synchronized (LOCK) {
            super.deleteObserver(arg0);
        }

    }

    /**
     * Update.
     *
     * @param arg0 the arg 0
     * @param arg1 the arg 1
     */
    @Override
    public void update(Observable arg0, Object arg1) {
        SearchObjectEnum searchStatus = (SearchObjectEnum) arg1;
        switch (searchStatus) {
            case DATABSEITEM_SELECT: {
                refreshDefaultSchemaComboItems();
                break;
            }

            case NO_DB_SELECTED: {
                addDefaultConnectionTextToCombo();
                break;
            }
            default: {
                break;
            }
        }
    }
}
