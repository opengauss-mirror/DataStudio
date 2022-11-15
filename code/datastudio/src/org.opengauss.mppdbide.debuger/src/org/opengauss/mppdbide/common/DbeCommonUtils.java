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

package org.opengauss.mppdbide.common;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.opengauss.mppdbide.debuger.service.DbeDebugService;
import org.opengauss.mppdbide.debuger.service.SourceCodeService;
import org.opengauss.mppdbide.debuger.vo.dbe.InfoCodeVo;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Description: DbeCommonUtils
 *
 * @since 3.0.0
 */
public final class DbeCommonUtils {
    /**
     * list of infoCode
     */
    public static volatile List<InfoCodeVo> infoCodes;

    /**
     * variable begin
     */
    public static final String BEGIN = "BEGIN";

    /**
     * variable END
     */
    public static final String END = "END";

    private static final String REPLACE_FUNCTION = "$function$";

    private static final String REPLACED = "$$";

    private DbeCommonUtils() {

    }

    /**
     * checkCanBreakLines
     *
     * @param sourceCode  code
     * @param conn        dbConnection
     * @param oid         oid
     * @param selectIndex index
     * @throws SQLException exception
     */
    public static void checkCanBreakLines(List<String> sourceCodes, IConnection conn, long oid, List<String> indexs)
            throws SQLException {
        List<InfoCodeVo> infos = DbeDebugService.getInfoCodes(conn, Arrays.asList(oid));
        List<InfoCodeVo> canBreaks = infos.stream().filter(item -> item.canbreak).collect(Collectors.toList());
        for (int i = 0; i < indexs.size(); i++) {
            String selectCode = sourceCodes.get(Integer.parseInt(indexs.get(i)));
            long count = canBreaks.stream().filter(item -> {
                String str = item.query.replaceAll(REPLACE_FUNCTION, REPLACED);
                return str.equalsIgnoreCase(selectCode);
            }).count();
            if (count == 0) {
                throw new SQLException(MessageConfigLoader.getProperty(IMessagesConstants.NOT_SUPPORT_BREAK));
            }
        }
    }

    /**
     * getBeginToEndLineNo
     *
     * @param sourceCode code
     * @return Map<String, Integer> map
     */
    public static Map<String, Integer> getBeginToEndLineNo(List<String> sourceCodes) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        boolean isBeginFlag = true;
        for (int i = 0; i < sourceCodes.size(); i++) {
            String code = sourceCodes.get(i);
            if (isBeginFlag && checkStrEquals(code, BEGIN)) {
                map.put(BEGIN, i);
                isBeginFlag = false;
                continue;
            }
            if (checkStrEquals(code, END)) {
                map.put(END, i);
            }
        }
        Integer begin = map.get(BEGIN);
        Integer end = map.get(END);
        if (begin == null) {
            map.put(BEGIN, getBeginIndex(sourceCodes, BEGIN));
        }
        if (end == null) {
            map.put(END, getEndIndex(sourceCodes, END));
        }
        return map;
    }

    /**
     * checkStrEquals
     *
     * @param firstCode firstCode
     * @param endCode endCode
     * @return boolean boolean
     */
    public static boolean checkStrEquals(String firstCode, String endCode) {
        return firstCode.toUpperCase(Locale.ENGLISH).trim().startsWith(endCode);
    }

    /**
     * getBeginIndex
     *
     * @param codes codes
     * @param match match
     * @return int int
     */
    public static int getBeginIndex(List<String> codes, String match) {
        for(int i =0; i<codes.size(); i++) {
            if (codes.get(i).toUpperCase(Locale.ENGLISH).contains(match)) {
                return i;
            }
        }
        return SourceCodeService.CodeDescription.INVALID_POSITION;
    }

    /**
     * getEndIndex
     *
     * @param codes codes
     * @param match match
     * @return int int
     */
    public static int getEndIndex(List<String> codes, String match) {
        for(int i =codes.size()-1; i<codes.size(); i--) {
            if (codes.get(i).toUpperCase(Locale.ENGLISH).contains(match) ) {
                return i;
            }
        }
        return SourceCodeService.CodeDescription.INVALID_POSITION;
    }

    /**
     * checkIsEqualLine
     *
     * @param codes codes
     * @param match match
     * @return int int
     */
    public static long checkIsEqualLine(List<String> sourceCodes, String match) {
        return sourceCodes.stream().filter(item -> checkStrEquals(item ,END)).count();
    }

    /**
     * getCanBreakLinesByInfo
     *
     * @param conn         dbConnection
     * @param inputsParams params
     * @param sourceCode   code
     * @return List string
     */
    public static List<String> getCanBreakLinesByInfo(IConnection conn, List<Object> inputsParams,
                                                      List<String> sourceCodes) {
        List<InfoCodeVo> infos = null;
        try {
            infos = DbeDebugService.getInfoCodes(conn, inputsParams);
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
            return Collections.emptyList();
        }
        return getBreakLines(infos, sourceCodes);
    }

    /**
     * getBreakLines
     *
     * @param infos      infoCode
     * @param sourceCode code
     * @return List string
     */
    public static List<String> getBreakLines(List<InfoCodeVo> infos, List<String> sourceCodes) {
        Map<String, Integer> map = getBeginToEndLineNo(sourceCodes);
        List<String> linse = new ArrayList<String>();
        for (int i = map.get(BEGIN); i < sourceCodes.size(); i++) {
            if (i >= map.get(END)) {
                if (checkIsEqualLine(sourceCodes, END) == 0) {
                    linse.add(String.valueOf(i));
                }
                return linse;
            }
            if (i < infos.size() && infos.get(i).canbreak) {
                linse.add(String.valueOf(i));
            }
        }
        return linse;
    }

    /**
     * compluteIndex
     *
     * @param infos      infos
     * @param sourceCode code
     * @return index int
     */
    public static int compluteIndex(List<InfoCodeVo> infos, List<String> sourceCodes) {
        Map<String, Integer> map = getBeginToEndLineNo(sourceCodes);
        int index = -1;
        for (int i = map.get(BEGIN) + 1; i < sourceCodes.size(); i++) {
            index = i;
            if (index >= map.get(END)) {
                return index;
            }
            if (!StringUtils.isBlank(sourceCodes.get(index)) && infos.size() >= index && infos.get(index).canbreak) {
                return i;
            }
        }
        return index;
    }
}