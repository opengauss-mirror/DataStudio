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

package org.opengauss.mppdbide.presentation.objectproperties;

import org.opengauss.mppdbide.presentation.util.AbstractValueComparator;

/**
 * 
 * Title: class
 * 
 * Description: The Class ColumnValuePropertiesComparator.
 * 
 * @param <T> the generic type
 * @since 3.0.0
 */
public class ColumnValuePropertiesComparator<T> extends AbstractValueComparator<T> {

    private static final long serialVersionUID = 1L;

    @Override
    protected int compareValues(T o1, T o2) {

        if (o2 instanceof ObjectPropColumnTabInfo) {

            return o2.toString().compareTo(o1.toString());
        }
        /*
         * new value for the first time will be string but next time when the
         * user will be select the checkbox it will be boolean.Hence o1 has been
         * checked for instance of boolean
         */
        if (o1 instanceof Boolean || o2 instanceof Boolean) {

            boolean parseBoolean = Boolean.parseBoolean(o1.toString());

            return Boolean.compare(parseBoolean, Boolean.valueOf(o2.toString()));
        }
        if (o2 instanceof String && o1 instanceof String) {
            String val1 = (String) o1;
            String val2 = (String) o2;
            return val1.compareTo(val2);

        }

        return 0;
    }

}
