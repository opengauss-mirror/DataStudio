/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.huawei.mppdbide.adapter.gauss.GaussDatatypeUtils;
import com.huawei.mppdbide.debuger.vo.VariableVo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;

/**
 * Title: class
 * Description: The Class VariableTableWindowCore.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class VariableTableWindowCore extends TableWindowCore<VariableVo> {
    private static enum TitleDesc {
        VARIABLE(IMessagesConstants.DEBUG_VARIABLE_VARIABLE),
        VALUE(IMessagesConstants.DEBUG_VARIABLE_VALUE),
        DATA_TYPE(IMessagesConstants.DEBUG_VARIABLE_DATA_TYPE);

        /**
         * Title Description
         */
        private final String desc;

        TitleDesc(String title) {
            this.desc = title;
        }

        /**
         * Gets the desc
         *
         * @return String the desc
         */
        public String getDesc() {
            return MessageConfigLoader.getProperty(desc);
        }
    }

    /**
     * Get the column title.
     *
     * @return List<String> the column title list
     */
    @Override
    protected List<String> getTitle() {
        return Arrays.asList(TitleDesc.values())
                .stream()
                .map(desc -> desc.getDesc())
                .collect(Collectors.toList());
    }

    /**
     * Get the column label provider.
     *
     * @param index the index of the column
     * @return ColumnLabelProvider the column label provider for column index
     */
    @Override
    protected ColumnLabelProvider getProvider(int index) {
        return new CurColumnLabelProvider(TitleDesc.values()[index]);
    }

    /**
     * Get the list element.
     *
     * @return List<VariableVo> the list element
     */
    @Override
    protected List<VariableVo> getListElement() {
        try {
            return DebugServiceHelper.getInstance().getDebugService().getVariables();
        } catch (SQLException e) {
            return new ArrayList<VariableVo>();
        }
    }

    /**
     * Title: class
     * Description: The Class CreateCurrentColumnLabelProvider.
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @version [openGauss DataStudio 1.0.1, 04,12,2020]
     * @since 04,12,2020
     */
    protected static class CurColumnLabelProvider extends ColumnLabelProvider {
        private TitleDesc desc;

        /**
         * Instantiates a new current column label provider.
         */
        public CurColumnLabelProvider(TitleDesc desc) {
            this.desc = desc;
        }

        /**
         * Gets the text.
         *
         * @param element the element
         * @return String the text
         */
        @Override
        public String getText(Object element) {
            String result = "";
            if (!(element instanceof VariableVo)) {
                return result;
            }
            VariableVo variableVo = (VariableVo) element;
            switch (desc) {
            case VARIABLE:
                result = variableVo.name;
                break;
            case VALUE:
                result = variableVo.value.toString();
                if ("NULL".equals(result)) {
                    result = "<NULL>";
                }
                break;
            case DATA_TYPE:
                int value = variableVo.dtype.intValue();
                result = GaussDatatypeUtils.getDataTypeHashMap().get(value).getTypename();
                break;
            default:
                break;
            }
            return result;
        }
    }

    /**
     * Auto column width.
     *
     * @param table the table
     */
    @Override
    protected void autoColWidth(final Table table) {
        table.addControlListener(new ControlAdapter() {
            /**
             * Sent when the size (width, height) of a control changes.
             *
             * @param e an event containing information about the resize
             */
            public void controlResized(final ControlEvent e) {
                for (int i = 0; i < table.getColumnCount(); i++) {
                    table.getColumn(i).setWidth((table.getSize().x / table.getColumnCount()));
                }
            }
        });
    }

    /**
     * Copy text.
     *
     * @param table the table
     */
    @Override
    protected void copyText(final Table table) {
        table.addMouseListener(new MouseAdapter() {
            /**
             * Sent when a mouse button is pressed.
             *
             * @param e an event containing information about the mouse button press
             */
            public void mouseDown(MouseEvent event) {
                Point pt = new Point(event.x, event.y);
                final TableItem item = table.getItem(pt);
                if (item == null) {
                    return;
                }
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection contents = new StringSelection(item.getText(0));
                clipboard.setContents(contents, null);
            }
        });
    }
}