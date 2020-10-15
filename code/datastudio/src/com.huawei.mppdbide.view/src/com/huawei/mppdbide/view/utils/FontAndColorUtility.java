/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
