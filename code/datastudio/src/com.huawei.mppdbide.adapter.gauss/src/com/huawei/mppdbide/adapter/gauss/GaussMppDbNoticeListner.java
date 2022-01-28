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
 * @since 3.0.0
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
