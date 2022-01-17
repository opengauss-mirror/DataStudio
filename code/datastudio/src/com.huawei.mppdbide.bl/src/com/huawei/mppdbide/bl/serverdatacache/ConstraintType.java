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

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum ConstraintType.
 * 
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
