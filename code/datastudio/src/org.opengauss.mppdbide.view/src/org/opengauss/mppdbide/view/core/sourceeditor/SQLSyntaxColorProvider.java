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

package org.opengauss.mppdbide.view.core.sourceeditor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.gauss.sqlparser.comm.ISQLSyntax;
import org.opengauss.mppdbide.view.prefernces.PreferenceWrapper;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLSyntaxColorProvider.
 *
 * @since 3.0.0
 */
public class SQLSyntaxColorProvider {

    /**
     * The Constant BACKGROUND.
     */
    public static final RGB BACKGROUND = new RGB(255, 255, 255);

    /**
     * use for debug position highlight
     */
    public static final RGB DEBUG_POSITION = new RGB(0xfc, 0xfc, 0x00);

    /**
     * use for debug breakpoint position highlight
     */
    public static final RGB DEBUG_BREAKPOINT_POSITION = new RGB(0xfc, 0xfc, 0x00);

    /**
     * use for debug stack first line position highlight
     */
    public static final RGB DEBUG_STACK_POSITION = new RGB(0xfc, 0xfc, 0x00);

    /**
     * The Constant SQL_MULTI_LINE_COMMENT.
     */
    public static final RGB SQL_MULTI_LINE_COMMENT = new RGB(70, 130, 180);

    /**
     * The Constant BACKGROUND_COLOR.
     */
    public static final Color BACKGROUND_COLOR = new Color(Display.getCurrent(), BACKGROUND);

    /**
     * The Constant REMAR_BACKGROUND_COLOR.
     */
    public static final Color BACKGROUND_COLOR_GREY = new Color(Display.getCurrent(), new RGB(167, 169, 169));

    /**
     * use for debug position highlight
     */
    public static final Color DEBUG_POSITION_COLOR = new Color(
            Display.getCurrent(),
            DEBUG_POSITION);

    /**
     * use for debug breakpoint position highlight
     */
    public static final Color DEBUG_BREAKPOINT_POSITION_COLOR = new Color(
            Display.getCurrent(),
            DEBUG_BREAKPOINT_POSITION);

    /**
     * use for debug stack first line position highlight
     */
    public static final Color DEBUG_STACK_POSITION_COLOR = new Color(
            Display.getCurrent(),
            DEBUG_STACK_POSITION);

    private static RGB sqlSingleLineComment = null;
    private static RGB defaultt = null;
    private static RGB unreservedKeyword = null;
    private static RGB reservedKeyword = null;
    private static RGB type = null;
    private static RGB predicates = null;
    private static RGB constants = null;
    private static RGB string = null;
    private static RGB sqlMultiLineComment = null;

    /**
     * The color table.
     */
    protected Map<RGB, Color> colorTable = new HashMap<RGB, Color>(10);

    /**
     * The Constant PREF_SQL_SINGLE_LINE_COMMENT.
     */
    public static final String PREF_SQL_SINGLE_LINE_COMMENT = "editor.syntaxcoloring.singlelinecomment";

    /**
     * The Constant PREF_SQL_MULTI_LINE_COMMENT.
     */
    public static final String PREF_SQL_MULTI_LINE_COMMENT = "editor.syntaxcoloring.singlelinecomment";

    /**
     * The Constant PREF_DEFAULT.
     */
    public static final String PREF_DEFAULT = "editor.syntaxcoloring.default";

    /**
     * The Constant PREF_UNRESERVED_KEYWORD.
     */
    public static final String PREF_UNRESERVED_KEYWORD = "editor.syntaxcoloring.unservedkeyword";

    /**
     * The Constant PREF_RESERVED_KEYWORD.
     */
    public static final String PREF_RESERVED_KEYWORD = "editor.syntaxcoloring.reservedkeyword";

    /**
     * The Constant PREF_TYPE.
     */
    public static final String PREF_TYPE = "editor.syntaxcoloring.type";

    /**
     * The Constant PREF_PREDICATES.
     */
    public static final String PREF_PREDICATES = "editor.syntaxcoloring.predicate";

