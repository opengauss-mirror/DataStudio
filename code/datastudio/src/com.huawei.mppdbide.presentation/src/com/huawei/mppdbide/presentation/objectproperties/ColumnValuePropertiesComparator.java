/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import com.huawei.mppdbide.presentation.util.AbstractValueComparator;

/**
 * 
 * Title: class
 * 
 * Description: The Class ColumnValuePropertiesComparator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @param <T> the generic type
 * @since 17 May, 2019
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
