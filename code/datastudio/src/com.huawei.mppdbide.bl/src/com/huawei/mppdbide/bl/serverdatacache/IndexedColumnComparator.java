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

package com.huawei.mppdbide.bl.serverdatacache;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * Title: class
 * 
 * Description: The Class IndexedColumnComparator.
 * 
 */

public class IndexedColumnComparator implements Comparator<IndexedColumnExpr>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(IndexedColumnExpr object1, IndexedColumnExpr object2) {
        return Integer.compare(object2.getPosition(), object1.getPosition());

    }

}