    /**
     * The Constant PREF_CONSTANTS.
     */
    public static final String PREF_CONSTANTS = "editor.syntaxcoloring.constants";

    /**
     * The Constant PREF_STRING.
     */
    public static final String PREF_STRING = "editor.syntaxcoloring.strings";

    /**
     * Dispose.
     */
    public void dispose() {
        Iterator<Color> entry = colorTable.values().iterator();
        boolean hasNext = entry.hasNext();
        while (hasNext) {
            ((Color) entry.next()).dispose();
            hasNext = entry.hasNext();
        }
    }

    /**
     * Gets the color.
     *
     * @param rgb the rgb
     * @return the color
     */
    public Color getColor(RGB rgb) {
        Color clr = (Color) colorTable.get(rgb);
        if (clr == null) {
            clr = new Color(Display.getCurrent(), rgb);
            colorTable.put(rgb, clr);
        }
        return clr;
    }

    /**
     * Sets the default preferences.
     *
     * @param preferenceStore the new default preferences
     */
    public static void setDefaultPreferences(PreferenceStore preferenceStore) {
        preferenceStore.setDefault(PREF_SQL_SINGLE_LINE_COMMENT, StringConverter.asString(new RGB(128, 128, 128)));
        preferenceStore.setDefault(PREF_SQL_MULTI_LINE_COMMENT, StringConverter.asString(new RGB(128, 128, 128)));
        preferenceStore.setDefault(PREF_DEFAULT, StringConverter.asString(new RGB(0, 0, 0)));
        preferenceStore.setDefault(PREF_UNRESERVED_KEYWORD, StringConverter.asString(new RGB(128, 0, 0)));
        preferenceStore.setDefault(PREF_RESERVED_KEYWORD, StringConverter.asString(new RGB(128, 0, 0)));
        preferenceStore.setDefault(PREF_TYPE, StringConverter.asString(new RGB(0, 0, 128)));
        preferenceStore.setDefault(PREF_PREDICATES, StringConverter.asString(new RGB(255, 0, 0)));
        preferenceStore.setDefault(PREF_CONSTANTS, StringConverter.asString(new RGB(0, 64, 0)));
        preferenceStore.setDefault(PREF_STRING, StringConverter.asString(new RGB(0, 128, 0)));
    }

    /**
     * Sets the preference color.
     *
     * @param ps the new preference color
     */
    public static void setPreferenceColor(PreferenceStore ps) {
        setSQLSingleLineComment(setPreferences(ps, PREF_SQL_SINGLE_LINE_COMMENT));
        setSQLMultiLineComment(setPreferences(ps, PREF_SQL_MULTI_LINE_COMMENT));
        setDEFAULT(setPreferences(ps, PREF_DEFAULT));
        setUnreservedKeyword(setPreferences(ps, PREF_UNRESERVED_KEYWORD));
        setReservedKeyword(setPreferences(ps, PREF_RESERVED_KEYWORD));
        setTYPE(setPreferences(ps, PREF_TYPE));
        setPREDICATES(setPreferences(ps, PREF_PREDICATES));
        setCONSTANTS(setPreferences(ps, PREF_CONSTANTS));
        setSTRING(setPreferences(ps, PREF_STRING));
    }

    /**
     * Sets the preferences.
     *
     * @param ps the ps
     * @param str the str
     * @return the rgb
     */
    public static RGB setPreferences(PreferenceStore ps, String str) {
        RGB rgb = null;
        if (validateColor(ps.getString(str))) {
            rgb = PreferenceConverter.getColor(ps, str);
        } else {
            rgb = PreferenceConverter.getDefaultColor(ps, str);
        }
        return rgb;

    }

    /**
     * Validate color.
     *
     * @param str the str
     * @return true, if successful
     */
    public static boolean validateColor(String str) {

        Pattern colorPattern = Pattern.compile("\\b(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\,"
                + "(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\,(?:25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\b");
        Matcher colorMatcher = colorPattern.matcher(str);

        if (!colorMatcher.matches()) {
            PreferenceWrapper.getInstance().setPreferenceValid(true);
            return false;
        }
        return true;

    }

