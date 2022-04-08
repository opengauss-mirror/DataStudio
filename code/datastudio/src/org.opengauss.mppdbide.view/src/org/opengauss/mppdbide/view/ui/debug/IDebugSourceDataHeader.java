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

package org.opengauss.mppdbide.view.ui.debug;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Title: IDebugSourceDataHeader for use
 *
 * @since 3.0.0
 */
public interface IDebugSourceDataHeader {
    /**
     * description: get title's name
     *
     * @return List<String> title names, if isShowOrder is true, then first column is order
     */
    List<String> getTitles();

    /**
     * description: get title length scale, this length match getTitles
     *
     * @return List<Integer> then size scales
     */
    default List<Integer> getTitleSizeScales() {
        return IntStream.iterate(1, seed -> seed)
                .limit(getTitles().size())
                .mapToObj(scale -> scale)
                .collect(Collectors.toList());
    }
}
