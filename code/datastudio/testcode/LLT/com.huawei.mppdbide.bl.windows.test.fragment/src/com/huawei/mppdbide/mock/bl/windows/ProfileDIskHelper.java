package com.huawei.mppdbide.mock.bl.windows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.ProfileDiskUtility;
import com.huawei.mppdbide.bl.serverdatacache.ProfileMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ProfileMetaData.ProfileInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

public class ProfileDIskHelper extends ProfileDiskUtility
{
	boolean throwDatabaseOperationException;
    public boolean isThrowDatabaseOperationException() {
		return throwDatabaseOperationException;
	}


	public void setThrowDatabaseOperationException(
			boolean throwDatabaseOperationException) {
		this.throwDatabaseOperationException = throwDatabaseOperationException;
	}


	public ProfileDIskHelper()
    {
        // TODO Auto-generated constructor stub
    }
   

    @Override
    public void writeProfileToDisk(IServerConnectionInfo serverInfo)
            throws DatabaseOperationException, DataStudioSecurityException
    {
        if(throwDatabaseOperationException){
        	throw new DatabaseOperationException("can not write");
        }else{
        	super.writeProfileToDisk(serverInfo);
        }
    }

    @Override
    public IServerConnectionInfo readProfileFromFile(ProfileInfo info)
           // throws IOException, DataStudioSecurityException
    {
        // TODO Auto-generated method stub
        return super.readProfileFromFile(info);
    }

    @Override
    public Path writeProfileMetaFile() throws DatabaseOperationException
    {
        // TODO Auto-generated method stub
        return super.writeProfileMetaFile();
    }

    @Override
    public List<IServerConnectionInfo> getProfiles()
            throws DatabaseOperationException, DataStudioSecurityException,
            IOException
    {
        // TODO Auto-generated method stub
        return super.getProfiles();
        
    }


    @Override
    public ProfileMetaData getMetaData()
    {
        // TODO Auto-generated method stub
        return super.getMetaData();
    }


    @Override
    public void setMetaData(ProfileMetaData metaData)
    {
        // TODO Auto-generated method stub
        super.setMetaData(metaData);
    }

    @Override
    public void dropProfileFolder(String folder, String connectionName)
            throws DatabaseOperationException
    {
        // TODO Auto-generated method stub
        super.dropProfileFolder(folder, connectionName);
        MPPDBIDELoggerUtility.error("databaseOperation Exception");
        throw new DatabaseOperationException("databaseOperation Exception");
    }


}
