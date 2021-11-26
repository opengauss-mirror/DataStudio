/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.TriggerMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: the class TriggerObjectGroup
 * Description: the class TriggerObjectGroup
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2021-04-29]
 * @since 2021-04-29
 */
public class TriggerObjectGroup extends OLAPObjectGroup<TriggerMetaData> {
    private static final String QUERY_ALL_TRIGGER = "select t.oid as oid, t.tgrelid as tableoid,"
            + " t.tgname as name, t.tgfoid as functionoid, t.tgtype as tgtype,"
            + " t.tgenabled as tgenable,"
            + " pg_get_triggerdef(t.oid) as ddlmsg"
            + " from pg_trigger t, pg_class c"
            + " where t.tgrelid = c.oid and c.relnamespace=?";

    private static final String QUERY_TRIGGER_BY_NAME = QUERY_ALL_TRIGGER
            + " and t.tgname = ?";
    private Namespace namespace;

    public TriggerObjectGroup(Object parentObject) {
        super(OBJECTTYPE.TRIGGER_GROUP, parentObject);
        if (parentObject instanceof Namespace) {
            namespace = (Namespace) parentObject;
        }
    }

    @Override
    public Database getDatabase() {
        return namespace.getDatabase();
    }

    /**
     * description: get namespace
     *
     * @return Namespace the namespace
     */
    public Namespace getNamespace() {
        return namespace;
    }

    /**
     * description: fetch all triggers from database
     *
     * @param Namespace the namespace
     * @param DBConnection the connection
     * @throws DatabaseOperationException operation exp
     * @throws DatabaseCriticalException critical exp
     */
    public static void fetchTriggers(Namespace ns, DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException {
        ns.getTriggerObjectGroup().clear();
        Object[] params = new Object[1];
        params[0] = Long.valueOf(ns.getOid());
        List<TriggerMetaData> triggres = fetchTriggers(ns, conn, QUERY_ALL_TRIGGER, params);
        for (TriggerMetaData trigger: triggres) {
            ns.addTrigger(trigger);
        }
    }

    /**
     * description: fetch all triggers from database by name
     *
     * @param Namespace the namespace
     * @param DBConnection the connection
     * @param String the query trigger name
     * @return List<TriggerMetaData> trigger metadata list
     * @throws DatabaseOperationException operation exp
     * @throws DatabaseCriticalException critical exp
     */
    public static List<TriggerMetaData> fetchTriggerByName(Namespace ns,
            DBConnection conn,
            String name)
            throws DatabaseOperationException, DatabaseCriticalException {
        Object[] params = new Object[2];
        params[0] = Long.valueOf(ns.getOid());
        params[1] = name;
        return fetchTriggers(ns, conn, QUERY_TRIGGER_BY_NAME, params);
    }

    /**
     * description: fetch all triggers from database
     *
     * @param Namespace the namespace
     * @param DBConnection the connection
     * @param String the query sql
     * @param Object[] the query params
     * @return List<TriggerMetaData> trigger metadata list
     * @throws DatabaseOperationException operation exp
     * @throws DatabaseCriticalException critical exp
     */
    public static List<TriggerMetaData> fetchTriggers(Namespace ns,
            DBConnection conn,
            String query,
            Object[] params) throws DatabaseOperationException, DatabaseCriticalException {
        List<TriggerMetaData> resutls = new LinkedList<>();
        try (PreparedStatement ps = conn.getPrepareStmt(query)) {
            for (int i = 0, n = params.length; i < n; i ++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TriggerMetaData trigger = parseTrigger(rs, ns);
                    resutls.add(trigger);
                }
            }
        } catch (DatabaseOperationException | SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);

            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        }
        return resutls;
    }

    private static TriggerMetaData parseTrigger(ResultSet rs, Namespace ns) throws SQLException {
        long oid = rs.getLong("oid");
        long tableoid = rs.getLong("tableoid");
        String name = rs.getString("name");
        long funcOid = rs.getLong("functionoid");
        String triggerType = rs.getString("tgtype");
        String triggerEnable = rs.getString("tgenable");
        String ddlMsg = rs.getString("ddlmsg");
        TriggerMetaData trigger = new TriggerMetaData(oid, name);
        trigger.setNamespace(ns);
        trigger.setTableoid(tableoid);
        trigger.setFuncOid(funcOid);
        trigger.setTriggerType(triggerType);
        trigger.setTriggerEnable(triggerEnable);
        trigger.setEnable("D".equalsIgnoreCase(triggerEnable) ? false : true);
        trigger.setDdlMsg(ddlMsg);
        return trigger;
    }
}