package com.hauwei.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.junit.Test;

import com.huawei.mppdbide.utils.stringparse.IServerMessageParseUtils;

public class IServerMessageParseUtilsTest {
    
    private static String SERVER_ERROR_MESSAGE = "errorCode=GS-00944\n" + 
            "SQLState='28000'\n" + 
            "errMsg=PL/SQL(DSUSER.ANONYMOUS BLOCK) terminated with compiling errors\n" + 
            "[1:2] PLC-00944 PL/SQL(DSUSER.WELCOME_MSG) terminated with compiling errors\n" + 
            "[4:1] PLC-00920 Undefined symbol aaa\n" + 
            "ioClient:@200c1349\n" + 
            "sql=EXEC DSUSER.\"WELCOME_MSG(\"\n" + 
            "(\n" + 
            "    'abc'   --P_NAME VARCHAR\n" + 
            ")\n" + 
            "sessionId=56\n" + 
            "serverIP=10.18.105.57:1525\n" + 
            "clientIP=10.18.151.75.";
    
    private static String SERVER_ERROR_MESSAGE_WITH_SELECT = "errorCode=GS-00944\n" + 
            "SQLState='28000'\n" + 
            "errMsg=PL/SQL(DSUSER.ANONYMOUS BLOCK) terminated with compiling errors\n" + 
            "[1:2] PLC-00944 PL/SQL(DSUSER.WELCOME_MSG) terminated with compiling errors\n" + 
            "[4:1] PLC-00920 Undefined symbol aaa\n" + 
            "ioClient:@200c1349\n" + 
            "sql=SELECT * from abc\n" +  
            "sessionId=56\n" + 
            "serverIP=10.18.105.57:1525\n" + 
            "clientIP=10.18.151.75.";
    
    private static String SELECT_QUERY_WITH_COMMENTS = "/* abcd */ SELECT 1";
    private static String EXEC_QUERY_WITH_COMMENTS = "/* AS\n" + 
            "ASASAA */EXEC DSUSER.\"abc / **\"('ABC');\n" + 
            "/* ABC */";
    
    private static String SELECT_FUNCTION = "SELECT DSUSER.\"ZTEST_F1\"\n" + 
            "(\n" + 
            "    10, --A BINARY_INTEGER\n" + 
            "    'abc'   --B VARCHAR\n" + 
            ")\n" + 
            " as \"ZTEST_F1\" FROM DUAL";
    
    @Test
    public void test_should_return_query_when_server_error_message_is_inputted()
    {
        String query = IServerMessageParseUtils.extractQueryFromErrorMessage(SERVER_ERROR_MESSAGE);
        assertEquals("EXEC DSUSER.\"WELCOME_MSG(\"\n" + 
                "(\n" + 
                "    'abc'   --P_NAME VARCHAR\n" + 
                ")", query);
    }
    
    @Test
    public void test_should_return_query_when_server_error_message_is_inputted_with_simple_select_query()
    {
        String query = IServerMessageParseUtils.extractQueryFromErrorMessage(SERVER_ERROR_MESSAGE_WITH_SELECT);
        assertEquals("SELECT * from abc", query);
    }
    
    @Test
    public void test_should_return_query_without_comments_when_query_with_select_comments_is_passed()
    {
        String extractQueryWithoutComments = IServerMessageParseUtils.extractQueryWithoutComments(SELECT_QUERY_WITH_COMMENTS);
        assertEquals("SELECT 1", extractQueryWithoutComments);
    }
    
    @Test
    public void test_should_return_query_without_comments_when_query_with_exec_comments_is_passed()
    {
        String extractQueryWithoutComments = IServerMessageParseUtils.extractQueryWithoutComments(EXEC_QUERY_WITH_COMMENTS);
        assertEquals("EXEC DSUSER.\"abc / **\"('ABC');\n/* ABC */", extractQueryWithoutComments);
    }
    
    @Test
    public void test_should_return_true_when_select_function_is_passed()
    {
        assertTrue(IServerMessageParseUtils.isQuerySelectFunction(SELECT_FUNCTION));
    }
}
