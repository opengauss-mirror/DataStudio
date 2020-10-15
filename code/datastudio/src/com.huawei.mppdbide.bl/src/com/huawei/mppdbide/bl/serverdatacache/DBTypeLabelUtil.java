/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBTypeLabelUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public abstract class DBTypeLabelUtil {

    private static Map<OBJECTTYPE, String> objectTypeLabelMap = null;

    /**
     * Load object type label map.
     */
    private static void loadObjectTypeLabelMap() {

        objectTypeLabelMap = new HashMap<OBJECTTYPE, String>(20);
        objectTypeLabelMap.put(OBJECTTYPE.COLUMN_METADATA, "Column");
        objectTypeLabelMap.put(OBJECTTYPE.VIEW_COLUMN_METADATA, "Column");

        objectTypeLabelMap.put(OBJECTTYPE.PLSQLFUNCTION, "Function");

        objectTypeLabelMap.put(OBJECTTYPE.PROCEDURE, "Procedure");

        objectTypeLabelMap.put(OBJECTTYPE.NAMESPACE, "Schema");

        objectTypeLabelMap.put(OBJECTTYPE.FOREIGN_TABLE, "Foreign Table");

        objectTypeLabelMap.put(OBJECTTYPE.FOREIGN_TABLE_GDS, "Foreign Table");

        objectTypeLabelMap.put(OBJECTTYPE.FOREIGN_TABLE_HDFS, "Foreign Table");

        objectTypeLabelMap.put(OBJECTTYPE.FOREIGN_PARTITION_TABLE, "Foreign Partition Table");

        objectTypeLabelMap.put(OBJECTTYPE.PARTITION_TABLE, "Partition Table");

        objectTypeLabelMap.put(OBJECTTYPE.TABLEMETADATA, "Table");

        objectTypeLabelMap.put(OBJECTTYPE.VIEW_META_DATA, "View");

        objectTypeLabelMap.put(OBJECTTYPE.SQLFUNCTION, "SQL Function");

        objectTypeLabelMap.put(OBJECTTYPE.CFUNCTION, "C Function");

        objectTypeLabelMap.put(OBJECTTYPE.SEQUENCE_METADATA_GROUP, "Sequence");

        objectTypeLabelMap.put(OBJECTTYPE.CONSTRAINT, "Constraint");

        objectTypeLabelMap.put(OBJECTTYPE.INDEX_METADATA, "Index");

        objectTypeLabelMap.put(OBJECTTYPE.PARTITION_METADATA, "Partition");

        objectTypeLabelMap.put(OBJECTTYPE.USER_ROLE, "User/Role");
        objectTypeLabelMap.put(OBJECTTYPE.TYPEMETADATA, "Datatype");
        objectTypeLabelMap.put(OBJECTTYPE.KEYWORDS, "Keywords");

        objectTypeLabelMap.put(OBJECTTYPE.SYNONYM_METADATA_GROUP, "Synonym");
    }

    /**
     * Gets the type label.
     *
     * @param type the type
     * @return the type label
     */
    public static String getTypeLabel(OBJECTTYPE type) {
        if (null == objectTypeLabelMap) {
            loadObjectTypeLabelMap();
        }

        return objectTypeLabelMap.getOrDefault(type, type.toString());

    }

}
