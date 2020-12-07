package com.huawei.mppdbide.common;

import org.postgresql.core.BaseStatement;
import org.postgresql.core.NoticeListener;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;

/**
 * author: z00588921
 * date: 2020/9/14
 * descript:
 */
public class GaussManager {
    public static final GaussManager instance = new GaussManager();
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
