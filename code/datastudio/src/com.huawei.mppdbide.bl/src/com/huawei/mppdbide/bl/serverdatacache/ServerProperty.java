/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class ServerProperty.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
