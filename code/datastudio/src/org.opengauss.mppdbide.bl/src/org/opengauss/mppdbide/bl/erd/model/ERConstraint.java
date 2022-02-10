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
 
package org.opengauss.mppdbide.bl.erd.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: ERConstraint 
 */

public class ERConstraint extends AbstractERConstraint {

    @Override
    public void setConstraintInfo(ResultSet rs, List<AbstractERConstraint> constraints) throws SQLException {
        ERConstraint keyData = new ERConstraint();
        keyData.setConsType(getKeysTypeFullName(rs.getString(IERNodeConstants.OLAP_CONSTRAINT_TYPE)));
        keyData.setKeyColIndex(getKeyColIndexFormStr(rs.getString(IERNodeConstants.OLAP_COLUMN_LIST)));
        constraints.add(keyData);
    }

    /**
     * 
     * @Title: getKeyColIndexFormStr
     * @Description: transform the string colList to List<Long>
     * @param colList: the column id list string query from db.
     * @return keyColList : the column id list.
     *
     */
    public List<Long> getKeyColIndexFormStr(String colList) {
        List<Long> keyColList = new ArrayList<>();
        if (colList != null) {
            String colString = colList.substring(1, colList.length() - 1);
            String[] colIndexList = colString.split("\\" + MPPDBIDEConstants.COMMA_SEPARATE);
            for (String str : colIndexList) {
                keyColList.add(Long.valueOf(str));
            }
        }

        return keyColList;
    }
}
