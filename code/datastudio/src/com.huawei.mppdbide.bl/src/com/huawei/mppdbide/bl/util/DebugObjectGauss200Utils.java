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

package com.huawei.mppdbide.bl.util;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class DebugObjectGauss200Utils.
 * 
 */

public class DebugObjectGauss200Utils {
    // Create Template C Function

    /**
     * Gets the new function object template.
     *
     * @param objectRetType the object ret type
     * @param namespace the namespace
     * @param command the command
     * @return the new function object template
     */
    public static String getNewFunctionObjectTemplate(String objectRetType, String namespace, String command) {
        StringBuilder sqlTemplate = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        sqlTemplate.append("CREATE [OR REPLACE] FUNCTION ");

        if (!"".equals(namespace)) {
            sqlTemplate.append(namespace).append('.');
        }

        sqlTemplate.append("function_name ([ parameter datatype[,parameter datatype] ])");
        sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);
        sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);

        sqlTemplate.append("\tRETURNS " + objectRetType);

        sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);

        if ("c".equals(command)) {
            sqlTemplate.append("\tLANGUAGE C");
        } else {
            sqlTemplate.append("\tLANGUAGE SQL");
        }

        sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);
        sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);

        if ("c".equals(command)) {
            sqlTemplate.append("AS");
            sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);
            sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);

            sqlTemplate.append("\t\'/*iso file path and name*/\',$$/*function name*/$$");
            sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);
            sqlTemplate.append(MPPDBIDEConstants.ESCAPE_FORWARDSLASH);
        } else {
            sqlTemplate.append("AS $$");
            sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);
            sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);

            sqlTemplate.append("\t/*executable_section*/");
            sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);
            sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);

            sqlTemplate.append("$$");
            sqlTemplate.append(MPPDBIDEConstants.LINE_SEPARATOR);
            sqlTemplate.append(MPPDBIDEConstants.ESCAPE_FORWARDSLASH);
        }

        return sqlTemplate.toString();
    }
}
