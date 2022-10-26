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

package org.opengauss.mppdbide.debuger.vo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opengauss.mppdbide.debuger.annotation.DumpFiled;
import org.opengauss.mppdbide.debuger.vo.dbe.InfoCodeVo;

/**
 * Title: the SourceCodeVo class
 *
 * @since 3.0.0
 */
public class SourceCodeVo {
    /**
     *  the source code
     */
    @DumpFiled
    public String pldbg_get_source;

    private List<InfoCodeVo> codes;

    /**
     * get source code
     *
     * @return String source code
     */
    public String getSourceCode() {
        return pldbg_get_source;
    }

    public void setSourceCode(String sourceCode) {
        this.pldbg_get_source = sourceCode;
    }

    public Map<Integer, String> getCodes() {
        if (codes == null) {
            return Collections.emptyMap();
        }
        return codes.stream().collect(Collectors.toMap(InfoCodeVo::getLineno, InfoCodeVo::getQuery));
    }

    public void setCodes(List<InfoCodeVo> codes) {
        this.codes = codes;
    }
}
