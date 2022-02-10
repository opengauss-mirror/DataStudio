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

package org.opengauss.mppdbide.bl.serverdatacache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.SynonymConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: SynonymUtil
 * 
 * Description:
 * 
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
