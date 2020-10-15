/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean.tokendata;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.stmtbeanif.StatementBeanIf;

/**
 * Title: ISQLTokenData Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 25-Dec-2019]
 * @since 25-Dec-2019
 */
public interface ISQLTokenData extends StatementBeanIf {
    /**
     * Gets the token.
     *
     * @return the token
     */
    IToken getToken();

    /**
     * Sets the token.
     *
     * @param token the new token
     */
    void setToken(IToken token);

    /**
     * Gets the token offset.
     *
     * @return the token offset
     */
    int getTokenOffset();

    /**
     * Sets the token offset.
     *
     * @param tokenOffset the new token offset
     */
    void setTokenOffset(int tokenOffset);

    /**
     * Gets the token length.
     *
     * @return the token length
     */
    int getTokenLength();

    /**
     * Sets the token length.
     *
     * @param tokenLength the new token length
     */
    void setTokenLength(int tokenLength);

    /**
     * Gets the token str.
     *
     * @return the token str
     */
    String getTokenStr();

    /**
     * Sets the token str.
     *
     * @param tokenStr the new token str
     */
    void setTokenStr(String tokenStr);

    /**
     * Gets the sub token bean.
     *
     * @return the sub token bean
     */
    SQLStmtTokenListBean getSubTokenBean();

    /**
     * Sets the sub token bean.
     *
     * @param subTokenBean the new sub token bean
     */
    void setSubTokenBean(SQLStmtTokenListBean subTokenBean);

}