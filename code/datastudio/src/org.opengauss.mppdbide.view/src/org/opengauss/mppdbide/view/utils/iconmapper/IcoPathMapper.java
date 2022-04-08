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

package org.opengauss.mppdbide.view.utils.iconmapper;

import java.util.HashMap;

import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class IcoPathMapper.
 *
 * @since 3.0.0
 */
public class IcoPathMapper {
    private static final Object LOCK = new Object();
    private static volatile IcoPathMapper myObject;
    private HashMap<OBJECTTYPE, String> icoMap;

    private IcoPathMapper() {
        icoMap = new HashMap<OBJECTTYPE, String>(5);
        /* Add all the object type's icon locations to the hash map */
        icoMap.put(OBJECTTYPE.USER_NAMESPACE_GROUP, IiconPath.ICO_USER_NAMESPACE_GROUP);
        icoMap.put(OBJECTTYPE.SYSTEM_NAMESPACE_GROUP, IiconPath.ICO_SYSTEM_NAMESPACE_GROUP);
        icoMap.put(OBJECTTYPE.NAMESPACE, IiconPath.ICO_NAMESPACE);
        icoMap.put(OBJECTTYPE.TABLEMETADATA, IiconPath.ICO_TABLE);
        icoMap.put(OBJECTTYPE.VIEW_META_DATA, IiconPath.ICO_VIEW);
        icoMap.put(OBJECTTYPE.FOREIGN_TABLE_GDS, IiconPath.FOREIGN_TABLE_GDS);
        icoMap.put(OBJECTTYPE.FOREIGN_TABLE_HDFS, IiconPath.FOREIGN_TABLE_HDFS);
        icoMap.put(OBJECTTYPE.FOREIGN_PARTITION_TABLE, IiconPath.ICO_FOREIGN_PARTITION_TABLE);
        icoMap.put(OBJECTTYPE.PARTITION_TABLE, IiconPath.PARTITION_TABLE);
        icoMap.put(OBJECTTYPE.PLSQLFUNCTION, IiconPath.ICO_FUNCTIONPLSQL);
        icoMap.put(OBJECTTYPE.PROCEDURE, IiconPath.ICO_PROCEDURE);
        icoMap.put(OBJECTTYPE.SQLFUNCTION, IiconPath.ICO_FUNCTIONSQL);
        icoMap.put(OBJECTTYPE.ALIAS, IiconPath.ICO_ALIAS);
        icoMap.put(OBJECTTYPE.COLUMN_METADATA, IiconPath.ICO_COLUMN);
        icoMap.put(OBJECTTYPE.VIEW_COLUMN_METADATA, IiconPath.ICO_COLUMN);
        icoMap.put(OBJECTTYPE.SEQUENCE_METADATA_GROUP, IiconPath.SEQUENCE_OBJECT);
        icoMap.put(OBJECTTYPE.CFUNCTION, IiconPath.ICO_FUNCTIONC);
        icoMap.put(OBJECTTYPE.KEYWORDS, IiconPath.KEYWORD);
        icoMap.put(OBJECTTYPE.TYPEMETADATA, IiconPath.DATATYPE);
        icoMap.put(OBJECTTYPE.SYNONYM_METADATA_GROUP, IiconPath.ICO_SYNONYM);
    }

    private static IcoPathMapper getInstance() {
        if (null == myObject) {
            synchronized (LOCK) {
                if (null == myObject) {
                    myObject = new IcoPathMapper();
                }
            }
        }
        return myObject;
    }

    /**
     * Gets the image path for object.
     *
     * @param type the type
     * @return the image path for object
     */
    public static String getImagePathForObject(OBJECTTYPE type) {
        return IcoPathMapper.getInstance().icoMap.get(type);
    }
}
