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

import java.util.Arrays;
import java.util.List;

/**
 * Title: CreateFunctionRelyInfo for use
 *
 * @since 3.0.0
 */
public interface CreateFunctionRelyInfo {
    /**
     * define language when select procedure
     */
    String PROCEDURE = "PROCEDURE";
    /**
     * define language when select plpgsql
     */
    String LANGUAGE_PLP = "PLPGSQL";
    /**
     * define language when select sql
     */
    String LANGUAGE_SQL = "SQL";
    /**
     * define language when select c
     */
    String LANGUAGE_C = "C";
    /**
     * define lanaguage when select trigger
     */
    String LANGUAGE_TRIGGER = "TRIGGER";

    /**
     *
     * description: get schema name
     *
     * @return String schema name
     */
    String getSchameName();

    /**
     *
     * description:get line separator
     *
     * @return String line separator
     */
    default String getLineSeparator() {
        return System.lineSeparator();
    }

    /**
     *
     * description: get slash
     *
     * @return String the slash
     */
    default String getEscapeForwardSlash() {
        return "/";
    }

    /**
     *
     * description: get all supported types
     *
     * @return List<String> the supported types
     */
    List<String> getSupportTypes();

    /**
     *
     * description: get all support function language
     *
     * @return List<String> the support language
     */
    default List<String> getSupportLanguage() {
        return Arrays.asList(
                LANGUAGE_PLP,
                LANGUAGE_SQL,
                LANGUAGE_C);
    }

    /**
     *
     * description: get function body template
     *
     * @param String the language
     * @return String the body template
     */
    String getFunctionBodyTemplate(String language);

    /**
     *
     * description: run source code, this interface must be async!
     *
     * @param sourceCode the code
     * @param autoCompile if auto compile
     */
    void execute(String sourceCode, boolean autoCompile);
}
