/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.gauss;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.adapter.AbstractConnectionDriver;
import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class GaussUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GaussUtils {

    private static final String CRITICAL_ERROR_57P = "57P";
    private static final String IGNORE_BIND_ERROR = "bind message";
    private static final String IGNORE_FUN_CALL_ERROR = "function call message contains";
    private static final String IGNORE_DESC_ERROR = "invalid DESCRIBE message subtype";
    private static final String SESSION_KILLED = "Session killed";

    private static final String OBJECT_NOT_FOUND_ERROR = "does not exist";

    private static List<String> criticalErrorCodeList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

    static {
        criticalErrorCodeList.add("08006");
        criticalErrorCodeList.add("08003");
        criticalErrorCodeList.add("08P01");
        criticalErrorCodeList.add("53300");
        criticalErrorCodeList.add("08001");
    }

    /**
     * Handle critical exception.
     *
     * @param excep the e
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void handleCriticalException(SQLException excep) throws DatabaseCriticalException {
        if (excep.getLocalizedMessage() != null && excep.getLocalizedMessage().contains(SESSION_KILLED)) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_EXECUTE_FAILED),
                    excep);
            throw new DatabaseCriticalException(IMessagesConstants.ERR_BL_EXECUTE_FAILED, excep);
        }
        String sqlState = excep.getSQLState();
        if ((sqlState != null)
                && (sqlState.startsWith(CRITICAL_ERROR_57P) || criticalErrorCodeList.contains(sqlState))) {
            if ("08P01".equalsIgnoreCase(sqlState) && null != excep.getMessage()
                    && (excep.getMessage().contains(IGNORE_BIND_ERROR)
                            || excep.getMessage().contains(IGNORE_FUN_CALL_ERROR)
                            || excep.getMessage().contains(IGNORE_DESC_ERROR))
                    || excep.getMessage().contains("Protocol error.")) {
                return;
            }
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_EXECUTE_FAILED),
                    excep);
            throw new DatabaseCriticalException(IMessagesConstants.ERR_BL_EXECUTE_FAILED, excep);
        }
    }

    /**
     * Checks if is object not found err.
     *
     * @param eex the e
     * @return true, if is object not found err
     */
    public static boolean isObjectNotFoundErr(SQLException eex) {
        String sqlState = eex.getSQLState();
        if (sqlState != null && sqlState.equals("42704") && null != eex.getMessage()
                && eex.getMessage().contains(OBJECT_NOT_FOUND_ERROR)) {
            return true;
        }

        return false;
    }

    /**
     * Checks if is protocol version needed.
     *
     * @param driver the driver
     * @return true, if is protocol version needed
     */
    public static boolean isProtocolVersionNeeded(AbstractConnectionDriver driver) {
        return (null != driver) && CustomStringUtility.isProtocolVersionNeeded(driver.getDriverName());
    }
}
