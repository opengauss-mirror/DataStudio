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

/*************************************************************************
TITLE - QUERY RESULT
DESCRIPTION - FUNCTIONS WITH RESPECT TO QUERY CONSOLE/RESULT WINDOW
MODIFICATION HISTORY - 
TEST CASES COVERED - NA
 *************************************************************************/
package script_library;

import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import object_repository.SaveAsElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import autoitx4java.AutoItX;

public class QueryResult {


	public static String ExportButton() throws Exception
	{
		AutoItX x = new AutoItX();
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.Focus(SaveAsElements.wResult, "", SaveAsElements.sMouseClick);
		BaseActions.MouseClick(SaveAsElements.wResult, "", SaveAsElements.sMouseClick, SaveAsElements.sMouseButton, SaveAsElements.iClick, SaveAsElements.ixcord,SaveAsElements.iycord );
		Thread.sleep(GlobalConstants.MedWait);
		if(x.winExists(SaveAsElements.wDisclaimer))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}
		if(x.winExists(SaveAsElements.sSave))
			return "Success";
		else
			return "Fail";
	}	

	public static String CurrentExport() throws Exception 
	{
		AutoItX x = new AutoItX();
		Thread.sleep(GlobalConstants.ModWait);
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.Focus(SaveAsElements.wResult, "", SaveAsElements.sMouseClick);
		BaseActions.MouseClick(SaveAsElements.wResult, "", SaveAsElements.sMouseClick, SaveAsElements.sMouseButton, SaveAsElements.iClick, SaveAsElements.iCurrentResultxcord,SaveAsElements.iCurrrentResultycord );
		Thread.sleep(GlobalConstants.MedWait);
		if(x.winExists(SaveAsElements.wDisclaimer))
		{
			BaseActions.Click(SaveAsElements.wDisclaimer,"",SaveAsElements.bDisclaimerOk );
		}
		if(x.winExists(SaveAsElements.sSave))
			return "Success";
		else
			return "Fail";
	}
	public static String CopyContent() throws Exception
	{
		AutoItX x = new AutoItX();
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Focus(SaveAsElements.wResult, "", SaveAsElements.sMouseClick);
		BaseActions.MouseClick(SaveAsElements.wResult, "", SaveAsElements.sMouseClick, SaveAsElements.sMouseButton, SaveAsElements.iClick, SaveAsElements.iResultxcord,SaveAsElements.iResultycord );
		return x.clipGet();

	}

	public static void SaveCsv(String sFilePath) throws Exception
	{
		AutoItX x = new AutoItX();
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.MouseClick(SaveAsElements.sSave, "", SaveAsElements.sSaveClick, SaveAsElements.sSaveButton, SaveAsElements.iClick, SaveAsElements.ix, SaveAsElements.iy);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
		x.send(sFilePath);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
		Thread.sleep(GlobalConstants.MedWait);    
		if(x.winExists("File Overwrite Confirmation."))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}
		Thread.sleep(GlobalConstants.MedWait);
	}

	public static void EmptyCsv() throws Exception
	{
		BaseActions.MouseClick(SaveAsElements.sSave, "", SaveAsElements.sSaveClick, SaveAsElements.sSaveButton, SaveAsElements.iClick, SaveAsElements.ix, SaveAsElements.iy);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
	}


	public static String ReadConsoleOutput(String sConsoleType) throws Exception
	{
		String sConsoleOutput;
		AutoItX x = new AutoItX();
		Thread.sleep(GlobalConstants.MinWait);

		if(sConsoleType.equalsIgnoreCase("GLOBAL"))
		{
			//Navigate to Global Console
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 2);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 3);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 3);
		}
		else
		{
			QueryEditor.SetTerminalFocus();
		} 
		//Copy Console Window Content
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
		Thread.sleep(GlobalConstants.MedWait);
		sConsoleOutput = x.clipGet();
		return sConsoleOutput;
	}

	public static void TerminalConsoleResultNavigation(String sTab) throws Exception
	{
		Thread.sleep(GlobalConstants.MinWait);
		//Navigate to Global Console
		QueryEditor.SetTerminalFocus();
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		if(sTab.equals("CONSOLE"))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 2);
		}
		else
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 2);
		}
	}
	public static int RecordCount(String filename) throws Exception {

		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 1 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}    


	public static void EmptyTable() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_V, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		Thread.sleep(GlobalConstants.MedWait);
	}
	public static String EmptyTableValidation() throws Exception
	{
		String sEmptyFlag;
		AutoItX x = new AutoItX();

		if(x.winExists("Internal Error")) 
		{
			sEmptyFlag = "Fail";
		}

		else
		{
			sEmptyFlag = "Pass";
		}
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		return sEmptyFlag;
	}
	public static String BulkQueryValidation() throws Exception
	{
		String sQueryFlag;
		AutoItX x = new AutoItX();

		if(x.winExists("Save As")) 
		{
			sQueryFlag = "Fail";
		}

		else
		{
			sQueryFlag = "Pass";
		}

		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.Click("Save As", "", "Button2");
		return sQueryFlag;
	}

	public static String TextandEmptyValidation() throws Exception
	{
		String sEmptyFlag;
		AutoItX x = new AutoItX();
		if(x.winExists("Save As"))
		{
			sEmptyFlag = "Pass";
			UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
		}
		else
		{
			sEmptyFlag = "Fail";
		}
		return sEmptyFlag;
	}
	public static void NextRecords(String sNumberOfRecords) throws Exception

	{	BaseActions.Focus(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sNextRecordControlID);
	Thread.sleep(GlobalConstants.MinWait);
	UtilityFunctions.KeyPress(KeyEvent.VK_END, 1);
	UtilityFunctions.KeyRelease(KeyEvent.VK_END, 1);
	UtilityFunctions.KeyPress(KeyEvent.VK_BACK_SPACE, 4);
	UtilityFunctions.KeyRelease(KeyEvent.VK_BACK_SPACE, 4);
	Thread.sleep(GlobalConstants.MinWait);
	BaseActions.SetText(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sNextRecordControlID, sNumberOfRecords);
	}

	public static void ExeNextRecord() throws Exception
	{
		BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sNextRecordExeControlID, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iRecordxcord, ConsoleResultElements.iRecordycord);
	}

	public static void ResultWindow() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
	}


	public static String EditCopyContent() throws Exception
	{
		AutoItX x = new AutoItX();
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Focus(SaveAsElements.wResult, "", SaveAsElements.sMouseClick);
		BaseActions.MouseClick(SaveAsElements.wResult, "", SaveAsElements.sMouseClick, SaveAsElements.sMouseButton, SaveAsElements.iClick, SaveAsElements.iResultxcord,SaveAsElements.iResultycord );
		return x.clipGet();
	}


	public static String CopyContentFirstTime() throws Exception
	{
		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 3);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		return x.clipGet();

	}
	public static String CopyContentSpecial() throws Exception
	{
		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.MouseClick(SaveAsElements.wResult, "", SaveAsElements.sMouseClick, SaveAsElements.sMouseButton, SaveAsElements.iClick, SaveAsElements.iResultxcord,SaveAsElements.iResultycord );
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		return x.clipGet();
	}
}

