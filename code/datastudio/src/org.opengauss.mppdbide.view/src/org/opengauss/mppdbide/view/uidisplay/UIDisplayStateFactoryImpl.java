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

package org.opengauss.mppdbide.view.uidisplay;

import org.opengauss.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateFactoryIf;
import org.opengauss.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateIf;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIDisplayStateFactoryImpl.
 *
 * @since 3.0.0
 */
public class UIDisplayStateFactoryImpl implements UIDisplayStateFactoryIf {
    /**
     * Gets the UI display state.
     *
     * @return the UI display state
     */
    @Override
    public UIDisplayStateIf getUIDisplayState() {
        return UIDisplayState.getInstaDisplayState();
    }
}
