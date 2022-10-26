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

package org.opengauss.mppdbide.view.handler.debug.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.common.DbeCommonUtils;
import org.opengauss.mppdbide.debuger.service.SourceCodeService;
import org.opengauss.mppdbide.debuger.service.SourceCodeService.CodeDescription;
import org.opengauss.mppdbide.debuger.vo.VersionVo;
import org.opengauss.mppdbide.utils.DebuggerStartVariable;
import org.opengauss.mppdbide.utils.VariableRunLine;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.sourceeditor.SQLSyntaxColorProvider;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class UpdateDebugPositionTask implements Runnable {
    private int showLine = -1;

    /**
     * The Update Debug Position Task
     *
     * @param showLine the show line
     */
    public UpdateDebugPositionTask(int showLine) {
        this.showLine = showLine;
        if (showLine != -1) {
            VariableRunLine.passLine.add(showLine);
        }
    }

    @Override
    public void run() {
        PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        if (plSourceEditor == null) {
            return;
        }
        Boolean isPlDebugger = true;
        try {
            VersionVo vo = new VersionVo();
            vo.version = plSourceEditor.getDatabase().getServerVersion();
            isPlDebugger = vo.isPldebugger();
            if (isPlDebugger) {
                plSourceEditor.removeDebugPosition();
                if (showLine >= 0) {
                    plSourceEditor.createDebugPosition(showLine);
                }
            } else {
                // dbedebugger
                if (VariableRunLine.isTerminate) {
                    dbeDebuggerRemark(plSourceEditor);
                }
            }
        } catch (MPPDBIDEException e) {
            MPPDBIDELoggerUtility.error("get version failed,err=" + e.getMessage());
        } catch (BadLocationException e) {
            MPPDBIDELoggerUtility.error("set debugPostion at " + showLine + " failed,err=" + e.getMessage());
        }
    }

    /**
     * The terminate Debug
     *
     * @return the PLSourceEditor
     */
    public static PLSourceEditor terminateDebug() {
        PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        plSourceEditor.removeDebugPosition();
        long oid = plSourceEditor.getDebugObject().getOid();
        String covs = DebuggerStartVariable.getStartInfo(oid).remarLinesStr;
        if (covs != null && !covs.isEmpty()) {
            List<String> lines = Arrays.asList(covs.split(","));
            lines.forEach(item -> {
                plSourceEditor.getSourceEditorCore().getSourceViewer()
                        .getTextWidget()
                        .setLineBackground(Integer.parseInt(item), 1,
                                SQLSyntaxColorProvider.BACKGROUND_COLOR_GREY);
            });
        }
        return plSourceEditor;
    }

    /**
     * The continue Debug
     *
     * @return void
     */
    public static void continueDebug() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        }
        PLSourceEditor pl = terminateDebug();
        String code = pl.getDebugObject().getSourceCode().getCode();
        List<String> list = DbeCommonUtils.getBreakLines(DbeCommonUtils.infoCodes,
                SourceCodeService.CodeDescription.getLines(code));
        list.forEach(core -> {
            try {
                Integer item = Integer.parseInt(core);
                if (VariableRunLine.runList.contains(String.valueOf(item))) {
                    pl.createPassPosition(item);
                } else {
                    pl.createFailPosition(item);
                }
            } catch (BadLocationException e) {
                MPPDBIDELoggerUtility.error(e.getMessage());
            }
        });
    }

    private void remarkBack(PLSourceEditor plSourceEditor) {
        long oid = plSourceEditor.getDebugObject().getOid();
        String covs = DebuggerStartVariable.getStartInfo(oid).remarLinesStr;
        if (covs != null && !covs.isEmpty()) {
            List<String> lines = Arrays.asList(covs.split(","));
            lines.forEach(item -> {
                if (!String.valueOf(showLine).equals(item)) {
                    plSourceEditor.getSourceEditorCore().getSourceViewer()
                            .getTextWidget()
                            .setLineBackground(Integer.parseInt(item), 1,
                                    SQLSyntaxColorProvider.BACKGROUND_COLOR_GREY);
                }
            });
        }
    }

    private void dbeDebuggerRemark(PLSourceEditor pl) {
        pl.removeDebugPosition();
        try {
            remarkBack(pl);
            IDebugObject iDebugObject = pl.getDebugObject();
            String code = iDebugObject.getSourceCode().getCode();
            Map<String, Integer> map = DbeCommonUtils.getBeginToEndLineNo(CodeDescription.getLines(code));
            if (showLine >= map.get(DbeCommonUtils.BEGIN) && map.get(DbeCommonUtils.END) >= showLine) {
                pl.createDebugPosition(showLine);
            }
            remark(showLine, pl);
        } catch (BadLocationException e) {
            MPPDBIDELoggerUtility.error("set debugPostion at " + showLine + " failed,err=" + e.getMessage());
        } catch (Exception e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        }
    }

    private void remark(int line, PLSourceEditor plSourceEditor) {
        String code = plSourceEditor.getDebugObject().getSourceCode().getCode();
        List<String> list = DbeCommonUtils.getBreakLines(DbeCommonUtils.infoCodes, CodeDescription.getLines(code));
        list.forEach(core -> {
            try {
                Integer item = Integer.parseInt(core);
                if (showLine != item && VariableRunLine.passLine.contains(item)) {
                    plSourceEditor.createPassPosition(item);
                } else {
                    if (showLine != item) {
                        plSourceEditor.createFailPosition(item);
                    }
                }
            } catch (BadLocationException e) {
                MPPDBIDELoggerUtility.error(e.getMessage());
            }
        });
    }
}
