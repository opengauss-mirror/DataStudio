package com.hauwei.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;

public class ProgressBarLabelFormatterTest
{
    @Test
    public void test_getProgressLabelForTableWithMsg()
    {
        String progressLabelForTableWithMsg = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg("abc", "def",
                "ghi", "jkl", IMessagesConstants.ABOUT_DATA_STUDIO_MSG);
        String msgParam = "abc" + '.' + "def" + '.' + "ghi" + '@' + "jkl";
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.ABOUT_DATA_STUDIO_MSG, msgParam);
        assertEquals(progressLabelForTableWithMsg, progressLabel);
    }
    
    @Test
    public void test_getProgressLabelForTableWithoutMsg()
    {
        String progressLabelForTableWithoutMsg = ProgressBarLabelFormatter.getProgressLabelForTableWithoutMsg("abc", "def",
                "ghi", "jkl");
        String msgParam = "abc" + '.' + "def" + '.' + "ghi" + '@' + "jkl";
        assertEquals(progressLabelForTableWithoutMsg, msgParam);
    }
    
    @Test
    public void test_getProgressLabelForDatabase()
    {
        String progressLabelForDatabase = ProgressBarLabelFormatter.getProgressLabelForDatabase("abc", "def", IMessagesConstants.ABOUT_DATA_STUDIO_MSG);
        String msgParam = "abc" + '@' + "def";
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.ABOUT_DATA_STUDIO_MSG, msgParam);
        assertEquals(progressLabelForDatabase, progressLabel);
    }
    
    @Test
    public void test_getProgressLabelForSchema()
    {
        String progressLabelForSchema = ProgressBarLabelFormatter.getProgressLabelForSchema("abc", "def", "ghi", IMessagesConstants.ABOUT_DATA_STUDIO_MSG);
        String msgParam = "abc" + '.' + "def" + '@' + "ghi";
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.ABOUT_DATA_STUDIO_MSG, msgParam);
        assertEquals(progressLabelForSchema, progressLabel);
    }
    
    @Test
    public void test_getProgressLabelForColumn()
    {
        String progressLabelForColumn = ProgressBarLabelFormatter.getProgressLabelForColumn("abc", "def", "ghi", "jkl",
                "mno", IMessagesConstants.ABOUT_DATA_STUDIO_MSG);
        String msgParam = "abc" + '.' + "def" + '.' + "ghi" + '.' + "jkl" + '@' + "mno";
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.ABOUT_DATA_STUDIO_MSG, msgParam);
        assertEquals(progressLabelForColumn, progressLabel);
    }
    
    @Test
    public void test_getProgressLabelForView()
    {
        String progressLabelForView = ProgressBarLabelFormatter.getProgressLabelForView("abc", "def", "ghi", "jkl",
                IMessagesConstants.ABOUT_DATA_STUDIO_MSG);
        String msgParam = "abc" + '.' + "def" + '.' + "ghi" + '@' + "jkl";
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.ABOUT_DATA_STUDIO_MSG, msgParam);
        assertEquals(progressLabelForView, progressLabel);
    }
    
    @Test
    public void test_getProgressLabelForBatchExport()
    {
        String progressLabelForBatchExport = ProgressBarLabelFormatter
                .getProgressLabelForBatchExport(IMessagesConstants.ABOUT_DATA_STUDIO_MSG, 10, 100, "def", "ghi", "jkl");
        StringBuilder builder =
                new StringBuilder(" (" + 10 + "/" + 100 + "): ");
        builder.append("def" + "." + "ghi" + "@" + "jkl");
        String details = builder.toString();
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.ABOUT_DATA_STUDIO_MSG, details);
        assertEquals(progressLabelForBatchExport, progressLabel);
    }
    
    @Test
    public void test_getProgressLabelForUserRole()
    {
        String progressLabelForUserRole = ProgressBarLabelFormatter.getProgressLabelForUserRole("abc", "def");
        String msgParam = "abc" + '@' + "def";
        assertEquals(progressLabelForUserRole, msgParam);
    }
}