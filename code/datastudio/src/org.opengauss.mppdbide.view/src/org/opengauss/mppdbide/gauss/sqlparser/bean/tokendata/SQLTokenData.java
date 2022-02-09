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

import org.eclipse.jface.text.rules.IToken;

/**
 * Title: SQLTokenData
 *
 * @since 3.0.0
 */
public class SQLTokenData implements ISQLTokenData {
    private IToken token = null;

    private int tokenOffset = 0;

    private int tokenLength = 0;

    private String tokenStr = null;

    private SQLStmtTokenListBean subTokenBean = null;

    /**
     * Gets the token.
     *
     * @return the token
     */
    @Override
    public IToken getToken() {
        return token;
    }

    /**
     * Sets the token.
     *
     * @param token the new token
     */
    @Override
    public void setToken(IToken token) {
        this.token = token;
    }

    /**
     * Gets the token offset.
     *
     * @return the token offset
     */
    @Override
    public int getTokenOffset() {
        return tokenOffset;
    }

    /**
     * Sets the token offset.
     *
     * @param tokenOffset the new token offset
     */
    @Override
    public void setTokenOffset(int tokenOffset) {
        this.tokenOffset = tokenOffset;
    }

    /**
     * Gets the token length.
     *
     * @return the token length
     */
    @Override
    public int getTokenLength() {
        return tokenLength;
    }

    /**
     * Sets the token length.
     *
     * @param tokenLength the new token length
     */
    @Override
    public void setTokenLength(int tokenLength) {
        this.tokenLength = tokenLength;
    }

    @Override
    public String getTokenStr() {
        return tokenStr;
    }

    @Override
    public void setTokenStr(String tokenStr) {
        this.tokenStr = tokenStr;
    }

    @Override
    public SQLStmtTokenListBean getSubTokenBean() {
        return subTokenBean;
    }

    @Override
    public void setSubTokenBean(SQLStmtTokenListBean subTokenBean) {
        this.subTokenBean = subTokenBean;
    }

}
