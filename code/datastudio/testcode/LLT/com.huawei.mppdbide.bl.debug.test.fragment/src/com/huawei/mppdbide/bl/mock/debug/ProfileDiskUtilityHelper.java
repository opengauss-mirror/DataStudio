package com.huawei.mppdbide.bl.mock.debug;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.huawei.mppdbide.bl.serverdatacache.ProfileDiskUtility;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

public class ProfileDiskUtilityHelper extends ProfileDiskUtility {
    public String getUserProfileFolderName(String profileId) {
        return "PROFILE" + profileId;
    }
    
    public Path getConnectionProfilepath(String profileFolderName) {
        Path parentPath = Paths.get(".", MPPDBIDEConstants.PROFILE_BASE_PATH, profileFolderName);
        return parentPath;
    }


}
