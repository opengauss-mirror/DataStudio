/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.erd;

import java.sql.SQLException;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.erd.model.EREntity;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * The Class EREntityPresentation.
 *
 * @ClassName: EREntityPresentation
 * @Description: The Class EREntityPresentation. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 * @author: f00512995
 * @version:
 * @since: Sep 15, 2019
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
