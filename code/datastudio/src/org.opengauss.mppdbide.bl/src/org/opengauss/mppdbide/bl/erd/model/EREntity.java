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

package org.opengauss.mppdbide.bl.erd.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.GaussUtils;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * Title: EREntity 
 */

public class EREntity extends AbstractEREntity {

    private String tableComments;

    /**
     * Instantiates a new ER entity.
     *
     * @param serverObject the server object
     * @param isCurrentTable the is current table
     * @param dbcon the dbcon
     */
    public EREntity(TableMetaData serverObject, boolean isCurrentTable, DBConnection dbcon) {
        super(serverObject, isCurrentTable, dbcon);
    }

    /**
     * Inits the ER entity.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    @Override
    public void initEREntity() throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        fillConstraints(dbcon);
        fillTableComments(dbcon);
        fillColumnComments(dbcon);
        fillAttributes();
        setHasColumnComments();
        setHasNotNullColumns();
    }

    /**
     * filling the column comments info for EREntity.
     *
     * @param dbcon the dbcon
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    @Override
    public void fillColumnComments(DBConnection dbcon)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        String olapTableConstraintDetailsQuery = ERQueryBuilder.getOLAPColumnCommentsQuery(serverObject);
        ResultSet rs = dbcon.execSelectAndReturnRs(olapTableConstraintDetailsQuery);
        boolean hasNext = false;
        try {
            hasNext = rs.next();
            while (hasNext) {
                columnComments.put(rs.getString(IERNodeConstants.OLAP_ATTRIBUTE_NAME),
                        rs.getString(IERNodeConstants.OLAP_DESCRIPTION));
                hasNext = rs.next();
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            dbcon.closeResultSet(rs);
        }
    }

    /**
     * Fill table comments.
     *
     * @param dbcon the dbcon
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void fillTableComments(DBConnection dbcon) throws DatabaseCriticalException, DatabaseOperationException {
        String olapTableConstraintDetailsQuery = ERQueryBuilder.getOLAPTableCommentsQuery(serverObject);
        ResultSet rs = dbcon.execSelectAndReturnRs(olapTableConstraintDetailsQuery);
        boolean hasNext = false;
        try {
            hasNext = rs.next();
            while (hasNext) {
                tableComments = rs.getString(IERNodeConstants.OLAP_DESCRIPTION);
                hasNext = rs.next();
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            dbcon.closeResultSet(rs);
        }
    }

    /**
     * filling the attributes info for EREntity.
     */
    @Override
    public void fillAttributes() {
        OLAPObjectList<ColumnMetaData> columns = getServerObject().getColumns();
        for (ColumnMetaData column : columns.getList()) {
            boolean inPrimaryKey = false;
            for (AbstractERConstraint constraint : getConstraints()) {
                ERConstraint cons = (ERConstraint) constraint;
                if (cons.getConsType().equals(IERNodeConstants.PRIMARY_KEY)
                        && cons.getKeyColIndex().contains(column.getOid())) {
                    inPrimaryKey = true;
                    break;
                }
            }
            ERAttribute attribute = new ERAttribute(column, inPrimaryKey, false);
            attribute.setComments(columnComments.get(attribute.getName()));
            this.addAttribute(attribute);
        }
    }

    /**
     * Fill constraints.
     *
     * @param dbcon the dbcon
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    @Override
    public void fillConstraints(DBConnection dbcon) throws DatabaseCriticalException, DatabaseOperationException {
        String olapTableConstraintDetails = ERQueryBuilder.getOLAPTableConstraintDetailsQuery(serverObject);
        ResultSet rs = dbcon.execSelectAndReturnRs(olapTableConstraintDetails);
        boolean hasNext = false;
        try {
            hasNext = rs.next();
            while (hasNext) {
                ERConstraint constraint = new ERConstraint();
                constraint.setConstraintInfo(rs, constraints);
                hasNext = rs.next();
            }
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            dbcon.closeResultSet(rs);
        }
    }

    /**
     * Gets the table comments.
     *
     * @return the table comments
     */
    public String getTableComments() {
        return tableComments;
    }

    /**
     * Gets the server object.
     *
     * @return the server object
     */
    @Override
    public TableMetaData getServerObject() {
        return (TableMetaData) serverObject;
    }

    /**
     * Gets the fully qualified name.
     *
     * @return the fully qualified name
     */
    @Override
    public String getFullyQualifiedName() {
        return ((TableMetaData) serverObject).getNameSpaceName() + "." + getName();
    }

}
