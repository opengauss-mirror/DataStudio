/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.util;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class DebugObjectGauss200Utils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
