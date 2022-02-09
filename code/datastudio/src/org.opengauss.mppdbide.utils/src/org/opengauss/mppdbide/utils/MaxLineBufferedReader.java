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

package org.opengauss.mppdbide.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class MaxLineBufferedReader.
 *
 * @since 3.0.0
 */
public class MaxLineBufferedReader extends BufferedReader {
    private static final int DEFAULT_MAX_LINE_LENGTH = 8192;
    private int maxLineLen;
    private boolean wholeLineFlag = true;
    private static final int CHAR_CR = 13;
    private static final int CHAR_LF = 10;

    /**
     * Instantiates a new max line buffered reader.
     *
     * @param reader the reader
     */
    public MaxLineBufferedReader(InputStreamReader reader) {
        super(reader);
        maxLineLen = DEFAULT_MAX_LINE_LENGTH;
    }

    /**
     * Instantiates a new max line buffered reader.
     *
     * @param reader the reader
     */
    public MaxLineBufferedReader(Reader reader) {
        super(reader);
        maxLineLen = DEFAULT_MAX_LINE_LENGTH;
    }

    /**
     * Checks if is whole line.
     *
     * @return true, if is whole line
     */
    public boolean isWholeLine() {
        return this.wholeLineFlag;
    }

    /**
     * Instantiates a new max line buffered reader.
     *
     * @param reader the reader
     * @param lineLengthLimit the line length limit
     */
    public MaxLineBufferedReader(InputStreamReader reader, int lineLengthLimit) {
        super(reader);
        maxLineLen = lineLengthLimit;
    }

    /**
     * Read max len line.
     *
     * @return the string
     * @throws DatabaseOperationException the database operation exception
     */
    public String readMaxLenLine() throws DatabaseOperationException {
        try {
            int currentPos = 0;
            char[] line = new char[maxLineLen];
            int currentChar;
            currentChar = super.read();

            while ((currentChar != CHAR_CR) && (currentChar != CHAR_LF) && (currentChar >= 0)) {
                line[currentPos++] = (char) currentChar;

                if (currentPos < maxLineLen) {
                    currentChar = super.read();
                } else {
                    break;
                }
            }
            this.wholeLineFlag = true;

            if (currentChar < 0) {
                // End of file
                if (currentPos > 0) {
                    // Return last line
                    return new String(line, 0, currentPos);
                } else {
                    return null;
                }
            } else {
                // Remove newline characters from the buffer
                if (currentChar == CHAR_CR) {
                    // Check for LF and remove from buffer
                    super.mark(1);
                    if (super.read() != CHAR_LF) {
                        super.reset();
                    }
                } else if (currentChar != CHAR_LF) {
                    this.wholeLineFlag = false;
                    // maxLineLen has been hit, but still need to
                    // remove newline characters.
                    super.mark(1);
                    int nextChar = super.read();
                    if (nextChar == CHAR_CR) {
                        super.mark(1);
                        if (super.read() != CHAR_LF) {
                            super.reset();
                        }
                    } else if (nextChar != CHAR_LF) {
                        super.reset();
                    }
                }
                return new String(line, 0, currentPos);
            }
        } catch (IOException exp) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_READING_DATA), exp);
            throw new DatabaseOperationException(IMessagesConstants.ERROR_READING_DATA);
        }
    }
}
