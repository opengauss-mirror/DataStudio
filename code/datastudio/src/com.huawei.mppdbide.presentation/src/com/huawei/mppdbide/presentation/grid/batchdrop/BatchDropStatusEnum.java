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

package com.huawei.mppdbide.presentation.grid.batchdrop;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum BatchDropStatusEnum.
 * 
 * @since 3.0.0
 */
public enum BatchDropStatusEnum {
    TO_START(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_STATUS_TOSTART)),
    IN_PROGRESS(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_STATUS_INPROGRESS)),
    ERROR(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_STATUS_ERROR)),
    COMPLETED(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_STATUS_COMPLETED));

    String value;

    /**
     * Instantiates a new batch drop status enum.
     *
     * @param value1 the value 1
     */
    BatchDropStatusEnum(String value1) {
        this.value = value1;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
