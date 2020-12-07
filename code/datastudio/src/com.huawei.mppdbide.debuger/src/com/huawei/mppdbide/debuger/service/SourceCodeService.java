/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.service;

import com.huawei.mppdbide.debuger.exception.DebugPositionNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Title: the SourceCodeService class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/21]
 * @since 2020/11/21
 */
public class SourceCodeService implements IService {
    private CodeDescription baseCodeDesc;
    private CodeDescription totalCodeDesc;

    @Override
    public void closeService() {
    }

    public void setBaseCode(String code) {
        this.baseCodeDesc = new CodeDescription(code);
    }

    public void setTotalCode(String code) {
        this.totalCodeDesc = new CodeDescription(code);
    }

    public int getFirstValidDebugPos() throws DebugPositionNotFoundException {
        // BEGIN line is not a valid debug pos, so add 1
        // out code line base on 0, but breakpoint base on 1, so add 1
        // next BEGIN line is default attach breakpoint, so add 1, total pos need add 3!
        return getBeignOfBaseCode() + 3;
    }

    public int getMaxValidDebugPos() {
        return this.baseCodeDesc.getMaxPostion();
    }

    public CodeDescription getBaseCodeDesc() {
        return this.baseCodeDesc;
    }

    public CodeDescription getTotalCodeDesc() {
        return this.totalCodeDesc;
    }

    public int getBeignOfBaseCode() throws DebugPositionNotFoundException {
        return this.baseCodeDesc.getBeginPosition();
    }

    public int getBeginOfTotalCode() throws DebugPositionNotFoundException {
        return this.totalCodeDesc.getBeginPosition();
    }

    public static class CodeDescription {
        public static final int INVALID_POSITION = -1;
        private String code;
        private List<String> codeList;
        private int beginPosition = INVALID_POSITION;
        public CodeDescription(String code) {
            this.code = code;
            this.codeList = getLines(this.code);
            this.beginPosition = getBeginFromCode(this.codeList);
        }

        public int getBeginPosition() throws DebugPositionNotFoundException {
            if (beginPosition == INVALID_POSITION) {
                throw new DebugPositionNotFoundException();
            }
            return beginPosition;
        }

        public int getMaxPostion() {
            return this.codeList.size();
        }

        public String getCodeByIndex(int idx) {
            return this.codeList.get(idx);
        }

        public static List<String> getLines(String srcCode) {
            return Arrays.stream(srcCode.split("[\\n]")).collect(Collectors.toList());
        }

        private int getBeginFromCode(List<String> lines) {
            for (int i = 0; i < lines.size(); i ++) {
                if (lines.get(i).toUpperCase().startsWith("BEGIN")) {
                    return i;
                }
            }
            return INVALID_POSITION;
        }
    }
}
