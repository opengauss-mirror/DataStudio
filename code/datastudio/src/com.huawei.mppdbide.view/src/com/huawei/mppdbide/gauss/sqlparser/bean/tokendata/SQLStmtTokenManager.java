/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean.tokendata;

import java.util.LinkedList;
import java.util.List;

import com.huawei.mppdbide.gauss.sqlparser.bean.DMLParamScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLStmtTokenManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author s00428892
 * @version [DataStudio 8.0.2, 04 Apr, 2020]
 * @since 04 Apr, 2020
 */
public class SQLStmtTokenManager {

    private List<SQLStmtTokenListBean> sqlTokenStmtList = new LinkedList<SQLStmtTokenListBean>();

    /**
     * Gets the sql token stmt list.
     *
     * @return the sql token stmt list
     */
    public List<SQLStmtTokenListBean> getSqlTokenStmtList() {
        return sqlTokenStmtList;
    }

    /**
     * Sets the sql token stmt list.
     *
     * @param sqlTokenStmtList the new sql token stmt list
     */
    public void setSqlTokenStmtList(List<SQLStmtTokenListBean> sqlTokenStmtList) {
        this.sqlTokenStmtList = sqlTokenStmtList;
    }

    /**
     * Adds the SQL stmt token list bean.
     *
     * @param sqlStmtTokenListBean the sql stmt token list bean
     */
    public void addSQLStmtTokenListBean(SQLStmtTokenListBean sqlStmtTokenListBean) {
        this.sqlTokenStmtList.add(sqlStmtTokenListBean);
    }

    /**
     * Adds the SQL stmt token list bean.
     *
     * @param scriptBlock the script block
     */
    public void addSQLStmtTokenListBean(ScriptBlockInfo scriptBlock) {

        List<ISQLTokenData> allTokenList = scriptBlock.getAllTokenList();
        if (!allTokenList.isEmpty()) {
            SQLStmtTokenListBean lSQLStmtTokenListBean = new SQLStmtTokenListBean();
            lSQLStmtTokenListBean.setSqlTokenData(allTokenList);
            lSQLStmtTokenListBean.setStatementType(scriptBlock.getTokenType());
            if (scriptBlock instanceof DMLParamScriptBlockInfo) {
                lSQLStmtTokenListBean.setDdlType(((DMLParamScriptBlockInfo) scriptBlock).getDdlType());
            }

            sqlTokenStmtList.add(lSQLStmtTokenListBean);
        }

    }

}
