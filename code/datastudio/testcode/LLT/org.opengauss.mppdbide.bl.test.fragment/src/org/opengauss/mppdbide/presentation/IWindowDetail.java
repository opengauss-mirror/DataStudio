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
 * This interface details the 
 *
 * @since 3.0.0
 */
public interface IWindowDetail
{
    /**
     * This method returns the title of the window being displayed 
     * @return - String - The title of this window
     */
    String getTitle();
    
    /**
     * This method returns ths short title of the window
     * @return
     */
    String getShortTitle();
            
    /**
     * This method returns the unique id assigned for this window
     * @return String - The id of the window
     */
    String getUniqueID();
    
    /**
     * returns the icon used for this window
     * @return
     */
    String getIcon();
}