/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.erd.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * Title: AbstractEREntity
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 30-Dec-2019]
 * @since 30-Dec-2019
 */
public abstract class AbstractEREntity extends AbstractERNode<ServerObject> {
    /** 
     * The dbcon. 
     */
    protected DBConnection dbcon;

    /** 
     * The is current table. 
     */
    protected boolean isCurrentTable;

    /** 
     * The attributes. 
     */
    protected List<AbstractERAttribute> attributes;

    /** 
     * The constraints. 
     */
    protected List<AbstractERConstraint> constraints;

    /** 
     * The column comments. 
     */
    protected Map<String, String> columnComments;

    /** 
     * The has column comments. 
     */
    protected boolean hasColumnComments;

    /**
     *  The has not null columns. 
     */
    protected boolean hasNotNullColumns;

    /**
     * Instantiates a new abstract ER entity.
     *
     * @param serverObject the server object
     * @param isCurrentTable the is current table
     * @param dbcon the dbcon
     */
    public AbstractEREntity(ServerObject serverObject, boolean isCurrentTable, DBConnection dbcon) {
        super(serverObject);
        this.isCurrentTable = isCurrentTable;
        this.dbcon = dbcon;
        this.attributes = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.columnComments = new HashMap<String, String>();
    }

    /**
     * Inits the ER entity.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    public abstract void initEREntity() throws DatabaseCriticalException, DatabaseOperationException, SQLException;

    /**
     * Gets the dbcon.
     *
     * @return the dbcon
     */
    public DBConnection getDbcon() {
        return dbcon;
    }

    /**
     * Gets the attributes.
     *
     * @return the attributes
     */
    public List<AbstractERAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Adds the attribute.
     *
     * @param attribute the attribute
     */
    public void addAttribute(AbstractERAttribute attribute) {
        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        if (attributes.contains(attribute)) {
            throw new IllegalArgumentException("Attribute already present");
        }
        attributes.add(attribute);
    }

    /**
     * filling the contraints info for EREntity.
     *
     * @param dbcon the dbcon
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    protected abstract void fillConstraints(DBConnection dbcon)
            throws DatabaseCriticalException, DatabaseOperationException;

    /**
     * filling the column comments info for EREntity.
     *
     * @param dbcon the dbcon
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    public abstract void fillColumnComments(DBConnection dbcon)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException;

    /**
     * filling the attributes info for EREntity.
     */
    protected abstract void fillAttributes();

    /**
     * Gets the fully qualified name.
     *
     * @return the fully qualified name
     */
    public abstract String getFullyQualifiedName();

    /**
     * Gets the table comments.
     *
     * @return the table comments
     */
    public abstract String getTableComments();

    /**
     * Checks if is current table.
     *
     * @return true, if is current table
     */
    public boolean isCurrentTable() {
        return isCurrentTable;
    }

    /**
     * Gets the column comments.
     *
     * @return the column comments
     */
    public Map<String, String> getColumnComments() {
        return columnComments;
    }

    /**
     * Gets the constraints.
     *
     * @return the constraints
     */
    public List<AbstractERConstraint> getConstraints() {
        return constraints;
    }

    /**
     * Sets the has column comments.
     */
    public void setHasColumnComments() {
        for (AbstractERAttribute attribute : attributes) {
            if (attribute.getComments() != null) {
                this.hasColumnComments = true;
                return;
            }
        }

        this.hasColumnComments = false;
    }

    /**
     * Sets the has not null columns.
     */
    public void setHasNotNullColumns() {
        for (AbstractERAttribute attribute : attributes) {
            if (attribute.getNullability().equals("NOT NULL")) {
                this.hasNotNullColumns = true;
                return;
            }
        }

        this.hasNotNullColumns = false;
    }

    /**
     * Checks if is checks for column comments.
     *
     * @return true, if is checks for column comments
     */
    public boolean isHasColumnComments() {
        return hasColumnComments;
    }

    /**
     * Checks if is checks for not null columns.
     *
     * @return true, if is checks for not null columns
     */
    public boolean isHasNotNullColumns() {
        return hasNotNullColumns;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return serverObject.getName();
    }
}
