/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.files;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntry.Builder;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSFileAttributes.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSFileAttributes implements FileAttribute<List<AclEntry>> {
    private Set<AclEntryPermission> userChosenPermissions;

    /**
     * Instantiates a new DS file attributes.
     *
     * @param userChosenPermissions the user chosen permissions
     */
    public DSFileAttributes(Set<AclEntryPermission> userChosenPermissions) {
        this.userChosenPermissions = userChosenPermissions;
    }

    @Override
    public List<AclEntry> value() {
        // lookup user principal
        FileSystem fileSystem = FileSystems.getDefault();
        UserPrincipalLookupService userPrincipalLookupService = fileSystem.getUserPrincipalLookupService();
        UserPrincipal userPrincipal = null;
        try {
            if (userPrincipalLookupService != null) {
                // Choose current operating system user principal
                userPrincipal = userPrincipalLookupService
                        .lookupPrincipalByName(EnvirnmentVariableValidator.validateAndGetUserName());
            }
        } catch (IOException exception) {
            // No need to throw the exception because ACLbuilder is taken care
            // of the exception if the userprincipal is empty/null
            MPPDBIDELoggerUtility.error("Exception occured while fetching the userprincipal", exception);
        }

        // select ACL flags
        // Can be placed on a directory and indicates that the ACL entry should
        // be added to each new non-directory file created.
        Set<AclEntryFlag> flags = EnumSet.of(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT);
        // Can be placed / on a directory and indicates that the ACL entry
        // should be added to each new directory created.
        // build ACL entry
        Builder builder = AclEntry.newBuilder();
        builder.setFlags(flags);

        if (userChosenPermissions != null && userChosenPermissions.size() > 0) {
            // set user chosen permissions to the file
            builder.setPermissions(userChosenPermissions);
        } else {
            // set default list of permissions
            builder.setPermissions(DSFilePermission.getDefaultPermission());
        }

        if (userPrincipal != null) {
            builder.setPrincipal(userPrincipal);
        }

        // Explicitly grants access to a file or directory for the assigned user
        // principle.
        builder.setType(AclEntryType.ALLOW);

        AclEntry entry = builder.build();
        List<AclEntry> aclEntryList = new ArrayList<AclEntry>(1);
        aclEntryList.add(entry);

        return aclEntryList;
    }

    @Override
    public String name() {
        return "acl:acl";
    }

}
