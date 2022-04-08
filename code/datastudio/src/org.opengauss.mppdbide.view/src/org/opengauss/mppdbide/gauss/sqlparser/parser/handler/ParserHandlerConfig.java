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

package org.opengauss.mppdbide.gauss.sqlparser.parser.handler;

import java.util.HashMap;
import java.util.Map;

import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;
import org.opengauss.mppdbide.gauss.sqlparser.parser.AbstractStmtParser;

/**
 * 
 * Title: RuleHandlerConfig
 * 
 * Description:RuleHandlerConfig
 *
 * @since 3.0.0
 */
public class ParserHandlerConfig {
    private Map<Integer, AbstractStmtParser> ruleLibrary = new HashMap<Integer, AbstractStmtParser>(10);

    private volatile boolean initilized = false;

    private static ParserHandlerConfig ruleHandlerConfig = new ParserHandlerConfig();

    /**
     * Gets the single instance of RuleHandlerConfig.
     *
     * @return single instance of RuleHandlerConfig
     */
    public static ParserHandlerConfig getInstance() {

        return ruleHandlerConfig;
    }

    private ParserHandlerConfig() {
    }

    /**
     * Gets the rule handle.
     *
     * @param type the type
     * @return the rule handle
     */
    public AbstractStmtParser getRuleHandle(int type) {
        return ruleLibrary.get(type);

    }

    /**
     * Adds the rule handle.
     *
     * @param type the type
     * @param stmtParser the stmt parser
     */
    public void addRuleHandle(int type, AbstractStmtParser stmtParser) {
        ruleLibrary.put(type, stmtParser);
    }

    /**
     * Checks if is initilized.
     *
     * @return true, if is initilized
     */
    public boolean isInitilized() {
        return initilized;
    }

    /**
     * Sets the initilized.
     *
     * @param initilized the new initilized
     */
    public void setInitilized(boolean initilized) {
        this.initilized = initilized;
    }

}
