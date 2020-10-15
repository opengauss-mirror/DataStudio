/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionManager;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: class
 * 
 * Description: The Class SequenceDataCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SequenceDataCore {

    private Namespace namespace;

    private SequenceMetadata sequenceMetadata;
    private DBConnection dbCon;

    /**
     * Instantiates a new sequence data core.
     *
     * @param ns the ns
     */
    public SequenceDataCore(Namespace ns) {
        this.namespace = ns;
        sequenceMetadata = new SequenceMetadata(ns);
        dbCon = null;

    }

    /**
     * Creates the connection.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void createConnection() throws MPPDBIDEException {
        ConnectionManager connectionManager = sequenceMetadata.getConnectionManager();
        if (dbCon == null && connectionManager != null) {
            dbCon = connectionManager.getFreeConnection();
        }
    }

    /**
     * Release connection.
     */
    public void releaseConnection() {
        if (dbCon != null) {
            sequenceMetadata.getDatabase().getConnectionManager().releaseAndDisconnection(dbCon);
            dbCon = null;
        }

    }

    /**
     * Gets the sequence metadata.
     *
     * @return the sequence metadata
     */
    public SequenceMetadata getSequenceMetadata() {
        return sequenceMetadata;
    }

    /**
     * Compose query.
     *
     * @return the string
     */
    public String composeQuery() {
        return sequenceMetadata.composeQuery(String.format(Locale.ENGLISH, "%s.%s",
                ServerObject.getQualifiedObjectName(sequenceMetadata.getNamespace().getName()),
                ServerObject.getQualifiedObjectName(sequenceMetadata.getSequenceName())));
    }

    /**
     * Execute create sequence.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void executeCreateSequence()
            throws DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException {
        String queryForSequence = composeQuery();
        dbCon.execNonSelect(queryForSequence);
        if (!(this.namespace.getServer().isServerInProgress()
                || this.namespace.getDatabase().isLoadingNamespaceInProgress())) {
            this.namespace.refreshSequences(dbCon);
        }

    }

    /**
     * Cancel query.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void cancelQuery() throws DatabaseCriticalException, DatabaseOperationException {
        dbCon.cancelQuery();

    }

}
