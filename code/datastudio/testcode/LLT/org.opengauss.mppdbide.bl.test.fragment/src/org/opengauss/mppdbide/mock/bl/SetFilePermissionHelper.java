package org.opengauss.mppdbide.mock.bl;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

public class SetFilePermissionHelper
{
    
    private static AclEntryType aclEntryType = AclEntryType.ALLOW; 
    
    // select ACL permissions 
    private static final Set<AclEntryPermission> DEFAULT_PERMISSIONS = EnumSet.of(
            AclEntryPermission.DELETE, //Permission to delete the file.
            AclEntryPermission.DELETE_CHILD, //Permission to delete a file or directory within a directory.

            AclEntryPermission.WRITE_DATA, //Permission to modify the file's data.
            AclEntryPermission.WRITE_ATTRIBUTES, //The ability to write (non-acl) file attributes.
            AclEntryPermission.WRITE_NAMED_ATTRS, //Permission to write the named attributes of a file. 
            AclEntryPermission.APPEND_DATA, //Permission to append data to a file.
            AclEntryPermission.SYNCHRONIZE, //Permission to access file locally at the server with synchronous reads and writes.

            AclEntryPermission.READ_ATTRIBUTES, //The ability to read (non-acl) file attributes.
            AclEntryPermission.READ_DATA, //Permission to read the data of the file.
            AclEntryPermission.READ_NAMED_ATTRS, //Permission to read the named attributes of a file. 

            AclEntryPermission.READ_ACL //Permission to read the ACL attribute.
            );

    /**
     * Create file or folder with security permissions
     * @param path
     * @param isLogDir
     * @throws DatabaseOperationException
     */
    public Path createFileWithPermission(String path, boolean isLogDir, 
            final Set<AclEntryPermission> user_chosen_permissions, AclEntryType aclEntryType) throws DatabaseOperationException
    {
        this.aclEntryType = aclEntryType;
        //Convert string to nio path object 
        Path newPath = Paths.get(path);

        //Check if file already exists
        boolean fileExists = Files.exists(newPath);
        
        //If file already exists and it is not logs folder, confirm for overwriting the file.
        
        // Create file attribute with security permission
            DSFileAttributes fileAttributes = new DSFileAttributes(user_chosen_permissions);
            try
            {
                if(isLogDir)
                {
                    //If create log directory and logs folder does not exist, create it with security permissions.
                    if(!fileExists)
                    {
                        newPath = Files.createDirectory(newPath, fileAttributes);    
                    }
                }
                else
                {
                    // Create file with security permissions.
                    newPath = Files.createFile(newPath, fileAttributes);    
                }
                
            }
            catch (IOException exception)
            {
                try
                {
                    Files.deleteIfExists(newPath);
            } catch (IOException e1) {
                MPPDBIDELoggerUtility.trace("Error while deleting file in exception.");
            }
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.FILE_PERMISSION_ERROR),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.FILE_PERMISSION_ERROR, exception);
        }
        return newPath;
    }
    
    /**
     * Create file attributes
     *
     */
    private static final class DSFileAttributes implements FileAttribute<List<AclEntry>>
    {
        private Set<AclEntryPermission> user_chosen_permissions;
        
        protected DSFileAttributes(Set<AclEntryPermission> user_chosen_permissions)
        {
            this.user_chosen_permissions = user_chosen_permissions;
        }
        
        @Override
        public List<AclEntry> value()
        {
            // lookup user principal
            FileSystem fileSystem = FileSystems.getDefault();
            UserPrincipalLookupService userPrincipalLookupService = fileSystem
                    .getUserPrincipalLookupService();
            UserPrincipal userPrincipal = null;
            try
            {
                // Choose current operating system user principal
                userPrincipal = userPrincipalLookupService
                        .lookupPrincipalByName(System
                                .getProperty("user.name"));
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

            // select ACL flags
            Set<AclEntryFlag> flags = EnumSet.of(
                    AclEntryFlag.FILE_INHERIT, //Can be placed on a directory and indicates that the ACL entry should be added to each new non-directory file created.
                    AclEntryFlag.DIRECTORY_INHERIT //Can be placed on a directory and indicates that the ACL entry should be added to each new directory created.
                    );

            

            // build ACL entry
            Builder builder = AclEntry.newBuilder();
            builder.setFlags(flags);
            
            if(user_chosen_permissions != null && user_chosen_permissions.size() > 0)
            {
                //set user chosen permissions to the file
                builder.setPermissions(user_chosen_permissions);    
            }
            else
            {
                // set default list of permissions 
                builder.setPermissions(SetFilePermissionHelper.DEFAULT_PERMISSIONS);
            }
            
            
            builder.setPrincipal(userPrincipal);
            builder.setType(SetFilePermissionHelper.aclEntryType);//Explicitly grants access to a file or directory for the assigned user principle.

            AclEntry entry = builder.build();
            List<AclEntry> aclEntryList = new ArrayList<AclEntry>(1);
            aclEntryList.add(entry);

            return aclEntryList;
        }

        @Override
        public String name()
        {
            return "acl:acl";
        }
    }

}