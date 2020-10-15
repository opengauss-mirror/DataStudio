/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.erd.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: ERConstraint Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author sWX316469
 * @version [DataStudio 6.5.1, 17-Oct-2019]
 * @since 17-Oct-2019
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
     * @Author: z00518937
     * @Date: Dec 7, 2019
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
