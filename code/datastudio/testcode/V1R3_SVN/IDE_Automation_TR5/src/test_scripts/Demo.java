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

package test_scripts;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.management.Query;

import org.apache.xmlbeans.impl.store.QueryDelegate;

import autoitx4java.AutoItX;
import object_repository.ConsoleResultElements;
import object_repository.CreateTableWizardElements;
import object_repository.EditWindowElements;
import object_repository.ExecQueryElements;
import object_repository.ExpQueryElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.ObjectBrowserElements;
import object_repository.SaveAsElements;
import object_repository.TablePropertyElements;
import object_repository.TablespaceElements;
import script_library.CreateTableWizardFunctions;
import script_library.DataMgmtFunctions;
import script_library.DebugOperations;
import script_library.EditTableDataFunctions;
import script_library.ExecutionHistoryFunctions;
import script_library.Login;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import script_library.TablespaceFunctions;
import script_library.ViewFunctions;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class Demo {

	public static void main(String[] args) throws Exception {

		Thread.sleep(GlobalConstants.MaxWait);

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.SetText(ExpQueryElements.wFileOpenWindow, "",ExpQueryElements.sSQLOpenControlID ,"path");
		String sButton = null;
		if(sButton.equalsIgnoreCase("OPEN"))
			BaseActions.Click(ExpQueryElements.wFileOpenWindow, "",ExpQueryElements.sOpenButton);
		else
			BaseActions.Click(ExpQueryElements.wFileOpenWindow, "",ExpQueryElements.sCancelButton);
		Thread.sleep(GlobalConstants.MedWait);
	}


}



