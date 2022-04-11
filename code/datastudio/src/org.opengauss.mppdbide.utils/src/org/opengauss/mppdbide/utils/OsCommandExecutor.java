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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;

import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class OsCommandExecutor.
 *
 * @since 3.0.0
 */
public class OsCommandExecutor {
    private String[] command;
    private File workingDir;
    private boolean isFinished;
    private boolean isCancel;
    private Process process;
    private String[] processEnvironment;
    private String errorMessage;
    private String inputMessage;
    private static final String PERMISSION_ERROR = "permission denied for relation";
    private int processTimeout;
    private volatile boolean isStopThread = false;
    private OutputStreamGobbler outputGobbler;
    private InputStreamGobbler inputGobbler;
    private ErrorStreamGobbler errorGobbler;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final AtomicReference<Exception> thExceptionInSGobbler = new AtomicReference<Exception>();
    private final AtomicReference<Exception> thExceptionErrSGobbler = new AtomicReference<Exception>();

    /**
     * Instantiates a new os command executor.
     *
     * @param commands the commands
     * @param en the en
     * @param trackOutStream the track out stream
     * @param trackErrStream the track err stream
     * @param workingDir the working dir
     */
    public OsCommandExecutor(String[] commands, String[] en, File workingDir, int processTimeout) {
        this.command = commands.clone();
        if (en != null) {
            this.processEnvironment = en.clone();
        } else {
            this.processEnvironment = new String[0];
        }
        this.isFinished = false;
        process = null;
        this.workingDir = workingDir;
        this.processTimeout = processTimeout;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class InputStreamGobbler. This class captures error
     * messages of the new process (gs_dump), and handles accordingly in DS code
     */
    class InputStreamGobbler extends Thread {
        InputStream is;

        public InputStreamGobbler(InputStream is) {
            this.is = is;
        }

        /**
         * Run.
         */
        public void run() {

            StringBuilder strBuilder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            MPPDBIDELoggerUtility.trace("handleInputStream Start");
            try {
                while (false == isStopThread) {

                    strBuilder.append(readInputMsg());
                    inputMessage = strBuilder.toString();

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException interruptedException) {
                        MPPDBIDELoggerUtility.error("Sleep InterruptedException", interruptedException);
                    }
                }

            } catch (Exception exception) {
                MPPDBIDELoggerUtility.error("GS_DUMP_LOG : IO Exception while Reading gs dump", exception);

            } finally {
                MPPDBIDELoggerUtility.trace("handleInputStream Exit");
            }
            MPPDBIDELoggerUtility.info("GS_DUMP Received data: " + inputMessage);
        }

        private String readInputMsg() {
            StringBuilder strBlder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            BoundedInputStream bips = null;
            try {
                bips = new BoundedInputStream(this.is, 8192);
                bips.setPropagateClose(false);
                checkForInputStream(strBlder, bips);
            } catch (IOException ioException) {
                thExceptionErrSGobbler.set(ioException);
            } finally {
                try {
                    if (bips != null) {
                        bips.close();
                    }
                } catch (IOException ioException) {
                    MPPDBIDELoggerUtility.error("Error while reading input stream", ioException);
                }
            }
            return strBlder.toString();
        }

        private void checkForInputStream(StringBuilder strBuilder, BoundedInputStream bis) throws IOException {
            List<String> lines = IOUtils.readLines(bis, MPPDBIDEConstants.GS_DUMP_ENCODING);
            if (lines.size() != 0) {
                for (String line : lines) {
                    strBuilder.append(MPPDBIDEConstants.LINE_SEPARATOR).append(line);
                }
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class InputStreamGobbler. This class works to provide
     * user input to gs_dump, which it expects from DS For DS, it is handled by
     * an OutputStream
     */
    class OutputStreamGobbler extends Thread {
        char[] param;
        OutputStream os;

        public OutputStreamGobbler(OutputStream os, char[] cs) {
            this.os = os;
            this.param = cs;
        }

        /**
         * Sends output to process
         * 
         * @return void
         */
        public void run() {
            PrintWriter pw = null;
            Writer outwriter = null;
            if (os != null) {
                try {
                    outwriter = new OutputStreamWriter(os, MPPDBIDEConstants.GS_DUMP_ENCODING);
                } catch (UnsupportedEncodingException exception) {
                    MPPDBIDELoggerUtility.error("encoding is not supported", exception);
                    thExceptionInSGobbler.set(exception);
                    return;
                }
                pw = new PrintWriter(outwriter);
            }
            if (pw != null) {
                pw.println(param); // passed to the process
                pw.flush();
                pw.close();
            }
            MPPDBIDELoggerUtility.info("GS_DUMP Sent data");
        }
    }

    class ErrorStreamGobbler extends Thread {
        InputStream is;

        public ErrorStreamGobbler(InputStream is) {
            this.is = is;
        }

        /**
         * Run.
         */
        public void run() {

            StringBuilder strBuilder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            MPPDBIDELoggerUtility.trace("handleErrorStream Start");
            try {
                while (false == isStopThread) {

                    strBuilder.append(readErrorMsg());
                    errorMessage = strBuilder.toString();

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException interruptedException) {
                        MPPDBIDELoggerUtility.error("Sleep InterruptedException", interruptedException);
                    }
                }

            } catch (Exception exception) {
                MPPDBIDELoggerUtility.error("GS_DUMP_LOG : IO Exception while Reading gs dump", exception);

            } finally {
                MPPDBIDELoggerUtility.trace("handleErrorStream Exit");
            }
            MPPDBIDELoggerUtility.error("GS_DUMP Received data: " + errorMessage);
        }

        private String readErrorMsg() {
            StringBuilder strBuilder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            BoundedInputStream bis = null;
            try {
                bis = new BoundedInputStream(this.is, 8192);
                bis.setPropagateClose(false);
                checkForInputStream(strBuilder, bis);
            } catch (IOException ioException) {
                thExceptionErrSGobbler.set(ioException);
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException ioException) {
                    MPPDBIDELoggerUtility.error("Error while reading error stream", ioException);
                }
            }
            return strBuilder.toString();
        }

        private void checkForInputStream(StringBuilder strBuilder, BoundedInputStream bis) throws IOException {
            List<String> lines = IOUtils.readLines(bis, MPPDBIDEConstants.GS_DUMP_ENCODING);
            if (lines.size() != 0) {
                for (String line : lines) {
                    strBuilder.append(MPPDBIDEConstants.LINE_SEPARATOR).append(line);
                }
            }
        }
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Start.
     *
     * @param cs the cs
     * @throws DatabaseOperationException the database operation exception
     */
    public void start(char[] cs) throws DatabaseOperationException {
        try {
            process = Runtime.getRuntime().exec(command, processEnvironment, workingDir);
        } catch (IOException ioException) {
            setFinished(true);
            if (ioException.getMessage().equalsIgnoreCase("permission denied")) {
                MPPDBIDELoggerUtility.error("external utility gs_dump is not executable", ioException);
            }
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR), ioException);
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR);
        }

