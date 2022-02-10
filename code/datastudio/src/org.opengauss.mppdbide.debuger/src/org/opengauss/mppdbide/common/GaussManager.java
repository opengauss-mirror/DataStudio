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

package org.opengauss.mppdbide.common;

import org.postgresql.core.BaseStatement;
import org.postgresql.core.NoticeListener;

import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * Title: GaussManager for use
 * Description: GaussManager to add default listener to PreparedStatement
 *
 * @since 3.0.0
 */
public class GaussManager {
    /**
     * singleton instance
     */
    public static final GaussManager INSTANCE = new GaussManager();

    private GaussManager() {
    }

    /**
     * add noticeListener to PreparedStatement object
     *
     * @param ps listener to set
     * @return boolean true if success
     */
    public boolean addNoticeListener(PreparedStatement ps) {
        return addNoticeListener(ps, getNoticeListener());
    }

    /**
     * add noticeListener to PreparedStatement object
     *
     * @param ps listener to set
     * @param listener the listener
     * @return boolean true if success
     */
    public boolean addNoticeListener(PreparedStatement ps, NoticeListener listener) {
        if (ps instanceof BaseStatement) {
            try {
                ((BaseStatement) ps).addNoticeListener(listener);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * get default noticeListener to PreparedStatement object
     *
     * @return NoticeListener instance of listener
     */
    public NoticeListener getNoticeListener() {
        return new GaussNoticeListener();
    }

    private static class GaussNoticeListener implements NoticeListener {
        @Override
        public void noticeReceived(SQLWarning notice) {
            if (notice == null || notice.getMessage() == null) {
                return;
            }
            String msgString = notice.getMessage();
            MPPDBIDELoggerUtility.info("default sql message:" + msgString);
        }
    }

}
