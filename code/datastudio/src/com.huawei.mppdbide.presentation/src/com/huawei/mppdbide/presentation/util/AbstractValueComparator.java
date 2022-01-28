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

package com.huawei.mppdbide.presentation.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractValueComparator.
 * 
 * @param <T> the generic type
 * @since 3.0.0
 */
public abstract class AbstractValueComparator<T> implements Comparator<T>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Compare.
     *
     * @param o1 the o 1
     * @param o2 the o 2
     * @return the int
     */
    @Override
    public int compare(T o1, T o2) {
        int res = 0;
        if (o1 == null) {
            res = (o2 != null) ? -1 : 0;
        } else if (o2 == null) {
            res = 1;
        } else {
            res = compareValues(o1, o2);

        }

        return res;
    }

    /**
     * Compare values.
     *
     * @param o1 the o 1
     * @param o2 the o 2
     * @return the int
     */
    protected abstract int compareValues(T o1, T o2);

}
