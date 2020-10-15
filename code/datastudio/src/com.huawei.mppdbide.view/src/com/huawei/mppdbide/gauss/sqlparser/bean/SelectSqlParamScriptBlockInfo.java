/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean;

import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * 
 * Title: SelectSqlParamScriptBlockInfo
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
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
