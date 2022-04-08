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

package org.opengauss.mppdbide.presentation.erd;

import java.sql.SQLException;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.erd.model.EREntity;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * The Class EREntityPresentation.
 *
 * @ClassName: EREntityPresentation
 * @Description: The Class EREntityPresentation.
 *
 * @since 3.0.0
 */
public class EREntityPresentation extends AbstractERPresentation<TableMetaData> {

    /** 
     * The entity model. 
     */
    private EREntity entityModel;

    /**
     * Instantiates a new ER entity presentation.
     *
     * @param serverObject the server object
     * @param dbcon the dbcon
     */
    public EREntityPresentation(TableMetaData serverObject, DBConnection dbcon) {
        super(serverObject, dbcon);
        this.entityModel = null;
    }

    /**
     * Inits the ER presentation.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    @Override
    public void initERPresentation() throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        if (!serverObject.isLoaded()) {
            serverObject.setLevel3Loaded(false);
            serverObject.getNamespace().fetchTableColumnMetaData(serverObject.getOid(),
                    serverObject.getNamespace().getTables(), dbcon);
        }
        EREntity entity = new EREntity(serverObject, true, dbcon);
        entity.initEREntity();
        entityModel = entity;
        addEntity(entityModel);
    }

    /**
     * Gets the entity.
     *
     * @return the entity
     */
    public EREntity getEntity() {
        return this.entityModel;
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    @Override
    public String getWindowTitle() {
        return getEntity().getFullyQualifiedName() + "-" + serverObject.getDatabaseName() + '@'
                + serverObject.getServerName();
    }
}
