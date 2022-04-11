package org.opengauss.mppdbide.mock.bl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.opengauss.mppdbide.bl.serverdatacache.ProfileMetaData;

public class ProfileMetaDataHelper extends ProfileMetaData
{
    private boolean throwDatabaseOperationException;
    private boolean throwDatabaseCriticalException;
    @Override
    public Map<String, ProfileInfo> getAllProfiles()
    {
        // need to fix the
        
      /*  // TODO Auto-generated method stub
        Map<String, String> map = new HashMap<String, String>(1);
        // TODO Auto-generated method stub
       
        map.put("newconnection", );

        return map;*/
        
        return super.getAllProfiles();
    }

    @Override
    public void addProfile(String name, String id, String path,String version)
    {
        // TODO Auto-generated method stub
        super.addProfile(name, id, path,version);
    }

    @Override
    public void deleteProfile(String name)
    {
        // TODO Auto-generated method stub
        super.deleteProfile(name);
    }

    public ProfileMetaDataHelper()
    {
        // TODO Auto-generated constructor stub
    }
    public String getUserProfileFolderName(String profileId) {
        return ".";
    }
    
    public Path getConnectionProfilepath(String profileFolderName) {
        Path parentPath = Paths.get(".");
        return parentPath;
    }

}
