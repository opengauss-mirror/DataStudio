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

package org.opengauss.mppdbide.gauss.format.stmtformatter.type;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;

/**
 * 
 * Title: AbstractStmtFormatter
 *
 * @since 3.0.0
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
