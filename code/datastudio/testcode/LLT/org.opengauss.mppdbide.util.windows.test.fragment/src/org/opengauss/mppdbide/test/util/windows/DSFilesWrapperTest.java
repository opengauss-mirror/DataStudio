/*
 * Copyright (c) 2026 Huawei Technologies Co., Ltd.
 * DataStudio is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.test.util.windows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.files.DSFileAttributes;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;

public class DSFilesWrapperTest
{
    private static final String OS_NAME = "os.name";
    private static final String LINUX = "Linux";

    private boolean isLinux() {
        if ((System.getProperty(OS_NAME)).contains(LINUX)) {
            return true;
        }
        return false;
    }
    
    @Test
    public void testTTA_file_test_001()
    {
        Path folderPath = Paths.get(".", "User2");
        Path p = null;
        try
        {
            p = DSFilesWrapper.createDirectory(folderPath, null);
        }
        catch (Exception e)
        {
            assertTrue(true);
        }

        assertNull(p);
    }

    @Test
    public void testTTA_file_test_002()
    {
        Path folderPath = Paths.get(".", "User2");
        Path p = null;
        try
        {
            p = DSFilesWrapper.createFile(folderPath, null);
        }
        catch (Exception e)
        {
            assertTrue(true);
        }

        assertNull(p);
    }

    @Test
    public void testTTA_file_test_003()
    {
        if (isLinux())
        {
            return;
        }
        Path filePath = Paths.get(".");
        Object attrib = null;
        try
        {
            attrib = Files.getAttribute(filePath, "acl:acl");
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        assertTrue(attrib instanceof List<?>);
        Set<AclEntryPermission> myFilePerm = new HashSet<AclEntryPermission>();

        FileAttribute<List<AclEntry>> defaultAttrib = new DSFileAttributes(myFilePerm);

        Path folderPath = Paths.get("\\Desktop", "User");
        Path p = null;
        try {
            p = Files.createDirectory(folderPath, defaultAttrib);
        } catch (IOException e) {
            fail("fail");
        }

        assertNotNull(p);
    }

    @Test
    public void testTTA_file_test_004()
    {
        if (isLinux())
        {
            return;
        }
        
        Path filePath = Paths.get(".");
        Object attrib = null;
        try
        {
            attrib = Files.getAttribute(filePath, "acl:acl");
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        assertTrue(attrib instanceof List<?>);
        Set<AclEntryPermission> myFilePerm = new HashSet<AclEntryPermission>();

        FileAttribute<List<AclEntry>> defaultAttrib = new DSFileAttributes(myFilePerm);

        Path folderPath = Paths.get("\\Desktop", "User");
        Path p = null;
        try {
            p = Files.createFile(folderPath, defaultAttrib);
        } catch (IOException e) {
            fail("fail");
        }

        assertNotNull(p);
    }
}
