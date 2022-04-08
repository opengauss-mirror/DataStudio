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

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * Title: class
 * 
 * Description: The Class FontAndColorUtility.
 *
 * @since 3.0.0
 */
public abstract class FontAndColorUtility {

    /**
     * Gets the font.
     *
     * @param name the name
     * @param height the height
     * @param style the style
     * @param parent the parent
     * @return the font
     */
    public static Font getFont(String name, int height, int style, Composite parent) {
        ResourceManager resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
        Font font = resManager.createFont(FontDescriptor.createFrom(name, height, style));
        return font;
    }

    /**
     * Gets the color.
     *
     * @param systemColorID the system color ID
     * @return the color
     */
    public static Color getColor(int systemColorID) {
        Display display = Display.getDefault();
        return display.getSystemColor(systemColorID);
    }
}
