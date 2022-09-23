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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.opengauss.mppdbide.debuger.annotation.ParseVo;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.vo.SourceCodeVo;
import org.opengauss.mppdbide.debuger.vo.VersionVo;
import org.opengauss.mppdbide.debuger.vo.dbe.InfoCodeVo;

/**
 * Convert openGauss 3.0 vo to openGauss2.0 vo
 *
 * @author gitam
 * @since 3.0.0
 */
public class QueryResVoConvertHelper {
    /**
     * Convert results to vo
     *
     * @param rs    the rs
     * @param clazz the class
     * @param conn  the connection
     * @param <T>   the Generics
     * @return return value
     * @throws SQLException the exception
     */
    public static <T> T parse(ResultSet rs, Class<T> clazz, IConnection conn) throws SQLException {
        VersionVo version = VersionHelper.getDebuggerVersion(conn);
        return parse(rs, clazz, version);
    }

    /**
     * Convert parse vo
     *
     * @param rs        the rs
     * @param clazz     the class
     * @param versionVo the vo param
     * @param <T>       the Generics
     * @return the converted value
     * @throws SQLException the exception
     */
    public static <T> T parse(ResultSet rs, Class<T> clazz, VersionVo versionVo) throws SQLException {
        T obj = null;
        // if openGauss2.0 command do not need to convert
        if (!getConvert(versionVo.getDebuggerVersion())) {
            obj = ParseVo.parse(rs, clazz);
        } else {
            if (SourceCodeVo.class.equals(clazz)) {
                obj = convertToSourceCodeVo(rs);
            }
        }
        return obj;
    }

    /**
     * Covert list
     *
     * @param rs    the rs
     * @param clazz the class
     * @param conn  the connection
     * @param <T>   the Generics
     * @return the converted value
     * @throws SQLException the exception
     */
    public static <T> List<T> parseList(ResultSet rs, Class<T> clazz, IConnection conn) throws SQLException {
        VersionVo version = VersionHelper.getDebuggerVersion(conn);
        return parseList(rs, clazz, version);
    }

    /**
     * Convert list
     *
     * @param rs        the rs
     * @param clazz     the class
     * @param versionVo the param
     * @param <T>       the Generics
     * @return the converted value
     * @throws SQLException the exception
     */
    public static <T> List<T> parseList(ResultSet rs, Class<T> clazz, VersionVo versionVo) throws SQLException {
        List<T> list = null;
        // if openGauss2.0 command do not need to convert
        if (!getConvert(versionVo.getDebuggerVersion())) {
            list = ParseVo.parseList(rs, clazz);
        } else {
            list = ConvertHandleUtil.process(ConvertVoEnum.getType(clazz), rs);
        }
        return list;
    }

    /**
     * Convert vo by class
     *
     * @param rs  the rs
     * @param <T> the Generics
     * @return the converted value
     * @throws SQLException the exception
     */
    public static <T> T convertToSourceCodeVo(ResultSet rs) throws SQLException {
        List<InfoCodeVo> infos = ParseVo.parseList(rs, InfoCodeVo.class);
        List<InfoCodeVo> infoCodeList = infos.stream().filter(item -> item.lineno != null).collect(Collectors.toList());
        StringBuffer buffer = new StringBuffer();
        infoCodeList.forEach(infoItem -> {
            if (!infoItem.query.endsWith("\n")) {
                infoItem.query = infoItem.query + "\n";
            }
            buffer.append(infoItem.query);
        });
        SourceCodeVo sourceCodeVo = new SourceCodeVo();
        sourceCodeVo.setCodes(infoCodeList);
        sourceCodeVo.setSourceCode(buffer.toString());
        return (T) sourceCodeVo;
    }

    private static boolean getConvert(String version) {
        return DebugConstants.DBE_DEBUGGER.equalsIgnoreCase(version);
    }
}
