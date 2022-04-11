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

import java.util.LinkedList;
import java.util.List;

import org.opengauss.mppdbide.gauss.sqlparser.bean.DMLParamScriptBlockInfo;
import org.opengauss.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLStmtTokenManager.
 *
 * @since 3.0.0
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
