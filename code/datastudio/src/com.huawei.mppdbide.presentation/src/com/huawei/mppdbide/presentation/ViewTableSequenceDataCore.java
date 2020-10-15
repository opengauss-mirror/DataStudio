/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/** 
 * Title: ViewTableSequenceDataCore
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 26-Jun-2020]
 * @since 26-Jun-2020
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
                    + ",pg_namespace sch WHERE tu.rolname = ? and tb.relname = ?  AND scl.relnamespace = sch.oid "
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
