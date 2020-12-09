/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.annotation;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * Title: class for LoadVO
 * 
 * Description: The dump VO load class
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00589921
 * @version [DataStudio for openGauss 1.0.0, 19 Sep, 2019]
 * @since 19 Sep, 2019
 */

public class ParseVo {
    /**
     * get inputs clz's all fields with annotation @DumpField.
     *
     * @param clz input clz
     * @return Map<String, Field> return map field name and Field
     */ 
    public static Map<String, Field> parseDumpFields(Class<?> clz) {
        Field[] fields = clz.getDeclaredFields();
        Map<String, Field> name2Field = new HashMap<>();
        for (Field f: fields) {
            DumpFiled dumpFiled = f.getAnnotation(DumpFiled.class);
            if (dumpFiled != null) {
                String name = dumpFiled.name();
                if ("".equals(name)) {
                    name = f.getName();
                }
                name2Field.put(name, f);
                f.setAccessible(true);
            }
        }
        return name2Field;
    }
    /**
     * fill inputs clz's all fields with annotation @DumpField by ResultSet.
     *
     * @param rs database query one row
     * @param clazz input clz
     * @return T new instance by ResultSet
     */
    public static <T> T parse(ResultSet rs, Class<T> clazz) {
        Map<String, Field> name2Field = parseDumpFields(clazz);
        return parse(rs, clazz, name2Field);
    }

    /**
     * get list of class from rs
     *
     * @param rs database query one row
     * @param clazz input clz
     * @return List<T> new instance by ResultSet
     * @throws SQLException execute with ResultSet error
     */
    public static <T> List<T> parseList(ResultSet rs, Class<T> clazz) throws SQLException {
        Map<String, Field> name2Field = parseDumpFields(clazz);
        List<T> vos = new ArrayList<>();
        while (rs.next()) {
            vos.add(ParseVo.parse(rs, clazz, name2Field));
        }
        return vos;
    }

    /**
     * fill inputs clz's all fields with annotation @DumpField by ResultSet.
     *
     * @param rs database query one row
     * @param clazz input clz
     * @param name2Field returned by parseDumpFields
     * @return T new instance by ResultSet
     */ 
    public static <T> T parse(ResultSet rs, Class<T> clazz, Map<String, Field> name2Field) {
        T obj;
        try {
            obj = clazz.newInstance();
            for (Entry<String, Field> entry: name2Field.entrySet()) {
                Field field = entry.getValue();
                Optional<Object> tmpObj = getValueByKey(rs, entry.getKey(), field);
                field.set(obj, tmpObj.orElse(null));
            }
        } catch (InstantiationException | SQLException | IllegalAccessException ignored) {
            obj = null;
        } 
        return obj;
    }

    /**
     * get value from rs by special field
     *
     * @param rs database query one row
     * @param key ResultSet's column
     * @param field input clz
     * @return Object new instance of ResultSet' column
     * @throws SQLException throw get column error
     */
    public static Optional<Object> getValueByKey(ResultSet rs, String key, Field field) throws SQLException {
        return getValueByKey(rs, key, field.getType());
    }

    /**
     * get  ResultSet' column value and convert to type.
     *
     * @param rs database query one row
     * @param key ResultSet's column
     * @param type input clz
     * @return Object new instance of ResultSet' column
     * @throws SQLException throw get column error
     */
    public static <T> Optional<Object> getValueByKey(
            ResultSet rs,
            String key,
            Class<T> type) throws SQLException {
        Object obj = rs.getObject(key);
        if (rs.wasNull()) {
            obj = null;
        }
        return Optional.ofNullable(obj);
    }
}
