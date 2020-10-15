/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConvertToOrientation.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ConvertToOrientation {

    /**
     * Convert to orientation enum.
     *
     * @param orientation the orientation
     * @return the table orientation
     */
    public static TableOrientation convertToOrientationEnum(String orientation) {
        if ("COLUMN".equals(orientation)) {
            return TableOrientation.COLUMN;
        } else if ("ROW".equals(orientation)) {
            return TableOrientation.ROW;
        }

        return TableOrientation.UNKNOWN;

    }

}
