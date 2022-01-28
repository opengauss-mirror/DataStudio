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

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class ServerProperty.
 * 
 */

public class ServerProperty {

    private String key;
    private String value;
    private String[] prop;

    /**
     * Gets the propery title.
     *
     * @return the propery title
     */
    public String getProperyTitle() {
        return "Property-X";
    }

    /**
     * Instantiates a new server property.
     *
     * @param key the key
     * @param value the value
     */
    public ServerProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Instantiates a new server property.
     *
     * @param key the key
     * @param value the value
     */
    public ServerProperty(String key, double value) {
        this(key, "" + value);
    }

    /**
     * Instantiates a new server property.
     *
     * @param key the key
     * @param value the value
     */
    public ServerProperty(String key, long value) {
        this(key, "" + value);
    }

    /**
     * Instantiates a new server property.
     *
     * @param key the key
     * @param value the value
     */
    public ServerProperty(String key, int value) {
        this(key, "" + value);
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Gets the prop.
     *
     * @return the prop
     */
    public String[] getProp() {
        prop = new String[2];
        prop[0] = getKey();
        prop[1] = getValue();
        return prop;
    }

}
