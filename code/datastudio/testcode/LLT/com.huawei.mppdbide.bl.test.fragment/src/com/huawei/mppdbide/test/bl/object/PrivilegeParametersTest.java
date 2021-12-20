package com.huawei.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.huawei.mppdbide.bl.export.PrivilegeParameters;
import com.huawei.mppdbide.utils.files.DSFileAttributes;
import com.huawei.mppdbide.utils.files.DSFilePermission;
import com.huawei.mppdbide.utils.files.FileValidationUtils;

public class PrivilegeParametersTest {
    
    @Test
    public void test_setPrevilegeWithGrantBuff() {
        PrivilegeParameters privParam = new PrivilegeParameters();
        StringBuffer st = new StringBuffer("prvParam");
        privParam.setPrevilegeWithGrantBuff(st);
    } 
    
   @Test 
   public void test_setPrevilegeBuff() {
       PrivilegeParameters privParam = new PrivilegeParameters();
       StringBuffer st = new StringBuffer("prvParam");
       privParam.setPriviledgeBuff(st);
   }  
   
   @Test
   public void test_fileValidationUtilTest() {
       String fileName = "testfile";
       assertEquals(true, FileValidationUtils.validateFileName(fileName));
   }
   
   @Test
   public void test_filePathValidationTest() {
       String fileName = "D:\\testfile";
       assertEquals(true, FileValidationUtils.validateFilePathName(fileName));
   }
   
   @Test
   public void testTTA_file_test_001() {
       DSFileAttributes attr = new DSFileAttributes(null);
       assertNotNull(attr.value());
       assertEquals("posix:permissions", attr.name());
       assertNotNull(DSFilePermission.getDefaultPermission());
   }

}
