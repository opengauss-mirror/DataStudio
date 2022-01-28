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

package com.huawei.mppdbide.view.prefernces;

/**
 * 
 * Title: interface IAutoCompletePreference
 * 
 * Description: The interface IAutoCompletePreference.
 *
 * @since 3.0.0
 */
public interface IAutoCompletePreference {

    /**
     * The auto complete preference key.
     */
    String AUTO_COMPLETE_PREFERENCE_KEY = "com.huawei.mppdbide.editor.autocomplete.preferences.wordsize";

    /**
     * The auto complete default word size.
     */
    int AUTO_COMPLETE_DEFAULT_WORD_SIZE = 2;

}
