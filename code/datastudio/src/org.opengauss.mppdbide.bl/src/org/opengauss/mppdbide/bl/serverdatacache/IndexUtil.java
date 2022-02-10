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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Iterator;

import org.opengauss.mppdbide.bl.serverdatacache.groups.OLAPObjectList;

/**
 * Title: IndexUtil
 * 
 * Description:
 *
 * @since 3.0.0
 */
public class IndexUtil {
    /**
     * Gets the column.
     *
     * @param columnList the column list
     * @param idx the idx
     * @return the column
     */
    private static ColumnMetaData getColumn(OLAPObjectList<ColumnMetaData> columnList, int idx) {
        Iterator<ColumnMetaData> itr = columnList.getList().iterator();
        boolean hasNext = itr.hasNext();
        while (hasNext) {
            ColumnMetaData col = itr.next();
            hasNext = itr.hasNext();
            if (col.getOid() == idx) {
                return col;
            }
        }

        return null;
    }

    /**
     * Convert to index.
     *
     * @param rs the rs
     * @param tableMetaData the table meta data
     * @param nameSpc the name spc
     * @param columnList the column list
     * @return the index meta data
     * @throws SQLException the SQL exception
     */
    public static IndexMetaData convertToIndex(ResultSet rs, TableMetaData tableMetaData, Namespace nameSpc,
            OLAPObjectList<ColumnMetaData> columnList) throws SQLException {
        long oid = rs.getLong("oid");
        String name = rs.getString("indexname");
        boolean hasNonHiddenCols = false;

        IndexMetaData index = getIndexObject(rs, tableMetaData, nameSpc, oid, name);

        String colListStr = rs.getString("cols");
        if (null != colListStr) {
            String[] colList = colListStr.trim().split(" ");

            int colIdxStrLen = colList.length;
            hasNonHiddenCols = getAllColumnForINdex(columnList, index, colList, colIdxStrLen);

            addIndexToTableMetadata(tableMetaData, hasNonHiddenCols, index);
        }

        getIndexFillFactor(rs, index);

        return index;
    }

    private static boolean getAllColumnForINdex(OLAPObjectList<ColumnMetaData> columnList, IndexMetaData index,
            String[] colList, int colIdxStrLen) {
        int colIdx = 0;
        boolean hasNonHiddenCols = false;
        String colIdxStr = null;
        for (int clmIndex = 0; clmIndex < colIdxStrLen; clmIndex++) {
            colIdxStr = colList[clmIndex];
            colIdx = Integer.parseInt(colIdxStr);
            if (colIdx < 0) {
                continue;
            }

            hasNonHiddenCols = addIndexInfo(columnList, index, colIdx);
        }
        return hasNonHiddenCols;
    }

    private static boolean addIndexInfo(OLAPObjectList<ColumnMetaData> columnList, IndexMetaData index, int colIdx) {
        boolean hasNonHiddenCols = false;
        if (colIdx == 0) {
            index.addExpr("<Expr>");
            hasNonHiddenCols = true;
        } else {
            ColumnMetaData col = getColumn(columnList, colIdx);
            if (null != col) {
                index.addColumn(col);
                hasNonHiddenCols = true;
            }
        }
        return hasNonHiddenCols;
    }

    private static void addIndexToTableMetadata(TableMetaData tableMetaData, boolean hasNonHiddenCols,
            IndexMetaData index) {
        /* Indexes on hidden column will be skipped */
        if (hasNonHiddenCols) {
            tableMetaData.addIndex(index);
        }
    }

    private static void getIndexFillFactor(ResultSet rs, IndexMetaData index) throws SQLException {
        String storageParams = rs.getString("reloptions");
        if (null != storageParams) {
            storageParams = Normalizer.normalize(storageParams, Form.NFKC);
            String[] storageParamList = storageParams.trim().split(",");
            String storageParam = null;
            int storageParamLen = storageParamList.length;
            for (int paramndex = 0; paramndex < storageParamLen; paramndex++) {
                storageParam = storageParamList[paramndex];
                if (storageParam.startsWith("fillfactor=")) {
                    String fillFactor = storageParam.substring(11);
                    index.setIndexFillFactor(Integer.parseInt(fillFactor));
                }
            }
        }
    }

    private static IndexMetaData getIndexObject(ResultSet rs, TableMetaData tableMetaData, Namespace nameSpc, long oid,
            String name) throws SQLException {
        IndexMetaData index = new IndexMetaData(oid, name);
        index.setTable(tableMetaData);
        index.setNamespace(nameSpc);
        index.setAccessMethId(rs.getLong("accessmethodid"));
        index.setUnique(rs.getBoolean("isunique"));
        index.setPrimary(rs.getBoolean("isprimary"));
        index.setExclusion(rs.getBoolean("isexclusion"));
        index.setImmediate(rs.getBoolean("isimmediate"));
        index.setLastClustered(rs.getBoolean("isclustered"));
        index.setCheckxmin(rs.getBoolean("checkmin"));
        index.setReady(rs.getBoolean("isready"));
        index.setIndexdeff(rs.getString("indexdef"));
        index.setLoaded(true);

        index.setTablespace(rs.getString("tablespace"));
        return index;
    }

}
