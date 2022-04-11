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

package org.opengauss.mppdbide.gauss.sqlparser.bean;

import org.opengauss.mppdbide.gauss.sqlparser.SQLToken;
import org.opengauss.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import org.opengauss.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * 
 * Title: SelectSqlParamScriptBlockInfo
 *
 * @since 3.0.0
 */
public class SelectSqlParamScriptBlockInfo extends ScriptBlockInfoImpl {

    /**
     * Instantiates a new select sql param script block info.
     *
     * @param parent the parent
     * @param token the token
     * @param abstractRuleHandler the abstract rule handler
     */
    public SelectSqlParamScriptBlockInfo(ScriptBlockInfo parent, SQLToken token, RuleHandlerIf abstractRuleHandler) {
        super(parent, token, abstractRuleHandler);
    }

}
