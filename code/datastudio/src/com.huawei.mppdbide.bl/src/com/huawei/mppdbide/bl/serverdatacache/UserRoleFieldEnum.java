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
 * Description: The Enum UserRoleFieldEnum.
 * 
 */

public enum UserRoleFieldEnum {
    OID((byte) 1, "oid"), NAME((byte) 2, "name"), ROL_INHERIT((byte) 3, "rolInherit"),
    ROL_CREATE_ROLE((byte) 4, "rolCreateRole"), ROL_CREATE_DB((byte) 5, "rolCreateDb"),
    ROL_CAN_LOGIN((byte) 6, "rolCanLogin"), ROL_REPLICATION((byte) 7, "rolReplication"),
    ROL_AUDIT_ADMIN((byte) 8, "rolAuditAdmin"), ROL_SYSTEM_ADMIN((byte) 9, "rolSystemAdmin"),
    ROL_CONN_LIMIT((byte) 10, "rolConnLimit"), ROL_VALID_BEGIN((byte) 11, "rolValidBegin"),
    ROL_VALID_UNTIL((byte) 12, "rolValidUntil"), ROL_RES_POOL((byte) 13, "rolResPool"), COMMENT((byte) 14, "comment"),
    IS_LOCK((byte) 15, "isLock"), PARENTS((byte) 16, "parents");

    /**
     * The id.
     */
    public final byte id;

    /**
     * The field name.
     */
    public final String fieldName;

    /**
     * Instantiates a new user role field enum.
     *
     * @param id the id
     * @param fieldName the field name
     */
    private UserRoleFieldEnum(byte id, String fieldName) {
        this.id = id;
        this.fieldName = fieldName;
    }
}
