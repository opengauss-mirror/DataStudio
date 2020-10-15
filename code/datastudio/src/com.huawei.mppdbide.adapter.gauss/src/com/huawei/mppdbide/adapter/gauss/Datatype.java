/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.gauss;

/**
 * 
 * Title: class
 * 
 * Description: The Class Datatype.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class Datatype {
    private String typename;
    private boolean isSupported;

    /**
     * Instantiates a new datatype.
     *
     * @param oid the oid
     * @param typename the typename
     * @param isSupported the is supported
     */
    public Datatype(int oid, String typename, boolean isSupported) {
        super();
        this.typename = typename;
        this.isSupported = isSupported;
    }

    /**
     * Gets the typename.
     *
     * @return the typename
     */
    public String getTypename() {
        return typename;
    }

    /**
     * Checks if is supported.
     *
     * @return true, if is supported
     */
    public boolean isSupported() {
        return isSupported;
    }

}
