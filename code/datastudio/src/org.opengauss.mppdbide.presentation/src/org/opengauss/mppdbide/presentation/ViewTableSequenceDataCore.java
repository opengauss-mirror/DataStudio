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

package org.opengauss.mppdbide.presentation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/** 
 * Title: ViewTableSequenceDataCore
 * 
 * @since 3.0.0
 */
public class ViewTableSequenceDataCore extends ViewTableDataCore {
    /**
     * Gets the query.
     *
     * @return the query
     * @throws DatabaseOperationException 
     */
    @Override
    public String getQuery() throws DatabaseOperationException {
        TableMetaData table = null;
        String result = null;

        if (serverObject != null) {
            table = (TableMetaData) serverObject;
            final String sequenceByTableSql = "WITH temp_sql as (SELECT tu.rolname as tableuser"
                    + ",tb.relname as tableName,tc.attname as columnName,scl.relname,sch.nspname FROM "
                    + "pg_class  scl,pg_depend sdp,pg_attrdef sc,pg_attribute tc,pg_class tb,pg_roles tu"
                    + ",pg_namespace sch WHERE sch.nspname = ? and tb.relname = ?  AND scl.relnamespace = sch.oid "
                    + "AND scl.relkind = 'S' AND sdp.refobjid = scl.oid AND sc.oid = sdp.objid "
                    + "AND tc.attrelid = sc.adrelid "
                    + "AND tc.attnum = sc.adnum AND tb.oid = tc.attrelid AND tu.oid = tb.relowner)"
                    + "select t.tableuser,t.tableName,t.columnName,decode(seq.sequence_name,t.relname,"
                    + "seq.sequence_name,'[No_Privilege]') as sequenceName"
                    + " ,decode(seq.sequence_name,t.relname,seq.sequence_schema,'[No_Privilege]') " 
                    + "as sequenceuser "
                    + " ,decode(seq.sequence_name,t.relname,seq.minimum_value,'[No_Privilege]') as minValue "
                    + " ,decode(seq.sequence_name,t.relname,seq.maximum_value,'[No_Privilege]')  as maxValue "
                    + " ,decode(seq.sequence_name,t.relname,seq.increment,'[No_Privilege]')  as increment "
                    + "from temp_sql t LEFT OUTER join information_schema.sequences seq on "
                    + "(t.nspname = seq.sequence_schema " + "AND t.relname = seq.sequence_name )";
            try (PreparedStatement statement = table.getConnectionManager().getObjBrowserConn()
                    .getPrepareStmt(sequenceByTableSql)) {
                statement.setString(1, table.getNamespace().getName());
                statement.setString(2, table.getName());
                result = statement.toString();
            } catch (DatabaseCriticalException | DatabaseOperationException | SQLException exception) {
                MPPDBIDELoggerUtility.error("Preparing to query related sequence sql failed", exception);
                throw new DatabaseOperationException(IMessagesConstants.PREPARED_QUERY_RELATED_SEQUENCE_FAILED);
            }
        }
        return result;
    }
}
