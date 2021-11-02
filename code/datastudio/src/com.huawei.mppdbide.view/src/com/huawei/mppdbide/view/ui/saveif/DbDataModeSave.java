/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.saveif;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionManager;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class DbDataModeSave
 * Description: The Class DbDataModeSave.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 02,06,2021]
 * @since 02,06,2021
 */
public class DbDataModeSave implements DataModeSave {
    private ConnectionManager manager;
    private DBConnection conn;

    // 0:trigger 1:view
    private int saveType;
    private String modeSaveId;

    public DbDataModeSave(ConnectionManager manager, String modeSaveId, int saveType) {
        this.modeSaveId = modeSaveId;
        this.manager = manager;
        this.saveType = saveType;
        try {
            this.conn = this.manager.getFreeConnection();
        } catch (MPPDBIDEException e) {
            MPPDBIDELoggerUtility.error("Fail to get free connection!");
        }
    }

    /**
     * Clear
     */
    public void clear() {
        if (conn != null) {
            this.manager.releaseConnection(conn);
            conn = null;
        }
    }

    /**
     * Gets id
     *
     * @return String the mode save id
     */
    public String getId() {
        return modeSaveId;
    }

    @Override
    public String saveData(String id, Object dataModel) {
        try {
            conn.execNonSelect(createTable());
            String serialData = serialData(dataModel);
            String newId = id + "_" + saveType;
            String dataInfo = executeAndGet(queryData(), new Object[] {newId});
            String sql = null;
            Object[] params = null;
            if (dataInfo == null) {
                sql = insertData();
                params = new Object[] {newId, serialData};
            } else {
                sql = updateData();
                params = new Object[] {serialData, newId};
            }
            executeNoGet(sql, params);
            return id;
        } catch (SQLException | DatabaseCriticalException | DatabaseOperationException exp) {
            MPPDBIDELoggerUtility.error("add new meta info failed!" + exp.getMessage());
        }
        return null;
    }

    @Override
    public <T> T loadData(String id, Class<T> clz) {
        String newId = id + "_" + saveType;
        try {
            String dataInfo = executeAndGet(queryData(), new Object[] {newId});
            if (dataInfo != null) {
                return unserialData(dataInfo, clz);
            }
        } catch (SQLException | DatabaseCriticalException | DatabaseOperationException exp) {
            MPPDBIDELoggerUtility.error("can't get meta info!" + exp.getMessage());
        }
        return null;
    }

    private void executeNoGet(String sql, Object[] params) throws SQLException,
            DatabaseCriticalException, DatabaseOperationException {
        try (PreparedStatement ps = conn.getPrepareStmt(sql)) {
            setPsParam(ps, params);
            ps.execute();
        }
    }

    private String executeAndGet(String sql, Object[] params) throws SQLException,
            DatabaseCriticalException, DatabaseOperationException {
        try (PreparedStatement ps = conn.getPrepareStmt(sql)) {
            setPsParam(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                } else {
                    return null;
                }
            }
        }
    }

    private void setPsParam(PreparedStatement ps, Object[] params) {
        IntStream.iterate(1, idx -> idx + 1)
            .limit(params.length)
            .forEach(idx -> {
                try {
                    ps.setObject(idx, params[idx - 1]);
                } catch (SQLException exp) {
                    MPPDBIDELoggerUtility.error("Fail to set object!");
                }
            });
    }

    private String serialData(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    private <T> T unserialData(String data, Class<T> clz) {
        Gson gson = new Gson();
        return (T) gson.fromJson(data, clz);
    }

    private String createTable() {
        return String.format(Locale.ENGLISH,
                "create table if not exists %s ("
                + "id varchar(256) primary key,"
                + "dataInfo varchar(2048)"
                + ")", getTableName());
    }

    private String insertData() {
        return String.format(Locale.ENGLISH,
                "insert into %s values (?, ?)",
                getTableName());
    }

    private String queryData() {
        return String.format(Locale.ENGLISH,
                "select dataInfo from %s where id = ?",
                getTableName());
    }

    private String updateData() {
        return String.format(Locale.ENGLISH,
                "update %s set dataInfo=? where id=?",
                getTableName());
    }

    private String getTableName() {
        return "public.t_create_metainfo";
    }
}
