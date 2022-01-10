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

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserNamespace.
 * 
 */

public class UserNamespace extends Namespace {

    private boolean isDrop;

    /**
     * Instantiates a new user namespace.
     *
     * @param oid the oid
     * @param name the name
     * @param parentDb the parent db
     */
    public UserNamespace(long oid, String name, Database parentDb) {
        super(oid, name, parentDb);
    }

    /**
     * Checks if is drop.
     *
     * @return true, if is drop
     */
    public boolean isDrop() {
        return isDrop;
    }

    /**
     * Drop.
     *
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void drop(DBConnection conn) throws DatabaseOperationException, DatabaseCriticalException {
        isDrop = true;
        String qry = String.format(Locale.ENGLISH, "Drop SCHEMA %s;", this.getDisplayName());
        conn.execNonSelect(qry);
        NamespaceUtilsBase.refreshNamespace(this.getOid(), isDrop, db);
        isDrop = false;
    }

    /**
     * Rename.
     *
     * @param newName the new name
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void rename(String newName, DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        String qry = String.format(Locale.ENGLISH, "ALTER SCHEMA %s RENAME TO %s ;", this.getDisplayName(),
                ServerObject.getQualifiedObjectName(newName));
        conn.execNonSelect(qry);
        NamespaceUtilsBase.refreshNamespaceMetaData(this.getOid(), db);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}