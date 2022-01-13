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
 * @since 3.0.0
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
