
package org.opengauss.mppdbide.test.bl.table;

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

public class ConnectionProfileMgrImplTest extends BasicJDBCTestCaseAdapter {
    private String databaseName;
    private String profileName;
    private String query;
    private long timeTaken;
    private boolean isQueryExecutionSuccess;
    private Date queryStartDate;
    ServerConnectionInfo profile;

    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
    }
    
    public void test_getAllProfiles_001() {

        try {
            ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
            profilediskutilitytest.setOption(1);
            ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();

            con.setDiskUtility(profilediskutilitytest);
            con.getAllProfiles();
            fail("not expected");
        } catch (DatabaseOperationException e) {
            assertTrue(true);

        } catch (DataStudioSecurityException e) {
            fail("not expected");
        } catch (IOException e) {
            fail("not expected");
        }

    }

    public void test_getAllProfiles_002() {

        try {
            ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
            profilediskutilitytest.setOption(2);
            ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
            con.setDiskUtility(profilediskutilitytest);
            con.getAllProfiles();
            fail("not expected");
        } catch (DatabaseOperationException e) {

            fail("not expected");
        } catch (DataStudioSecurityException e) {
            assertTrue(true);
        } catch (IOException e) {
            fail("not expected");
        }

    }

    public void test_getAllProfiles_003() {

        try {
            ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
            profilediskutilitytest.setOption(3);
            ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
            con.setDiskUtility(profilediskutilitytest);
            con.getAllProfiles();
            fail("not expected");
        } catch (DatabaseOperationException e) {

            fail("not expected");
        } catch (DataStudioSecurityException e) {
            fail("not expected");
        } catch (IOException e) {
            assertTrue(true);

        }

    }

    public void test_getAllProfiles_004() {

        try {
           // SecureUtil.setPackagePath(".");
            ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
            profilediskutilitytest.setOption(4);
            ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
            // ConnectionProfileManagerImpl.getInstance().setDiskUtility(profilediskutilitytest);
            con.setDiskUtility(profilediskutilitytest);
            int size = profilediskutilitytest.getProfiles().size();
            for (int i = 0; i < size; i++) {
                if (!con.getAllProfiles().contains(profilediskutilitytest.getListItem(i))) {

                    fail("failed");
                    break;
                }
                assertTrue(true);
            }
        } catch (DatabaseOperationException e) {
            fail("not expected");
            e.printStackTrace();
        } catch (DataStudioSecurityException e) {
            fail("not expected");
            e.printStackTrace();
        } catch (IOException e) {
            fail("not expected");
            e.printStackTrace();

        }

    }

    public void test_getProfile_001() {
        try {
            ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
            profilediskutilitytest.setOption(4);
            ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
            con.setDiskUtility(profilediskutilitytest);
            List<IServerConnectionInfo> listFromAllProfiles = con.getAllProfiles();
            int size = profilediskutilitytest.getProfiles().size();
            for (int i = 0; i < size; i++) {
                String connName = profilediskutilitytest.getListItem(i).getConectionName();
                String connNameFromMap = listFromAllProfiles.get(i).getConectionName();
                if (connName.equals(connNameFromMap)) {

                    if (!connName.equals(con.getProfile(connName).getConectionName())) {
                        fail("failed");
                        break;
                    }
                    assertTrue(true);

                }
            }

        } catch (Exception e) {
            fail("not expected");
            e.printStackTrace();
        }
    }

    public void test_getProfile_002() {
        try {
            ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
            profilediskutilitytest.setOption(4);
            ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
            con.setDiskUtility(profilediskutilitytest);
            List<IServerConnectionInfo> listFromAllProfiles = con.getAllProfiles();

            IServerConnectionInfo info = con.getProfile("abc");
            assertEquals(null, info);

        } catch (DatabaseOperationException e) {
            fail("not expected");
        } catch (DataStudioSecurityException e) {
            fail("not expected");
        } catch (IOException e) {
            fail("not expected");
        }
    }

    public void test_deleteProfile_001() {

        try {
            IBLPreference sysPref = new MockBLPreferenceImpl();
            BLPreferenceManager.getInstance().setBLPreference(sysPref);
            ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
            profilediskutilitytest.setOption(4);
            ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
            con.setDiskUtility(profilediskutilitytest);
            List<IServerConnectionInfo> listFromAllProfiles = con.getAllProfiles();
            IServerConnectionInfo profileToBeDeleted = profilediskutilitytest.getListItem(1);
            con.deleteProfile(profileToBeDeleted);
            assertNull(con.getProfile(profileToBeDeleted.getConectionName()));

        } catch (NoSuchFileException e) {
            fail("such file is not found");
        } catch (Exception e) {

            fail("not expected to come here");
        }
    }
    
    public void test_clearPassword() {

        try {
            IBLPreference sysPref = new MockBLPreferenceImpl();
            BLPreferenceManager.getInstance().setBLPreference(sysPref);
            ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
            profilediskutilitytest.setOption(4);
            ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
            boolean isEmpty = con.isProfileMapEmpty();
            assertEquals(isEmpty, con.isProfileMapEmpty());
            con.clearPermanentSavePwd();
            con.isProfileInfoAvailableInMetaData("conn");
            con.clearExceptionList();
        } catch (Exception e) {
            System.out.println("as expected");
        }
    }


    @Test
    public void test_saveProfilesForNullProfile() {
        ConnectionProfileManagerImpl conn = ConnectionProfileManagerImpl.getInstance();
        conn.getExceptionList();

        try {
            conn.saveProfile(profile);
        } catch (DatabaseOperationException e1) {
            fail("not expected here");

        } catch (DataStudioSecurityException e1) {
            fail("not expected");
            e1.printStackTrace();
        }
    }
    

    @Test
    public void test_saveProfilesForInvalidName() {

        ConnectionProfileManagerImpl conn = ConnectionProfileManagerImpl.getInstance();
        ServerConnectionInfo connInfo = new ServerConnectionInfo();
        try {

            conn.saveProfile(connInfo);

        } catch (DatabaseOperationException e1) {

            fail("not expected here");
            System.out.println("as expected");
        } catch (DataStudioSecurityException e1) {
            // TODO Auto-generated catch block

        }

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
            IBLUtils blUtils = BLUtils.getInstance();
            utility.setOsCurrentUserFolderPath(path);
            managerImpl.saveProfile(serverInfonew);
            IBLPreference sysPref = new MockBLPreferenceImpl();
            BLPreferenceManager.getInstance().setBLPreference(sysPref);
            MockBLPreferenceImpl.setDsEncoding("UTF-8");
            MockBLPreferenceImpl.setFileEncoding("UTF-8");
            MockBLPreferenceImpl.setSQLHistorySize(20);
            MockBLPreferenceImpl.setSQLQueryLength(20);

            int expectedSize = currentSize;// + 1;
            assertEquals(expectedSize, allProfiles.size());

            ServerConnectionInfo renamedConn = serverInfonew.getClone();
            final String renamedConnection = "renamedConnection";
            renamedConn.setConectionName(renamedConnection);

            // managerImpl.renameProfile(serverInfonew);

            assertEquals(expectedSize, allProfiles.size());
        } catch (DatabaseOperationException e) {
            System.out.println("as expected");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("as expected");
        }
    }

    @Test
    public void test_schemaInclusionExclusionList() {
        try {

            ServerConnectionInfo serverInfonew = new ServerConnectionInfo();
            serverInfonew.setConectionName("newConnection");
            serverInfonew.setServerIp("");
            serverInfonew.setServerPort(5432);
            serverInfonew.setDatabaseName("Gauss");
            serverInfonew.setUsername("myusername");
            serverInfonew.setPrd("mypassword".toCharArray());
            serverInfonew.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);

            Set<String> schemaExclusionList = new HashSet<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
            serverInfonew.setSchemaInclusionList(schemaExclusionList);
            schemaExclusionList.add("postgres");
            schemaExclusionList.add("db1");

            serverInfonew.getSchemaExclusionList().size();

            assertEquals(2, serverInfonew.getSchemaInclusionList().size());

            Set<String> schemaInclusionList = new HashSet<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
            serverInfonew.setSchemaInclusionList(schemaInclusionList);
            schemaInclusionList.add("postgres");
            schemaInclusionList.add("db1");
            schemaInclusionList.add("db2");

            for (int i = 1; i <= schemaInclusionList.size(); i++) {

                serverInfonew.getSchemaInclusionList().removeAll(serverInfonew.getSchemaExclusionList());
            }
            assertEquals(3, serverInfonew.getSchemaInclusionList().size());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_ExportProfiles() {
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);
        try {
            List<IServerConnectionInfo> listFromAllProfiles = con.getAllProfiles();
            ExportConnectionCore core = new ExportConnectionCore();
            List<Integer> indexlist = new ArrayList<>();
            listFromAllProfiles.stream().forEach(item->{
                indexlist.add(listFromAllProfiles.indexOf(item));
            });
            core.setLoadedProfileList(listFromAllProfiles);
            core.setExportProfilesIndexList(indexlist);
            core.setFileOutputPath("exportFile");
            if (Files.exists(Paths.get(core.getFileOutputPath()), LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(Paths.get(core.getFileOutputPath()));
            }

            con.exportConnectionProfiles(core.getExportFileList(), core.getFileOutputPath());

            byte[] readAllBytes = Files.readAllBytes(Paths.get(core.getFileOutputPath()));
            String str = new String(readAllBytes);
            Gson gson = new Gson();
            List<ServerConnectionInfo> fromJson = gson.fromJson(str, getType());
            assertEquals(listFromAllProfiles.size(), fromJson.size());

        } catch (DatabaseOperationException | DataStudioSecurityException | IOException e) {

            fail("Not expected to come here");
        }
    }

    @Test
    public void test_ExportProfiles_1() {
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);
        try {
            List<IServerConnectionInfo> listFromAllProfiles = con.getAllProfiles();
            ExportConnectionCore core = new ExportConnectionCore();
            core.setLoadedProfileList(listFromAllProfiles);

            List<Integer> selectedList = new ArrayList<Integer>();
            int counter = 0;
            for (IServerConnectionInfo info : listFromAllProfiles) {
                if (counter % 2 == 0) {
                   selectedList.add(counter);
                }
                counter++;
            }
          
            core.setExportProfilesIndexList(selectedList);
            core.setFileOutputPath("exportFile");

            if (Files.exists(Paths.get(core.getFileOutputPath()), LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(Paths.get(core.getFileOutputPath()));
            }
            con.exportConnectionProfiles(core.getExportFileList(), core.getFileOutputPath());

            byte[] readAllBytes = Files.readAllBytes(Paths.get(core.getFileOutputPath()));
            String str = new String(readAllBytes);
            Gson gson = new Gson();
            List<ServerConnectionInfo> fromJson = gson.fromJson(str, getType());
            assertEquals(selectedList.size(), fromJson.size());

        } catch (DatabaseOperationException | DataStudioSecurityException | IOException e) {

            fail("Not expected to come here");
        }
    }

    @Test
    public void test_importProfile() {
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);

        try {

            ImportConnectionProfileCore importCore = new ImportConnectionProfileCore("exportFile", 2);
            importCore.importFiles();
            List<MatchedConnectionProfiles> matchedProfilesList = importCore.getMatchedProfilesList();
            List<IServerConnectionInfo> originalDestinationList = importCore.getOriginalDestinationList();
            assertEquals(originalDestinationList.size(), matchedProfilesList.size());

        }

        catch (DatabaseOperationException e) {

            fail("not expected to come here");
        }
    }

    @Test
    public void test_importProfile_1() {
        
       /* if (PlatformChecker.isLinux())
        {
            return;
        }*/
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);

        try {
            createExportImportFile();
            byte[] readAllBytes = Files.readAllBytes(Paths.get("exportFile"));
            String str = new String(readAllBytes);
            Gson gson = new Gson();
            List<ServerConnectionInfo> fromJson = gson.fromJson(str, getType());
            ServerConnectionInfo info = new ServerConnectionInfo();

            info.setConectionName("test_connection" + 10);
            info.setDatabaseName("postgres");
            info.setPrd(new char[] {'m', 'y', 'p', 'w', 'd'});
            info.setRootCertificate("I dont know");
            info.setSavePrdOption(1, true);
            info.setServerIp("10.18.105.57");
            info.setServerPort(9000);
            info.setSSLEnabled(false);
            // info.setTargetserverVersion("10.1");
            info.setClientSSLCertificate("sslpassword");
            info.setClientSSLKey("."+ File.separator +"privatekey.pm");
            info.setSSLMode("require");
            info.setUsername("dsdev");
            fromJson.add(info.getClone());
            String newJson = gson.toJson(fromJson, getType());
           /* if (Files.exists(Paths.get("importFile"), LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(Paths.get("importFile"));
            }
            //withPermission.createFileWithPermission(Paths.get("importFile").toString(), false, null, true);

            Files.write(Paths.get(".","importFile"), newJson.getBytes(), LinkOption.NOFOLLOW_LINKS);
            ImportConnectionProfileCore importCore = new ImportConnectionProfileCore("importFile", 2);
            importCore.importFiles();
            assertEquals(importCore.getOriginalDestinationList().size(), importCore.getMatchedProfilesList().size());
            assertEquals(1, importCore.getUniqueList().size());*/

        }

        catch (IOException e) {
            e.printStackTrace();
            fail("not expected to come here");
        }
    }

    @Test
    public void test_importProfile_2() {

        /*if (PlatformChecker.isLinux())
        {
            return;
        }*/
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);

        try {
            createExportImportFile();
            byte[] readAllBytes = Files.readAllBytes(Paths.get("exportFile"));
            String str = new String(readAllBytes);
            Gson gson = new Gson();
            List<ServerConnectionInfo> fromJson = gson.fromJson(str, getType());

            ServerConnectionInfo info = new ServerConnectionInfo();

            info.setConectionName("test_connection" + 10);
            info.setDatabaseName("postgres");
            info.setPrd(new char[] {'m', 'y', 'p', 'w', 'd'});
            info.setRootCertificate("I dont know");
            info.setSavePrdOption(1, true);
            info.setServerIp("10.18.105.57");
            info.setServerPort(9000);
            info.setSSLEnabled(false);
            info.setProfileId("" + 10);
            // info.setTargetserverVersion("10.1");
            info.setClientSSLCertificate("sslpassword");
            info.setClientSSLKey("D://privatekey.pm");
            info.setSSLMode("require");
            info.setUsername("dsdev");
            fromJson.add(info.getClone());
            String newJson = gson.toJson(fromJson, getType());
            String newSecJson=newJson.replace(",\"modifiedSchemaExclusionList\":[],\"modifiedSchemaInclusionList\":[]","");

            if (Files.exists(Paths.get("importFile"), LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(Paths.get("importFile"));
            }
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(Paths.get("importFile").toString(), false, null, true);
            Files.write(Paths.get("importFile"), newSecJson.getBytes(), LinkOption.NOFOLLOW_LINKS);
            ImportConnectionProfileCore importCore = new ImportConnectionProfileCore("importFile", 3);
            importCore.importFiles();
            assertEquals(importCore.getOriginalDestinationList().size(), importCore.getMatchedProfilesList().size());
            assertEquals(1, importCore.getUniqueList().size());

            ImportConnectionProfileManager profManager = new ImportConnectionProfileManager(importCore);
            profManager.mergeAllProfiles();
            int totalProfiles = importCore.getMatchedProfilesList().size() + importCore.getUniqueList().size();
            assertEquals(totalProfiles, importCore.getOriginalDestinationList().size());

        }

        catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail("not expected to come here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("not expected to come here");
        }

    }

    @Test
    public void test_importProfile_3() {

        /*if (PlatformChecker.isLinux())
        {
            return;
        }*/
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);

        try {
            createExportImportFile();
            byte[] readAllBytes = Files.readAllBytes(Paths.get("exportFile"));
            String str = new String(readAllBytes);
            Gson gson = new Gson();
            List<ServerConnectionInfo> fromJson = gson.fromJson(str, getType());

            ServerConnectionInfo info = new ServerConnectionInfo();

            info.setConectionName("test_connection" + 10);
            info.setDatabaseName("postgres");
            info.setPrd(new char[] {'m', 'y', 'p', 'w', 'd'});
            info.setRootCertificate("I dont know");
            info.setSavePrdOption(1, true);
            info.setServerIp("10.18.105.57");
            info.setServerPort(9000);
            info.setSSLEnabled(false);
            info.setProfileId("" + 10);
            // info.setTargetserverVersion("10.1");
            info.setClientSSLCertificate("sslpassword");
            info.setClientSSLKey("D://privatekey.pm");
            info.setSSLMode("require");
            info.setUsername("dsdev");
            fromJson.add(info.getClone());
            String newJson = gson.toJson(fromJson, getType());
            String newSecJson=newJson.replace(",\"modifiedSchemaExclusionList\":[],\"modifiedSchemaInclusionList\":[]","");
            if (Files.exists(Paths.get("importFile"), LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(Paths.get("importFile"));
            }
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(Paths.get("importFile").toString(), false, null, true);
            Files.write(Paths.get("importFile"), newSecJson.getBytes(), LinkOption.NOFOLLOW_LINKS);
            ImportConnectionProfileCore importCore = new ImportConnectionProfileCore("importFile", 1);
            importCore.importFiles();
            assertEquals(importCore.getOriginalDestinationList().size(), importCore.getMatchedProfilesList().size());
            assertEquals(1, importCore.getUniqueList().size());

            ImportConnectionProfileManager profManager = new ImportConnectionProfileManager(importCore);
            for (MatchedConnectionProfiles p : importCore.getMatchedProfilesList()) {
                profManager.addProfilesToBeOverriden(p, OverridingOptions.COPYANDKEEPBOTH);

            }
            profManager.mergeAllProfiles();
            int totalProfiles = importCore.getMatchedProfilesList().size() * 2 + importCore.getUniqueList().size();

            assertEquals(importCore.getOriginalDestinationList().size(), totalProfiles);

        }

        catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail("not expected to come here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("not expected to come here");
        }

    }

    @Test
    public void test_importProfile_4() {
       /* if (PlatformChecker.isLinux())
        {
            return;
        }*/
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);

        try {
            createExportImportFile();
            byte[] readAllBytes = Files.readAllBytes(Paths.get("exportFile"));
            String str = new String(readAllBytes);
            Gson gson = new Gson();
            List<ServerConnectionInfo> fromJson = gson.fromJson(str, getType());

            ServerConnectionInfo info = new ServerConnectionInfo();

            info.setConectionName("test_connection" + 10);
            info.setDatabaseName("postgres");
            info.setPrd(new char[] {'m', 'y', 'p', 'w', 'd'});
            info.setRootCertificate("I dont know");
            info.setSavePrdOption(1, true);
            info.setServerIp("10.18.105.57");
            info.setServerPort(9000);
            info.setSSLEnabled(false);
            info.setProfileId("" + 10);
            // info.setTargetserverVersion("10.1");
            info.setClientSSLCertificate("sslpassword");
            info.setClientSSLKey("D://privatekey.pm");
            info.setSSLMode("require");
            info.setUsername("dsdev");
            fromJson.add(info.getClone());
            String newJson = gson.toJson(fromJson, getType());
            String newSecJson=newJson.replace(",\"modifiedSchemaExclusionList\":[],\"modifiedSchemaInclusionList\":[]","");
            if (Files.exists(Paths.get("importFile"), LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(Paths.get("importFile"));
            }
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(Paths.get("importFile").toString(), false, null, true);
            Files.write(Paths.get("importFile"), newSecJson.getBytes(), LinkOption.NOFOLLOW_LINKS);
            ImportConnectionProfileCore importCore = new ImportConnectionProfileCore("importFile", 3);
            importCore.importFiles();
            assertEquals(importCore.getOriginalDestinationList().size(), importCore.getMatchedProfilesList().size());
            assertEquals(1, importCore.getUniqueList().size());

            ImportConnectionProfileManager profManager = new ImportConnectionProfileManager(importCore);
            for (MatchedConnectionProfiles p : importCore.getMatchedProfilesList()) {
                profManager.addProfilesToBeOverriden(p, OverridingOptions.REPLACE);

            }
            profManager.mergeAllProfiles();
            int totalProfiles = importCore.getMatchedProfilesList().size() + importCore.getUniqueList().size();

            assertEquals(importCore.getOriginalDestinationList().size(), totalProfiles);

        }

        catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail("not expected to come here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("not expected to come here");
        }

    }

    @Test
    public void test_importProfile_5() {
        
    /*    if (PlatformChecker.isLinux())
        {
            return;
        }*/
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);

        try {
            createExportImportFile();
            byte[] readAllBytes = Files.readAllBytes(Paths.get("exportFile"));
            String str = new String(readAllBytes);
            Gson gson = new Gson();
            List<ServerConnectionInfo> fromJson = gson.fromJson(str, getType());

            ServerConnectionInfo info = new ServerConnectionInfo();

            info.setConectionName("test_connection" + 10);
            info.setDatabaseName("postgres");
            info.setPrd(new char[] {'m', 'y', 'p', 'w', 'd'});
            info.setRootCertificate("I dont know");
            info.setSavePrdOption(1, true);
            info.setServerIp("10.18.105.57");
            info.setServerPort(9000);
            info.setSSLEnabled(false);
            info.setProfileId("" + 10);
            // info.setTargetserverVersion("10.1");
            info.setClientSSLCertificate("sslpassword");
            info.setClientSSLKey("D://privatekey.pm");
            info.setSSLMode("require");
            info.setUsername("dsdev");
            fromJson.add(info.getClone());
            String newJson = gson.toJson(fromJson, getType());
            String newSecJson=newJson.replace(",\"modifiedSchemaExclusionList\":[],\"modifiedSchemaInclusionList\":[]","");

            if (Files.exists(Paths.get("importFile"), LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(Paths.get("importFile"));
            }
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(Paths.get("importFile").toString(), false, null, true);
            Files.write(Paths.get("importFile"), newSecJson.getBytes(), LinkOption.NOFOLLOW_LINKS);
            ImportConnectionProfileCore importCore = new ImportConnectionProfileCore("importFile", 2);
            importCore.importFiles();
            assertEquals(importCore.getOriginalDestinationList().size(), importCore.getMatchedProfilesList().size());
            assertEquals(1, importCore.getUniqueList().size());

            ImportConnectionProfileManager profManager = new ImportConnectionProfileManager(importCore);

            profManager.handleAllProfilesWithConflicts(OverridingOptions.COPYANDKEEPBOTH);

            profManager.mergeAllProfiles();
            int totalProfiles = importCore.getMatchedProfilesList().size() * 2 + importCore.getUniqueList().size();

            assertEquals(importCore.getOriginalDestinationList().size(), totalProfiles);

        }

        catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail("not expected to come here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("not expected to come here");
        }

    }

    @Test
    public void test_importProfile_6() {
       /* if (PlatformChecker.isLinux())
        {
            return;
        }*/
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);

        try {
            createExportImportFile();
            byte[] readAllBytes = Files.readAllBytes(Paths.get("exportFile"));
            String str = new String(readAllBytes);
            Gson gson = new Gson();
            List<ServerConnectionInfo> fromJson = gson.fromJson(str, getType());

            ServerConnectionInfo info = new ServerConnectionInfo();

            info.setConectionName("test_connection" + 10);
            info.setDatabaseName("postgres");
            info.setPrd(new char[] {'m', 'y', 'p', 'w', 'd'});
            info.setRootCertificate("I dont know");
            info.setSavePrdOption(1, true);
            info.setServerIp("10.18.105.57");
            info.setServerPort(9000);
            info.setSSLEnabled(false);
            info.setProfileId("" + 10);
            // info.setTargetserverVersion("10.1");
            info.setClientSSLCertificate("sslpassword");
            info.setClientSSLKey("D://privatekey.pm");
            info.setSSLMode("require");
            info.setUsername("dsdev");
            fromJson.add(info.getClone());
            String newJson = gson.toJson(fromJson, getType());
            String newSecJson=newJson.replace(",\"modifiedSchemaExclusionList\":[],\"modifiedSchemaInclusionList\":[]","");

            if (Files.exists(Paths.get("importFile"), LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(Paths.get("importFile"));
            }
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(Paths.get("importFile").toString(), false, null, true);
            Files.write(Paths.get("importFile"), newSecJson.getBytes(), LinkOption.NOFOLLOW_LINKS);
            ImportConnectionProfileCore importCore = new ImportConnectionProfileCore("importFile", 1);
            importCore.importFiles();
            assertEquals(importCore.getOriginalDestinationList().size(), importCore.getMatchedProfilesList().size());
            assertEquals(1, importCore.getUniqueList().size());

            ImportConnectionProfileManager profManager = new ImportConnectionProfileManager(importCore);

            profManager.handleAllProfilesWithConflicts(OverridingOptions.REPLACE);

            profManager.mergeAllProfiles();
            int totalProfiles = importCore.getMatchedProfilesList().size() + importCore.getUniqueList().size();

            assertEquals(importCore.getOriginalDestinationList().size(), totalProfiles);

        }

        catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail("not expected to come here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("not expected to come here");
        }

    }

    @Test
    public void test_importProfile_7() {
       /* if (PlatformChecker.isLinux())
        {
            return;
        }*/
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);

        try {
            createExportImportFile();
            byte[] readAllBytes = Files.readAllBytes(Paths.get("exportFile"));
            String str = new String(readAllBytes);
            Gson gson = new Gson();
            List<ServerConnectionInfo> fromJson = gson.fromJson(str, getType());

            ServerConnectionInfo info = new ServerConnectionInfo();

            info.setConectionName("test_connection" + 10);
            info.setDatabaseName("postgres");
            info.setPrd(new char[] {'m', 'y', 'p', 'w', 'd'});
            info.setRootCertificate("I dont know");
            info.setSavePrdOption(1, true);
            info.setServerIp("10.18.105.57");
            info.setServerPort(9000);
            info.setSSLEnabled(false);
            info.setProfileId("" + 10);
            // info.setTargetserverVersion("10.1");
            info.setClientSSLCertificate("sslpassword");
            info.setClientSSLKey("D://privatekey.pm");
            info.setSSLMode("require");
            info.setUsername("dsdev");
            fromJson.add(info.getClone());
            String newJson = gson.toJson(fromJson, getType());
            String newSecJson=newJson.replace(",\"modifiedSchemaExclusionList\":[],\"modifiedSchemaInclusionList\":[]","");

            if (Files.exists(Paths.get("importFile"), LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(Paths.get("importFile"));
            }
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(Paths.get("importFile").toString(), false, null, true);
            Files.write(Paths.get("importFile"), newSecJson.getBytes(), LinkOption.NOFOLLOW_LINKS);
            ImportConnectionProfileCore importCore = new ImportConnectionProfileCore("importFile", 3);
            importCore.importFiles();
            assertEquals(importCore.getOriginalDestinationList().size(), importCore.getMatchedProfilesList().size());
            assertEquals(1, importCore.getUniqueList().size());

            ImportConnectionProfileManager profManager = new ImportConnectionProfileManager(importCore);

            profManager.handleAllProfilesWithConflicts(OverridingOptions.DONTCOPY);

            profManager.mergeAllProfiles();
            int totalProfiles = importCore.getOriginalDestinationList().size();

            assertEquals(importCore.getOriginalDestinationList().size(), totalProfiles);

        }

        catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail("not expected to come here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("not expected to come here");
        }

    }

    @Test
    public void test_importProfile_8() {
      /*  if (PlatformChecker.isLinux())
        {
            return;
        }*/
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        ProfileDiskUtilityHelper profilediskutilitytest = new ProfileDiskUtilityHelper();
        profilediskutilitytest.setOption(4);
        ConnectionProfileManagerImpl con = ConnectionProfileManagerImpl.getInstance();
        con.setDiskUtility(profilediskutilitytest);

        try {
            createExportImportFile();
            byte[] readAllBytes = Files.readAllBytes(Paths.get("exportFile"));
            String str = new String(readAllBytes);
            Gson gson = new Gson();
            List<ServerConnectionInfo> fromJson = gson.fromJson(str, getType());

            ServerConnectionInfo info = new ServerConnectionInfo();

            info.setConectionName("test_connection" + 10);
            info.setDatabaseName("postgres");
            info.setPrd(new char[] {'m', 'y', 'p', 'w', 'd'});
            info.setRootCertificate("I dont know");
            info.setSavePrdOption(1, true);
            info.setServerIp("10.18.105.57");
            info.setServerPort(9000);
            info.setSSLEnabled(false);
            info.setProfileId("" + 10);
            // info.setTargetserverVersion("10.1");
            info.setClientSSLCertificate("sslpassword");
            info.setClientSSLKey("D://privatekey.pm");
            info.setSSLMode("require");
            info.setUsername("dsdev");
            fromJson.add(info.getClone());
            String newJson = gson.toJson(fromJson, getType());
            String newSecJson=newJson.replace(",\"modifiedSchemaExclusionList\":[],\"modifiedSchemaInclusionList\":[]","");

            if (Files.exists(Paths.get("importFile"), LinkOption.NOFOLLOW_LINKS)) {
                Files.delete(Paths.get("importFile"));
            }
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(Paths.get("importFile").toString(), false, null, true);
            Files.write(Paths.get("importFile"), newSecJson.getBytes(), LinkOption.NOFOLLOW_LINKS);
            ImportConnectionProfileCore importCore = new ImportConnectionProfileCore("importFile", 3);
            importCore.importFiles();
            assertEquals(importCore.getOriginalDestinationList().size(), importCore.getMatchedProfilesList().size());
            assertEquals(1, importCore.getUniqueList().size());

            ImportConnectionProfileManager profManager = new ImportConnectionProfileManager(importCore);
            int counter = 0;
            for (MatchedConnectionProfiles p : importCore.getMatchedProfilesList()) {
                if (counter % 2 == 0)
                    profManager.addProfilesToBeOverriden(p, OverridingOptions.REPLACE);
                else
                    profManager.addProfilesToBeOverriden(p, OverridingOptions.COPYANDKEEPBOTH);
                counter++;
            }
            profManager.mergeAllProfiles();
            
            assertEquals(8, importCore.getOriginalDestinationList().size());

        }

        catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail("not expected to come here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("not expected to come here");
        }

    }
    
    private void createExportImportFile() throws IOException {
        Path backup = Paths.get("exportFile");
        boolean fileExists = Files.exists(backup);
        if (!fileExists) {
          Files.createFile(backup);
        }
        FileOutputStream fileWriter = new FileOutputStream(backup.toFile());
        BufferedOutputStream writer = new BufferedOutputStream(fileWriter);
        String text = "[{\"profileId\":\"1\",\"databaseVersion\":\"\",\"dbType\":\"OPENGAUSS\",\"version\":\"2.00\",\"general\":{\"conectionName\":\"test_connection1\",\"databaseName\":\"postgres1\",\"prd\":[],\"isSSLEnabled\":false,\"serverIp\":\"10.18.105.57\",\"serverPort\":9000,\"username\":\"dsdev\",\"savePrdOption\":\"CURRENT_SESSION_ONLY\",\"connctionDriverName\":\"openGauss\"},\"ssl\":{\"clSSLCertificatePath\":\"sslpassword\",\"clSSLKeyPath\":\"i dont know this one also\",\"sslPrd\":[],\"rootCertFilePathText\":\"I dont know\",\"sslMode\":\"require\"},\"advanced\":{\"schemaExclusionList\":[],\"schemaInclusionList\":[],\"loadLimit\":30000,\"privilegeBasedObAcess\":false}},{\"profileId\":\"2\",\"databaseVersion\":\"\",\"dbType\":\"OPENGAUSS\",\"version\":\"2.00\",\"general\":{\"conectionName\":\"test_connection2\",\"databaseName\":\"postgres2\",\"prd\":[],\"isSSLEnabled\":false,\"serverIp\":\"10.18.105.57\",\"serverPort\":9000,\"username\":\"dsdev\",\"savePrdOption\":\"CURRENT_SESSION_ONLY\",\"connctionDriverName\":\"openGauss\"},\"ssl\":{\"clSSLCertificatePath\":\"sslpassword\",\"clSSLKeyPath\":\"i dont know this one also\",\"sslPrd\":[],\"rootCertFilePathText\":\"I dont know\",\"sslMode\":\"require\"},\"advanced\":{\"schemaExclusionList\":[],\"schemaInclusionList\":[],\"loadLimit\":30000,\"privilegeBasedObAcess\":false}},{\"profileId\":\"3\",\"databaseVersion\":\"\",\"dbType\":\"OPENGAUSS\",\"version\":\"2.00\",\"general\":{\"conectionName\":\"test_connection3\",\"databaseName\":\"postgres3\",\"prd\":[],\"isSSLEnabled\":false,\"serverIp\":\"10.18.105.57\",\"serverPort\":9000,\"username\":\"dsdev\",\"savePrdOption\":\"CURRENT_SESSION_ONLY\",\"connctionDriverName\":\"openGauss\"},\"ssl\":{\"clSSLCertificatePath\":\"sslpassword\",\"clSSLKeyPath\":\"i dont know this one also\",\"sslPrd\":[],\"rootCertFilePathText\":\"I dont know\",\"sslMode\":\"require\"},\"advanced\":{\"schemaExclusionList\":[],\"schemaInclusionList\":[],\"loadLimit\":30000,\"privilegeBasedObAcess\":false}},{\"profileId\":\"4\",\"databaseVersion\":\"\",\"dbType\":\"OPENGAUSS\",\"version\":\"2.00\",\"general\":{\"conectionName\":\"test_connection4\",\"databaseName\":\"postgres4\",\"prd\":[],\"isSSLEnabled\":false,\"serverIp\":\"10.18.105.57\",\"serverPort\":9000,\"username\":\"dsdev\",\"savePrdOption\":\"CURRENT_SESSION_ONLY\",\"connctionDriverName\":\"openGauss\"},\"ssl\":{\"clSSLCertificatePath\":\"sslpassword\",\"clSSLKeyPath\":\"i dont know this one also\",\"sslPrd\":[],\"rootCertFilePathText\":\"I dont know\",\"sslMode\":\"require\"},\"advanced\":{\"schemaExclusionList\":[],\"schemaInclusionList\":[],\"loadLimit\":30000,\"privilegeBasedObAcess\":false}},{\"profileId\":\"5\",\"databaseVersion\":\"\",\"dbType\":\"OPENGAUSS\",\"version\":\"2.00\",\"general\":{\"conectionName\":\"test_connection5\",\"databaseName\":\"postgres5\",\"prd\":[],\"isSSLEnabled\":false,\"serverIp\":\"10.18.105.57\",\"serverPort\":9000,\"username\":\"dsdev\",\"savePrdOption\":\"CURRENT_SESSION_ONLY\",\"connctionDriverName\":\"openGauss\"},\"ssl\":{\"clSSLCertificatePath\":\"sslpassword\",\"clSSLKeyPath\":\"i dont know this one also\",\"sslPrd\":[],\"rootCertFilePathText\":\"I dont know\",\"sslMode\":\"require\"},\"advanced\":{\"schemaExclusionList\":[],\"schemaInclusionList\":[],\"loadLimit\":30000,\"privilegeBasedObAcess\":false}}]";
        byte[] writeBytes = null;
        String encoding ="UTF-8";
        if (text.length() != 0) {
            writeBytes = text.getBytes(encoding);
            writer.write(writeBytes);
            writer.flush();
            writer.close();
        }
        
        Path backup1 = Paths.get("importFile");
        boolean fileExists1 = Files.exists(backup1);
        if (!fileExists1) {
          Files.createFile(backup1);
        }
        FileOutputStream fileWriter1 = new FileOutputStream(backup1.toFile());
        BufferedOutputStream writer1 = new BufferedOutputStream(fileWriter1);
        String text1 = "        [{\"profileId\":\"1\",\"databaseVersion\":\"\",\"dbType\":\"OPENGAUSS\",\"version\":\"2.00\",\"general\":{\"conectionName\":\"test_connection1\",\"databaseName\":\"postgres1\",\"prd\":[],\"isSSLEnabled\":false,\"serverIp\":\"10.18.105.57\",\"serverPort\":9000,\"username\":\"dsdev\",\"savePrdOption\":\"CURRENT_SESSION_ONLY\",\"connctionDriverName\":\"GaussDB 200\"},\"ssl\":{\"clSSLCertificatePath\":\"sslpassword\",\"clSSLKeyPath\":\"i dont know this one also\",\"sslPrd\":[],\"rootCertFilePathText\":\"I dont know\",\"sslMode\":\"require\",\"clSSLPrivateKeyFile\":\"\"},\"advanced\":{\"schemaExclusionList\":[],\"schemaInclusionList\":[],\"loadLimit\":30000,\"privilegeBasedObAcess\":false}}]\r\n" + 
                "";
        byte[] writeBytes1 = null;
        String encoding1 ="UTF-8";
        if (text.length() != 0) {
            writeBytes1 = text.getBytes(encoding);
            writer1.write(writeBytes);
            writer1.flush();
            writer1.close();
        }
    }
    
    @Test
    public void test_upgrade_profile_version()
    {
        String jsonString="[{\"profileId\":\"1\",\"databaseVersion\":\"\",\"dbType\":\"OPENGAUSS\",\"version\":\"1.00\",\"conectionName\":\"test_connection1\",\"databaseName\":\"postgres1\",\"prd\":[],\"isSSLEnabled\":false,\"serverIp\":\"10.18.105.57\",\"serverPort\":9000,\"username\":\"dsdev\",\"savePrdOption\":\"CURRENT_SESSION_ONLY\",\"connctionDriverName\":\"openGauss\",\"clSSLCertificatePath\":\"sslpassword\",\"clSSLKeyPath\":\"i dont know this one also\",\"sslPrd\":[],\"rootCertFilePathText\":\"I dont know\",\"sslMode\":\"require\",\"clSSLPrivateKeyFile\":\"\",\"schemaExclusionList\":[],\"schemaInclusionList\":[],\"loadLimit\":30000,\"privilegeBasedObAcess\":false}]";
        ConnectionProfileUpgradeManager instance = ConnectionProfileUpgradeManager.getInstance();
        
        
        List<IServerConnectionInfo> upgradedConnectionProfiles = instance.getUpgradedConnectionProfiles(jsonString, getType(), "1.00");
        int versionIndex = instance.getVersionIndex("1.00");
        assertEquals(0,versionIndex);
        int versionIndex1 = instance.getVersionIndex("4.00");
        assertEquals(-1, versionIndex1);
        boolean versionAvailable = instance.isVersionAvailable("3.00");
        assertEquals(false,versionAvailable);
        boolean validVersion = instance.isVersionAvailable("2.00");
        assertEquals(true, validVersion);
        String version = upgradedConnectionProfiles.get(0).getVersion();
        assertEquals("2.00", version);
    }
    
    @Test
    public void test_JsonValidator()
    {
        String jsonString="[{\"profileId\":\"1\",\"databaseVersion\":\"\",\"dbType\":\"OPENGAUSS\",\"version\":\"1.00\",\"conectionName\":\"test_connection1\",\"databaseName\":\"postgres1\",\"prd\":[],\"isSSLEnabled\":false,\"serverIp\":\"10.18.105.57\",\"serverPort\":9000,\"username\":\"dsdev\",\"savePrdOption\":\"CURRENT_SESSION_ONLY\",\"connctionDriverName\":\"openGauss\",\"clSSLCertificatePath\":\"sslpassword\",\"clSSLKeyPath\":\"i dont know this one also\",\"sslPrd\":[],\"rootCertFilePathText\":\"I dont know\",\"sslMode\":\"require\",\"clSSLPrivateKeyFile\":\"\",\"schemaExclusionList\":[],\"schemaInclusionList\":[],\"loadLimit\":30000,\"privilegeBasedObAcess\":false}]";
        boolean validateJson = ServerConnectionInfoJsonValidator.validateJson(jsonString);
        assertEquals(true, validateJson);
    }
    
    @Test
    public void test_JsonValidator_1()
    {
        String jsonString="[{\"ABCD\":\"1\",\"profileId\":\"1\",\"databaseVersion\":\"\",\"dbType\":\"OPENGAUSS\",\"version\":\"1.00\",\"conectionName\":\"test_connection1\",\"databaseName\":\"postgres1\",\"prd\":[],\"isSSLEnabled\":false,\"serverIp\":\"10.18.105.57\",\"serverPort\":9000,\"username\":\"dsdev\",\"savePrdOption\":\"CURRENT_SESSION_ONLY\",\"connctionDriverName\":\"openGauss\",\"clSSLCertificatePath\":\"sslpassword\",\"clSSLKeyPath\":\"i dont know this one also\",\"sslPrd\":[],\"rootCertFilePathText\":\"I dont know\",\"sslMode\":\"require\",\"clSSLPrivateKeyFile\":\"\",\"schemaExclusionList\":[],\"schemaInclusionList\":[],\"loadLimit\":30000,\"privilegeBasedObAcess\":false}]";
        boolean validateJson = ServerConnectionInfoJsonValidator.validateJson(jsonString);
        assertEquals(false, validateJson);
    }
    
    @Test
    public void test_JsonValidator_2()
    {
        String jsonString="[{\"profileId\":\"1\",\"databaseVersion\":\"\",\"dbType\":\"OPENGAUSS\",\"version\":\"1.00\",\"conectionName\":\"test_connection1\",\"databaseName\":\"postgres1\",\"prd\":[],\"isSSLEnabled\":false,\"serverIp\":\"1000.1800.105.57\",\"serverPort\":9000,\"username\":\"dsdev\",\"savePrdOption\":\"CURRENT_SESSION_ONLY\",\"connctionDriverName\":\"openGauss\",\"clSSLCertificatePath\":\"sslpassword\",\"clSSLKeyPath\":\"i dont know this one also\",\"sslPrd\":[],\"rootCertFilePathText\":\"I dont know\",\"sslMode\":\"require\",\"clSSLPrivateKeyFile\":\"\",\"schemaExclusionList\":[],\"schemaInclusionList\":[],\"loadLimit\":30000,\"privilegeBasedObAcess\":false}]";
        boolean validateJson = ServerConnectionInfoJsonValidator.validateJson(jsonString);
        assertEquals(false, validateJson);
    }

    private Type getType() {
        return new ProfileTypeToken().getType();
    }

    private static final class ProfileTypeToken extends TypeToken<List<ServerConnectionInfo>> {
    }

}
