/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.gauss;

import java.sql.SQLWarning;

import org.postgresql.core.NoticeListener;

import com.huawei.mppdbide.utils.messaging.IMessageQueue;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.utils.messaging.MessageType;

/**
 * 
 * Title: class
 * 
 * Description: The Class GaussMppDbNoticeListner.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GaussMppDbNoticeListner implements NoticeListener {
    private IMessageQueue msgQ;

    /**
     * Instantiates a new gauss mpp db notice listner.
     *
     * @param messageQueue the message queue
     */
    public GaussMppDbNoticeListner(MessageQueue messageQueue) {
        msgQ = messageQueue;
    }

    @Override
    public void noticeReceived(SQLWarning notice) {
        if (null == notice || null == notice.getMessage() || null == msgQ) {
            return;
        }

        String msgString = notice.getMessage();
        Message msg = new Message(MessageType.NOTICE, msgString);
        msgQ.push(msg);
    }

}
