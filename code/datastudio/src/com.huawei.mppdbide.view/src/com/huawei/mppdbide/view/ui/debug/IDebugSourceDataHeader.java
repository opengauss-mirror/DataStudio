/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.debug;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Title: IDebugSourceDataHeader for use
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-31]
 * @since 2020-12-31
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
