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

package org.opengauss.mppdbide.view.utils.icon;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.impl.TrimmedWindowImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.opengauss.mppdbide.bl.erd.model.AbstractERAttribute;
import org.opengauss.mppdbide.bl.erd.model.ERAttribute;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IconUtility.
 *
 * @since 3.0.0
 */
public interface IconUtility extends IiconPath {

    /**
     * The Constant ICON_PATH.
     */
    static final String ICON_PATH = "icons" + File.separator;

    /**
     * Gets the icon image.
     *
     * @param filename the filename
     * @param clazz the clazz
     * @return the icon image
     */
    public static Image getIconImage(String filename, Object clazz) {
        if (null == IconMap.getImageMap().get(filename)) {
            Bundle bundle = FrameworkUtil.getBundle((Class<?>) clazz);
            if (null != bundle) {
                URL url = FileLocator.find(bundle, new Path(ICON_PATH + filename), null);
                IconMap.getImageMap().put(filename, ImageDescriptor.createFromURL(url).createImage());
            }
        }

        return IconMap.getImageMap().get(filename);
    }

    /**
     * Gets the icon small image.
     *
     * @param filename the filename
     * @param clazz the clazz
     * @return the icon small image
     */
    public static Image getIconSmallImage(String filename, Object clazz) {
        if (null == IconMap.getImageMap().get(filename)) {
            Bundle bundle = FrameworkUtil.getBundle((Class<?>) clazz);
            if (null != bundle) {
                int imageSize = GUIHelper.convertHorizontalPixelToDpi(12);
                URL url = FileLocator.find(bundle, new Path(ICON_PATH + filename), null);

                ImageData scaleImageData = ImageDescriptor.createFromURL(url).getImageData();
                if (null != scaleImageData) {
                    ImageData imageData = scaleImageData.scaledTo(imageSize, imageSize);
                    IconMap.getImageMap().put(filename, ImageDescriptor.createFromImageData(imageData).createImage());
                }
            }
        }

        return IconMap.getImageMap().get(filename);
    }

    /**
     * Gets the icon image uri.
     *
     * @param filename the filename
     * @param clazz the clazz
     * @return the icon image uri
     */
    public static String getIconImageUri(String filename, Object clazz) {
        Bundle bundle = FrameworkUtil.getBundle((Class<?>) clazz);
        if (null != bundle) {
            URL url = FileLocator.find(bundle, new Path(ICON_PATH + filename), null);
            if (null == url) {
                return null;
            }
            return url.toString();
        }

        return null;
    }

    /**
     * Gets the icon image url.
     *
     * @param filename the filename
     * @param clazz the clazz
     * @return the icon image url
     */
    public static URL getIconImageUrl(String filename, Object clazz) {
        Bundle bundle = FrameworkUtil.getBundle((Class<?>) clazz);
        if (null != bundle) {
            URL url = FileLocator.find(bundle, new Path(ICON_PATH + filename), null);
            if (null == url) {
                return null;
            }
            return url;
        }

        return null;
    }

    /**
     * Gets the icon for debug object type.
     *
     * @param type the type
     * @return the icon for debug object type
     */

    // icon image for different function

    public static Image getIconForDebugObjectType(OBJECTTYPE type) {
        String iconName = "";
        switch (type) {
            case PLSQLFUNCTION: {
                iconName = IiconPath.ICO_FUNCTIONPLSQL;
                break;
            }
            case SQLFUNCTION: {
                iconName = IiconPath.ICO_FUNCTIONSQL;
                break;
            }
            case CFUNCTION: {
                iconName = IiconPath.ICO_FUNCTIONC;
                break;
            }
            case FUNCTION_GROUP: {
                iconName = IiconPath.ICO_FUNCTION_FOLDER;
                break;
            }
            default: {
                return null;
            }
        }
        return IconUtility.getIconImage(iconName, IconUtility.class);
    }

