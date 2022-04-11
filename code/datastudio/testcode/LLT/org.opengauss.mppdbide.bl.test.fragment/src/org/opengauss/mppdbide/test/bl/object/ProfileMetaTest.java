
package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.ProfileDiskUtility;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfoJsonValidator;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.connectionupgrade.ConnectionProfileUpgradeManager;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.util.BLUtils;
import org.opengauss.mppdbide.bl.util.IBLUtils;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.mock.bl.ProfileDiskUtilityHelper;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ExportConnectionCore;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileCore;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileCore.MatchedConnectionProfiles;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileManager;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.OverridingOptions;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.files.FilePermissionFactory;
import org.opengauss.mppdbide.utils.files.ISetFilePermission;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;

public class ProfileMetaTest extends BasicJDBCTestCaseAdapter {
	ServerConnectionInfo profile;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		CommonLLTUtils.runLinuxFilePermissionInstance();
	}

	@Test
	public void test_renameProfile() {
		try {
			ConnectionProfileManagerImpl managerImpl = ConnectionProfileManagerImpl.getInstance();
			ProfileDiskUtility utility = new ProfileDiskUtility();
			managerImpl.setDiskUtility(utility);
			List<IServerConnectionInfo> allProfiles = managerImpl.getAllProfiles();
			int currentSize = allProfiles.size();
			ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
			serverInfonew.setConectionName("newConnection");
			serverInfonew.setServerIp("");
			serverInfonew.setServerPort(5432);
			serverInfonew.setDatabaseName("Gauss");
			serverInfonew.setUsername("myusername");
			serverInfonew.setPrd("mypassword".toCharArray());
			serverInfonew.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
			final String path = "/home/test/";
			final String path1 = "home/test1";
			IBLUtils blUtils = BLUtils.getInstance();
			utility.setOsCurrentUserFolderPath(path);
			List<Integer> profIds = utility.getMetaData().getAllProfileIds();
			utility.getMetaData().addProfile("Prof1", "109", path, "v1800");
			utility.getMetaData().addProfile("Prof1", "109", path1, "v1800");
			utility.getMetaData().writeToDisk("home/test1");
			utility.getMetaData().getProfileId("jshfedhjs");
			managerImpl.saveProfile(serverInfonew);	
			

		} catch (DatabaseOperationException e) {
			System.out.println("as expected");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("as expected");
		}
	}
	
	@Test
	public void test_Invalidpath() {
		try {
			ConnectionProfileManagerImpl managerImpl = ConnectionProfileManagerImpl.getInstance();
			ProfileDiskUtility utility = new ProfileDiskUtility();
			managerImpl.setDiskUtility(utility);
			List<IServerConnectionInfo> allProfiles = managerImpl.getAllProfiles();
			int currentSize = allProfiles.size();
			ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
			serverInfonew.setConectionName("newConnection");
			serverInfonew.setServerIp("");
			serverInfonew.setServerPort(5432);
			serverInfonew.setDatabaseName("Gauss");
			serverInfonew.setUsername("myusername");
			serverInfonew.setPrd("mypassword".toCharArray());
			serverInfonew.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
			final String path = "/home/test/";
			final String path1 = "home/test1";
			IBLUtils blUtils = BLUtils.getInstance();
			utility.setOsCurrentUserFolderPath(path);
			List<Integer> profIds = utility.getMetaData().getAllProfileIds();
			utility.getMetaData().addProfile("Prof1", "109", path, "v1800");
			utility.getMetaData().addProfile("Prof1", "109", path1, "v1800");
			utility.getMetaData().getProfileId("Prof1");
			utility.getMetaData().deleteProfile("Prof1");
			
			utility.getMetaData().writeToDisk("shjgfsyhjd");
			
			

		} catch (DatabaseOperationException e) {
			System.out.println("as expected");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("as expected");
		}
	}
}
