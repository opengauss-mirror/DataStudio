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

import java.util.Locale;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.bl.export.EXPORTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.groups.TriggerObjectGroup;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: TriggerMetaData for use
 *
 * @since 3.0.0
 */
public class TriggerMetaData extends BatchDropServerObject implements GaussOLAPDBMSObject {
    private boolean enable = true;
    private long tableoid;
    private long funcOid;
    private String triggerType;
    private String triggerEnable;
    private Namespace namespace;
    private String ddlMsg = "";

    public TriggerMetaData(long oid, String name) {
        super(oid, name, OBJECTTYPE.TRIGGER_METADATA, false);
    }

    @Override
    public String getDropQuery(boolean isCascade) {
        return String.format(Locale.ENGLISH, "DROP TRIGGER %s ON %s.%s",
                ServerObject.getQualifiedObjectName(this.getName()),
                getNamespace().getName(),
                ServerObject.getQualifiedObjectName(geTableMetaData().getName()));
    }

    /**
     * drop trigger
     *
     * @param DBConnection conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execDrop(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        conn.execNonSelect(getDropQuery(false));
    }

    /**
     * enable sql
     *
     * @param boolean if is enable
     * @return String the alter string
     */
    public String alterEnableString(boolean enable) {
        final String queryFormat = "ALTER TABLE %s.%s %s TRIGGER %s";
        return String.format(Locale.ENGLISH,
                queryFormat,
                getNamespace().getName(),
                ServerObject.getQualifiedObjectName(geTableMetaData().getName()),
                enable ? "ENABLE" : "DISABLE",
                ServerObject.getQualifiedObjectName(getName())
        );
    }

    /**
     * rename sql
     *
     * @param String the newName
     * @param long trigger relid
     * @param Namespace the namespace
     * @param DBConnection the dbConnection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execRename(String newName, long tgrelid, Namespace namespace, DBConnection dbConnection)
            throws DatabaseCriticalException, DatabaseOperationException {
        StringBuilder sb = new StringBuilder();
        TableMetaData table = namespace.getTablesGroup().getObjectById(tgrelid);
        sb.append("ALTER TRIGGER ");
        sb.append(getDisplayName());
        sb.append(" ON ");
        sb.append(namespace.getName() + "." + table.getName());
        sb.append(" RENAME TO ");
        sb.append(ServerObject.getQualifiedObjectName(newName));
        dbConnection.execNonSelect(sb.toString());
    }


    @Override
    public boolean isExportAllowed(EXPORTTYPE exportType) {
        return true;
    }

    @Override
    public Database getDatabase() {
        return namespace.getDatabase();
    }

    /**
     * description: enable trigger flag or not
     *
     * @param enable true if enable
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * description: get the enable state
     *
     * @return boolean true if enable
     */
    public boolean getEnable() {
        return this.enable;
    }

    /**
     * description: get the table oid
     *
     * @return long the tableoid
     */
    public long getTableoid() {
        return tableoid;
    }

    /**
     * description: set the table oid
     *
     * @param long the tableoid to set
     */
    public void setTableoid(long tableoid) {
        this.tableoid = tableoid;
    }

    /**
     * description: get function oid
     *
     * @return long the funcOid
     */
    public long getFuncOid() {
        return funcOid;
    }

    /**
     * description: set the function oid
     *
     * @param long the funcOid to set
     */
    public void setFuncOid(long funcOid) {
        this.funcOid = funcOid;
    }

    /**
     * description: get the trigger type
     *
     * @return String the triggerType
     */
    public String getTriggerType() {
        return triggerType;
    }

    /**
     * description: set the trigger type
     *
     * @param String the triggerType to set
     */
    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    /**
     * description: get the trigger enable
     *
     * @return String the triggerEnable
     */
    public String getTriggerEnable() {
        return triggerEnable;
    }

    /**
     * description: set the trigger enable
     *
     * @param String the triggerEnable to set
     */
    public void setTriggerEnable(String triggerEnable) {
        this.triggerEnable = triggerEnable;
    }

    /**
     * description: get the namespace
     *
     * @return Namespace the namespace
     */
    public Namespace getNamespace() {
        return namespace;
    }

    /**
     * description: set the namespace
     *
     * @param Namespace the namespace to set
     */
    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    /**
     * description: get ddl msg
     *
     * @return String the msg
     */
    public String getDdlMsg() {
        return ddlMsg;
    }

    /**
     * description: set the msg
     *
     * @param String the msg
     */
    public void setDdlMsg(String msg) {
        this.ddlMsg = msg;
    }

    /**
     * description: get the header
     *
     * @return String the header string
     */
    public String getHeader() {
        return "--"
                + MPPDBIDEConstants.LINE_SEPARATOR + "-- Name: " + getName() + "; Type: "
                + getTypeLabel() + "; Schema: " + getNamespace().getName() + ";"
                + MPPDBIDEConstants.LINE_SEPARATOR + "--" + MPPDBIDEConstants.LINE_SEPARATOR
                + MPPDBIDEConstants.LINE_SEPARATOR;
    }

    /**
     * description: get the table metadata
     *
     * @return TableMetaData the table metadata
     */
    public TableMetaData geTableMetaData() {
        return namespace.getTablesGroup().getObjectById(tableoid);
    }

    @Override
    public TriggerObjectGroup getParent() {
        return namespace.getTriggerObjectGroup();
    }
}
