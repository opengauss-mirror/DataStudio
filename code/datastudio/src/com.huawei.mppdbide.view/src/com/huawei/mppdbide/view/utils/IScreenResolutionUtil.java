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

package com.huawei.mppdbide.view.utils;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * Title: IScreenResolutionUtil
 * 
 * Description:IScreenResolutionUtil
 *
 * @since 3.0.0
 */
public interface IScreenResolutionUtil {

    /**
     * The Constant screenSize.
     */
    static final Dimension SCREENSIZE = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * Gets the screen height.
     *
     * @return the screen height
     */
    public static int getScreenHeight() {
        return SCREENSIZE.height;
    }

    /**
     * Gets the screen width.
     *
     * @return the screen width
     */
    public static int getScreenWidth() {
        return SCREENSIZE.width;
    }

}
