/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum ConstraintType.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public enum ConstraintType {
    CHECK_CONSTRSINT("CHECK"),
    UNIQUE_KEY_CONSTRSINT("UNIQUE"),
    PRIMARY_KEY_CONSTRSINT("PRIMARY KEY"),
    FOREIGN_KEY_CONSTRSINT("FOREIGN KEY"),
    PARTIAL_CLUSTER_KEY("PARTIAL CLUSTER KEY"),
    EXCLUSION_CONSTRSINT("EXCULSION KEY");
    /**
     * strType of Constraint type
     */
    public final String strType;

    ConstraintType(String strType) {
        this.strType = strType;
    }
    
    /**
     * convert string to constraint type
     * 
     * @param strType to convert string
     * @return ConstaintType the constraint type
     * */
    public static ConstraintType strTypeConvert(String strType) {
        try {
            return ConstraintType.valueOf(strType);
        } catch (IllegalArgumentException | NullPointerException validExp) {
            for (ConstraintType constraintType: ConstraintType.values()) {
                if (constraintType.strType.equals(strType)) {
                    return constraintType;
                }
            }
            return EXCLUSION_CONSTRSINT;
        }
    }
}
