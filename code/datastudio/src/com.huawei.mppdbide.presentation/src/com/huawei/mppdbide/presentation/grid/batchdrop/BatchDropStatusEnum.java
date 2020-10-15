/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
