/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.files;

import java.nio.file.attribute.AclEntryPermission;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSFilePermission.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author s00428892
 * @version [DataStudio 8.0.2, 24 Apr, 2020]
 * @since 24 Apr, 2020
 */
public class DSFilePermission {
    // select ACL permissions
    // Permission to delete the file.
    private static final Set<AclEntryPermission> DEFAULT_PERMISSIONS = EnumSet.of(AclEntryPermission.DELETE,
            // Permission to delete a file or directory within a directory.
            AclEntryPermission.DELETE_CHILD,
            // Permission to modify the file's data.
            AclEntryPermission.WRITE_DATA,
            // The ability to write (non-acl) file attributes.
            AclEntryPermission.WRITE_ATTRIBUTES,
            // Permission to write the named attributes of a file.
            AclEntryPermission.WRITE_NAMED_ATTRS,
            // Permission to append data to a file.
            AclEntryPermission.APPEND_DATA,
            // Permission to access file locally at the server with synchronous
            // reads and writes.
            AclEntryPermission.SYNCHRONIZE,
            // The ability to read (non-acl)file attributes.
            AclEntryPermission.READ_ATTRIBUTES,
            // Permission to read the data of the file.
            AclEntryPermission.READ_DATA,
            // Permission to read the named attributes of a file.
            AclEntryPermission.READ_NAMED_ATTRS,
            // Permission to read the ACL attribute.
            AclEntryPermission.READ_ACL);
    
    /**
     * gets the Default file Permission
     * 
     * @return DEFAULT_PERMISSIONS the DEFAULT_PERMISSIONS
     */
    public static Set<AclEntryPermission> getDefaultPermission() {
        return new HashSet<AclEntryPermission>(DEFAULT_PERMISSIONS);
    }
}
