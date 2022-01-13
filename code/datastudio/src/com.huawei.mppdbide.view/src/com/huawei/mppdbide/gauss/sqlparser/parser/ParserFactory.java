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

package com.huawei.mppdbide.gauss.sqlparser.parser;

import com.huawei.mppdbide.gauss.sqlparser.SQLDDLTypeEnum;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import com.huawei.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import com.huawei.mppdbide.gauss.sqlparser.parser.handler.ParserHandlerConfig;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ParserFactory
 *
 * @since 3.0.0
 */
public abstract class ParserFactory {

    /**
     * Gets the custom sql stmt.
     *
     * @param stmtTokenListBean the stmt token list bean
     * @return the custom sql stmt
     */
    public static TCustomSqlStatement getCustomSqlStmt(SQLStmtTokenListBean stmtTokenListBean) {
        TCustomSqlStatement prepareStmtObject = null;
        if (stmtTokenListBean.getStatementType() == SQLTokenConstants.T_SQL_DDL_CREATE) {
            if (!(stmtTokenListBean.getDdlType() == SQLDDLTypeEnum.FUNCTION
                    || stmtTokenListBean.getDdlType() == SQLDDLTypeEnum.PROCEDURE
                    || stmtTokenListBean.getDdlType() == SQLDDLTypeEnum.PACKAGE)) {
                return prepareStmtObject;
            }
        }
        AbstractStmtParser ruleHandle = ParserHandlerConfig.getInstance()
                .getRuleHandle(stmtTokenListBean.getStatementType());

        if (null != ruleHandle) {
            try {
                prepareStmtObject = ruleHandle.prepareStmtObject(stmtTokenListBean);
            } catch (GaussDBSQLParserException exception) {
                MPPDBIDELoggerUtility.error("error while parsing the statement");
            }
        }
        return prepareStmtObject;
    }

}
