/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.createfunction.CreateFunctionRelyInfo;

/**
 *
 * Title: class
 *
 * Description: The Class CreateFunction.
 *
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CreateTriggerFunction extends CreateFunctionBase {
    /**
     * Execute.
     */
    @Execute
    public void execute() {
        MPPDBIDELoggerUtility.info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_CREATE_FUNCTION));
        baseExecute(CreateFunctionRelyInfo.LANGUAGE_TRIGGER);
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return baseCanExecute();
    }
}
