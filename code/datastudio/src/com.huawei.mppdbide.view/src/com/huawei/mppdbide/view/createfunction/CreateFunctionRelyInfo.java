/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.createfunction;

import java.util.Arrays;
import java.util.List;

/**
 * Title: CreateFunctionRelyInfo for use
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2021-04-25]
 * @since 2021-04-25
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
