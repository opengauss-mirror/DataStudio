/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean.tokendata;

import org.eclipse.jface.text.rules.IToken;

/**
 * Title: SQLTokenData Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author sWX316469
 * @version [DataStudio 6.5.1, 11-Oct-2019]
 * @since 11-Oct-2019
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
