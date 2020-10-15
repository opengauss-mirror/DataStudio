/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.util;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecTimer.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ExecTimer implements IExecTimer {

    private String activityName;
    private long startTime;
    private long stopTime;
    private static final String TIME_UNIT = " ms";

    /**
     * Instantiates a new exec timer.
     *
     * @param name the name
     */
    public ExecTimer(String name) {
        this.activityName = name;
        startTime = 0;
        stopTime = 0;
    }

    @Override
    public IExecTimer start() {
        startTime = System.currentTimeMillis();
        stopTime = 0;
        if (MPPDBIDELoggerUtility.isTraceEnabled()) {
            MPPDBIDELoggerUtility.trace("Timer started for :" + activityName);
        }

        return this;
    }

    @Override
    public void stop() throws DatabaseOperationException {
        this.stopTime = System.currentTimeMillis();
        if (0 == startTime) {
            MPPDBIDELoggerUtility.error("ExecTimer not started.");
            throw new DatabaseOperationException("ExecTimer not started.");
        }
    }

    @Override
    public void logTime() throws DatabaseOperationException {
        if (0 == startTime || 0 == stopTime) {
            MPPDBIDELoggerUtility.error("ExecTimer not started.");
            throw new DatabaseOperationException("ExecTimer not started.");
        }

        long elapsedTime = stopTime - startTime;
        if (MPPDBIDELoggerUtility.isTraceEnabled()) {
            MPPDBIDELoggerUtility.trace("ExecTimer [" + this.activityName + "] " + elapsedTime + TIME_UNIT);
        }
    }

    @Override
    public void stopAndLog() throws DatabaseOperationException {
        this.stop();
        this.logTime();
    }

    @Override
    public void stopAndLogNoException() {
        try {
            stopAndLog();
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Timer stop failed while execute cursor.", exception);
        }
    }

    /**
     * Checks if is timer stop.
     *
     * @return true, if is timer stop
     */
    @Override
    public boolean isTimerStop() {
        if (this.stopTime != 0) {
            return true;
        }
        return false;
    }

    /**
     * Gets the elapsed time.
     *
     * @return the elapsed time
     * @throws DatabaseOperationException the database operation exception
     */
    @Override
    public String getElapsedTime() throws DatabaseOperationException {
        if (0 == startTime || 0 == stopTime) {
            MPPDBIDELoggerUtility.error("ExecTimer not started.");
            throw new DatabaseOperationException("ExecTimer not started.");
        }

        long elapsedTime = stopTime - startTime;
        long diffDays = elapsedTime / (24 * 60 * 60 * 1000);
        long diffHours = elapsedTime / (60 * 60 * 1000) % 24;
        long diffMinutes = elapsedTime / (60 * 1000) % 60;
        long diffSeconds = elapsedTime / 1000 % 60;

        StringBuilder str = calculateElapsedTime(elapsedTime, diffDays, diffHours, diffMinutes, diffSeconds);

        return str.toString();
    }

    /**
     * Calculate elapsed time.
     *
     * @param elapsedTime the elapsed time
     * @param diffDays the diff days
     * @param diffHours the diff hours
     * @param diffMinutes the diff minutes
     * @param diffSeconds the diff seconds
     * @return the string builder
     */
    private static StringBuilder calculateElapsedTime(long elapsedTime, long diffDays, long diffHours, long diffMinutes,
            long diffSeconds) {
        StringBuilder str = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (diffDays > 0) {
            str.append(diffDays + " days");
        }

        if (diffHours > 0) {
            str.append(str.length() > 0 ? ", " : " ");
            str.append(diffHours + " hours");
        }

        if (diffMinutes > 0) {
            str.append(str.length() > 0 ? ", " : " ");
            str.append(diffMinutes + " mins");
        }

        if (diffSeconds > 0) {
            str.append(str.length() > 0 ? ", " : " ");
            str.append(diffSeconds + " sec");
        }
        if (elapsedTime >= 0 && diffSeconds <= 0 && diffMinutes <= 0 && diffHours <= 0 && diffDays <= 0) {
            str.append(elapsedTime + " ms");
        }
        return str;
    }

    /**
     * Gets the elapsed time with units.
     *
     * @param totalTime the total time
     * @return the elapsed time with units
     */
    public static String getElapsedTimeWithUnits(long totalTime) {

        long elapsedTime = totalTime;
        long diffSeconds = elapsedTime / 1000 % 60;
        long diffMinutes = elapsedTime / (60 * 1000) % 60;
        long diffHours = elapsedTime / (60 * 60 * 1000) % 24;
        long diffDays = elapsedTime / (24 * 60 * 60 * 1000);

        StringBuilder elapsedTmUnits = calculateElapsedTime(elapsedTime, diffDays, diffHours, diffMinutes, diffSeconds);

        return elapsedTmUnits.toString();
    }

    /**
     * Gets the elapsed time in ms.
     *
     * @return the elapsed time in ms
     * @throws DatabaseOperationException the database operation exception
     */
    @Override
    public long getElapsedTimeInMs() throws DatabaseOperationException {
        if (0 == startTime || 0 == stopTime) {
            MPPDBIDELoggerUtility.error("ExecTimer not started.");
            throw new DatabaseOperationException("ExecTimer not started.");
        }

        long elapsedTime = stopTime - startTime;
        return elapsedTime;
    }

    @Override
    public String getDynamicElapsedTime(boolean isVisible) {
        if (this.startTime == 0) {
            return "";
        }

        long now = System.currentTimeMillis();
        if (isVisible) {
            return getElapsedTimeWithUnits(now - this.startTime);
        }
        return "";
    }
}
