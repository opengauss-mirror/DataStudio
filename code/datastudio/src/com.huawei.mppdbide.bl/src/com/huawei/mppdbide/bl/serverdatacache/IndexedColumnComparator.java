/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * Title: class
 * 
 * Description: The Class IndexedColumnComparator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class IndexedColumnComparator implements Comparator<IndexedColumnExpr>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(IndexedColumnExpr object1, IndexedColumnExpr object2) {
        return Integer.compare(object2.getPosition(), object1.getPosition());

    }

}
