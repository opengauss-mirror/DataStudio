package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;

public class ServerObjectTest extends BasicJDBCTestCaseAdapter
{

    @Test
    public void testTTA_ServerObject_getTypeLabel()
    {
        
           try
           {
               ServerObject serObj = new ServerInst(OBJECTTYPE.PROCEDURE);
               assertNotNull(serObj.getTypeLabel());
           }

           catch (Exception e)
           {
               e.printStackTrace();
           }
    }
    
    @Test
    public void testTTA_ServerObject_isQualifiedPartitionValue()
    {
        String nullValue = ServerObject.isQualifiedPartitionValue(null);
        assertEquals("", nullValue);
        String value = ServerObject.isQualifiedPartitionValue("Test");
        assertEquals("'Test'",value);
    }
    
    @Test
    public void testTTA_ServerObject_getDisplayLabel()
    {
        ServerInst server = new ServerInst(OBJECTTYPE.PROCEDURE);
        server.setName("MyTest");
        assertEquals("MyTest", server.getDisplayLabel());
        assertEquals(false, server.isLoadingInProgress());
        assertEquals("MyTest", server.getObjectBrowserLabel());
        assertEquals(true, server.isLoaded());
    }
    
  /*  @Test
    public void testTTA_ServerObject_getDisplayLabelNull()
    {
        ServerInst server = new ServerInst(OBJECTTYPE.PROCEDURE);
        server.setName("\0");
        assertEquals("\0", server.getDisplayLabel());
    }*/
    
    private final class ServerInst extends ServerObject
    {
        public ServerInst(OBJECTTYPE type)
        {
            super(type);
        }
    }
    
}
