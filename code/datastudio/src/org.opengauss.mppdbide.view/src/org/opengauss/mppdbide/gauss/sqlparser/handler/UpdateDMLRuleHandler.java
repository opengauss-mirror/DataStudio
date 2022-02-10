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

package org.opengauss.mppdbide.gauss.sqlparser.handler;

import org.eclipse.jface.text.rules.IToken;

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * 
 * Title: UpdateDMLRuleHandler
 *
 * @since 3.0.0
 */
public class UpdateDMLRuleHandler extends AbstractDMLRuleHandler {

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(ScriptBlockInfo curBlock, IToken token) {

        String lastKnownToken = curBlock.getLastKnownToken();

        if (curBlock.isNested() && getSQLToken(token).getType() == SQLTokenConstants.T_SQL_DML_SELECT
                && SQLFoldingConstants.SQL_BRACKET_START.equalsIgnoreCase(lastKnownToken)) {

            return true;
        } else if (isCaseStmtNested(token)) {
            return true;
        }

        return false;
    }

}
