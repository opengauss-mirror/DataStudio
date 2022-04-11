package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.ISourceCode;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import org.opengauss.mppdbide.bl.util.DebugObjectGauss200Utils;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtilsHelper.EXCEPTIONENUM;
import org.opengauss.mppdbide.mock.bl.ExceptionConnectionHelper;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import com.mockrunner.mock.jdbc.MockResultSet;

public class DebugObjectGauss200UtilsTest extends BLTestAdapter
{
    @Test
    public void test_getNewCObjectTemplate_001()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            DebugObjectGroup debugObjectGroup =
                    new DebugObjectGroup(OBJECTTYPE.FUNCTION_GROUP, database.getNameSpaceById(1));

            String namespacename = "";
            if (null != debugObjectGroup.getNamespace())
            {
                namespacename = debugObjectGroup.getNamespace().getQualifiedObjectName();
            }
            assertEquals(
                    ("CREATE [OR REPLACE] FUNCTION pg_catalog.function_name ([ parameter datatype[,parameter datatype] ])"
                            + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR
                            + "\tRETURNS return_datatype" + MPPDBIDEConstants.LINE_SEPARATOR + "\tLANGUAGE C"
                            + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR + "AS"
                            + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR
                            + "\t\'/*iso file path and name*/\',$$/*function name*/$$"
                            + MPPDBIDEConstants.LINE_SEPARATOR + "/"),
                    DebugObjectGauss200Utils.getNewFunctionObjectTemplate("return_datatype",
                            namespacename, "c"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_getNewCObjectTemplate_002()
    {
        try
        {
            Database database = connProfCache.getDbForProfileId(profileId);
            DebugObjectGroup debugObjectGroup =
                    new DebugObjectGroup(OBJECTTYPE.FUNCTION_GROUP, database.getNameSpaceById(1));

            String namespacename = "";
            if (null != debugObjectGroup.getNamespace())
            {
                namespacename = debugObjectGroup.getNamespace().getQualifiedObjectName();
            }
            assertEquals(
                    ("CREATE [OR REPLACE] FUNCTION pg_catalog.function_name ([ parameter datatype[,parameter datatype] ])"
                            + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR
                            + "\tRETURNS return_datatype" + MPPDBIDEConstants.LINE_SEPARATOR + "\tLANGUAGE SQL"
                            + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR + "AS $$"
                            + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR
                            + "\t/*executable_section*/" + MPPDBIDEConstants.LINE_SEPARATOR
                            + MPPDBIDEConstants.LINE_SEPARATOR + "$$" + MPPDBIDEConstants.LINE_SEPARATOR + "/"),
                    DebugObjectGauss200Utils.getNewFunctionObjectTemplate("return_datatype",
                            namespacename, "pl/sql"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_002()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test", OBJECTTYPE.CFUNCTION, database);
            debugObject.refreshSourceCode();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_002_01()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test", OBJECTTYPE.CFUNCTION, database);
            assertEquals(debugObject.getObjectType(), OBJECTTYPE.CFUNCTION);
            debugObject.refreshSourceCode();
            try
            {
                debugObject.setNamespace(database.getNameSpaceById(1));
                DebugObjects.DebugObjectsUtils.convertToObject(null, database);
            }
            catch (NullPointerException e)
            {
                System.out.println("expected...");
            }
            assertNotNull(debugObject);
            assertNull(debugObject.getLang());
            assertEquals("pg_catalog.test", debugObject.getDisplayName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_002_02()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test", OBJECTTYPE.CFUNCTION, database);
            debugObject.refreshSourceCode();
            ObjectParameter op = new ObjectParameter();
            op.setType(PARAMETERTYPE.IN);
            op.setDataType("refcursor");
            ObjectParameter[] params = new ObjectParameter[1];
            params[0] = op;
            debugObject.setObjectParameters(params);
            debugObject.generateDropQuery();
            debugObject.getLang();
            assertNotNull(debugObject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_06()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            ExceptionConnectionHelper exceptionConnection = new ExceptionConnectionHelper();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);
            StringBuilder strSourcecode = new StringBuilder();

            strSourcecode.append("\"Declare").append("\nc INT = 6;").append("\nd INT;BEGIN");
            strSourcecode.append("\nc := c+1;").append("\nc := perform nestedfunc()");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;").append("\nc := 100;");
            strSourcecode.append("\nd := c + 200;").append("\nreturn d;").append("\nend;\")");
            MockResultSet indexRS = preparedstatementHandler.createResultSet();
            indexRS.addRow(new Object[] {4, strSourcecode.toString()});
            String query = "select headerlines, definition from PG_GET_FUNCTIONDEF(1);";
            preparedstatementHandler.prepareResultSet(query, indexRS);
            
            String query2 = "select xmin1, cmin1 from pldbg_get_funcVer(" + 1 + ")";
            MockResultSet versionRS = preparedstatementHandler.createResultSet();
            versionRS.addRow(new Object[] {1, 1});
            preparedstatementHandler.prepareResultSet(query2, versionRS);
            
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            // Server server = new Server(serverInfo);
            // Namespace ns = database.getNameSpaceById(1);
            DebugObjects dbgobj = new DebugObjects(1, "test", OBJECTTYPE.CFUNCTION, database);
            exceptionConnection.setThrowExceptionSetString(true);

            ISourceCode code = dbgobj.getLatestSouceCode();
            assertNotNull(code);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not excepted to come here");
        }
    }

    @Test
    public void testTTA_BL_DebugObject_FUNC_001_66()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            ExceptionConnectionHelper exceptionConnection = new ExceptionConnectionHelper();
            exceptionConnection.setNeedExceptioStatement(true);
            exceptionConnection.setNeedExceptionResultset(true);
            exceptionConnection.setThrownResultSetNext(EXCEPTIONENUM.YES);

            getJDBCMockObjectFactory().getMockDriver().setupConnection(exceptionConnection);
            StringBuilder strSourcecode = new StringBuilder();

            strSourcecode.append("\"Declare").append("\nc INT = 6;").append("\nd INT;BEGIN");
            strSourcecode.append("\nc := c+1;").append("\nc := perform nestedfunc()");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;");
            strSourcecode.append("\nc := c+1;").append("\nc := c+1;").append("\nc := 100;");
            strSourcecode.append("\nd := c + 200;").append("\nreturn d;").append("\nend;\")");
            MockResultSet indexRS = preparedstatementHandler.createResultSet();
            indexRS.addRow(new Object[] {null});
            String query = "select a.headerlines, a.definition, b.xmin, b.cmin from pg_proc b left join "
                    + "(select * from PG_GET_FUNCTIONDEF(" + 1 + ")) a on (1) where b.oid=" + 1;
            preparedstatementHandler.prepareResultSet(query, indexRS);
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects dbgobj = new DebugObjects(1, "test", OBJECTTYPE.CFUNCTION, database);
            exceptionConnection.setThrowExceptionSetString(true);

            dbgobj.getLatestSouceCode();
            fail("Not Excepted to come here");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assert(true);
        }
    }

    @Test
    public void testTTA_BL_DebugObject_getLatestInfo()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test", OBJECTTYPE.CFUNCTION, database);
            assertNotNull(debugObject.getLatestInfo());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testTTA_BL_DebugObject_isChanged()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test", OBJECTTYPE.CFUNCTION, database);
            assertFalse(debugObject.isChanged(debugObject.getLatestInfo()));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testTTA_BL_DebugObject_handleChange()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            CommonLLTUtils.getColMetaDataPreScale(preparedstatementHandler);
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            DBConnection dbconn1 = CommonLLTUtils.getDBConnection();
            database.getConnectionManager().setObjBrowserConn(dbconn1);
            DebugObjects debugObject = new DebugObjects(1, "test", OBJECTTYPE.CFUNCTION, database);
            debugObject.handleChange(debugObject.getLatestInfo());
            assertEquals(debugObject.getSourceCode().getCode(), debugObject.getLatestInfo());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