        inputStream = process.getInputStream();
        outputStream = process.getOutputStream();

        /* Start stream threads */
        captureStreams(cs);

        try {
            boolean processTimedout = process.waitFor(processTimeout, TimeUnit.SECONDS);

            isStopThread = true;

            // if exitValue is true then process is terminated, else it is
            // timeout
            if (processTimedout) {
                int exitValue = process.exitValue();

                // If returnValue is 0 then normal termination
                if (exitValue != 0) {

                    MPPDBIDELoggerUtility.info("GS_DUMP process exited abnormally");

                    if (null != errorMessage && errorMessage.contains("is not super or sysadmin role")) {
                        errorMessage = MessageConfigLoader
                                .getProperty(IMessagesConstants.BATCH_EXPORT_NO_USER_PREVILAGE);
                    }

                    if (null != errorMessage && errorMessage.contains(PERMISSION_ERROR)) {
                        errorMessage = MessageConfigLoader
                                .getProperty(IMessagesConstants.BATCH_EXPORT_NO_RELATION_PREVILAGE);
                    }
                    throwThreadException();
                    throwIntruptException();
                }
            } else {
                MPPDBIDELoggerUtility.info("GS_DUMP process timed out");
                errorMessage = MessageConfigLoader.getProperty(IMessagesConstants.PROCESS_TIMEOUT_ERROR);
                destroyProcess();

                throw new DatabaseOperationException(IMessagesConstants.PROCESS_TIMEOUT_ERROR);
            }

        } catch (InterruptedException iException) {
            errorMessage = iException.getMessage();
            throwIntruptException();
        } finally {
            stopCapturingStreams();
            setFinished(true);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private void throwThreadException() throws DatabaseOperationException {
        Exception exception = thExceptionErrSGobbler.get();
        if (exception != null) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR));
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR);
        }
        exception = thExceptionInSGobbler.get();
        if (exception != null) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR));
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR);
        }
    }

    private void captureStreams(char[] cs) {
        this.outputGobbler = new OutputStreamGobbler(process.getOutputStream(), cs);
        this.outputGobbler.start();

        /*
         * due to some reason, error prints from gs_dump is not appearing in
         * errorstream, it is coming in DS input pipe/new process outputstream.
         * And output prints from gs_dump is appearing in errorstream. So, to
         * read error messages, process errorstream is not redirected.
         */
        /*
         * gs_dump behaviour of input and error streams is different in both
         * windows and linux
         */

        this.inputGobbler = new InputStreamGobbler(this.process.getInputStream());
        this.errorGobbler = new ErrorStreamGobbler(this.process.getErrorStream());

        this.inputGobbler.start();
        this.errorGobbler.start();
    }

    private void stopCapturingStreams() {
        if (this.outputGobbler != null && this.outputGobbler.isAlive()) {
            try {
                this.outputGobbler.join();
            } catch (InterruptedException exception) {
                MPPDBIDELoggerUtility.error("error while stopping thread", exception);
            }
        }
        if (this.inputGobbler != null && this.inputGobbler.isAlive()) {
            try {
                this.inputGobbler.join();
            } catch (InterruptedException exception) {
                MPPDBIDELoggerUtility.error("error while stopping thread", exception);
            }
        }
        if (this.errorGobbler != null && this.errorGobbler.isAlive()) {
            try {
                this.errorGobbler.join();
            } catch (InterruptedException exception) {
                MPPDBIDELoggerUtility.error("error while stopping thread", exception);
            }
        }
    }

    /**
     * Destroy Process.
     */
    private void destroyProcess() {
        process.destroy();
        if (process.isAlive()) {
            process.destroyForcibly();
        }
    }

    /**
     * Throw intrupt exception.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    private void throwIntruptException() throws DatabaseOperationException {
        if (!isCancel()) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_INTRUPTED));
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_FAIL_PROCESS_INTRUPTED);
        } else {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CANCEL_ON_USER_REQUEST));
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_CANCEL_ON_USER_REQUEST);
        }
    }

    /**
     * Checks if is finished.
     *
     * @return true, if is finished
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Sets the finished.
     *
     * @param isFinishd the new finished
     */
    public void setFinished(boolean isFinishd) {
        this.isFinished = isFinishd;
    }

    /**
     * Checks if is cancel.
     *
     * @return true, if is cancel
     */
    public boolean isCancel() {
        return isCancel;
    }

    /**
     * Cancel.
     */
    public void cancel() {
        this.isCancel = true;
        if (!isFinished() && null != process) {
            process.destroy();
        }
    }

}
