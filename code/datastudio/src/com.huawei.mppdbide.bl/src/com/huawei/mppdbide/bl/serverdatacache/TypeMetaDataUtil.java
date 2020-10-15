/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.ArrayList;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class TypeMetaDataUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
