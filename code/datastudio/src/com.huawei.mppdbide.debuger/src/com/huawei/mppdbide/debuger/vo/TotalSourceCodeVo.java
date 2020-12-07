/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.vo;

import com.huawei.mppdbide.debuger.annotation.DumpFiled;

/**
 * Title: the TotalSourceCodeVo class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/19]
 * @since 2020/11/19
 */
public class TotalSourceCodeVo {
    @DumpFiled
    private Integer headerlines;

    @DumpFiled
    private String definition;

    public String getSourceCode() {
        return definition.replaceAll("\\$function\\$", "\\$\\$") + "/";
    }

    public int getHeadlines() {
        return headerlines;
    }
}
