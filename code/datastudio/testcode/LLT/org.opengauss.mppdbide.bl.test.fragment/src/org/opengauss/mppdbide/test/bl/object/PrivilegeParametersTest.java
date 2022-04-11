package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import org.opengauss.mppdbide.bl.export.PrivilegeParameters;
import org.opengauss.mppdbide.utils.files.DSFileAttributes;
import org.opengauss.mppdbide.utils.files.DSFilePermission;
import org.opengauss.mppdbide.utils.files.FileValidationUtils;

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
