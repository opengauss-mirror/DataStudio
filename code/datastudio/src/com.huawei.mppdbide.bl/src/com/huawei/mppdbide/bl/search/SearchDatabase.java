/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.search;

import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchDatabase.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
