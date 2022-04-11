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

package org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata;

import java.util.List;

import org.opengauss.mppdbide.gauss.sqlparser.SQLDDLTypeEnum;
import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;

/**
 * Title: SQLStmtTokenListBean
 * 
 * @since 3.0.0
 */
public class SQLStmtTokenListBean {

    private List<ISQLTokenData> sqlTokenData = null;

    private int statementType = SQLTokenConstants.T_SQL_UNKNOWN;

    private SQLDDLTypeEnum ddlType = SQLDDLTypeEnum.UNKNOWN;

    /**
     * Gets the sql token data.
     *
     * @return the sql token data
     */
    public List<ISQLTokenData> getSqlTokenData() {
        return sqlTokenData;
    }

    /**
     * Sets the sql token data.
     *
     * @param sqlTokenData the new sql token data
     */
    public void setSqlTokenData(List<ISQLTokenData> sqlTokenData) {
        this.sqlTokenData = sqlTokenData;
    }

    /**
     * Gets the statement type.
     *
     * @return the statement type
     */
    public int getStatementType() {
        return statementType;
    }

    /**
     * Sets the statement type.
     *
     * @param statementType the new statement type
     */
    public void setStatementType(int statementType) {
        this.statementType = statementType;
    }

    /**
     * Gets the ddl type.
     *
     * @return the ddl type
     */
    public SQLDDLTypeEnum getDdlType() {
        return ddlType;
    }

    /**
     * Sets the ddl type.
     *
     * @param ddlType the new ddl type
     */
    public void setDdlType(SQLDDLTypeEnum ddlType) {
        this.ddlType = ddlType;
    }

}
