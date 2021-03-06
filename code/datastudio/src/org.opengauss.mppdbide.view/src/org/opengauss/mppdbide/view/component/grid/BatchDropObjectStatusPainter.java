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

package org.opengauss.mppdbide.view.component.grid;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import org.opengauss.mppdbide.presentation.grid.batchdrop.BatchDropStatusEnum;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchDropObjectStatusPainter.
 *
 * @since 3.0.0
 */
public class BatchDropObjectStatusPainter extends ImagePainter {
    private final Image completedImg;
    private final Image inProgressImg;
    private final Image errorImg;
    private final Image toStartImg;

    /**
     * Instantiates a new batch drop object status painter.
     */
    public BatchDropObjectStatusPainter() {
        this(true);
    }

    /**
     * Instantiates a new batch drop object status painter.
     *
     * @param paintBg the paint bg
     */
    public BatchDropObjectStatusPainter(boolean paintBg) {
        super(paintBg);
        this.completedImg = IconUtility.getIconImage(IiconPath.ICON_DROP_OBJECTS_COMPLETED, this.getClass());
        this.inProgressImg = IconUtility.getIconImage(IiconPath.ICON_DROP_OBJECTS_INPROGRESS, this.getClass());
        this.errorImg = IconUtility.getIconImage(IiconPath.ICON_DROP_OBJECTS_ERROR, this.getClass());
        this.toStartImg = IconUtility.getIconImage(IiconPath.ICON_DROP_OBJECTS_TOSTART, this.getClass());
    }

    /**
     * Gets the preferred width.
     *
     * @param status the status
     * @return the preferred width
     */
    public int getPreferredWidth(BatchDropStatusEnum status) {
        return getImage(status).getBounds().width;
    }

    /**
     * Gets the preferred height.
     *
     * @param status the status
     * @return the preferred height
     */
    public int getPreferredHeight(BatchDropStatusEnum status) {
        return getImage(status).getBounds().height;
    }

    /**
     * Paint icon image.
     *
     * @param gc the gc
     * @param rectangle the rectangle
     * @param yOffset the y offset
     * @param status the status
     */
    public void paintIconImage(GC gc, Rectangle rectangle, int yOffset, BatchDropStatusEnum status) {
        Image statusImage = getImage(status);

        // Center image
        int xAxis = rectangle.x + (rectangle.width / 2) - (statusImage.getBounds().width / 2);

        gc.drawImage(statusImage, xAxis, rectangle.y + yOffset);
    }

    /**
     * Gets the image.
     *
     * @param status the status
     * @return the image
     */
    public Image getImage(BatchDropStatusEnum status) {
        if (status == BatchDropStatusEnum.TO_START) {
            return this.toStartImg;
        }
        if (status == BatchDropStatusEnum.IN_PROGRESS) {
            return this.inProgressImg;
        }
        if (status == BatchDropStatusEnum.ERROR) {
            return this.errorImg;
        }
        if (status == BatchDropStatusEnum.COMPLETED) {
            return this.completedImg;
        }

        return this.toStartImg;
    }

    /**
     * Gets the image.
     *
     * @param cell the cell
     * @param configRegistry the config registry
     * @return the image
     */
    @Override
    protected Image getImage(ILayerCell cell, IConfigRegistry configRegistry) {
        return getImage(convertDataType(cell, configRegistry));
    }

    /**
     * Convert data type.
     *
     * @param cell the cell
     * @param configRegistry the config registry
     * @return the batch drop status enum
     */
    protected BatchDropStatusEnum convertDataType(ILayerCell cell, IConfigRegistry configRegistry) {
        if (cell.getDataValue() instanceof BatchDropStatusEnum) {
            return (BatchDropStatusEnum) cell.getDataValue();
        }

        return BatchDropStatusEnum.TO_START;
    }
}
