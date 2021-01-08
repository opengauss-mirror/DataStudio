/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
