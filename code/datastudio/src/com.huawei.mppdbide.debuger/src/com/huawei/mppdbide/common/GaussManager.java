/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.common;

import org.postgresql.core.BaseStatement;
import org.postgresql.core.NoticeListener;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * 
 * Title: GaussManager for use
 * 
 * Description: GaussManager to add default listener to PreparedStatement
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00589921
 * @version [DataStudio for openGauss 1.0.0, 19 Sep, 2019]
 * @since 19 Sep, 2019
 */
public class GaussManager {
    public static final GaussManager INSTANCE = new GaussManager();
    private GaussManager() {}

    public boolean addNoticeListener(PreparedStatement ps) {
        return addNoticeListener(ps, getNoticeListener());
    }
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

    public NoticeListener getNoticeListener() {
        return new GaussNoticeListener();
    }

    private static class GaussNoticeListener implements NoticeListener {

        @Override
        public void noticeReceived(SQLWarning notice) {
            if (null == notice || null == notice.getMessage()) {
                return;
            }
            String msgString = notice.getMessage();
            MPPDBIDELoggerUtility.info("default sql message:" + msgString);
        }
    }

}
