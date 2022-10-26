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

package org.opengauss.mppdbide.view.ui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;
import org.opengauss.mppdbide.debuger.service.SourceCodeService;
import org.opengauss.mppdbide.debuger.vo.dbe.ExportParamVo;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.coverage.CoverageService;
import org.opengauss.mppdbide.view.handler.debug.DBConnectionProvider;
import org.opengauss.mppdbide.view.service.CoverageServiceFactory;
import org.opengauss.mppdbide.view.utils.ExportUtil;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.vo.CoverageVo;

/**
 * Coverage History
 *
 * @since 3.0.0
 */
public class CoverageHistory extends Dialog {
    private static final int SPACE_BETWEEN_RULER = 1;
    private static final int SERIAL_NUMBER_COLUMN = 0;
    private static final int TOTAL_LINE = 1;
    private static final int TOTAL_RUN_LINE_NUM = 2;
    private static final int TOTAL_COVERAGE = 3;
    private static final int REMARK_LINE = 4;
    private static final int REMARK_RUM_LINE_NUM = 5;
    private static final int REMARK_COVERAGE = 6;
    private static final int UPDATE_TIME = 7;

    private Table table = null;
    private ToolItem saveToolItem = null;
    private ToolItem deleteToolItem = null;
    private ToolItem deleteAllToolItem = null;
    private CompositeRuler fCompositeRuler;
    private SQLSyntax syntax;
    private CoverageServiceFactory coverageServiceFactory = null;

    private String profileName;

