/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.Properties;

import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.utils.MemoryCleaner;
import com.huawei.mppdbide.utils.security.SecureUtil;

/**
 * Title: ServerUtil
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 25-Jun-2020]
 * @since 25-Jun-2020
 */
public class ServerUtil {
    /**
     * Clear property details.
     *
     * @param props the props
     */
    public static void clearPropertyDetails(Properties props) {
        SecureUtil.cleanKeyString(props.getProperty("password"));
        props.setProperty("password", "");
        props.remove("password");
    }

    /**
     * Clear connection info.
     *
     * @param connInfo the conn info
     */
    public static void clearConnectionInfo(IServerConnectionInfo connInfo) {
        connInfo.clearPasrd();

        MemoryCleaner.cleanUpMemory();
    }
}
