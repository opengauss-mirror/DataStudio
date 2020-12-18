/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.SortedMap;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ForeignTable.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ForeignTable extends TableMetaData {

    private static final String DROP_QUERY = "DROP FOREIGN TABLE IF EXISTS ";

    /**
     * Instantiates a new foreign table.
     *
     * @param ns the ns
     * @param type the type
     */
    public ForeignTable(Namespace ns, OBJECTTYPE type) {
        super(0, "notablename", ns, null, type);
    }

    /**
     * Gets the all F tables in schema.
     *
     * @param conn the conn
     * @param ns the ns
     * @param oid the oid
     * @return the all F tables in schema
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static List<TableMetaData> getAllFTablesInSchema(DBConnection conn, Namespace ns, long oid)
            throws DatabaseCriticalException, DatabaseOperationException {
        String qry1 = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, "
                + "ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as "
                + "desc, xctbl.nodeoids as nodes,tbl.reloptions as reloptions, "
                + "frgn.ftoptions, tbl.parttype as parttype, " + "array_to_string(part.partkey,',') as partkey "
                + "from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') "
                + "left join (select d.description, d.objoid from pg_description d "
                + "where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on "
                + "(tbl.oid = xctbl.pcrelid) "
                + "left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) left join "
                + "pg_tablespace ts on ts.oid = tbl.reltablespace " + "where tbl.relkind = 'f'";
        if (ns.getPrivilegeFlag()) {
            qry1 += " and has_table_privilege(frgn.ftrelid, 'SELECT')";
        }
        qry1 += " and tbl.relnamespace = %d;";
        String qry = String.format(Locale.ENGLISH, qry1, oid);
        return getTableInfo(conn, ns, qry);
    }

    /**
     * Gets the column info.
     *
     * @param ftabGroup the ftab group
     * @param conn the conn
     * @param ns the ns
     * @param oid the oid
     * @return the column info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void getColumnInfo(ForeignTableGroup ftabGroup, DBConnection conn, Namespace ns, long oid)
            throws DatabaseCriticalException, DatabaseOperationException {
        String qry1 = "WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = "
                + "%dand relkind = 'f' and parttype ='n'), "
                + "attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid "
                + ",c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid "
                + "from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ "
                + "where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d "
                + "where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr))"
                + "select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,"
                + "default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx;";

        String qry = String.format(Locale.ENGLISH, qry1, oid);
        PartitionTable.getColumnIndoByExecuteQuery(ftabGroup, conn, ns, qry);
    }

    /**
     * Gets the table info.
     *
     * @param conn the conn
     * @param ns the ns
     * @param qry the qry
     * @return the table info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private static List<TableMetaData> getTableInfo(DBConnection conn, Namespace ns, String qry)
            throws DatabaseCriticalException, DatabaseOperationException {
        ResultSet rs = conn.execSelectAndReturnRs(qry);
        ArrayList<TableMetaData> allFtabs = new ArrayList<TableMetaData>(0);
        TableMetaData ftab = null;
        try {
            String ftOptions = null;
            boolean hasNext = rs.next();
            while (hasNext) {
                if (!"n".equals(rs.getString("parttype"))) {
                    ftab = ForeignPartitionTable.convertToForeignPartitionTable(rs, ns);
                } else {
                    ftOptions = rs.getString("ftoptions");
                    if (ftOptions == null) {
                        ftOptions = "";
                    }
                    if (ftOptions.contains("format=orc")) {
                        ftab = new ForeignTable(ns, OBJECTTYPE.FOREIGN_TABLE_HDFS);
                    } else {
                        ftab = new ForeignTable(ns, OBJECTTYPE.FOREIGN_TABLE_GDS);
                    }
                    getForeignErrorTable(ftOptions, ns);
                    ftab.fillTablePropertiesFromRS(rs);
                }

                allFtabs.add(ftab);
                hasNext = rs.next();
            }

            return allFtabs;
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            conn.closeResultSet(rs);
        }
    }
    
    /**
     * getForeignErrorTable table
     *
     * @param ftOptions option
     * @param ns namespace
     */
    public static void getForeignErrorTable(String ftOptions, Namespace ns) {
        String[] ftOptionsArray = ftOptions.split(",");
        for (int i = 0; i < ftOptionsArray.length; i++) {
            if (ftOptionsArray[i].contains("error_table")) {
                String[] errorTableArray = ftOptionsArray[i].split("=");
                String tableName = errorTableArray[1];
                if (tableName.endsWith("}")) {
                    tableName = tableName.replace('}', ' ');
                }
                ns.getErrorTableList().add(tableName.trim());
            }
        }
    }

    @Override
    public String getSearchName() {
        return getName() + " - " + getNamespace().getName() + " - " + getTypeLabel();
    }

    @Override
    public String getDropQuery(boolean isCascade) {
        StringBuilder query = new StringBuilder(DROP_QUERY);
        query.append(this.getDisplayName());

        if (isCascade) {
            query.append(MPPDBIDEConstants.CASCADE);
        }

        return query.toString();
    }

    @Override
    public boolean isDropAllowed() {
        return false;
    }

    @Override
    public boolean isExportAllowed(EXPORTTYPE exportType) {
        return false;
    }
}
