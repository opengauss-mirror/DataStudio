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

package com.huawei.mppdbide.view.createfunction;

import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: CreateFunctionUiData for use
 *
 * @since 3.0.0
 */
public class CreateFunctionUiData {
    /**
     * Title: ErrType for use
     * Description: this use to show err msg of ui param
     */
    public enum ErrType {
        ERR_SUCCESS("success"),
        ERR_FUNCNAME(IMessagesConstants.CREATE_FUNCTION_UI_ERR_FUNC_NAME),
        ERR_FUNCBODY(IMessagesConstants.CREATE_FUNCTION_UI_ERR_FUNC_BODY);

        /**
         * The error message string
         */
        public final String errMsg;
        ErrType(String errMsg) {
            this.errMsg = errMsg;
        }

        /**
         * Gets property
         *
         * @param ErrType the error type
         * @return String the error string
         */
        public String getProperty(ErrType errType) {
            return MessageConfigLoader.getProperty(errType.errMsg);
        }
    }

    private CreateFunctionRelyInfo relyInfo;
    private String functionName;
    private boolean functionNameCase;
    private String language;
    private String functionReturnType;
    private List<List<String>> paramList;
    private String functionBody;

    public CreateFunctionUiData(
            CreateFunctionRelyInfo relyInfo,
            String functionName,
            boolean functionNameCase,
            String language,
            String functionReturnType,
            List<CreateFunctionParam> paramList,
            String functionBody) {
        this.relyInfo = relyInfo;
        this.functionName = functionName;
        this.functionNameCase = functionNameCase;
        this.language = language;
        this.functionReturnType = functionReturnType;
        this.paramList = new ArrayList<>();
        for (CreateFunctionParam param: paramList) {
            this.paramList.add(new ArrayList<String>(param.getDatas()));
        }
        this.functionBody = functionBody;
    }

    /**
     * Check if is valid
     *
     * @return ErrType the error type
     */
    public ErrType valid() {
        if ("".equals(this.functionName)) {
            return ErrType.ERR_FUNCNAME;
        }
        if ("".equals(this.functionBody)) {
            return ErrType.ERR_FUNCBODY;
        }
        return ErrType.ERR_SUCCESS;
    }

    private boolean isProcedure() {
        return CreateFunctionRelyInfo.PROCEDURE.equals(language);
    }

    private boolean isTrigger() {
        return CreateFunctionRelyInfo.LANGUAGE_TRIGGER.equals(language);
    }

    private String functionType() {
        return isProcedure() ? "PROCEDURE" : "FUNCTION";
    }

    /**
     * Get function define
     *
     * @return String the function define
     */
    public String getFunctionDefine() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("CREATE OR REPLACE " + functionType() + " ");
        sb.append(relyInfo.getSchameName());
        sb.append(".");
        if (functionNameCase) {
            sb.append("\"").append(functionName).append("\"");
        } else {
            sb.append(functionName);
        }
        sb.append(" (");
        sb.append(formatParam());
        sb.append(")");

        sb.append(relyInfo.getLineSeparator());

        if (!isProcedure()) {
            if (isTrigger()) {
                sb.append("\tRETURNS ").append(CreateFunctionRelyInfo.LANGUAGE_TRIGGER);
                sb.append(relyInfo.getLineSeparator());
            }
            if (!"".equals(functionReturnType)) {
                sb.append("\tRETURNS ").append(functionReturnType);
                sb.append(relyInfo.getLineSeparator());
            }
            sb.append("\tLANGUAGE "
                + (isTrigger() ? CreateFunctionRelyInfo.LANGUAGE_PLP : language));
            sb.append(relyInfo.getLineSeparator());
        }
        sb.append(functionBody);
        return sb.toString();
    }

    /**
     * Format params
     *
     * @return String the formatted param string
     */
    public String formatParam() {
        if (isTrigger()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (this.paramList.size() != 0) {
            sb.append(relyInfo.getLineSeparator());
        }

        int index = 0;
        for (List<String> param: this.paramList) {
            String name = param.get(0).trim();
            String mode = param.get(1).trim();
            String type = param.get(2).trim();
            if (!CreateFunctionParam.DEFAULT_PARAM_NAME.equals(name)) {
                sb.append(name);
                sb.append(" ");
            }
            sb.append(mode);
            sb.append(" ");
            sb.append(type);
            String defaultValue = param.get(3).trim();
            if (!"".equals(defaultValue)) {
                sb.append(" DEFAULT ");
                sb.append(defaultValue);
            }
            if (index != this.paramList.size() - 1) {
                sb.append(",");
                sb.append(relyInfo.getLineSeparator());
            }
            index += 1;
        }
        if (this.paramList.size() != 0) {
            sb.append(relyInfo.getLineSeparator());
        }

        return sb.toString();
    }
}