    /**
     * CoverageHistory
     *
     * @param parent      the parent
     * @param profileName the profile Name
     * @param profileId   the profile Id
     */
    public CoverageHistory(Shell parent, String profileName) {
        super(parent);
        this.profileName = profileName;
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    public Control createContents(final Composite parent) {
        final ScrolledComposite mainSc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        mainSc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final Composite currentComposite = new Composite(mainSc, SWT.BORDER);
        mainSc.setContent(currentComposite);
        GridLayout layout = new GridLayout(1, false);
        currentComposite.setLayout(layout);
        createToolbar(currentComposite);
        table = new Table(currentComposite, SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        GridData tableGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableGD.heightHint = 300;
        table.setLayoutData(tableGD);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setTopIndex(10);
        TableColumn serialno = new TableColumn(table, SWT.LEFT);
        serialno.setText(MessageConfigLoader.getProperty(IMessagesConstants.SERIAL_NO));
        TableColumn totalLine = new TableColumn(table, SWT.LEFT);
        totalLine.setText(org.opengauss.mppdbide.utils.loader.MessageConfigLoader.getProperty(
                IMessagesConstants.TOTAL_LINE));
        TableColumn totalRunLineNum = new TableColumn(table, SWT.LEFT);
        totalRunLineNum.setText(org.opengauss.mppdbide.utils.loader.MessageConfigLoader.getProperty(
                IMessagesConstants.TOTAL_RUN_LINE_NUM));
        TableColumn totalCoverage = new TableColumn(table, SWT.LEFT);
        totalCoverage.setText(org.opengauss.mppdbide.utils.loader.MessageConfigLoader.getProperty(
                IMessagesConstants.TOTAL_COVERAGE));
        TableColumn remarkLine = new TableColumn(table, SWT.LEFT);
        remarkLine.setText(org.opengauss.mppdbide.utils.loader.MessageConfigLoader.getProperty(
                IMessagesConstants.REMARK_LINE));
        TableColumn remarkRunLineNum = new TableColumn(table, SWT.LEFT);
        remarkRunLineNum.setText(org.opengauss.mppdbide.utils.loader.MessageConfigLoader.getProperty(
                IMessagesConstants.REMARK_RUM_LINE_NUM));
        TableColumn remarkCoverage = new TableColumn(table, SWT.LEFT);
        remarkCoverage.setText(org.opengauss.mppdbide.utils.loader.MessageConfigLoader.getProperty(
                IMessagesConstants.REMARK_COVERAGE));
        TableColumn updateTime = new TableColumn(table, SWT.LEFT);
        updateTime.setText(org.opengauss.mppdbide.utils.loader.MessageConfigLoader.getProperty(
                IMessagesConstants.UPDATE_TIME));
        serialno.pack();
        totalLine.pack();
        totalRunLineNum.pack();
        totalCoverage.pack();
        remarkCoverage.pack();
        updateTime.setWidth(150);
        remarkLine.setWidth(200);
        remarkRunLineNum.setWidth(200);
        displaySqlHistoryObject();
        mainSc.setExpandHorizontal(true);
        mainSc.setExpandVertical(true);
        mainSc.setMinSize(currentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mainSc.pack();
        return parent;
    }

    /**
     * configureShell
     *
     * @param shell shell
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.COVERAGE_HISTORY_TITLE, profileName));
        shell.setImage(IconUtility.getIconImage(IiconPath.SQL_HISTORY1, this.getClass()));
        shell.setSize(1000, 750);
    }

    /**
     * the display Sql History Object
     */
    public void displaySqlHistoryObject() {
        PLSourceEditor pl = UIElement.getInstance().getVisibleSourceViewer();
        long oid = pl.getDebugObject().getOid();
        coverageServiceFactory = new CoverageServiceFactory(
                new DBConnectionProvider(pl.getDebugObject().getDatabase()));
        CoverageService coverageService = null;
        try {
            coverageService = coverageServiceFactory.getCoverageService();
            List<CoverageVo> ls = coverageService.getCoverageInfoByOid(oid);
            setInput(ls);
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        } finally {
            if (coverageService != null) {
                coverageService.closeService();
            }
        }
    }

    /**
     * setInput
     *
     * @param items the value
     */
    public void setInput(List<CoverageVo> items) {
        int index = 1;
        for (CoverageVo item : items) {
            TableItem row = new TableItem(table, SWT.NONE);
            setColValues(row, item, index);
            index++;
            row.setData(item);
        }
        if (table != null) {
            if (table.getItems().length > 0) {
                enablebuttons(true);
            }
        }
    }

    private void setColValues(TableItem row, CoverageVo item, Integer count) {
        row.setText(SERIAL_NUMBER_COLUMN, count.toString());
        row.setText(TOTAL_LINE, String.valueOf(item.totalLineNum));
        row.setText(TOTAL_RUN_LINE_NUM, String.valueOf(item.coverageLineNum));
        row.setText(TOTAL_COVERAGE, String.valueOf(item.totalPercent));
        row.setText(REMARK_LINE, String.valueOf(item.remarkLinesArr));
        row.setText(REMARK_RUM_LINE_NUM, String.valueOf(item.remarkCoverageLinesArr));
        row.setText(REMARK_COVERAGE, String.valueOf(item.remarkPercent));
        row.setText(UPDATE_TIME, item.parseDate());
    }

    public SQLSyntax getSyntax() {
        return syntax;
    }

    public void setSyntax(SQLSyntax syntax) {
        this.syntax = syntax;
    }

    /**
     * getCompositeRuler
     *
     * @return CompositeRuler the return value
     */
    public CompositeRuler getCompositeRuler() {
        fCompositeRuler = new CompositeRuler(SPACE_BETWEEN_RULER);
        fCompositeRuler.addDecorator(1, new LineNumberRulerColumn());

        return fCompositeRuler;
    }

    private void createToolbar(final Composite parent) {
        final ToolBar bar = new ToolBar(parent, SWT.FLAT | SWT.FOCUSED);
        final Image sqlcloseIcon = IconUtility.getIconImage(IiconPath.LOAD_QUERY_SQL, getClass());
        saveToolItem = new ToolItem(bar, SWT.PUSH);
        saveToolItem.setEnabled(false);
        saveToolItem.setImage(sqlcloseIcon);
        saveToolItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_REPORT));
        saveToolItem.addSelectionListener(new ExportCoverageReport());

        final Image deleteIcon = IconUtility.getIconImage(IiconPath.DELETE_SELECTED, getClass());
        deleteToolItem = new ToolItem(bar, SWT.PUSH);
        deleteToolItem.setEnabled(false);
        deleteToolItem.setImage(deleteIcon);
        deleteToolItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.DELETE_COVERAGE));
        deleteToolItem.addSelectionListener(new DeletesqlClass());