    /**
     * Creates the text attribute.
     *
     * @param syntax the syntax
     * @return the text attribute
     */
    public TextAttribute createTextAttribute(String syntax) {
        switch (syntax) {
            case ISQLSyntax.SQL_COMMENT: {
                return new TextAttribute(getColor(getSQLSingleLineComment()));
            }
            case ISQLSyntax.SQL_MULTILINE_COMMENT: {
                return new TextAttribute(getColor(getSqlMultiLineComment()));
            }
            case ISQLSyntax.SQL_STRING: {
                return new TextAttribute(getColor(getSTRING()));
            }
            case ISQLSyntax.SQL_DOUBLE_QUOTES_IDENTIFIER: {
                return new TextAttribute(getColor(getSTRING()));
            }
            default: {
                return new TextAttribute(getColor(getDEFAULT()));
            }
        }
    }

    /**
     * Gets the SQL single line comment.
     *
     * @return the SQL single line comment
     */
    public static RGB getSQLSingleLineComment() {
        return sqlSingleLineComment;
    }

    /**
     * Sets the SQL single line comment.
     *
     * @param sqlSingleLineCmnt the new SQL single line comment
     */
    public static void setSQLSingleLineComment(RGB sqlSingleLineCmnt) {
        sqlSingleLineComment = sqlSingleLineCmnt;
    }

    /**
     * Gets the default.
     *
     * @return the default
     */
    public static RGB getDEFAULT() {
        return defaultt;
    }

    /**
     * Sets the default.
     *
     * @param dEFAULT the new default
     */
    public static void setDEFAULT(RGB dEFAULT) {
        defaultt = dEFAULT;
    }

    /**
     * Gets the unreserved keyword.
     *
     * @return the unreserved keyword
     */
    public static RGB getUnreservedKeyword() {
        return unreservedKeyword;
    }

    /**
     * Sets the unreserved keyword.
     *
     * @param unReservedKeyword the new unreserved keyword
     */
    public static void setUnreservedKeyword(RGB unReservedKeyword) {
        unreservedKeyword = unReservedKeyword;
    }

    /**
     * Gets the reserved keyword.
     *
     * @return the reserved keyword
     */
    public static RGB getReservedKeyword() {
        return reservedKeyword;
    }

    /**
     * Sets the reserved keyword.
     *
     * @param rESERVEDKeyword the new reserved keyword
     */
    public static void setReservedKeyword(RGB rESERVEDKeyword) {
        reservedKeyword = rESERVEDKeyword;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public static RGB getTYPE() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param tYPE the new type
     */
    public static void setTYPE(RGB tYPE) {
        type = tYPE;
    }

    /**
     * Gets the predicates.
     *
     * @return the predicates
     */
    public static RGB getPREDICATES() {
        return predicates;
    }

    /**
     * Sets the predicates.
     *
     * @param pREDICATES the new predicates
     */
    public static void setPREDICATES(RGB pREDICATES) {
        predicates = pREDICATES;
    }

    /**
     * Gets the constants.
     *
     * @return the constants
     */
    public static RGB getCONSTANTS() {
        return constants;
    }

    /**
     * Sets the constants.
     *
     * @param cONSTANTS the new constants
     */
    public static void setCONSTANTS(RGB cONSTANTS) {
        constants = cONSTANTS;
    }

    /**
     * Gets the string.
     *
     * @return the string
     */
    public static RGB getSTRING() {
        return string;
    }

    /**
     * Sets the string.
     *
     * @param sTRING the new string
     */
    public static void setSTRING(RGB sTRING) {
        string = sTRING;
    }

    /**
     * Gets the sql multi line comment.
     *
     * @return the sql multi line comment
     */
    public static RGB getSqlMultiLineComment() {
        return sqlMultiLineComment;
    }

    /**
     * Sets the SQL multi line comment.
     *
     * @param coMMENT the new SQL multi line comment
     */
    public static void setSQLMultiLineComment(RGB coMMENT) {
        sqlMultiLineComment = coMMENT;
    }

}
