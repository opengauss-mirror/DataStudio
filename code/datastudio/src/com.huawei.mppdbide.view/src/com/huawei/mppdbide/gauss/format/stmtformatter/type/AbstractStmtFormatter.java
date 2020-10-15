/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.stmtformatter.type;

import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;

/**
 * 
 * Title: AbstractStmtFormatter
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 05-Dec-2019]
 * @since 05-Dec-2019
 */
public class AbstractStmtFormatter<E extends TCustomSqlStatement> {

    private FmtOptionsIf options = null;

    public FmtOptionsIf getOptions() {
        return options;
    }

    public void setOptions(FmtOptionsIf options) {
        this.options = options;
    }

    /**
     * stmt forma
     * 
     * @param stmt stmt to be formatted
     * @return returns the statement string
     */
    public String format(E stmt) {
        return doFormat(stmt);
    }

    /**
     * before stmt format
     * 
     * @param stmt stmt to be formatted
     */
    protected void beforeFormat(E stmt) {
    }

    /**
     * stmt format
     * 
     * @param stmt stmt to be formatted
     * @return returns the statement string
     */
    protected String doFormat(E stmt) {
        return null;
    }

    /**
     * after Stmt Format
     * 
     * @param stmt stmt to be formatted
     */
    protected void afterFormat(E stmt) {
    }

}
