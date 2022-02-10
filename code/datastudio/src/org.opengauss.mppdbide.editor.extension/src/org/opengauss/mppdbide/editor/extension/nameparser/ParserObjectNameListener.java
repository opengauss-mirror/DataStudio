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

package org.opengauss.mppdbide.editor.extension.nameparser;

import java.util.ArrayList;

import org.antlr.v4.runtime.ParserRuleContext;

import org.opengauss.mppdbide.parser.grammar.PostgresParser;
import org.opengauss.mppdbide.parser.grammar.PostgresParserBaseListener;

/**
 * Title: ParserObjectNameListener
 * 
 * Description:The listener interface for receiving parserObjectName events. The
 * class that is interested in processing a parserObjectName event implements
 * this interface, and the object created with that class is registered with a
 * component using the component's <code>addParserObjectNameListener<code>
 * method. When the parserObjectName event occurs, that object's appropriate
 * method is invoked.
 * 
 * @since 3.0.0
 */
public class ParserObjectNameListener extends PostgresParserBaseListener {
    private String objectType;
    private String objectName;
    private String schemaName;
    private String funcName;
    private ArrayList<String[]> args = new ArrayList<String[]>(5);
    private String objReturnType;

    @Override
    public void enterFunction_type(PostgresParser.Function_typeContext ctx) {
        objectType = ctx.getText();
    }

    @Override
    public void enterFunction_name(PostgresParser.Function_nameContext ctx) {
        objectName = ctx.getText();
    }

    @Override
    public void enterFunction_aguments(PostgresParser.Function_agumentsContext ctx) {

    }

    @Override
    public void enterSchema_name(PostgresParser.Schema_nameContext ctx) {
        schemaName = ctx.getText();
    }

    @Override
    public void enterFunct_name(PostgresParser.Funct_nameContext ctx) {
        funcName = ctx.getText();
    }

    @Override
    public void enterFunc_returns(PostgresParser.Func_returnsContext ctx) {
        objReturnType = ctx.getChild(1).getText();
    }

    @Override
    public void enterOne_funtion_argument(PostgresParser.One_funtion_argumentContext ctx) {
        String[] argsInfo = new String[3];
        argsInfo[1] = "IN";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParserRuleContext child = (ParserRuleContext) ctx.getChild(i);
            if (child.getClass().getName().contains("arg_nameContext")) {
                argsInfo[0] = child.getText();
            } else if (child.getClass().getName().contains("arg_modeContext")) {
                argsInfo[1] = child.getText();
            } else if (child.getClass().getName().contains("TypenameContext")) {
                argsInfo[2] = child.getText();
            }
        }
        args.add(argsInfo);
    }

    /**
     * Gets the object name.
     *
     * @return the object name
     */
    public String[] getObjectName() {

        return new String[] {objectType, objectName};
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name
     */
    public String getSchemaName() {

        return schemaName;
    }

    /**
     * Gets the func name.
     *
     * @return the func name
     */
    public String getFuncName() {

        return funcName;
    }

    /**
     * Gets the arguements.
     *
     * @return the arguements
     */
    public ArrayList<String[]> getArguements() {

        return args;
    }

    /**
     * Gets the return type.
     *
     * @return the return type
     */
    public String getreturnType() {

        return objReturnType;
    }

}
