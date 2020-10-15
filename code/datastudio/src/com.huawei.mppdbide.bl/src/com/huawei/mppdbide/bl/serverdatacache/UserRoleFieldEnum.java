/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum UserRoleFieldEnum.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
