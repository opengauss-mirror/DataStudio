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

package com.huawei.mppdbide.gauss.sqlparser.bean.tokendata;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.stmtbeanif.StatementBeanIf;

/**
 * Title: ISQLTokenData
 *
 * @since 3.0.0
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