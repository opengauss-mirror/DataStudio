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

package org.opengauss.mppdbide.presentation.objectproperties;

import java.util.List;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintType;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.IndexedColumnExpr;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.Tablespace;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class FilterServerObjPropInfoUtility.
 * 
 * @since 3.0.0
 */
public class FilterServerObjPropInfoUtility {

    /**
     * Fetch info for row.
     *
     * @param serverObject the server object
     * @param numOfColumns the num of columns
     * @return the object[]
     */
    public static Object[] fetchInfoForRow(ServerObject serverObject, int numOfColumns) {

        switch (serverObject.getType()) {
            case COLUMN_METADATA: {
                Object[] colInfo = new Object[numOfColumns];
                colInfo[0] = ((ColumnMetaData) serverObject).getName();
                colInfo[1] = formColumnDatatypeInfo(serverObject);
                colInfo[2] = "" + !((ColumnMetaData) serverObject).isNotNull();
                colInfo[3] = ((ColumnMetaData) serverObject).getColDescription();
                return colInfo;
            }
            case CONSTRAINT: {
                Object[] consInfo = new Object[numOfColumns];
                consInfo[0] = ((ConstraintMetaData) serverObject).getName();
                consInfo[1] = "";
                consInfo[2] = getConstraintTypeString(((ConstraintMetaData) serverObject).getConstraintType());
                consInfo[3] = ((ConstraintMetaData) serverObject).getCheckConstraintExpr();
                consInfo[4] = ((ConstraintMetaData) serverObject).isDeferable();
                consInfo[5] = ((ConstraintMetaData) serverObject).getParent().getNamespace().getName();
                consInfo[6] = ((ConstraintMetaData) serverObject).getTableSpace();
                return consInfo;
            }
            case INDEX_METADATA: {
                Object[] indexInfo = new Object[numOfColumns];
                indexInfo[0] = ((IndexMetaData) serverObject).getName();
                indexInfo[1] = formIndexedColumns(serverObject);
                indexInfo[2] = ((IndexMetaData) serverObject).isUnique();
                indexInfo[3] = ((IndexMetaData) serverObject).getWhereExpr();
                indexInfo[4] = getTablespace(((IndexMetaData) serverObject).getTablespace());
                return indexInfo;
            }
            default: {
                break;
            }

        }

        return new Object[12];
    }

    /**
     * Form column datatype info.
     *
     * @param serverObject the server object
     * @return the object
     */
    private static Object formColumnDatatypeInfo(ServerObject serverObject) {

        String name = ((ColumnMetaData) serverObject).getDataType().getName();
        StringBuilder datatypeString = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        datatypeString.append(name);
        ((ColumnMetaData) serverObject).formPrecisionAndArrayDimensionForQuery(datatypeString);

        return datatypeString.toString();
    }

    /**
     * Gets the constraint type string.
     *
     * @param constraintType the constraint type
     * @return the constraint type string
     */
    private static Object getConstraintTypeString(ConstraintType constraintType) {

        if (constraintType == ConstraintType.CHECK_CONSTRSINT) {
            return 'c';
        } else if (constraintType == ConstraintType.UNIQUE_KEY_CONSTRSINT) {
            return 'u';
        } else {
            return 'p';
        }
    }

    /**
     * Gets the tablespace.
     *
     * @param tablespace the tablespace
     * @return the tablespace
     */
    private static Object getTablespace(Tablespace tablespace) {

        if (tablespace == null) {
            return "";
        }
        return tablespace.getName();

    }

    /**
     * Form indexed columns.
     *
     * @param serverObject the server object
     * @return the object
     */
    private static Object formIndexedColumns(ServerObject serverObject) {
        List<IndexedColumnExpr> columns = ((IndexMetaData) serverObject).getIndexedColumns();
        StringBuilder cols = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        for (IndexedColumnExpr indexCol : columns) {
            if (indexCol.getCol() == null) {
                cols.append(indexCol.getExpr());
                cols.append(',');
            } else {

                cols.append(indexCol.getCol().getName());
                cols.append(',');
            }
        }
        cols.deleteCharAt(cols.length() - 1);
        return cols.toString();
    }

}
