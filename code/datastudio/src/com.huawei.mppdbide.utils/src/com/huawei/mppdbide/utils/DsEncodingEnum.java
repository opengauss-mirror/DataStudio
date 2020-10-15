/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum DsEncodingEnum.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public enum DsEncodingEnum {
    UTF_8("UTF-8"), GBK("GBK"), LATIN1("LATIN1");

    private final String value;

    /**
     * Instantiates a new ds encoding enum.
     *
     * @param value the value
     */
    private DsEncodingEnum(String value) {
        this.value = value;
    }

    /**
     * Gets the encoding.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return value;
    }
}
