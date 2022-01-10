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
package script_library;

import java.awt.event.KeyEvent;

import autoitx4java.AutoItX;
import object_repository.EditWindowElements;
import object_repository.GlobalConstants;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class EditTableDataFunctions {


	public static void autoTableNavigation() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		BaseActions.MouseClick(EditWindowElements.wSQLTerminal, "", EditWindowElements.sMouseClick, EditWindowElements.sButton,EditWindowElements.iClick,110,11);
		
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);	
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.MouseClick(EditWindowElements.wSQLTerminal, "", EditWindowElements.sMouseClick, EditWindowElements.sButton, EditWindowElements.iClick, EditWindowElements.iObjectxcord, EditWindowElements.iObjectycord);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
	}

	public static void systemTableNavigation() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		BaseActions.MouseClick(EditWindowElements.wSQLTerminal, "", EditWindowElements.sMouseClick, EditWindowElements.sButton,EditWindowElements.iClick,110,11);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);	
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.MouseClick(EditWindowElements.wSQLTerminal, "", EditWindowElements.sMouseClick, EditWindowElements.sButton, EditWindowElements.iClick,110 ,281 );
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

	}
	
	public static void closeEditData() throws Exception
	{
		BaseActions.MouseClick("Data Studio", "", "SWT_Window016", "left", 1, 229, 13);
		
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
	}

	public static void closeEditTableResult() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
		if(BaseActions.WinExists("Confirm Discard Changes"))
		{
			BaseActions.Click("Confirm Discard Changes", "", "Button1");
		}
	}	

	public static void editTableWindow() throws Exception
	{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}

	public static void editTableWizard(String sOption,String sQuery) throws Exception
	{

		switch (sOption.toUpperCase()) {
		case "SELECT":
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.SetText(EditWindowElements.wEditWindow ,"", EditWindowElements.sSelectTextBox, sQuery);
			break;

		case "WHERE":
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.SetText(EditWindowElements.wEditWindow ,"", EditWindowElements.sWhereTextBox, sQuery);

			break;

		case "ORDERBY":
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.SetText(EditWindowElements.wEditWindow ,"", EditWindowElements.sOrderbyTextBox, sQuery);
			break;

		default:
			break;
		}
	}

	public static void Button(String sButtonToBeClicked) throws Exception
	{
		switch (sButtonToBeClicked.toUpperCase()) {
		case "OK":
			BaseActions.Click(EditWindowElements.wEditWindow ,"", EditWindowElements.bOkButton);
			break;

		case "CANCEL":
			BaseActions.Click(EditWindowElements.wEditWindow ,"", EditWindowElements.bCancelButton);
			break;

		case "EXECUTE":

			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.Click("Data Studio","", "Button4");
			break;

		case "EDIT":
			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.Click("Data Studio","", "Button5");
			break;

		default:
			break;
		}
	}

	public static String copyEditTableData() throws Exception
	{
		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		return x.clipGet();
	}

	public static String copyEditTableResultData() throws Exception
	{
		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_K, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_K, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		return x.clipGet();
	}


	public static void editDataOperations(String sOption) throws Exception
	{

		switch (sOption.toUpperCase()) {

		case "ADD":
			Thread.sleep(GlobalConstants.MedWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			Thread.sleep(GlobalConstants.ModWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
			Thread.sleep(GlobalConstants.ModWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "DELETE":
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "PASTE":
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "POST":
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "ROLLBACK":
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "COMMIT":
			Thread.sleep(GlobalConstants.MinWait);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			Thread.sleep(GlobalConstants.MinWait);
			BaseActions.Winwait("Commit Successful");
			Thread.sleep(GlobalConstants.MedWait);
			BaseActions.Click("Commit Successful", "", "Button1");
			break;

		default:
			break;
		}

	}

}
