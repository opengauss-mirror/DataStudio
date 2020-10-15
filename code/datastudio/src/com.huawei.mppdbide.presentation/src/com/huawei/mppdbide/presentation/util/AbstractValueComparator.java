/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @param <T> the generic type
 * @since 17 May, 2019
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
