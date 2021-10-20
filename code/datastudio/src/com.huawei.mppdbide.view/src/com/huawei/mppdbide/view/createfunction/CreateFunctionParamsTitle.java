/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.createfunction;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: CreateFunctionParamsTitle for use
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2021-04-25]
 * @since 2021-04-25
 */
public class CreateFunctionParamsTitle {
    private static final String PARAM_NAME = IMessagesConstants.CREATE_FUNCTION_UI_PARAM_NAME;
    private static final String PARAM_MODE = IMessagesConstants.CREATE_FUNCTION_UI_PARAM_MODE;
    private static final String PARAM_TYPE = IMessagesConstants.CREATE_FUNCTION_UI_PARAM_TYPE;
    private static final String PARAM_DEFAULT = IMessagesConstants.CREATE_FUNCTION_UI_PARAM_DEFAULT;
    private static List<String> titleList = Arrays.asList(PARAM_NAME, PARAM_MODE, PARAM_TYPE, PARAM_DEFAULT);

    /**
     * Get titles
     *
     * @return List<String> the title list
     */
    public List<String> getTitles() {
        return titleList.stream()
                .map(param -> getContent(param))
                .collect(Collectors.toList());
    }

    /**
     * Get scales
     *
     * @return List<Integer> the scale list
     */
    public List<Integer> getScales() {
        return Arrays.asList(1, 1, 1, 1);
    }

    private String getContent (String param) {
        return MessageConfigLoader.getProperty(param);
    }
}
