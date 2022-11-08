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

package org.opengauss.mppdbide.view.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.opengauss.mppdbide.common.DbeCommonUtils;
import org.opengauss.mppdbide.debuger.vo.dbe.ExportParamVo;
import org.opengauss.mppdbide.utils.IMessagesConstantsOne;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Export Util
 *
 * @since 3.0.0
 */
public final class ExportUtil {
    private static final String USER_DIR = "user.dir";
    private static final String FILE_SEP = "file.separator";
    private static final String LINE_SEP = "line.separator";
    private static String outpath;
    private static File file;

    private ExportUtil() {

    }

    public static String getPath() {
        return outpath;
    }

    /**
     * exportReport
     *
     * @param vo the vo
     * @param sqlName the sqlName
     * @return the value
     */
    public static String exportReport(ExportParamVo vo, String sqlName) {
        long oid = vo.oid;
        Map<Integer, String> executeSql = vo.executeSql;
        List<String> list = vo.list;
        String html = vo.html;
        URL url = getUrl("exportTemplate.html");
        Document parse = null;
        parse = getText(html, url, oid, sqlName);
        Map<String, Integer> offset = DbeCommonUtils.getBeginToEndLineNo(
                executeSql.values().stream().collect(Collectors.toList()));
        Element data = parse.getElementById("data");
        Element datatr = data.appendElement("tr");
        list.forEach(item -> {
            Element td = datatr.appendElement("td");
            td.text(item);
        });
        String index = vo.index;
        boolean[] flag = new boolean[]{true};
        Element excuteSql = parse.getElementById("executeSql");
        executeSql.forEach((k, v) -> {
            Element tr = excuteSql.appendElement("tr");
            if (flag[0]) {
                Element td0 = tr.appendElement("td");
                td0.attr("rowspan", executeSql.values().size() + "");
                td0.text(index);
                flag[0] = false;
            }
            Element serialNo = tr.appendElement("td");
            serialNo.text(k.toString());
            Element td = tr.appendElement("td");
            Element td2 = tr.appendElement("td");
            Element div = td.appendElement("div");
            td2.text(v);
            if (vo.remarkLines.contains(k.toString())) {
                td2.addClass("bac_remark");
            }
            boolean isCanBreak = Arrays.asList(vo.canBreakLine.split(",")).contains(String.valueOf(k));
            if (vo.coveragePassLines.contains((k - 1) + "") && isCanBreak) {
                div.addClass("bac_pass");
            } else {
                if (k > (offset.get(DbeCommonUtils.BEGIN) + 1) && k < (offset.get(DbeCommonUtils.END) + 1)
                        && isCanBreak) {
                    div.addClass("bac_fail");
                }
            }
        });
        return parse.outerHtml();
    }

    private static URL getUrl(String propertiesFileName) {
        ClassLoader classLoader = ExportUtil.class.getClassLoader();
        StringBuffer msgFileCon = new StringBuffer("exportTemplate_");
        String messageFileName = propertiesFileName;
        String locale = Locale.getDefault().toString();
        if (locale.equals(MPPDBIDEConstants.CHINESE_LOCALE)) {
            messageFileName = msgFileCon.append(locale).append(".html").toString();
        }
        return classLoader.getResource(messageFileName);
    }

    private static Document getText(String html, URL url, long oid, String sqlName) {
        Document parse = null;
        try {
            if (html == null) {
                String path = FileLocator.toFileURL(url).getPath().substring(1);
                file = new File(path);
                String workDir = System.getProperty(USER_DIR);
                String fileSepa = System.getProperty(FILE_SEP);
                String dir = String.format(Locale.ENGLISH, "%s%shis_coverage%s", workDir, fileSepa, fileSepa);
                File outFile = new File(dir);
                if (!outFile.exists()) {
                    outFile.mkdir();
                }
                outpath = String.format(Locale.ENGLISH, "%s%s_%s_%s.html", dir, oid, sqlName,
                        System.currentTimeMillis());
                parse = Jsoup.parse(file, "gbk");
                convertZhCn(parse);
            } else {
                parse = Jsoup.parse(html);
            }
        } catch (IOException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        }
        return parse;
    }

    private static void convertZhCn(Document parse) {
        String locale = Locale.getDefault().toString();
        if (!locale.equals(MPPDBIDEConstants.CHINESE_LOCALE)) {
            return;
        }
        Element execStatement = parse.getElementById(IMessagesConstantsOne.EXP_EXECUTE_STATEMENT);
        execStatement.text(MessageConfigLoader.getProperty(IMessagesConstantsOne.EXP_EXECUTE_STATEMENT));
        Element searialNum = parse.getElementById(IMessagesConstantsOne.EXP_SERIAL_NUMBER);
        searialNum.text(MessageConfigLoader.getProperty(IMessagesConstantsOne.EXP_SERIAL_NUMBER));
        Element totalRows = parse.getElementById(IMessagesConstantsOne.EXP_TOTAL_ROWS);
        totalRows.text(MessageConfigLoader.getProperty(IMessagesConstantsOne.EXP_TOTAL_ROWS));
        Element runLines = parse.getElementById(IMessagesConstantsOne.EXP_TOTAL_RUNNING_LINES);
        runLines.text(MessageConfigLoader.getProperty(IMessagesConstantsOne.EXP_TOTAL_RUNNING_LINES));
        Element totalCoverage = parse.getElementById(IMessagesConstantsOne.EXP_TOTAL_COVERAGE);
        totalCoverage.text(MessageConfigLoader.getProperty(IMessagesConstantsOne.EXP_TOTAL_COVERAGE));
        Element markRow = parse.getElementById(IMessagesConstantsOne.EXP_MARK_ROW);
        markRow.text(MessageConfigLoader.getProperty(IMessagesConstantsOne.EXP_MARK_ROW));
        Element execLine = parse.getElementById(IMessagesConstantsOne.EXP_MARK_EXECUTION_LINE);
        execLine.text(MessageConfigLoader.getProperty(IMessagesConstantsOne.EXP_MARK_EXECUTION_LINE));
        Element markCoverage = parse.getElementById(IMessagesConstantsOne.EXP_MARKER_COVERAGE);
        markCoverage.text(MessageConfigLoader.getProperty(IMessagesConstantsOne.EXP_MARKER_COVERAGE));
        Element inputParams = parse.getElementById(IMessagesConstantsOne.EXP_INPUT_PARAMS);
        inputParams.text(MessageConfigLoader.getProperty(IMessagesConstantsOne.EXP_INPUT_PARAMS));
        Element updateTime = parse.getElementById(IMessagesConstantsOne.EXP_UPDATE_TIME);
        updateTime.text(MessageConfigLoader.getProperty(IMessagesConstantsOne.EXP_UPDATE_TIME));
    }

    /**
     * loadText
     *
     * @param path the path
     * @param text the text
     * @throws IOException the exception
     */
    public static void loadText(String path, String text) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
        String[] split = text.split("     ");
        for (String str : split) {
            bw.write(str + System.getProperty(LINE_SEP));
        }
        if (bw != null) {
            bw.close();
        }
    }
}
