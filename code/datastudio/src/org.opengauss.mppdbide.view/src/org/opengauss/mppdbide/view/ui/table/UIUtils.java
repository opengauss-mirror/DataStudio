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

package org.opengauss.mppdbide.view.ui.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.IndexedColumnExpr;
import org.opengauss.mppdbide.bl.serverdatacache.IndexedColumnType;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableOrientation;
import org.opengauss.mppdbide.bl.serverdatacache.Tablespace;
import org.opengauss.mppdbide.bl.serverdatacache.TablespaceType;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIUtils.
 *
 * @since 3.0.0
 */
public final class UIUtils {

    private static Map<String, Boolean> scaleMap = new HashMap<String, Boolean>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    private static Map<String, Boolean> precisionMap = new HashMap<String, Boolean>(
            MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

    static {
        initalizeScaleMapper();
        initializePrecisionMapper();
    }

    /**
     * Gets the columnwise string.
     *
     * @param table the table
     * @param index the index
     * @param isConstraintsNotUpdated the is constraints not updated
     * @return the columnwise string
     */
    public static String getColumnwiseString(Table table, int index, boolean isConstraintsNotUpdated) {
        StringBuilder columnListStr = new StringBuilder(512);
        TableItem col = null;
        TableItem[] items = table.getItems();
        int tblLen = items.length;
        for (int i = 0; i < tblLen; i++) {
            col = items[i];
            if (isConstraintsNotUpdated) {
                columnListStr.append(ServerObject.getQualifiedObjectName(col.getText(index)));
            } else {
                columnListStr.append(col.getText(index));

            }
            columnListStr.append(", ");
        }

        int len = columnListStr.length();
        if (len > 0) {
            columnListStr.setLength(len - 2);
        }

        return columnListStr.toString();
    }

    /**
     * Enable disable scale field for datatype.
     *
     * @param datatype the datatype
     * @return true, if successful
     */
    public static boolean enableDisableScaleFieldForDatatype(String datatype) {
        return scaleMap.get(datatype);

    }

    /**
     * Initalize scale mapper.
     */
    public static void initalizeScaleMapper() {
        scaleMap.put("char", false);
        scaleMap.put("varchar", false);
        scaleMap.put("text", false);
        scaleMap.put("integer", true);
        scaleMap.put("smallint", false);
        scaleMap.put("bigint", false);
        scaleMap.put("daterange", false);
        scaleMap.put("date", false);
        scaleMap.put("money", false);
        scaleMap.put("numeric", true);
        scaleMap.put("decimal", true);
        scaleMap.put("double precision", false);
        scaleMap.put("real", false);
        scaleMap.put("interval", false);
        scaleMap.put("time without time zone", false);
        scaleMap.put("timestamp without time zone", false);
        scaleMap.put("boolean", false);
        scaleMap.put("bit", false);
        scaleMap.put("box", false);
        scaleMap.put("bytea", false);
        scaleMap.put("cidr", false);
        scaleMap.put("circle", false);
        scaleMap.put("inet", false);
        scaleMap.put("lseg", false);
        scaleMap.put("macaddr", false);
        scaleMap.put("path", false);
        scaleMap.put("point", false);
        scaleMap.put("polygon", false);
        scaleMap.put("time with time zone", false);
        scaleMap.put("timestamp with time zone", false);
        scaleMap.put("tsquery", false);
        scaleMap.put("tsvector", false);
        scaleMap.put("txid_snapshot", false);
        scaleMap.put("uuid", false);
        scaleMap.put("varbit", false);
        scaleMap.put("xml", false);
        scaleMap.put("smallserial", false);
        scaleMap.put("serial", false);
        scaleMap.put("bigserial", false);
        scaleMap.put("clob", false);
        scaleMap.put("blob", false);
        // mapping orc data types
        initalizeScaleMapperForORC();

    }

    /**
     * Initalize scale mapper for ORC.
     */
    private static void initalizeScaleMapperForORC() {
        scaleMap.put("nchar", false);
        scaleMap.put("character", false);
        scaleMap.put("clob", false);
        scaleMap.put("character varying", false);
        scaleMap.put("nvarchar2", false);
        scaleMap.put("varchar2(n)", false);
        scaleMap.put("tinyint", false);
        scaleMap.put("int", false);
        scaleMap.put("binary double", false);
        scaleMap.put("numrange", false);
        scaleMap.put("funcver", false);
        scaleMap.put("bool", false);
        scaleMap.put("smalldatetime", false);
        scaleMap.put("oid", false);
    }

    /**
     * Enable disable precision field for datatype.
     *
     * @param datatype the datatype
     * @return true, if successful
     */
    public static boolean enableDisablePrecisionFieldForDatatype(String datatype) {

        return precisionMap.get(datatype);
    }

    /**
     * Initialize precision mapper.
     */
    public static void initializePrecisionMapper() {
        precisionMap.put("char", true);
        precisionMap.put("varchar", true);
        precisionMap.put("text", false);
        precisionMap.put("integer", true);
        precisionMap.put("smallint", false);
        precisionMap.put("bigint", false);
        precisionMap.put("daterange", false);
        precisionMap.put("date", false);
        precisionMap.put("money", false);
        precisionMap.put("numeric", true);
        precisionMap.put("decimal", true);
        precisionMap.put("double precision", false);
        precisionMap.put("real", false);
        precisionMap.put("interval", false);
        precisionMap.put("time without time zone", false);
        precisionMap.put("timestamp without time zone", false);
        precisionMap.put("boolean", false);
        precisionMap.put("bit", true);
        precisionMap.put("box", false);
        precisionMap.put("bytea", false);
        precisionMap.put("cidr", false);
        precisionMap.put("circle", false);
        precisionMap.put("inet", false);
        precisionMap.put("lseg", false);
        precisionMap.put("macaddr", false);
        precisionMap.put("path", false);
        precisionMap.put("point", false);
        precisionMap.put("polygon", false);
        precisionMap.put("time with time zone", false);
        precisionMap.put("timestamp with time zone", false);
        precisionMap.put("tsquery", false);
        precisionMap.put("tsvector", false);
        precisionMap.put("txid_snapshot", false);
        precisionMap.put("uuid", false);
        precisionMap.put("varbit", true);
        precisionMap.put("xml", false);
        precisionMap.put("smallserial", false);
        precisionMap.put("serial", false);
        precisionMap.put("bigserial", false);
        precisionMap.put("clob", false);
        precisionMap.put("blob", false);
        // for orc datatype
        initializePrecisionMapperForORC();
    }

    /**
     * Initialize precision mapper for ORC.
     */
    private static void initializePrecisionMapperForORC() {
        precisionMap.put("nchar", false);
        precisionMap.put("character", false);
        precisionMap.put("clob", false);
        precisionMap.put("character varying", false);
        precisionMap.put("nvarchar2", false);
        precisionMap.put("varchar2(n)", false);
        precisionMap.put("tinyint", false);
        precisionMap.put("int", false);
        precisionMap.put("binary double", false);
        precisionMap.put("numrange", false);
        precisionMap.put("funcver", false);
        precisionMap.put("bool", false);
        precisionMap.put("smalldatetime", false);
        precisionMap.put("oid", false);
    }

    /**
     * Adds the selected col.
     *
     * @param fromtable the fromtable
     * @param totable the totable
     */
    public static void addSelectedCol(Table fromtable, Table totable) {
        if (fromtable.getSelectionCount() == 1) {
            int itemcount = totable.getItemCount();
            int index = 0;
            TableItem[] items = totable.getItems();
            TableItem tempItem = null;
            String slecedColText = fromtable.getItem(fromtable.getSelectionIndex()).getText(0);
            for (; index < itemcount; index++) {
                tempItem = items[index];
                if (null != tempItem && tempItem.getText(0).equalsIgnoreCase(slecedColText)) {
                    return;
                }
            }

            TableItem row = new TableItem(totable, SWT.NONE);
            row.setText(slecedColText);
        }
    }

    /**
     * Removes the selected col.
     *
     * @param table the table
     * @return the int
     */
    public static int removeSelectedCol(Table table) {
        int selected = table.getSelectionIndex();

        if (selected >= 0) {
            table.remove(selected);
        }

        return selected;
    }

    /**
     * Display namespace list.
     *
     * @param db the db
     * @param selNamespace the sel namespace
     * @param cmbAny the cmb any
     * @param addDefault the add default
     */
    public static void displayNamespaceList(Database db, String selNamespace, Combo cmbAny, boolean addDefault) {
        Iterator<Namespace> objectIter = null;
        int selIdx = 0;

        ServerObject namespace = null;
        boolean hasMore = false;

        cmbAny.removeAll();

        if (addDefault) {
            cmbAny.add("");
        }

        objectIter = db.getAllNameSpaces().iterator();
        hasMore = objectIter.hasNext();
        String name = null;
        while (hasMore) {
            namespace = objectIter.next();
            name = namespace.getName();
            cmbAny.add(name);
            if ((null != selNamespace) && (name.equals(selNamespace))) {
                selIdx = cmbAny.getItemCount() - 1;
            }
            hasMore = objectIter.hasNext();
        }
        cmbAny.select(selIdx);
    }

    /**
     * Display datatype list.
     *
     * @param types the types
     * @param typeMetaData the type meta data
     * @param cmbAny the cmb any
     */
    public static void displayDatatypeList(ArrayList<TypeMetaData> types, TypeMetaData typeMetaData, Combo cmbAny) {
        Iterator<TypeMetaData> objectIter = null;
        String dtName = "";
        TypeMetaData typename = null;
        boolean hasMore = false;
        int selectIdx = 0;
        int idx = 0;

        cmbAny.removeAll();

        objectIter = types.iterator();
        hasMore = objectIter.hasNext();

        if (typeMetaData != null && typeMetaData.getName().startsWith("_")) {
            dtName = typeMetaData.getName().substring(1, typeMetaData.getName().length());
        } else if (typeMetaData != null) {
            dtName = typeMetaData.getName();
        }

        if (!hasMore) {
            return;
        }

        String name = null;
        while (hasMore) {
            typename = objectIter.next();
            name = typename.getName();
            cmbAny.add(name);

            if (null != typeMetaData && name.equals(convertToDisplayDatatype(dtName))) {
                selectIdx = idx;
            }

            idx++;
            hasMore = objectIter.hasNext();
        }

        cmbAny.select(selectIdx);
    }

    /**
     * Convert to display datatype.
     *
     * @param datatypeName the datatype name
     * @return the string
     */
    public static String convertToDisplayDatatype(String datatypeName) {
        if (datatypeName != null) {
            switch (datatypeName) {
                case "bpchar": {
                    return "char";
                }
                case "int2": {
                    return "smallint";
                }
                case "int4": {
                    return "integer";
                }
                case "int8": {
                    return "bigint";
                }
                case "float8": {
                    return "double precision";
                }
                case "float4": {
                    return "real";
                }
                case "bool": {
                    return "boolean";
                }
                case "float": {
                    return "binary double";
                }
                case "time": {
                    return "time without time zone";
                }
                case "timestamp": {
                    return "timestamp without time zone";
                }
                case "timetz": {
                    return "time with time zone";
                }
                case "timestamptz": {
                    return "timestamp with time zone";
                }
                default: {
                    return datatypeName;
                }
            }
        }
        return "";
    }

    /**
     * Display tablename list.
     *
     * @param ns the ns
     * @param cmbAny the cmb any
     * @param addDefault the add default
     */
    public static void displayTablenameList(Namespace ns, Combo cmbAny, boolean addDefault) {
        cmbAny.removeAll();
        if (addDefault) {
            cmbAny.add("");
        }
        ArrayList<TableMetaData> tables = ns.getAllTablesForNamespace();
        for (TableMetaData table : tables) {
            cmbAny.add(table.getName());
        }
        cmbAny.select(-1);
    }

    /**
     * Display tablespace list handler.
     *
     * @param database the database
     * @param cmbAny the cmb any
     * @param addDefault the add default
     */
    public static void displayTablespaceListHandler(Database database, Combo cmbAny, boolean addDefault) {
        Iterator<Tablespace> objectIterator = null;
        Tablespace tablespace = null;
        boolean hasMoreRecrd = false;

        objectIterator = database.getServer().getTablespaceGroup().getSortedServerObjectList().iterator();
        hasMoreRecrd = objectIterator.hasNext();

        cmbAny.removeAll();

        String name = null;
        while (hasMoreRecrd) {
            tablespace = objectIterator.next();
            name = tablespace.getName();
            cmbAny.add(name);
            hasMoreRecrd = objectIterator.hasNext();
        }

        cmbAny.select(0);
    }

    /**
     * Gets the tablemetadata from combo.
     *
     * @param ns the ns
     * @param cmbSchema the cmb schema
     * @return the tablemetadata from combo
     */
    public static TableMetaData getTablemetadataFromCombo(Namespace ns, Combo cmbSchema) {
        int selectedNamespace = cmbSchema.getSelectionIndex();

        if (selectedNamespace >= 0) {
            if (ns.getTables().get(cmbSchema.getText()) != null) {
                return ns.getTables().get(cmbSchema.getText());
            }
        }
        return null;
    }

    /**
     * Gets the dtype from combo.
     *
     * @param namespace the namespace
     * @param db the db
     * @param cmbDatatype the cmb datatype
     * @return the dtype from combo
     */
    public static TypeMetaData getDtypeFromCombo(Namespace namespace, Database db, Combo cmbDatatype) {
        int selectionIndex = cmbDatatype.getSelectionIndex();

        if (selectionIndex >= 0) {

            if (null != namespace) {
                return namespace.getTypes().getList().get(selectionIndex);
            } else {

                return db.getDefaultDatatype().getList().get(selectionIndex);
            }
        }

        return null;
    }

    /**
     * Gets the orc dtype from combo.
     *
     * @param db the db
     * @param cmbDatatype the cmb datatype
     * @return the orc dtype from combo
     */
    public static TypeMetaData getOrcDtypeFromCombo(Database db, Combo cmbDatatype) {
        int selectionIndex = cmbDatatype.getSelectionIndex();
        if (selectionIndex >= 0) {
            return db.getORCDatatype().getList().get(selectionIndex);

        }

        return null;
    }

    /**
     * Display tablespace list.
     *
     * @param db the db
     * @param cmbAny the cmb any
     * @param addDefault the add default
     * @param orientationType the orientation type
     */
    public static void displayTablespaceList(Database db, Combo cmbAny, boolean addDefault,
            TableOrientation orientationType) {
        Iterator<Tablespace> objectIter = null;
        Tablespace tablespace = null;

        boolean hasMore = false;

        TablespaceType tspcType = TablespaceType.NORMAL;

        objectIter = db.getServer().getTablespaceGroup().getSortedServerObjectList().iterator();
        hasMore = objectIter.hasNext();

        cmbAny.removeAll();

        String name = null;

        int index = 0;
        int defaultIndex = -1;
        while (hasMore) {
            tablespace = objectIter.next();
            if (tspcType.equals(tablespace.getTablespaceType())) {
                name = tablespace.getName();
                cmbAny.add(name);
                if ("pg_default".equals(name)) {
                    defaultIndex = index;
                }
                index++;
            }
            hasMore = objectIter.hasNext();
        }

        if (defaultIndex != -1) {
            cmbAny.select(defaultIndex);
        }
    }

    /**
     * Display column list.
     *
     * @param tableDbObject the table db object
     * @param cmbAny the cmb any
     */
    public static void displayColumnList(TableMetaData tableDbObject, Combo cmbAny) {
        Iterator<ColumnMetaData> objectIter = null;
        ColumnMetaData column = null;
        boolean hasMore = false;

        cmbAny.removeAll();

        objectIter = tableDbObject.getColumns().getList().iterator();
        hasMore = objectIter.hasNext();

        if (!hasMore) {
            return;
        }

        String name = null;
        while (hasMore) {
            column = objectIter.next();
            name = column.getName();
            cmbAny.add(name);
            hasMore = objectIter.hasNext();
        }
        cmbAny.select(-1);
    }

    /**
     * Display datatype schema list.
     *
     * @param db the db
     * @param selNamespace the sel namespace
     * @param cmbAny the cmb any
     * @param addDefault the add default
     */
    public static void displayDatatypeSchemaList(Database db, String selNamespace, Combo cmbAny, boolean addDefault) {
        int selIdx = 0;
        cmbAny.removeAll();

        if (addDefault) {
            cmbAny.add("");
        }
        cmbAny.add("information_schema");
        cmbAny.add("pg_catalog");

        if ((null != selNamespace) && ("pg_catalog".equals(selNamespace))) {
            selIdx = 2;
        } else if ((null != selNamespace) && ("information_schema".equals(selNamespace))) {
            selIdx = 1;
        }
        cmbAny.select(selIdx);
    }

    /**
     * Gets the namespace for datatype.
     *
     * @param db the db
     * @param cmbSchema the cmb schema
     * @return the namespace for datatype
     */
    public static Namespace getNamespaceForDatatype(Database db, Combo cmbSchema) {
        int selectedNamespace = cmbSchema.getSelectionIndex();

        selectedNamespace -= 1;

        if (selectedNamespace >= 0) {
            ArrayList<Namespace> ns = db.getAllNameSpaces();
            // INITIALIZATION
            for (Namespace np : ns) {
                if (np.getName().equals(cmbSchema.getText())) {
                    return np;
                }
            }

            // INITIALIZATION
        }

        return null;
    }

    /**
     * Gets the datatype family for tooltip.
     *
     * @param datatypeName the datatype name
     * @return the datatype family for tooltip
     */
    public static String getDatatypeFamilyForTooltip(String datatypeName) {
        String[] numberDatatypes = {"int4", "integer", "numeric", "decimal"};
        String[] charDataTypes = {"bpchar", "character", "char"};
        String[] varcharDataTypes = {"character varying", "varchar"};
        String[] bitStringDataTypes = {"bit", "bit varying", "varbit"};
        String[] dateTimeDatatypes = {"date", "interval", "time", "time without time zone", "timetz",
            "time with time zone", "timestamptz", "timestamp with time zone"};
        Arrays.sort(numberDatatypes);
        Arrays.sort(charDataTypes);
        Arrays.sort(varcharDataTypes);
        Arrays.sort(bitStringDataTypes);
        Arrays.sort(dateTimeDatatypes);

        if (Arrays.binarySearch(numberDatatypes, datatypeName) >= 0) {
            return "NUMERIC";
        } else if (Arrays.binarySearch(charDataTypes, datatypeName) >= 0) {
            return "CHAR";
        } else if (Arrays.binarySearch(varcharDataTypes, datatypeName) >= 0) {
            return "VARCHAR";
        } else if (Arrays.binarySearch(bitStringDataTypes, datatypeName) >= 0) {
            return "BITSTRING";
        } else if (Arrays.binarySearch(dateTimeDatatypes, datatypeName) >= 0) {
            return "DATETIME";
        }

        return "";
    }

}
