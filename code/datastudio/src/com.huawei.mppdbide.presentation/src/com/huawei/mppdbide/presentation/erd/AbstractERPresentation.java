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

package com.huawei.mppdbide.presentation.erd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.erd.model.AbstractERAssociation;
import com.huawei.mppdbide.bl.erd.model.AbstractERConstraint;
import com.huawei.mppdbide.bl.erd.model.AbstractEREntity;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * The Class IERPresentation.
 *
 * @param <E> the element type
 * @ClassName: IERPresentation
 * @Description: The Class IERPresentation.
 *
 * @since 3.0.0
 */
public abstract class AbstractERPresentation<E> {
    /** 
     * The dbcon. 
     */
    protected DBConnection dbcon;
    
    /** 
     * The server object. 
     */
    protected E serverObject;
    
    /** 
     * The entities. 
     */
    protected List<AbstractEREntity> entities;
    
    /** 
     * The foreign constraints. 
     */
    protected List<AbstractERConstraint> foreignConstraints;
    
    /** 
     * The associations. 
     */
    protected List<AbstractERAssociation> associations;

    /**
     * Instantiates a new abstract ER presentation.
     *
     * @param serverObject the server object
     * @param dbcon the dbcon
     */
    public AbstractERPresentation(E serverObject, DBConnection dbcon) {
        this.entities = new ArrayList<>();
        this.associations = new ArrayList<>();
        this.foreignConstraints = new ArrayList<>();
        this.dbcon = dbcon;
        this.serverObject = serverObject;
    }

    /**
     * Inits the ER presentation.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    public abstract void initERPresentation()
            throws DatabaseCriticalException, DatabaseOperationException, SQLException;

    /**
     * Gets the dbcon.
     *
     * @return the dbcon
     */
    public DBConnection getDbcon() {
        return this.dbcon;
    }

    /**
     * Adds the entity.
     *
     * @param entity the entity
     */
    public void addEntity(AbstractEREntity entity) {
        entities.add(entity);
    }

    /**
     * Gets the entities.
     *
     * @return the entities
     */
    public List<AbstractEREntity> getEntities() {
        return entities;
    }

    /**
     * Adds the association.
     *
     * @param association the association
     */
    public void addAssociation(AbstractERAssociation association) {
        associations.add(association);
    }

    /**
     * Gets the associations.
     *
     * @return the associations
     */
    public List<AbstractERAssociation> getAssociations() {
        return associations;
    }

    /**
     * Adds the foreign constraint.
     *
     * @param keyData the key data
     */
    public void addForeignConstraint(AbstractERConstraint keyData) {
        if (foreignConstraints == null) {
            foreignConstraints = new ArrayList<>();
        }
        if (foreignConstraints.contains(keyData)) {
            throw new IllegalArgumentException("Constraint already present");
        }
        foreignConstraints.add(keyData);
    }

    /**
     * Gets the foreign constraints.
     *
     * @return the foreign constraints
     */
    public List<AbstractERConstraint> getForeignConstraints() {
        return foreignConstraints;
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    public abstract String getWindowTitle();
}
