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

package org.opengauss.mppdbide.gauss.sqlparser.parser;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import org.opengauss.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TCustomASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: AbstractStmtParser
 *
 * @since 3.0.0
 */
public abstract class AbstractStmtParser {

    /**
     * Gets the AST parser.
     *
     * @param tokenStr the token str
     * @param astParserMap the ast parser map
     * @return the AST parser
     */
    public AbstractASTNodeParser<TCustomASTNode> getASTParser(String tokenStr, Map<String, Class<?>> astParserMap) {
        Class<?> parseNodeClass = astParserMap.get(getTokenLowerCase(tokenStr));
        if (null == parseNodeClass) {
            MPPDBIDELoggerUtility.error("No class associated with parser node" + tokenStr);
            AbstractASTNodeParser lAbstractASTNodeParser = getDefaultNodeParser();
            return lAbstractASTNodeParser;
        }
        AbstractASTNodeParser<TCustomASTNode> processor = null;
        try {
            processor = (AbstractASTNodeParser<TCustomASTNode>) parseNodeClass.newInstance();
            processor.setKeywordList(astParserMap.keySet());
        } catch (InstantiationException | IllegalAccessException exception) {
            MPPDBIDELoggerUtility.error("Exception occured while getting CustomNode processor");
        }
        return processor;
    }

    /**
     * overload method to return the custom node parser
     * 
     * @return CTEASTNodeParser CTE Node parser
     */
    protected CTEASTNodeParser getDefaultNodeParser() {
        return new CTEASTNodeParser();
    }

    private String getTokenLowerCase(String tokenStr) {
        if (null == tokenStr) {
            return null;
        }
        return tokenStr.toLowerCase();
    }

    /**
     * Gets the custom sql statement.
     *
     * @return the custom sql statement
     */
    public abstract TCustomSqlStatement getCustomSqlStatement();

    /**
     * Prepare stmt object.
     *
     * @param stmtTokenListBean the stmt token list bean
     * @return the t custom sql statement
     */
    public TCustomSqlStatement prepareStmtObject(SQLStmtTokenListBean stmtTokenListBean) {

        List<ISQLTokenData> allTokenList = stmtTokenListBean.getSqlTokenData();

        ListIterator<ISQLTokenData> listIterator = allTokenList.listIterator();

        TCustomSqlStatement tsqlStmt = getCustomSqlStatement();

        // create the object from the
        String tokenString = getTokenString(listIterator);

        AbstractASTNodeParser<TCustomASTNode> astParser = getASTParser(tokenString, getAstNodeParserMap());

        TCustomASTNode prepareASTStmtObject = null;
        TCustomASTNode preAstNode = null;

        while (null != astParser && listIterator.hasNext()) {

            prepareASTStmtObject = astParser.prepareASTStmtObject(listIterator);

            if (null == prepareASTStmtObject) {
                break;
            }

            if (null != preAstNode) {
                preAstNode.setNextNode(prepareASTStmtObject);
            }

            preAstNode = prepareASTStmtObject;

            tsqlStmt.addAstNode(tokenString, prepareASTStmtObject);
            tokenString = getTokenString(listIterator);

            if (null != tokenString && null != tsqlStmt.getCustomAstNode(getTokenLowerCase(tokenString))) {
                // multiple tokens found can't parse the statement.
                throw new GaussDBSQLParserException(
                        "Multiple AST Tokens found in the same statement. Can't parse the statement");
            }

            astParser = getASTParser(tokenString, getAstNodeParserMap());

        }

        return tsqlStmt;
    }

    private String getTokenString(ListIterator<ISQLTokenData> listIterator) {

        if (listIterator.hasNext()) {
            ISQLTokenData lSQLTokenData = listIterator.next();
            listIterator.previous();
            return lSQLTokenData.getTokenStr();
        }
        return null;
    }

    /**
     * Gets the ast node parser map.
     *
     * @return the ast node parser map
     */
    protected abstract Map<String, Class<?>> getAstNodeParserMap();
}
