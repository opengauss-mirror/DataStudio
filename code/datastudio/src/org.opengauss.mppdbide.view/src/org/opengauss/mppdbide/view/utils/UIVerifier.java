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

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIVerifier.
 *
 * @since 3.0.0
 */
public abstract class UIVerifier {

    /**
     * Verify text size.
     *
     * @param txtInput the txt input
     * @param textLimit the text limit
     */
    public static void verifyTextSize(Text txtInput, int textLimit) {
        txtInput.addVerifyListener(new VerifyListener() {
            @Override
            public void verifyText(VerifyEvent event) {
                try {
                    int textLength = ((Text) event.widget).getTextChars().length + event.text.length();
                    if (textLength > textLimit) {
                        event.doit = false;
                    }
                } catch (NumberFormatException numberFormatException) {
                    event.doit = false;
                }
            }
        });
    }
    
    /**
     * Verify styled text size.
     *
     * @param txtInput the txt input
     */
    public static void verifyStyledTextSize(StyledText txtInput) {
        txtInput.addVerifyListener(new VerifyListener() {
            @Override
            public void verifyText(VerifyEvent event) {
                String text = ((StyledText) event.widget).getText() + event.text;
                try {
                    if (text.length() > 32) {
                        event.doit = false;
                    }
                } catch (NumberFormatException numberFormatException) {
                    event.doit = false;
                }
            }
        });
    }
}
