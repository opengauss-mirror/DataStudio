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

package org.opengauss.mppdbide.adapter.gauss;

/**
 * 
 * Title: class
 * 
 * Description: The Class Datatype.
 *
 * @since 3.0.0
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
