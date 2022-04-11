package org.opengauss.mppdbide.mock.bl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.bl.serverdatacache.ProfileDiskUtility;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

public class ProfileDiskUtilityHelper extends ProfileDiskUtility {
	List<IServerConnectionInfo> list = new ArrayList<IServerConnectionInfo>();
	int option;

	public int getOption() {
		return option;
	}

	public void setOption(int option) {
		this.option = option;
	}

	public ProfileDiskUtilityHelper() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<IServerConnectionInfo> getProfiles()
			throws DatabaseOperationException, DataStudioSecurityException,
			IOException {
		// TODO Auto-generated method stub
		char[] password = { 'g', 'a', 'u', 's', 's' };
		char[] sslpassword = { 'g', 'a', 'u', 's', 's', 's', 's', 'l' };

		if (option == 1) {
			throw new DatabaseOperationException(
					"database operation exception occurred");
		}
		if (option == 2) {

			throw new DataStudioSecurityException(
					"datastudio security exception occurred ", new Exception());

		}
		if (option == 3) {
			throw new IOException();
		}

		if (option == 4) {
		    IServerConnectionInfo info = new ServerConnectionInfo();
			list.clear();
			for (int i = 1; i <= 5; i++) {
				info.setConectionName("test_connection" + i);
				info.setDatabaseName("postgres" + i);
				info.setPrd(password);
				info.setRootCertificate("I dont know");
				info.setSavePrdOption(1,true);
				info.setServerIp("10.18.105.57");
				info.setServerPort(9000);
				info.setSSLEnabled(false);
				info.setProfileId(i+"");
				// info.setTargetserverVersion("10.1");
				info.setClientSSLCertificate("sslpassword");
				info.setClientSSLKey("i dont know this one also");
				info.setSSLMode("require");
				info.setUsername("dsdev");
				list.add(info.getClone());
			}

			return list;
		}

		if (option == 5) {
			List<IServerConnectionInfo> list = new ArrayList<IServerConnectionInfo>();

			return list;
		}
		return null;
		// return super.getProfiles();
	}
	  public IServerConnectionInfo getListItem(int id)
	  {
		 // System.out.println("inside getListItem()"+list.get(0).getConectionName());
	      IServerConnectionInfo profile=list.get(id);
		  return profile;
	  }
	@Override
	public void dropProfileFolder(String folder, String connectionName)
			throws DatabaseOperationException {
		try {

        } catch (Exception exception) {
            MPPDBIDELoggerUtility.error("database operation", exception);
            throw new DatabaseOperationException("database operation", exception);
        }
	}

	@Override
	public void writeProfileToDisk(IServerConnectionInfo serverInfo)
			throws DatabaseOperationException, DataStudioSecurityException {
		if (option == 6) {
		    MPPDBIDELoggerUtility.error("database operation");
			throw new DatabaseOperationException("database operation");

		}
		if (option == 7) {
		    MPPDBIDELoggerUtility.error("dataStudioSecurityException");
			throw new DataStudioSecurityException(
					"dataStudioSecurityException", new Exception());
		}
		
	}
	
    public String getUserProfileFolderName(String profileId) {
        return "PROFILE" + profileId;
    }

    public Path getConnectionProfilepath(String profileFolderName) {
        Path parentPath = Paths.get(".", MPPDBIDEConstants.PROFILE_BASE_PATH, profileFolderName);
        return parentPath;
    }
}
