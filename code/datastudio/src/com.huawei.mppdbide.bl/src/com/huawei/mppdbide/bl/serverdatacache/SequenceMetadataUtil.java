/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SequenceMetadataUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public abstract class SequenceMetadataUtil {

    private static final String QUERY = "select oid,relnamespace,relowner,relname from pg_class where relkind='S' ";
    private static final String QUERY_SCHEMA_FILTER = "and relnamespace=";

    private static final String QUERY_OID_FILTER = " and oid=%d";
    private static final String QUERY_PRIVILEGE_OID_FILTER = " and has_sequence_privilege(oid, 'USAGE');";

    /**
     * Sets the query privilege filter namespace.
     *
     * @param ns the ns
     * @return the string
     */
    private static String setQueryPrivilegeFilterNamespace(String ns) {
        return " and has_sequence_privilege(QUOTE_IDENT(" + ServerObject.getLiteralName(ns)
                + ") || '.' || QUOTE_IDENT(relname), 'USAGE');";
    }

    /**
     * Gets the all sequences by namespace ID query.
     *
     * @param ns the ns
     * @return the all sequences by namespace ID query
     */
    public static String getAllSequencesByNamespaceIDQuery(long ns) {

        return QUERY + QUERY_SCHEMA_FILTER + ns;
    }

    /**
     * Gets the all sequences by namespace.
     *
     * @param ns the ns
     * @param nsoid the nsoid
     * @return the all sequences by namespace
     */
    public static String getAllSequencesByNamespace(String ns, long nsoid) {

        return QUERY + QUERY_SCHEMA_FILTER + nsoid + setQueryPrivilegeFilterNamespace(ns) + ";";
    }

    /**
     * Gets the refresh query.
     *
     * @param oid the oid
     * @param privilegeFlag the privilege flag
     * @return the refresh query
     */
    public static String getRefreshQuery(long oid, boolean privilegeFlag) {

        String qry = QUERY + QUERY_OID_FILTER;
        if (privilegeFlag) {
            qry += QUERY_PRIVILEGE_OID_FILTER;
        } else {
            qry += ";";
        }
        qry = String.format(Locale.ENGLISH, qry, oid);
        return qry;
    }

    /**
     * Fetchsequence.
     *
     * @param sequenceQuery the sequence query
     * @param conn the conn
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void fetchsequence(String sequenceQuery, DBConnection conn, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        ResultSet rs = null;
        boolean hasNext = false;
        SequenceMetadata sequence = null;
        Namespace ns = null;

        try {
            rs = conn.execSelectAndReturnRs(sequenceQuery);
            hasNext = rs.next();
            while (hasNext) {
                sequence = convertToSequenceMetaData(rs, db);
                ns = sequence.getNamespace();
                ns.addSequence(sequence);
                ns.getDatabase().getSearchPoolManager().addsequenceToSearchPool(sequence);
                hasNext = rs.next();
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);

            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Convert to sequence meta data.
     *
     * @param rs the rs
     * @param db the db
     * @return the sequence metadata
     * @throws DatabaseOperationException the database operation exception
     */
    public static SequenceMetadata convertToSequenceMetaData(ResultSet rs, Database db)
            throws DatabaseOperationException {
        SequenceMetadata sequence = null;

        try {

            long namespaceId = rs.getLong("relnamespace");
            Namespace namespace = db.getNameSpaceById(namespaceId);

            long oid = rs.getLong("oid");
            String name = rs.getString("relname");
            sequence = new SequenceMetadata(oid, name, namespace);
            sequence.setOwner(rs.getString("relowner"));

            return sequence;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_TO_CONVERT_SEQ_DATA),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_TO_CONVERT_SEQ_DATA, exception);
        }
    }

    /**
     * Gets the searched sequence.
     *
     * @param namespace the namespace
     * @param tblId the tbl id
     * @param tblName the tbl name
     * @return the searched sequence
     */
    public static SequenceMetadata getSearchedSequence(Namespace namespace, int tblId, String tblName) {
        SequenceMetadata seq = new SequenceMetadata(tblId, tblName, namespace);
        seq.setOwner("relowner");
        return seq;

    }

    // CHECKSTYLE:OFF:
    /**
     * Refresh.
     *
     * @param oid the l
     * @param database the database
     * @param sequenceMdata the sequence mdata
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void refresh(long oid, Database database, SequenceMetadata sequenceMdata)
            throws DatabaseCriticalException, DatabaseOperationException {

        database.getSearchPoolManager()
                .removeSequenceFromSearchPool(sequenceMdata.getNamespace().getSequenceGroup().getObjectById(oid));
        sequenceMdata.getNamespace().getSequenceGroup().removeFromGroup(oid);
        boolean privilegeFlag = sequenceMdata.getPrivilegeFlag();
        String qry = SequenceMetadataUtil.getRefreshQuery(oid, privilegeFlag);
        boolean hasNext = false;
        SequenceMetadata sequence = null;

        ResultSet resultset = null;

        try {
            resultset = database.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(qry);
            hasNext = resultset.next();
            if (!hasNext) {
                sequenceMdata.getNamespace().getDatabase().getSearchPoolManager()
                        .removeSequenceFromSearchPool(sequenceMdata);
                if (privilegeFlag) {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE));
                    throw new DatabaseOperationException(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE);
                } else {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED));
                    throw new DatabaseOperationException(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED);
                    // refreshing a sequence didnt get any row, means it is
                    // dropped
                }
            }
            while (hasNext) {
                sequence = SequenceMetadataUtil.convertToSequenceMetaData(resultset, database);
                sequenceMdata.getNamespace().addSequence(sequence);
                hasNext = resultset.next();
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            database.getConnectionManager().closeRSOnObjBrowserConn(resultset);
        }

    }
}
