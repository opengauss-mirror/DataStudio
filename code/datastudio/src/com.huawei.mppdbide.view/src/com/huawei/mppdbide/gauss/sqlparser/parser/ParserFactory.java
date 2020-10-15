/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Title: ParserFactory Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
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
