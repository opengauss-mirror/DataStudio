/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.handler;

import java.util.HashMap;
import java.util.Map;

import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.parser.AbstractStmtParser;

/**
 * 
 * Title: RuleHandlerConfig
 * 
 * Description:RuleHandlerConfig
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author sWX316469
 * @version [DataStudio 6.5.1, 15 Oct, 2019]
 * @since 15 Oct, 2019
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
