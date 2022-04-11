package org.opengauss.mppdbide.bl.mock.debug;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.opengauss.mppdbide.bl.serverdatacache.ProfileDiskUtility;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

public class ProfileDiskUtilityHelper extends ProfileDiskUtility {
    public String getUserProfileFolderName(String profileId) {
        return "PROFILE" + profileId;
    }
    
    public Path getConnectionProfilepath(String profileFolderName) {
        Path parentPath = Paths.get(".", MPPDBIDEConstants.PROFILE_BASE_PATH, profileFolderName);
        return parentPath;
    }


}
