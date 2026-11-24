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

package org.opengauss.mppdbide.utils.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;

public class DSFilesWrapperTest {
    @Test
    public void test_filePathExist_001() {
        assertFalse(DSFilesWrapper.isExistingDirectory(null));
    }


    @Test
    public void test_filePathExist_002() {
        assertFalse(DSFilesWrapper.isExistingDirectory(""));
    }
    
    @Test
    public void test_filePathExist_003() {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isExist = DSFilesWrapper.isExistingDirectory("c:\\");
        if (os.contains("win")) {
            assertTrue(isExist);
        } else {
            assertFalse(isExist);
        }
    }

    @Test
    public void test_filePathExist_004() {
        assertFalse(DSFilesWrapper.isExistingDirectory("c:\\1.txt"));
    }

    @Test
    public void test_filePathExist_005() {
        try {
            String strPath = "c:\\";
            Path path = Paths.get(strPath);
            Path createPath = DSFilesWrapper.createDirectory(path, null);
            assertNotNull(createPath);
        } catch (FileOperationException e) {
            fail("can\'t be there!");
        }
    }
}
