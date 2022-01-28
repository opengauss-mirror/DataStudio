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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.SynonymConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: Class
 * 
 * Description: The Class SynonymMetaData.
 *
 * @since 3.0.0
 */
public class SynonymMetaData extends BatchDropServerObject implements GaussOLAPDBMSObject, ISynonymMetaData {
    /**
     * Refresh synonym statement
     */
    public static final String REFRESH_SYNONYM_STATEMENT = SynonymUtil.SYNONYM_STATEMENT
            + " and schema_name = ? and synonym_name = ?;";

    private static int virtualId = 1;
    private static final Object ID_LOCK = new Object();

    private Namespace namespace;
    private String owner;
    private String objectOwner;
    private String objectName;

    /**
     * Instantiates a new synonym meta data.
     * 
     * @param name the name
     * @param Namespace the namespace
     */
    public SynonymMetaData(String name, Namespace namespace) {
        super(getNextVirtualID(), name, OBJECTTYPE.SYNONYM_METADATA_GROUP, true);
        this.namespace = namespace;
    }

    /**
     * Gets the drop query.
     *
     * @param isCascade the is cascade
     * @return the drop query
     */
    @Override
    public String getDropQuery(boolean isCascade) {
        String dropSynonymQuery = "DROP SYNONYM IF EXISTS ";
        StringBuilder query = null;
        query = new StringBuilder(dropSynonymQuery);
        query.append(namespace.getQualifiedObjectName()).append('.').append(this.getQualifiedObjectName());
        return query.toString();
    }

    /**
     * Refresh.
     *
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refresh(DBConnection dbConnection) throws DatabaseOperationException, DatabaseCriticalException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement = dbConnection.getPrepareStmt(REFRESH_SYNONYM_STATEMENT);
            preparedStatement.setString(1, namespace.getQualifiedObjectName());
            preparedStatement.setString(2, getQualifiedObjectName());
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                removeSynonym();
                SynonymMetaData synonymMetaData = SynonymUtil.convertToSynonym(this, rs, namespace);
                namespace.addSynonym(synonymMetaData);
            } else {
                removeSynonym();
            }
        } catch (SQLException | DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            dbConnection.closeResultSet(rs);
            dbConnection.closeStatement(preparedStatement);
        }
    }

    /**
     * Removes the synonym.
     */
    public void removeSynonym() {
        this.namespace.getDatabase().getSearchPoolManager().removeSynonymFromSearchPool(this);
        this.namespace.getSynonymGroup().removeFromGroup(getOid());
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Namespace getParent() {
        return namespace;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    public Database getDatabase() {
        return namespace.getDatabase();
    }

    /**
     * Gets the search name.
     *
     * @return the search name
     */
    @Override
    public String getSearchName() {
        return getName() + " - " + getParent().getQualifiedObjectName() + " - " + getTypeLabel();
    }

    /**
     * Gets the drop name.
     *
     * @return the drop name
     */
    public String getDropName() {
        return getParent().getQualifiedObjectName() + '.' + getQualifiedObjectName();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * Gets the window title name.
     *
     * @return the window title name
     */
    @Override
    public String getWindowTitleName() {
        return namespace.getQualifiedObjectName() + '.' + getName() + '-' + getDatabaseName() + '@' + getServerName();
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     * @Title: getOwner
     * @Description: get the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner.
     *
     * @param owner the owner
     * @Title: setOwner
     * @Description: set the owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets the object owner.
     *
     * @return the object owner
     * @Title: getObjectOwner
     * @Description: get the object owner
     */
    public String getObjectOwner() {
        return objectOwner;
    }

    /**
     * Sets the object owner.
     *
     * @param objectOwner the object owner
     * @Title: setObejctOwner
     * @Description: set the object owner
     */
    public void setObjectOwner(String objectOwner) {
        this.objectOwner = objectOwner;
    }

    /**
     * Gets the object name.
     *
     * @return the object name
     * @Title: getObjectName
     * @Description: get the object name
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Sets the object name.
     *
     * @param objectName the object name
     * @Title: setObjectName
     * @Description: set the object name
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Drop synonym.
     *
     * @param conn the dbConnection
     * @param isCasecade if is casecade
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @Title: dropSynonym
     * @Description: drop synonym
     */
    public void dropSynonym(DBConnection conn, boolean isCasecade)
            throws DatabaseOperationException, DatabaseCriticalException {
        conn.execNonSelect(getDropQuery(isCasecade));
        removeSynonym();
    }

    private static int getNextVirtualID() {
        int nextId;
        synchronized (ID_LOCK) {
            nextId = virtualId;
            virtualId++;
        }
        return nextId;
    }

    /**
     * Gets the server name.
     *
     * @return the server name
     */
    public String getServerName() {
        if (namespace == null) {
            return "";
        }
        return namespace.getServer().getName();
    }

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    public String getDatabaseName() {
        if (namespace == null) {
            return "";
        }
        return namespace.getDatabase().getName();
    }

    @Override
    public void refreshSynonym(DBConnection dbConnection) throws DatabaseOperationException, DatabaseCriticalException {
    }

    @Override
    public Server getServer() {
        return namespace.getServer();
    }

}
