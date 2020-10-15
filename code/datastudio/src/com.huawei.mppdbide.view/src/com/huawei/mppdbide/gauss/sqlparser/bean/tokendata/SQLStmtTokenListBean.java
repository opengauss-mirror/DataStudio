/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean.tokendata;

import java.util.List;

import com.huawei.mppdbide.gauss.sqlparser.SQLDDLTypeEnum;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;

/**
 * Title: SQLStmtTokenListBean
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 30-Dec-2019]
 * @since 30-Dec-2019
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
