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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Title: enum class
 *
 * Description: The Class PartitionTypeEnum
 *
 * @since 3.0.0
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
