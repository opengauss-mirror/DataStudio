/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import javax.annotation.PreDestroy;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.MarkupDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.RegexMarkupValue;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.RichTextCellPainter;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.PaddingDecorator;
import org.eclipse.nebula.widgets.nattable.resize.command.ColumnResizeCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.RowResizeCommand;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.SelectionStyleLabels;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import com.huawei.mppdbide.view.component.IGridUIPreference;
import com.huawei.mppdbide.view.component.IGridUIPreference.ColumnWidthType;
import com.huawei.mppdbide.view.component.grid.core.DsGridRichTextPainter;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class TableGridStyleConfiguration.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class TableGridStyleConfiguration extends DefaultNatTableStyleConfiguration
        implements ITableGridStyleLabelFactory {

    /**
     * The regex markup value.
     */
    protected RegexMarkupValue regexMarkupValue;
    private IGridUIPreference uiPref;
    private IDataGridContext dataGridContext;

    /**
     * Instantiates a new table grid style configuration.
     *
     * @param regexMarkupValue the regex markup value
     * @param uiPref the ui pref
     * @param dataGridContext the data grid context
     */
    public TableGridStyleConfiguration(RegexMarkupValue regexMarkupValue, IGridUIPreference uiPref,
            IDataGridContext dataGridContext) {
        this.regexMarkupValue = regexMarkupValue;
        this.uiPref = uiPref;
        this.dataGridContext = dataGridContext;
        /*
         * 1. text wrapping enabled and 2. auto-resizing disabled. 3. cell
         * Padding added to 2 pixels
         */
        // wrapText: true, calculateByTextLength: false, calculateByTextHeight:
        // false
        RichTextCellPainter richTextCellPainter = null;
        if (uiPref.getColumnWidthStrategy() == ColumnWidthType.DATA_WIDTH) {
            // Automatically grow the column length to max configured size.
            richTextCellPainter = new GridRichTextPainter();
        } else {
            // Restricted column length
            richTextCellPainter = new RichTextCellPainter(uiPref.isWordWrap(), false, false);
        }

        this.cellPainter = new BackgroundPainter(new PaddingDecorator(richTextCellPainter, 2));

        /*
         * Can control 1. Support Text wrapping? 2. Repaint background? 3. space
         * between text & cell border? 4. calculate borders based on content?
         */
        // wrapText

    }

    /**
     * Configure registry.
     *
     * @param configRegistry the config registry
     */
    @Override
    public void configureRegistry(IConfigRegistry configRegistry) {
        super.configureRegistry(configRegistry);

        // Markuip for highlighting
        MarkupDisplayConverter markupCoverter = new DSHtmlEscapedMarkupDisplayConverter(this.uiPref,
                this.dataGridContext);
        markupCoverter.registerMarkup("highlight", regexMarkupValue);

        // Register markup display converter for normal display mode.
        configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, markupCoverter,
                DisplayMode.NORMAL, GridRegion.BODY);

        configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new MarkupDisplayConverter(),
                DisplayMode.EDIT, GridRegion.BODY);

        // Display corner icon.
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
                new ImagePainter(IconUtility.getIconImage(IiconPath.DSGRID_CORNER_BUTTON, this.getClass())),
                DisplayMode.NORMAL, GridRegion.CORNER);

        // Configure null value style
        configureSelectionCellStyle(configRegistry);

        // Configure Date Time display configuration
    }

    /**
     * Configure selection cell style.
     *
     * @param configuredRegistry the configured registry
     */
    protected void configureSelectionCellStyle(IConfigRegistry configuredRegistry) {
        Style style = new Style();
        style.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);

        configuredRegistry.registerConfigAttribute(
                // This is attribute to apply
                CellConfigAttributes.CELL_STYLE,
                // this is value of the attribute
                style,
                // apply during normal rendering i.e
                // except during selection or edit
                DisplayMode.SELECT,
                // apply above for all cells with this label
                SelectionStyleLabels.SELECTION_ANCHOR_STYLE);

        Style nullStyle = new Style();
        nullStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_GRAY);
        configuredRegistry.registerConfigAttribute(
                // this is attribute to apply
                CellConfigAttributes.CELL_STYLE,
                // this is value of the attribute
                nullStyle,
                // apply during normal rendering
                // except during selection or edit
                DisplayMode.NORMAL,
                // apply the above for all cells with this label
                IEditTableGridStyleLabelFactory.COL_LABEL_NULL_VALUES);

    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        uiPref = null;
        this.cellPainter = null;
        this.dataGridContext = null;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class GridRichTextPainter.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class GridRichTextPainter extends RichTextCellPainter {
        private GridRichTextPainter() {
            super(uiPref.isWordWrap(), true, true);
            this.richTextPainter = new DsGridRichTextPainter(true);
        }

        @Override
        public int getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
            int preferredWidth = super.getPreferredWidth(cell, gc, configRegistry);
            int maxWidth = TableGridStyleConfiguration.this.uiPref.getMaxDisplayDataLength();

            return (preferredWidth > maxWidth) ? maxWidth : preferredWidth;
        }

        @Override
        public void paintCell(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
            IStyle cellStyle = CellStyleUtil.getCellStyle(cell, configRegistry);
            setupGCFromConfig(gc, cellStyle);

            String htmlText = CellDisplayConversionUtils.convertDataType(cell, configRegistry);

            Rectangle painterBounds = new Rectangle(bounds.x, bounds.y - this.richTextPainter.getParagraphSpace(), 1000,
                    bounds.height);
            this.richTextPainter.preCalculate(htmlText, gc, painterBounds, true);

            painterBounds.width = this.richTextPainter.getPreferredSize().x;
            painterBounds.height = this.richTextPainter.getPreferredSize().y;
            this.richTextPainter.paintHTML(htmlText, gc, painterBounds);
            int height = this.richTextPainter.getPreferredSize().y - 2 * this.richTextPainter.getParagraphSpace();
            if (height > bounds.height) {
                cell.getLayer().doCommand(new RowResizeCommand(cell.getLayer(), cell.getRowPosition(),
                        GUIHelper.convertVerticalDpiToPixel(height) + (cell.getBounds().height - bounds.height)));
            }

            int width = (cell.getBounds().width < bounds.width) ? 0 : cell.getBounds().width - bounds.width;

            if (this.richTextPainter.getPreferredSize().x > bounds.width) {
                cell.getLayer().doCommand(new ColumnResizeCommand(cell.getLayer(), cell.getColumnPosition(),
                        GUIHelper.convertHorizontalDpiToPixel(this.richTextPainter.getPreferredSize().x) + width));
            }

        }

    }
}
