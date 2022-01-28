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

package com.huawei.mppdbide.presentation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;

/**
 * Title: ViewSequenceCore
 * 
 * @since 3.0.0
 */
public class ViewSequenceCore extends AbstractViewTableDataCore {
    private static final String VIEW_TABLE_DATA = "VIEW_TABLE_DATA_";
    private SequenceMetadata serverObject;
    private ViewSequnceWindowDetails details;

    private static final String tableBySequenceSql = " SELECT seq.sequence_name as sequenceName, seq.sequence_schema as sequenceuser,"
            + " seq.minimum_value as minValue,seq.maximum_value as maxValue , seq.increment as increment, tc.attname as columnName, tu.rolname as tableuser"
            + " , tb.relname as tableName FROM information_schema.sequences seq, pg_namespace sch, pg_class scl, pg_depend sdp"
            + " , pg_attrdef sc, pg_attribute tc, pg_class tb, pg_roles tu"
            + " WHERE seq.sequence_schema = ? AND seq.sequence_name = ? AND sch.nspname = seq.sequence_schema"
            + " AND scl.relnamespace = sch.oid AND scl.relname = seq.sequence_name AND scl.relkind = 'S' AND sdp.refobjid = scl.oid "
            + " AND sc.oid = sdp.objid AND tc.attrelid = sc.adrelid AND tc.attnum = sc.adnum AND tb.oid = tc.attrelid"
            + " AND tu.oid = tb.relowner;";
    public static String getTableBySequenceSql()
    {
        return tableBySequenceSql;
    }

    @Override
    public ServerObject getServerObject() {
        return serverObject;
    }

    @Override
    public IWindowDetail getWindowDetails() {
        return details;
    }

    @Override
    public String getWindowTitle() {
        SequenceMetadata seqData = null;
        String windowTitle = null;
        if (serverObject != null) {
            seqData = (SequenceMetadata) serverObject;
            windowTitle = seqData.getNamespace().getName() + '.' + seqData.getName() + '-'
                    + seqData.getDatabase().getDbName() + '@' + seqData.getDatabase().getServerName();
            ;
        }
        return windowTitle;
    }

    @Override
    public String getProgressBarLabel() {
        SequenceMetadata seqData = null;
        String progressLabelForTableWithMsg = null;
        if (serverObject != null) {
            seqData = (SequenceMetadata) serverObject;
            progressLabelForTableWithMsg = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(seqData.getName(),
                    seqData.getNamespace().getName(), seqData.getDatabase().getDbName(),
                    seqData.getDatabase().getServerName(), IMessagesConstants.VIEW_TABLE_PROGRESS_NAME);
        }
        return progressLabelForTableWithMsg;
    }

    @Override
    public String getQuery() throws DatabaseOperationException {
        SequenceMetadata seqData = null;
        String result = null;
        if (serverObject != null) {

            seqData = (SequenceMetadata) serverObject;
            final String tableBySequenceSql = " SELECT seq.sequence_name as sequenceName, seq.sequence_schema as sequenceuser,"
                    + " seq.minimum_value as minValue,seq.maximum_value as maxValue , seq.increment as increment, tc.attname as columnName, tu.rolname as tableuser"
                    + " , tb.relname as tableName FROM information_schema.sequences seq, pg_namespace sch, pg_class scl, pg_depend sdp"
                    + " , pg_attrdef sc, pg_attribute tc, pg_class tb, pg_roles tu"
                    + " WHERE seq.sequence_schema = ? AND seq.sequence_name = ? AND sch.nspname = seq.sequence_schema"
                    + " AND scl.relnamespace = sch.oid AND scl.relname = seq.sequence_name AND scl.relkind = 'S' AND sdp.refobjid = scl.oid "
                    + " AND sc.oid = sdp.objid AND tc.attrelid = sc.adrelid AND tc.attnum = sc.adnum AND tb.oid = tc.attrelid"
                    + " AND tu.oid = tb.relowner;";

            try (PreparedStatement statement = seqData.getConnectionManager().getObjBrowserConn()
                    .getPrepareStmt(tableBySequenceSql)) {
                statement.setString(1, seqData.getNamespace().getName());
                statement.setString(2, seqData.getName());
                result = statement.toString();
            } catch (DatabaseCriticalException | DatabaseOperationException | SQLException exception) {
                MPPDBIDELoggerUtility.error("Preparing to query related table sql failed", exception);
                throw new DatabaseOperationException(IMessagesConstants.PREPARED_QUERY_RELATED_TABLE_FAILED);
            }
        }

        return result;
    }

    private class ViewSequnceWindowDetails implements IWindowDetail {

        @Override
        public String getTitle() {
            return getWindowTitle();
        }

        @Override
        public String getUniqueID() {
            return VIEW_TABLE_DATA + getTitle();
        }

        @Override
        public String getShortTitle() {
            SequenceMetadata seqData = null;
            String sequenceName = null;
            if (serverObject != null) {
                seqData = (SequenceMetadata) serverObject;
                sequenceName = seqData.getDisplayName();
            }
            return sequenceName;
        }
    }

    /**
     * the init
     * 
     * @param obj the obj
     */
    public void init(ServerObject obj) {
        if (obj instanceof SequenceMetadata) {
            this.serverObject = (SequenceMetadata) obj;
            details = new ViewSequnceWindowDetails();
        }

    }
    
    /**
     * Checks if is table dropped.
     *
     * @return true, if is table dropped
     */
    @Override
    public boolean isTableDropped() {
        return this.serverObject.isTableDropped();
    }

}
