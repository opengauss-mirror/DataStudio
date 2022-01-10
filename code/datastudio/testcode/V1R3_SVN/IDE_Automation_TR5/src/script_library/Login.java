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
TITLE - IDE LOGIN/LOGOUT
DESCRIPTION - SCRIPT FOR IDE LOGIN AND IDE LOGOUT
MODIFICATION HISTORY - 
TEST CASES COVERED - NA
 *************************************************************************/
package script_library;


import java.awt.event.KeyEvent;

import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.ObjectBrowserElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import autoitx4java.AutoItX;

public class Login {


	public static void LaunchIDE(String sPath) throws Exception {
		/*************************************************************************
	FUNCTION NAME		: LaunchIDE()
	DESCRIPTION	 		: Function to invoke the IDE tool
	IN PARAMETERS		: path {exe path of the IDE Tool}
	RETURN PARAMETERS	: None
		 *************************************************************************/
		AutoItX x = new AutoItX();
		if(x.winExists(LoginElements.wIDEWindow))
			x.winClose(LoginElements.wIDEWindow);
		UtilityFunctions.ProcessClose("Data Studio.exe");
		x.run(sPath);
		Thread.sleep(GlobalConstants.MedWait);
		x.winWait(LoginElements.wDBConnection);
		x.winActive(LoginElements.wDBConnection);
		Thread.sleep(GlobalConstants.MinWait);
	}
	public static String IDELogin(String sConnectionName,String sHost,String sHostPort,String sDBName,String sUsername,String sPassword, String sSavePassword) throws Exception{
		/*************************************************************************
		FUNCTION NAME : IDELogin()
		DESCRIPTION : Function to Login to IDE Tool
		IN PARAMETERS : sConnectionName {Name of the Connection}
		: sHost {Host Name}
		: sHostPort {Port Number}
		: sDBName {Database Name}
		: sUserName {User Name}
		: sPassword {Password}
		RETURN PARAMETERS : None
		 *************************************************************************/
		BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.sConnectionName);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName,sConnectionName);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHost, sHost);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHostPort, sHostPort);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sDBName, sDBName);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sUsername, sUsername);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sPassword, sPassword);
		BaseActions.Click(LoginElements.wDBConnection, "",LoginElements.lstSavePassword );
		switch(sSavePassword)
		{
		case "NO":
			UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
			break;
		case "SESSION":
			UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
			break;
		case "PERMENANT":
			UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
			break;
		default :
			break;
		}

		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

		BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
		Thread.sleep(2500);
		DisplayGlobalConsole();
		DisplayDebugWindows();
		Thread.sleep(GlobalConstants.ModWait);
		String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");
		if(sConsoleOutput.contains("successfull"))
			return "Connected";
		else
		{ 
			UtilityFunctions.ProcessClose("Data Studio.exe");
			return "Not Connected";
		} 
	}

	public static String multipleLogin(String sConnectionName,String sHost,String sHostPort,String sDBName,String sUsername,String sPassword, String sSavePassword) throws Exception
	{
		BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.sConnectionName);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName,sConnectionName);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHost, sHost);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHostPort, sHostPort);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sDBName, sDBName);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sUsername, sUsername);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sPassword, sPassword);
		BaseActions.Click(LoginElements.wDBConnection, "",LoginElements.lstSavePassword );
		switch(sSavePassword)
		{
		case "NO":
			UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
			break;
		case "SESSION":
			UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
			break;
		case "PERMENANT":
			UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
			break;
		default :
			break;
		}

		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

		BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
		Thread.sleep(2500);
		String sConsoleOutput = QueryResult.ReadConsoleOutput("GLOBAL");
		if(sConsoleOutput.contains("successfull"))
			return "Connected";
		else
		{ 
			UtilityFunctions.ProcessClose("Data Studio.exe");
			return "Not Connected";
		} 

	}


	public static void IDELogout() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: IDELogout()
	DESCRIPTION	 		: Function to Logout from IDE Tool
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		AutoItX x = new AutoItX();
		x.winActivate(LoginElements.wIDEWindow);
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
				ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MinWait);
		x.winClose(LoginElements.wIDEWindow);
		UtilityFunctions.ProcessClose("Data Studio.exe");
	}

	public static void DisplayDebugWindows() throws Exception {

		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_V, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		Thread.sleep(GlobalConstants.MedWait);
		}


	public static void DebugWindows() throws Exception {


		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_V, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		Thread.sleep(GlobalConstants.MedWait);
		}

	public static void DisplayGlobalConsole() throws Exception {
		//Display Global Console Window
		if(BaseActions.ObjExists(LoginElements.wIDEWindow,"","SWT_Window028"))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
		}
		else
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_V, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
		}

	}

}