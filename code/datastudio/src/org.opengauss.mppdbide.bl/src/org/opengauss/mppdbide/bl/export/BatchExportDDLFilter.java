/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.bl.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.input.BOMInputStream;

import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.MaxLineBufferedReader;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchExportDDLFilter.
 * 
 */

public class BatchExportDDLFilter {

    private static final int MAX_LINE_LEN = 8192;
    final BufferedOutputStream bfs;
    String encoding = null;

    private static final String[] LINESTOREMOVE = {"PostgreSQL database dump", "Dumped from database version",
        "Dumped by gs_dump version", "SET statement_timeout", "SET client_encoding", "SET standard_conforming_strings",
        "SET check_function_bodies", "SET client_min_messages", "SET default_with_oids"};
    /*
     * if any line in LINESTOREMOVE has length grater than MAX_CHAR_LEN,
     * accept() function has to change
     */
    private static final String SINGLE_LINE_COMMENT_PREFIX = "--";

    private static boolean stringContainsItemFromList(final String inputStr) {
        return Arrays.stream(LINESTOREMOVE).parallel().anyMatch(new Predicate<String>() {

            @Override
            public boolean test(String str) {
                return inputStr.contains(str);
            }
        });
    }

    /**
     * Instantiates a new batch export DDL filter.
     *
     * @param fs the fs
     */
    public BatchExportDDLFilter(BufferedOutputStream fs) {
        this.bfs = fs;
        String fileEncoding = BLPreferenceManager.getInstance().getBLPreference().getFileEncoding();
        encoding = fileEncoding.isEmpty() ? Charset.defaultCharset().name() : fileEncoding;
    }

    private boolean test(String line) {
        return !stringContainsItemFromList(line) && !line.equals(SINGLE_LINE_COMMENT_PREFIX);
    }

    private void accept(List<String> lines) throws IOException {
        boolean testfail = false;
        for (String str : lines) {
            if (!test(str)) {
                testfail = true;
            }
        }
        if (testfail) {
            return;
        }
        for (String s : lines) {
            byte[] bytes = s.getBytes(encoding);
            bfs.write(bytes);
        }

        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(MPPDBIDEConstants.LINE_SEPARATOR);
        byte[] bytes = sb.toString().getBytes(encoding);
        bfs.write(bytes);
    }

    /**
     * Removes the comments.
     *
     * @param filepath the filepath
     * @throws FileOperationException the file operation exception
     */
    public void removeComments(String filepath) throws FileOperationException {
        File file = new File(filepath);

        FileInputStream input = null;
        MaxLineBufferedReader mf = null;
        try {
            input = new FileInputStream(file);
            mf = readFile(input);
        } catch (IOException e) {
            MPPDBIDELoggerUtility.debug("BatchExportDDLFilter: Error while removing comments");
            throw new FileOperationException(IMessagesConstants.ERR_IO_ERROR_EXPORT);
        } catch (DatabaseOperationException e) {
            MPPDBIDELoggerUtility.debug("BatchExportDDLFilter: Error while removing comments");
            throw new FileOperationException(IMessagesConstants.ERR_IO_ERROR_EXPORT);
        } finally {
            try {
                if (mf != null) {
                    mf.close();
                }
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("BatchExportDDLFilter: Error while closing max line buffered reader",
                        exception);
            }

            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("BatchExportDDLFilter: Error while closing stream", exception);
            }

            try {
                Files.deleteIfExists(Paths.get(filepath));
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("BatchExportDDLFilter:  Error while deleting file", exception);
            }
        }
    }

    private MaxLineBufferedReader readFile(FileInputStream input) throws DatabaseOperationException, IOException {
        MaxLineBufferedReader mf = null;
        CharsetDecoder decoder = Charset.forName(encoding).newDecoder();
        if (decoder != null) {
            decoder.onMalformedInput(CodingErrorAction.IGNORE);
            BOMInputStream bomInputStream = new BOMInputStream(input);
            InputStreamReader reader = new InputStreamReader(bomInputStream, decoder);
            mf = new MaxLineBufferedReader(reader, MAX_LINE_LEN);

            String line;
            List<String> readlines = new ArrayList<String>(5);

            do {
                line = mf.readMaxLenLine();
                if (line == null) {
                    break;
                }
                readlines.add(line);
                while (!mf.isWholeLine()) {
                    line = mf.readMaxLenLine();
                    if (line == null) {
                        break;
                    }
                    readlines.add(line);
                }
                this.accept(readlines);
                readlines.clear();

            } while (true);
        }
        return mf;
    }

    /**
     * Close output stream.
     */
    public void closeOutputStream() {
        if (this.bfs != null) {
            try {
                this.bfs.close();
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("byte array outputStream not closed", exception);
            }
        }
    }
}
