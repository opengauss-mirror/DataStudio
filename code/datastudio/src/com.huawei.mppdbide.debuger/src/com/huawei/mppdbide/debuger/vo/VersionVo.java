/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.vo;

import com.huawei.mppdbide.debuger.annotation.DumpFiled;

/**
 * Title: VersionVo for use
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-21]
 * @since 2020-12-21
 */
public class VersionVo {
    /**
     * this is version str
     */
    @DumpFiled
    public String serverversionstr;

    /**
     * this is version number
     */
    @DumpFiled
    public Integer serverversionnum;

    /**
     * this is proxy api version
     */
    @DumpFiled
    public Integer proxyapiver;

    /**
     * this is server process id
     */
    @DumpFiled
    public Long serverprocessid;
}
