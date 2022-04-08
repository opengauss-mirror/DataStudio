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

package org.opengauss.mppdbide.presentation;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IWindowDetail.
 * 
 * @since 3.0.0
 */
public interface IWindowDetail {

    /**
     * Gets the title.
     *
     * @return the title
     */
    String getTitle();

    /**
     * Gets the short title.
     *
     * @return the short title
     */
    String getShortTitle();

    /**
     * Gets the unique ID.
     *
     * @return the unique ID
     */
    String getUniqueID();

    /**
     * Gets the icon.
     *
     * @return the icon
     */
    default String getIcon() {
        return null;
    }

    /**
     * Checks if is closeable.
     *
     * @return true, if is closeable
     */
    default boolean isCloseable() {
        return true;
    }
}