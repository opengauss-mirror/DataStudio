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

package org.opengauss.mppdbide.debuger.service;

import java.util.List;

import org.opengauss.mppdbide.common.DbeCommonUtils;
import org.opengauss.mppdbide.debuger.exception.DebugPositionNotFoundException;

/**
 * Title: the SourceCodeService class
 *
 * @since 3.0.0
 */
public class DbeSourceCodeService extends SourceCodeService {
    /**
     * get begin debug line number
     *
     * @return int return begin debug line number in code line
     * @throws DebugPositionNotFoundException debug position exp
     */
    @Override
    public int getBeginDebugCodeLine() throws DebugPositionNotFoundException {
        List<String> terminalCodes = super.totalCodeDesc.getCodeList();
        return DbeCommonUtils.compluteIndex(DbeCommonUtils.infoCodes, terminalCodes);
    }
}
