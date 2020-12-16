/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaDataUtils;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.IndexedColumnExpr;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.objectbrowser.ObjectBrowserObjectRefreshPresentation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesTableImpl.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class PropertiesTableImpl implements IServerObjectProperties {
    private static final String TABLE_DESC = "tableDesc";

    /**
     * The table.
     */
    protected TableMetaData table;

    /**
     * The tab name list.
     */
    protected List<String> tabNameList;

    /**
     * The table properties.
     */
    protected List<List<String[]>> tableProperties;
    private String defaultTableSpaceName;
    private ConvertToObjectPropertyData convertToObjectPropertyData;

    /**
     * Instantiates a new properties table impl.
     *
     * @param obj the obj
     */
    public PropertiesTableImpl(Object obj) {
        this.table = (TableMetaData) obj;
        tabNameList = null;
        tableProperties = null;
        convertToObjectPropertyData = new OlapConvertToObjectPropertyData();

    }

    /**
     * Gets the object name.
     *
     * @return the object name
     */
    @Override
    public String getObjectName() {
        return table.getQualifiedObjectName();
    }

    /**
     * Gets the all properties.
     *
     * @param conn the conn
     * @return the all properties
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public List<IObjectPropertyData> getAllProperties(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        if (!table.isLoaded()) {
            ObjectBrowserObjectRefreshPresentation.refreshSeverObject(table);
        }
        tabNameList = new ArrayList<String>(5);
        tableProperties = new ArrayList<List<String[]>>(5);
        tabNameList.add(PropertiesConstants.GENERAL);
        tabNameList.add(PropertiesConstants.COLUMNS);
        tabNameList.add(PropertiesConstants.CONSTRAINTS);
        tabNameList.add(PropertiesConstants.INDEX);
        /*
         * get the comments associated with the table.
         */
        Map<String, String> commentsList = getComments(conn);
        defaultTableSpaceName = this.table.getDatabase().getDBDefaultTblSpc();
        tableProperties.add(getGeneralProperty(conn, commentsList));
        tableProperties.add(getColumnInfo(conn, commentsList));
        tableProperties.add(getConstraintInfo(conn));
        tableProperties.add(getIndexInfo(conn));
        return convertToObjectPropertyData.getObjectPropertyData(tabNameList, tableProperties, this.table, this);

    }

    /**
     * Gets the general property.
     *
     * @param conn the conn
     * @param commentsList the comments list
     * @return the general property
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public List<String[]> getGeneralProperty(DBConnection conn, Map<String, String> commentsList)
            throws DatabaseCriticalException, DatabaseOperationException {

        String qry = getQueryForFetchingGeneralProperties();

        ResultSet rs = conn.execSelectAndReturnRs(qry);
        boolean hasNext = false;
        List<String[]> props = new ArrayList<String[]>(5);
        String[] genPropHeader = {MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_PROPERTY),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_VALUE)};
        props.add(genPropHeader);
        try {
            hasNext = rs.next();

            /* only one record expected */
            if (hasNext) {
                addGeneralPropertiesInList(commentsList, rs, props);

            } else {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.TABLE_DOES_NOT_EXIST));
                throw new DatabaseOperationException(IMessagesConstants.TABLE_DOES_NOT_EXIST);

            }
        } catch (SQLException exception) {
            GaussUtils.handleCriticalException(exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }

        return props;
    }

    private void addGeneralPropertiesInList(Map<String, String> commentsList, ResultSet rs, List<String[]> props)
            throws SQLException {
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.OID_MSG),
                Long.toString(table.getOid())).getProp());

        String tableName = table.getName();
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.TABLE_PROPERTIES_TABLE_NAME),
                tableName).getProp());

        // DTS2014110708770 start
        String tblSpcName = rs.getString("spcname");
        if ("DEFAULT".equals(tblSpcName)) {
            tblSpcName = defaultTableSpaceName;
        }
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACE_MSG), tblSpcName)
                .getProp());

        // DTS2014110708770 end
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.IS_TEMP),
                table.isTempTable() ? "true" : "false").getProp());
        String tabletype = rs.getString("relpersistence");
        getTypeProperty(props, tabletype);
        props.add(
                new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.OWNER_MSG), rs.getString("Owner"))
                        .getProp());
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.PAGES_MSG),
                Integer.toString(rs.getInt("pages"))).getProp());
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.ROW_COUNT),
                Long.toString(rs.getLong("rows_count"))).getProp());
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.HAS_INDEX),
                rs.getBoolean("has_index") ? "true" : "false").getProp());
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.IS_SHARED),
                rs.getBoolean("is_shared") ? "true" : "false").getProp());

        StringBuilder sbOpts = getTableOptionProperty(rs, props);
        String hashOId = "";
        if (rs.getBoolean("hashoid")) {
            hashOId = "yes";
        } else {
            hashOId = "no";
        }
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.HAS_OID), hashOId).getProp());
        if (sbOpts.length() > 0) {
            props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_MSG),
                    sbOpts.toString()).getProp());
        }

        String tableDescriptionPos = TABLE_DESC;

        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.DESC_MSG),
                commentsList.get(tableDescriptionPos)).getProp());
    }

    private String getQueryForFetchingGeneralProperties() {
        String qry = "SELECT tbl.relpersistence as relpersistence, case when tbl.reltablespace = 0 then"
                + " 'DEFAULT' else tblsp.spcname end, auth.rolname as owner, tbl.relpages pages, "
                + "tbl.reltuples as rows_count, tbl.relhasindex as has_index, tbl.relisshared as is_shared, "
                + "tbl.relchecks as check_count, tbl.relhaspkey as has_pkey, tbl.relhasrules as has_rules, "
                + "tbl.relhastriggers as has_triggers, array_to_string(tbl.reloptions, ',') as options,"
                + "tbl.relhasoids as hashoid, d.description as tbl_desc "
                + "FROM pg_class tbl LEFT JOIN pg_roles auth on " + "(tbl.relowner = auth.oid) left join "
                + "pg_description d on (tbl.oid = d.objoid) "
                + "LEFT JOIN pg_tablespace tblsp ON (tbl.reltablespace = tblsp.oid) WHERE tbl.oid = " + table.getOid()
                + ';';
        return qry;
    }

    /**
     * Gets the constraint info.
     *
     * @param conn the conn
     * @return the constraint info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public List<String[]> getConstraintInfo(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        List<String[]> uiConstraintList = new ArrayList<String[]>(5);
        List<ConstraintInfo> infoList = getConstrainColumnlist(conn);

        String[] consHeader = {MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_CONSTRAINT_CONSTRAINTNAME),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_CONSTRAINT_COLUMNS),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_CONSTRAINT_TYPE),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_EXPRESSION),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_CONSTRAINT_ISDEFFERED),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_CONSTRAINT_SCHEMA),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_TABLESPACE)};
        uiConstraintList.add(consHeader);
        for (ConstraintInfo info : infoList) {
            int jindex = 0;
            String[] consInfo = new String[consHeader.length];
            consInfo[jindex] = info.getConstraintName();
            consInfo[++jindex] = formConsColToDisplay(info.getColumns());
            consInfo[++jindex] = info.getConstraintType();
            consInfo[++jindex] = info.getConstraintExpr();
            consInfo[++jindex] = "" + info.isDeferred();
            consInfo[++jindex] = info.getConsSchema();
            consInfo[++jindex] = info.getTableSpace() == null ? defaultTableSpaceName : info.getTableSpace();
            uiConstraintList.add(consInfo);
        }

        return uiConstraintList;
    }

    /**
     * Gets the index info.
     *
     * @param conn the conn
     * @return the index info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public List<String[]> getIndexInfo(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        // index expression need to be removed after the confirmation
        List<IndexMetaData> indexList = getIndexMetaData(conn);
        List<String[]> uiIndexList = new ArrayList<String[]>();
        String[] indexHeader = {MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_INDEX_INDEXNAME),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_INDEX_INDEXCOLUMNS),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_INDEX_ISUNIQUE),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_EXPRESSION),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_TABLESPACE)};

        uiIndexList.add(indexHeader);
        for (IndexMetaData index : indexList) {
            int jindex = 0;
            String[] indexInfo = new String[indexHeader.length];
            indexInfo[jindex] = index.getName();
            indexInfo[++jindex] = splitIndexedColumns(index.getIndexedColumns());
            indexInfo[++jindex] = "" + index.isUnique();
            indexInfo[++jindex] = index.getWhereExpr() == null ? getWhereExpr(index, conn) : index.getWhereExpr();
            indexInfo[++jindex] = index.getTablespc();
            uiIndexList.add(indexInfo);
        }
        return uiIndexList;
    }

    /**
     * Gets the where expr.
     *
     * @param index the index
     * @param conn the conn
     * @return the where expr
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private String getWhereExpr(IndexMetaData index, DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {

        return index.getwhereExpresionforProperty(conn);
    }

    /**
     * Split indexed columns.
     *
     * @param indexedColumns the indexed columns
     * @return the string
     */
    private String splitIndexedColumns(ArrayList<IndexedColumnExpr> indexedColumns) {
        String indexes = indexedColumns.toString();
        return indexes.substring(1, indexes.length() - 1);
    }

    /**
     * Form cons col to display.
     *
     * @param colIndexes the col indexes
     * @return the string
     */
    private String formConsColToDisplay(String colIndexes) {
        String colNames = colIndexes;
        List<String> splittedColNames = Arrays.asList(colNames.split(","));
        StringBuilder str = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        List<ColumnMetaData> colMetadataList = table.getColumnMetaDataList();
        int index = 0;
        for (ColumnMetaData colMetadata : colMetadataList) {
            if (index == splittedColNames.size()) {
                break;
            }
            if (colMetadata.getOid() == Integer.parseInt(splittedColNames.get(index))) {
                str.append(colMetadata.getName());
                str.append(",");
                index++;
            }
        }
        str.deleteCharAt(str.length() - 1);

        return str.toString();
    }

    /**
     * Gets the column info.
     *
     * @param dbcon the dbcon
     * @param commentsList the comments list
     * @return the column info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public List<String[]> getColumnInfo(DBConnection dbcon, Map<String, String> commentsList)
            throws DatabaseCriticalException, DatabaseOperationException {
        List<ColumnMetaData> colMetaData = getColumnMetaData(dbcon);
        List<String[]> uiColumnList = new ArrayList<String[]>(5);
        String[] columnHeader = {MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_COLUMNS_COLUMNSNAME),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_COLUMNS_DATATYPE),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_COLUMNS_ISNULLABLE),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_COLUMNS_COMMENTS)};
        uiColumnList.add(columnHeader);
        for (ColumnMetaData col : colMetaData) {
            String[] colInfo = new String[columnHeader.length];
            int cnt = 0;
            colInfo[cnt] = col.getName();
            if (null != col.getDataType()) {
                colInfo[++cnt] = col.getDisplayDatatype();
            } else {

                colInfo[++cnt] = "";
            }
            // retrieve the boolean value and change to string
            colInfo[++cnt] = "" + !col.isNotNull();
            if (commentsList.containsKey(col.getName())) {

                colInfo[++cnt] = commentsList.get(col.getName());
            }
            uiColumnList.add(colInfo);

        }

        return uiColumnList;
    }

    /**
     * Gets the comments.
     *
     * @param conn the conn
     * @return the comments
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public Map<String, String> getComments(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {

        String comment = null;
        String attributeName = "";
        Map<String, String> commentInfo = new HashMap<String, String>();
        ResultSet rs = null;
        try {

            String query = "select a.attrelid,a.attname ,d.objsubid, d.description from pg_description d "
                    + "left join pg_attribute a on (d.objoid = a.attrelid and a.attnum = d.objsubid)"
                    + " where d.objoid = " + this.table.getOid() + ';';
            rs = conn.execSelectAndReturnRs(query);

            while (rs.next()) {
                comment = rs.getString("description");
                attributeName = rs.getString("attname");

                if (attributeName == null) {
                    attributeName = TABLE_DESC;
                }
                commentInfo.put(attributeName, comment);

            }

        } catch (SQLException exc) {
            GaussUtils.handleCriticalException(exc);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exc);

        } finally {
            conn.closeResultSet(rs);
        }
        return commentInfo;
    }

    /**
     * Gets the header.
     *
     * @return the header
     */
    @Override
    public String getHeader() {
        return table.getNameSpaceName() + '.' + table.getName() + '-' + table.getDatabaseName() + '@'
                + table.getServerName();
    }

    /**
     * Gets the unique ID.
     *
     * @return the unique ID
     */
    @Override
    public String getUniqueID() {
        return table.getOid() + '@' + table.getServerName() + "properties";
    }

    /**
     * Gets the type property.
     *
     * @param props the props
     * @param tabletype the tabletype
     * @return the type property
     */
    private void getTypeProperty(List<String[]> props, String tabletype) {
        switch (getEnumTableType(tabletype)) {
            case UNLOGGED: {
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.TYEP_MSG),
                        MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_UNLOGGED)).getProp());
                break;
            }
            case TEMPORARY: {
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.TYEP_MSG),
                        MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_TEMPORARY)).getProp());
                break;
            }
            case PERMANENT: {
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.TYEP_MSG),
                        MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_NORMAL)).getProp());
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum TableType.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private enum TableType {

        UNLOGGED, TEMPORARY, PERMANENT;

    }

    /**
     * Gets the enum table type.
     *
     * @param tbltype the tbltype
     * @return the enum table type
     */
    private TableType getEnumTableType(String tbltype) {
        if ("u".equals(tbltype)) {
            return TableType.UNLOGGED;
        } else if ("t".equals(tbltype)) {
            return TableType.TEMPORARY;
        } else {
            return TableType.PERMANENT;
        }

    }

    /**
     * Gets the table option property.
     *
     * @param rs the rs
     * @param props the props
     * @return the table option property
     * @throws SQLException the SQL exception
     */
    private StringBuilder getTableOptionProperty(ResultSet rs, List<String[]> props) throws SQLException {
        String optionsStr = rs.getString("options");
        String[] optionsArr = optionsStr != null ? optionsStr.split(",") : null;
        StringBuilder sbOpts = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        if (null != optionsArr && optionsArr.length > 0) {
            int index = 0;
            int optLen = optionsArr.length;
            String[] opts = null;
            for (; index < optLen; index++) {
                if (optionsArr[index] != null && optionsArr[index].contains("=")) {
                    opts = optionsArr[index].split("=");
                    String filterTableOptionProperty = splitTableOptionProperty(opts[0]);
                    if (null != filterTableOptionProperty) {
                        props.add(new ServerProperty(filterTableOptionProperty, opts[1]).getProp());
                    }

                } else {
                    sbOpts.append(optionsArr[index]);
                }
            }
        }
        return sbOpts;
    }

    /**
     * Split table option property.
     *
     * @param options the options
     * @return the string
     */
    private String splitTableOptionProperty(String options) {
        switch (options) {
            case "orientation": {
                return MessageConfigLoader.getProperty(IMessagesConstants.TABLEPROPERTIES_OPTIONS_ORIENTATION);
            }
            case "fillfactor": {
                return MessageConfigLoader.getProperty(IMessagesConstants.TABLEPROPERTIES_OPTIONS_FILLFACTOR);
            }
            case "compression": {
                return MessageConfigLoader.getProperty(IMessagesConstants.TABLEPROPERTIES_OPTIONS_COMPRESSION);
            }
            case "MAX_Batchrow": {
                return MessageConfigLoader.getProperty(IMessagesConstants.TABLEPROPERTIES_OPTIONS_MAX_BATCHROW);
            }
            case "Partial_cluster_rows": {
                return MessageConfigLoader.getProperty(IMessagesConstants.TABLEPROPERTIES_OPTIONS_PARTIAL_CLUSTER_ROWS);
            }
            case "version": {
                return MessageConfigLoader.getProperty(IMessagesConstants.VERSION);
            }
            default: {
                return null;
            }
        }

    }

    /**
     * Gets the constrain columnlist.
     *
     * @param conn the conn
     * @return the constrain columnlist
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private List<ConstraintInfo> getConstrainColumnlist(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {

        String columnslist = ConstraintMetaDataUtils.getTableConstraintQry(this.table.getOid());

        ConstraintInfo info = null;
        List<ConstraintInfo> consInfoList = new ArrayList<ConstraintInfo>(5);
        ResultSet rs = null;
        try {

            rs = conn.execSelectAndReturnRs(columnslist);
            boolean hasNext = rs.next();
            while (hasNext) {
                info = new ConstraintInfo();
                info.setConstraintName(rs.getString("constraintname"));
                info.setColumns(rs.getString("columnlist"));
                info.setConstraintExpr(rs.getString("const_def"));
                info.setConstraintType(rs.getString("constrainttype"));
                info.setTablespace(rs.getString("tablespace"));
                Namespace ns = this.table.getDatabase().getNameSpaceById(rs.getLong("namespaceid"));
                info.setConsSchema(ns.getDisplayName());
                info.setDeferred(rs.getBoolean("deferred"));
                consInfoList.add(info);
                hasNext = rs.next();
            }

        } catch (SQLException ex) {
            GaussUtils.handleCriticalException(ex);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, ex);

        } finally {
            conn.closeResultSet(rs);

        }
        return consInfoList;
    }

    /**
     * Gets the column meta data.
     *
     * @param dbcon the dbcon
     * @return the column meta data
     */
    private List<ColumnMetaData> getColumnMetaData(DBConnection dbcon) {

        List<ColumnMetaData> listCol = this.table.getColumnMetaDataList();

        return listCol;
    }

    /**
     * Gets the index meta data.
     *
     * @param dbcon the dbcon
     * @return the index meta data
     */
    private List<IndexMetaData> getIndexMetaData(DBConnection dbcon) {
        List<IndexMetaData> listIndex = this.table.getIndexMetaDataList();
        return listIndex;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    public Database getDatabase() {
        return this.table.getDatabase();
    }
}
