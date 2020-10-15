/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.synonym.olap;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.bl.serverdatacache.SynonymUtil;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: Class
 * 
 * Description: The Class SynonymWrapper.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author c00550043
 * @version
 * @since Mar 12, 2020
 */
public class SynonymWrapper {
    private SynonymInfo synonymInfo;
    private Database database;
    private Namespace namespace;
    private SynonymObjectGroup synonymObjectGroup;
    private SynonymMetaData synonymMetaData;

    /**
     * Instantiates a new synonym wrapper.
     *
     * @param SynonymGroup the synonym group
     * @param info the info
     */
    public SynonymWrapper(SynonymObjectGroup objectGroup, SynonymInfo info) {
        this.synonymInfo = info;
        this.synonymObjectGroup = objectGroup;
        this.namespace = (Namespace) objectGroup.getParent();
        this.database = objectGroup.getDatabase();
        this.synonymMetaData = new SynonymMetaData(info.getSynonymName(), this.namespace);
    }

    /**
     * Gets the metadata.
     *
     * @return the metadata
     */
    public SynonymMetaData getMetadata() {
        return synonymMetaData;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Gets the synonym group.
     *
     * @return the synonym group
     */
    public SynonymObjectGroup getSynonymObjectGroup() {
        return synonymObjectGroup;
    }

    /**
     * Execute compose query.
     *
     * @param connection the connection
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void executeComposeQuery(DBConnection connection) throws MPPDBIDEException {
        final String generateCreateSynonymSql = synonymInfo.generateCreateSynonymSql();
        connection.execNonSelect(generateCreateSynonymSql);

    }

    /**
     * Refresh.
     *
     * @param connection the connection
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void refresh(DBConnection connection) throws MPPDBIDEException {
        SynonymUtil.fetchSynonyms(namespace, connection);
    }

}
