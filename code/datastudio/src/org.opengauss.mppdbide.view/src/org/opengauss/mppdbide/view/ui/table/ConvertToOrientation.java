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

package org.opengauss.mppdbide.view.ui.table;

import org.opengauss.mppdbide.bl.serverdatacache.TableOrientation;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConvertToOrientation.
 *
 * @since 3.0.0
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
