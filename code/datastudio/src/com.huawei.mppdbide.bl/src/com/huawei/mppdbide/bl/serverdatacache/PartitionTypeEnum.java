/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Title: enum class
 *
 * Description: The Class PartitionTypeEnum
 *
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public enum PartitionTypeEnum {
    BY_RANGE("r", "BY RANGE"),
    BY_INTERVAL("i", "BY INTERVAL"),
    BY_LIST("l", "BY LIST"),
    BY_HASH("h", "BY HASH");

    private String typeCode;
    private String typeName;

    PartitionTypeEnum(String typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    /**
     * Gets the type code
     *
     * @return String the type code
     */
    public String getTypeCode () {
        return typeCode;
    }

    /**
     * Gets the type name
     *
     * @return String the type name
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Gets the partition type map
     *
     * @return Map<String, String> the partition type map
     */
    public static Map<String, String> getPartitionTypeMap() {
        Map<String, String> partitionTypeMap = new HashMap<String, String>();
        for (PartitionTypeEnum partType : PartitionTypeEnum.values()) {
            partitionTypeMap.put(partType.getTypeCode(), partType.getTypeName());
        }
        return partitionTypeMap;
    }

    /**
     * Gets the partition type name array
     *
     * @return String[] the partition type name array
     */
    public static String[] getPartitionTypeNameArray() {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (PartitionTypeEnum part : PartitionTypeEnum.values()) {
            arrayList.add(part.getTypeName());
        }
        String[] partitionTypeNameArray = new String[arrayList.size()];
        partitionTypeNameArray = (String []) arrayList.toArray(partitionTypeNameArray);
        return partitionTypeNameArray;
    }
}