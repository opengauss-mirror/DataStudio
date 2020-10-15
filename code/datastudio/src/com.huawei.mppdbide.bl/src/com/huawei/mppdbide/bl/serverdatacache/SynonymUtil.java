/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Title: SynonymUtil
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 25-Jun-2020]
 * @since 25-Jun-2020
 */

public class SynonymUtil {
    /**
     * Synonym Statement
     */
    public static final String SYNONYM_STATEMENT = "select pgs.synname as synonym_name, pgr.rolname as owner,"
            + " pgn.nspname" + "  as schema_name," + " pgs.synobjname "
            + "as table_name from pg_synonym pgs, pg_namespace pgn,pg_roles pgr where"
            + " pgn.oid=pgs.synnamespace and pgs.synowner=pgr.oid";

    /**
     * fetch synonym statement
     */
    public static final String FETCH_SYNONYM_STATEMENT = SYNONYM_STATEMENT + " and schema_name =?";

    /**
     * Fetch synonyms.
     *
     * @param Namespace the namespace
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @Author: c00550043
     * @Date: Mar 12, 2020
     * @Title: fetchSynonyms
     * @Description: Fetch Synonyms.
     */
    public static void fetchSynonyms(Namespace namespace, DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement = dbConnection.getPrepareStmt(FETCH_SYNONYM_STATEMENT);
            preparedStatement.setString(1, namespace.getQualifiedObjectName());

            rs = preparedStatement.executeQuery();

            namespace.getSynonyms().clear();
            boolean hasNext = rs.next();
            while (hasNext) {
                SynonymMetaData synonymMetaData = convertToSynonym(null, rs, namespace);
                namespace.addSynonym(synonymMetaData);
                hasNext = rs.next();
            }
        } catch (DatabaseOperationException | SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            if (exception instanceof SQLException) {
                String sqlState = ((SQLException) exception).getSQLState();
                if ("42P01".equalsIgnoreCase(sqlState)) {
                    namespace.setSynoymSupported(false);
                    /*
                     * Synonyms are not supported in this server. Do not throw
                     * exception in this case
                     */
                    return;
                }
            }

            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            if (rs != null) {
                dbConnection.closeResultSet(rs);
            }
        }
    }

    /**
     * convertToSynonym method
     * 
     * @param synonym meta data
     * @param rs resultset
     * @param ns namespace
     * @return object synonym
     * @throws SQLException exception
     */
    public static SynonymMetaData convertToSynonym(SynonymMetaData synonym, ResultSet rs, Namespace ns)
            throws SQLException {
        SynonymMetaData synonymMetaData = synonym;
        if (null == synonymMetaData) {
            synonymMetaData = new SynonymMetaData(rs.getString(SynonymConstants.SYN_NAME), ns);
        }

        synonymMetaData.setName(rs.getString(SynonymConstants.SYN_NAME));
        synonymMetaData.setOwner(rs.getString(SynonymConstants.OWNER.toLowerCase(Locale.ENGLISH)));
        synonymMetaData.setObjectOwner(rs.getString(SynonymConstants.SCHEMA_NAME));
        synonymMetaData.setObjectName(rs.getString(SynonymConstants.TAB_NAME));
        return synonymMetaData;
    }
}
