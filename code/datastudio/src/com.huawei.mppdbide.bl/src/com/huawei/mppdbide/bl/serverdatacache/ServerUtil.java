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

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.Properties;

import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.utils.MemoryCleaner;
import com.huawei.mppdbide.utils.security.SecureUtil;

/**
 * Title: ServerUtil
 *
 * @since 3.0.0
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
