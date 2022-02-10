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

package org.opengauss.mppdbide.bl.serverdatacache;

import java.util.ArrayList;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class TypeMetaDataUtil.
 * 
 */

public class TypeMetaDataUtil {

    /**
     * Gets the data type from namespace.
     *
     * @param datatypeOid the datatype oid
     * @param nsList the ns list
     * @return the data type from namespace
     */
    public static String getDataTypeFromNamespace(int datatypeOid, ArrayList<Namespace> nsList) {
        String convertedDataType = null;

        if (nsList != null) {
            for (Namespace ns : nsList) {
                convertedDataType = getDataTypeFromNamespace(datatypeOid, ns);
                if (convertedDataType != null) {
                    break;
                }
            }
        }

        if (convertedDataType == null) {
            convertedDataType = MPPDBIDEConstants.UNKNOWN_DATATYPE_STR;
        }

        return convertedDataType;
    }

    /**
     * Gets the data type from namespace.
     *
     * @param rettype the rettype
     * @param ns the ns
     * @return the data type from namespace
     */
    private static String getDataTypeFromNamespace(int rettype, Namespace ns) {
        if (ns != null) {
            TypeMetaData tmd = ns.getTypeByOid(rettype);
            if (tmd != null) {
                return tmd.getName();
            }
        }

        return null;
    }
}
