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

package org.opengauss.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.view.utils.Preferencekeys;

/**
 * 
 * Title: class
 * 
 * Description: The Class BLPreferenceImpl.
 *
 * @since 3.0.0
 */
public class BLPreferenceImpl implements IBLPreference, IPropertyChangeListener {

    private static final String SQL_QUERY_LENGTH = ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH;
    private static final String SQL_HISTORY_SIZE = ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE;
    private static final String OBECT_COUNT_FOR_LAZY_RENDERING = "org.opengauss.mppdbide.environment.sessionsetting.lazyrendering";
    private PreferenceStore ps = null;
    private static volatile IBLPreference systemPrefernce = null;
    private static final Object LOCK = new Object();

    /**
     * Instantiates a new BL preference impl.
     */
    public BLPreferenceImpl() {
        this.ps = PreferenceWrapper.getInstance().getPreferenceStore();
        this.ps.addPropertyChangeListener(this);
    }

    /**
     * Gets the BL preference.
     *
     * @return the BL preference
     */
    public static IBLPreference getBLPreference() {
        if (null == systemPrefernce) {
            synchronized (LOCK) {
                if (null == systemPrefernce) {
                    systemPrefernce = new BLPreferenceImpl();
                }

            }
        }
        return systemPrefernce;

    }

    /**
     * Gets the SQL history size.
     *
     * @return the SQL history size
     */
    @Override
    public int getSQLHistorySize() {
        return ps.getInt(SQL_HISTORY_SIZE);
    }

    /**
     * Gets the SQL query length.
     *
     * @return the SQL query length
     */
    @Override
    public int getSQLQueryLength() {
        return ps.getInt(SQL_QUERY_LENGTH);
    }

    /**
     * Gets the DS encoding.
     *
     * @return the DS encoding
     */
    @Override
    public String getDSEncoding() {

        return ps.getString(UserEncodingOption.DATA_STUDIO_ENCODING);
    }

    /**
     * Gets the file encoding.
     *
     * @return the file encoding
     */
    @Override
    public String getFileEncoding() {
        return ps.getString(UserEncodingOption.FILE_ENCODING);
    }

    /**
     * Checks if is include encoding.
     *
     * @return true, if is include encoding
     */
    public boolean isIncludeEncoding() {
        return ps.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_ENCODING);
    }

    /**
     * Property change.
     *
     * @param event the event
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(SQL_HISTORY_SIZE)) {
            Integer historySize = (Integer) event.getNewValue();
            SQLHistoryFactory.getInstance().setHistoryRetensionSize(historySize);
        }

        if (event.getProperty().equals(SQL_QUERY_LENGTH)) {
            Integer queryLength = (Integer) event.getNewValue();
            SQLHistoryFactory.getInstance().setSQLQuerySize(queryLength);
        }

    }

    /**
     * Read preference value for object count to show in object browser
     */
    @Override
    public int getLazyRenderingObjectCount() {
        return ps.getInt(OBECT_COUNT_FOR_LAZY_RENDERING);
    }

    /**
     * Gets the date format.
     *
     * @return the date format.
     */
    @Override
    public String getDateFormat() {
        return ps.getString(MPPDBIDEConstants.DATE_FORMAT_VALUE);
    }

    /**
     * Gets the date format.
     *
     * @return the date format.
     */
    @Override
    public String getTimeFormat() {
        return ps.getString(MPPDBIDEConstants.TIME_FORMAT_VALUE);
    }
    
    @Override
    public int getImportFileSizeInMb() {
        return ps.getInt(Preferencekeys.FILE_LIMIT_FOR_TABLE_DATA);
    }
}
