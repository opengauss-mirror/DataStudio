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

package org.opengauss.mppdbide.utils.messaging;

import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class Message.
 *
 * @since 3.0.0
 */
public class Message {
    private MessageType type;
    private String message;

    /**
     * Instantiates a new message.
     *
     * @param type the type
     * @param message the message
     */
    public Message(MessageType type, String message) {
        super();
        this.type = type;
        this.message = message;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the info.
     *
     * @param msg the msg
     * @return the info
     */
    public static Message getInfo(String msg) {
        return new Message(MessageType.INFO, msg);
    }

    /**
     * Gets the warn.
     *
     * @param msg the msg
     * @return the warn
     */
    public static Message getWarn(String msg) {
        return new Message(MessageType.WARN, msg);
    }

    /**
     * Gets the error.
     *
     * @param msg the msg
     * @return the error
     */
    public static Message getError(String msg) {
        return new Message(MessageType.ERROR, msg);
    }

    /**
     * Gets the info from const.
     *
     * @param msgConst the msg const
     * @return the info from const
     */
    public static Message getInfoFromConst(String msgConst) {
        return getInfo(MessageConfigLoader.getProperty(msgConst));
    }

    /**
     * Gets the warn from const.
     *
     * @param msgConst the msg const
     * @return the warn from const
     */
    public static Message getWarnFromConst(String msgConst) {
        return getWarn(MessageConfigLoader.getProperty(msgConst));
    }

    /**
     * Gets the error from const.
     *
     * @param msgConst the msg const
     * @return the error from const
     */
    public static Message getErrorFromConst(String msgConst) {
        return getError(MessageConfigLoader.getProperty(msgConst));
    }
}