        final Image deleteallIcon = IconUtility.getIconImage(IiconPath.DELETE_ALL, getClass());
        deleteAllToolItem = new ToolItem(bar, SWT.PUSH);
        deleteAllToolItem.setEnabled(false);
        deleteAllToolItem.setImage(deleteallIcon);
        deleteAllToolItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.DELETE_COVERAGE_ALL));
        deleteAllToolItem.addSelectionListener(new DeleteAllsqlClass());
    }

    private final class DeletesqlClass implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            TableItem[] tableItems = table.getSelection();
            if (tableItems.length == 0) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DELETE_COVERAGE_REPORT_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.SELECT_QUERY_TO_DELETE));
                return;
            }
            int type = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DELETE_COVERAGE_REPORT_TITLE),
                    MessageConfigLoader.getProperty(
                            IMessagesConstants.DELETE_SELECTED_COVERAGE_HISTORY_ALERT, profileName,
                            MPPDBIDEConstants.LINE_SEPARATOR));
            deleteSQLHistory(type, tableItems);
            resetSerialNumber();
        }

        private void displayInfoMessage() {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DELETE_SQL),
                    MessageConfigLoader.getProperty(IMessagesConstants.PINNED_DELETION));
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    private final class ExportCoverageReport implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            TableItem[] items = table.getSelection();
            if (items.length == 0) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_REPORT),
                        MessageConfigLoader.getProperty(IMessagesConstants.COVERAGE_REPORT_TO_SELECT));
                return;
            }
            int type = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_REPORT),
                    MessageConfigLoader.getProperty(IMessagesConstants.SURE_EXPORT_REPORT));
            if (type != 0) {
                return;
            }
            Boolean isFlag = true;
            String html = null;
            String serialNum;
            for (int index = 0; index < items.length; index++) {
                TableItem item = items[index];
                if (!(item.getData() instanceof CoverageVo)) {
                    break;
                }
                CoverageVo vo = (CoverageVo) item.getData();
                List<String> list = getData(item);
                serialNum = String.valueOf(index + 1);
                list.add(0, serialNum);
                Map<Integer, String> code = getCode(vo.sourceCode);
                ExportParamVo expVo = new ExportParamVo();
                expVo.oid = vo.oid;
                expVo.index = serialNum;
                expVo.executeSql = code;
                expVo.remarkLines = vo.remarkLinesArr.stream().collect(Collectors.toSet());
                expVo.coveragePassLines = vo.coverageLinesArr.stream().collect(Collectors.toSet());
                expVo.list = list;
                if (isFlag) {
                    html = ExportUtil.exportReport(expVo);
                    isFlag = false;
                } else {
                    expVo.html = html;
                    html = ExportUtil.exportReport(expVo);
                }
            }
            try {
                ExportUtil.loadText(ExportUtil.getPath(), html);
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.EXECDIALOG_HINT),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_PATH, ExportUtil.getPath(),
                                MPPDBIDEConstants.LINE_SEPARATOR));
            } catch (IOException e) {
                MPPDBIDELoggerUtility.error(e.getMessage());
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
        }
    }

    private Map<Integer, String> getCode(String sourceCode) {
        Map<Integer, String> code = new HashMap<Integer, String>();
        List<String> codes = SourceCodeService.CodeDescription.getLines(sourceCode);
        for (int i = 0; i < codes.size(); i++) {
            code.put(i + 1, codes.get(i));
        }
        return code;
    }

    private List<String> getData(TableItem item) {
        if (item.getData() instanceof CoverageVo) {
            CoverageVo coverageVo = (CoverageVo) item.getData();
            return Stream.of(String.valueOf(coverageVo.totalLineNum), String.valueOf(coverageVo.coverageLineNum),
                    coverageVo.totalPercent,
                    String.valueOf(coverageVo.remarkLinesArr), String.valueOf(coverageVo.remarkCoverageLinesArr),
                    coverageVo.remarkPercent, coverageVo.params,
                    coverageVo.parseDate()).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private final class DeleteAllsqlClass implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            TableItem[] items = table.getItems();
            int type = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DELETE_COVERAGE_REPORT_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DELETE_ALL_COVERAGE_HISTORY_ALERT, profileName,
                            MPPDBIDEConstants.LINE_SEPARATOR));
            deleteSQLHistory(type, items);
            resetSerialNumber();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    private void deleteSQLHistory(int type, TableItem[] items) {
        if (type == 0) {
            List<CoverageVo> historyItems = new LinkedList<CoverageVo>();
            int counter = 0;
            for (TableItem item : items) {
                if (item.getData() instanceof CoverageVo) {
                    historyItems.add((CoverageVo) item.getData());
                    item.dispose();
                    counter++;
                }
            }
            if (counter != 0) {
                CoverageService[] service = new CoverageService[1];
                try {
                    service[0] = coverageServiceFactory.getCoverageService();
                    historyItems.forEach(item -> {
                        service[0].delCoverageInfoByOid(item.oid, item.cid);
                    });
                } catch (SQLException e) {
                    MPPDBIDELoggerUtility.error(e.getMessage());
                } finally {
                    service[0].closeService();
                }
            }
        }
    }

    private void resetSerialNumber() {
        if (table != null) {
            TableItem[] items = table.getItems();
            int size = items.length;
            if (size <= 0) {
                enablebuttons(false);
                return;
            }

            for (int indx = 0; indx < size; indx++) {
                items[indx].setText(0, Integer.toString(indx + 1));
            }
        }
    }

    /**
     * enablebuttons
     *
     * @param hasValue the param
     */
    protected void enablebuttons(boolean hasValue) {
        saveToolItem.setEnabled(hasValue);
        deleteAllToolItem.setEnabled(hasValue);
        deleteToolItem.setEnabled(hasValue);
    }
}
