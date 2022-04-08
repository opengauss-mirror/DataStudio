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

package org.opengauss.mppdbide.bl.search;

import java.util.List;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchDatabase.
 * 
 */

public class SearchDatabase extends Database {

    private OLAPObjectGroup<SearchNamespace> searchNs;
    private Database db;

    /**
     * Instantiates a new search database.
     *
     * @param server the server
     * @param oid the oid
     * @param selectedDb the selected db
     */
    public SearchDatabase(Server server, long oid, Database selectedDb) {
        super(server, oid, selectedDb.getName());
        this.db = selectedDb;
        searchNs = new OLAPObjectGroup<SearchNamespace>(OBJECTTYPE.NAMESPACE_GROUP, this);

    }

    /**
     * Gets the db.
     *
     * @return the db
     */
    public Database getDb() {
        return this.db;
    }

    /**
     * Adds the search namespaces.
     *
     * @param searchNameSpace the search name space
     */
    public void addSearchNamespaces(SearchNamespace searchNameSpace) {
        this.searchNs.addToGroup(searchNameSpace);

    }

    /**
     * Gets the all search name spaces.
     *
     * @return the all search name spaces
     */
    public List<SearchNamespace> getAllSearchNameSpaces() {

        return this.searchNs.getSortedServerObjectList();
    }

    @Override
    public boolean equals(Object obj) {
        return db.equals(obj);
    }

    @Override
    public int hashCode() {

        return db.hashCode();
    }
}