    /**
     * Gets the icon for invalid debug object type.
     *
     * @param type the type
     * @return the icon for invalid debug object type
     */

    // icon image for different function

    public static Image getIconForInvalidDebugObjectType(OBJECTTYPE type) {
        String iconName = "";
        switch (type) {
            case PLSQLFUNCTION: {
                iconName = IiconPath.ICO_FUNCTIONPLSQL_INVALID;
                break;
            }
            case SQLFUNCTION: {
                iconName = IiconPath.ICO_FUNCTIONSQL_INVALID;
                break;
            }
            default: {
                return null;
            }
        }
        return IconUtility.getIconImage(iconName, IconUtility.class);
    }

    /**
     * Gets the icon for foreign table type.
     *
     * @param type the type
     * @return the icon for foreign table type
     */
    public static Image getIconForForeignTableType(OBJECTTYPE type) {
        String iconName = "";
        switch (type) {
            case FOREIGN_TABLE_GDS: {
                iconName = IiconPath.FOREIGN_TABLE_GDS;
                break;
            }
            case FOREIGN_TABLE_HDFS: {
                iconName = IiconPath.FOREIGN_TABLE_HDFS;
                break;
            }
            case FOREIGN_PARTITION_TABLE: {
                iconName = IiconPath.ICO_FOREIGN_PARTITION_TABLE;
                break;
            }
            default: {
                return null;
            }
        }
        return IconUtility.getIconImage(iconName, IconUtility.class);
    }

    /**
     * Sets the window icon.
     *
     * @param modelService the model service
     * @param application the application
     */
    public static void setWindowIcon(EModelService modelService, MApplication application) {
        TrimmedWindowImpl mainWindow = (TrimmedWindowImpl) modelService.find(UIConstants.UI_MAIN_WINDOW_ID,
                application);
        if (mainWindow != null) {
            mainWindow.setIconURI(ICO_MPPDBIDE_APP_ICON_URI);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class IconMap.
     */
    public abstract class IconMap {
        private static Map<String, Image> imagemap = new HashMap<String, Image>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        /**
         * Gets the image map.
         *
         * @return the image map
         */
        public static Map<String, Image> getImageMap() {
            return imagemap;
        }

    }

    /**
     * Gets the object image.
     *
     * @param attribute the attribute
     * @return iconName: the icon path.
     *
     * @Title: getObjectImage
     * @Description: get the object's iconPath according to the data type
     */
    public static String getObjectImage(AbstractERAttribute attribute) {
        String iconName = "";
        if (attribute instanceof ERAttribute) {
            iconName = getOLAPImage((ERAttribute) attribute);
        }
        return iconName;
    }

    /**
     * Get the OLAP object image.
     *
     * @param attribute the attribute
     * @return the icon name
     */
    public static String getOLAPImage(ERAttribute attribute) {
        String typeName = attribute.getServerObject().getDataType().getName();
        String iconName = "";
        switch (typeName) {
            case "bool":
                iconName = IiconPath.TYPE_BOOLEAN;
                break;
            case "char":
            case "varchar":
            case "nvarchar2":
            case "varchar2(n)":
            case "character":
            case "text":
            case "_text":
            case "bpchar":
                iconName = IiconPath.TYPE_STRING;
                break;
            case "double":
            case "int1":
            case "int2":
            case "int4":
            case "_int4":
            case "int8":
            case "numeric":
            case "decimal":
            case "float":
            case "float4":
            case "float8":
                iconName = IiconPath.TYPE_NUMBER;
                break;
            case "date":
            case "interval":
            case "timetz":
            case "timestamptz":
            case "timestamp":
            case "time":
            case "smalldatetime":
                iconName = IiconPath.TYPE_DATETIME;
                break;
            case "blob":
            case "clob":
                iconName = IiconPath.TYPE_LOB;
                break;
            default:
                iconName = IiconPath.TYPE_UNKNOWN;
        }
        return iconName;
    }
}