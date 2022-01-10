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



import java.io.File;

import object_repository.ConsoleResultElements;
import object_repository.DebugElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import script_library.DebugOperations;
import script_library.Login;
import script_library.ObjectBrowserPane;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import autoitx4java.AutoItX;

public class SR_V1R2_DS_Debug_Pwd_Enh {

	public static void main(String sARNumber) throws Exception {

		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_Debug_Pwd_Enh");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_Debug_Pwd_Enh");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
		String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
		String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
		String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
		String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
		String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
        
		UtilityFunctions.deleteDir(new File(GlobalConstants.sProfilePath));
		
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);

			if(sExecute.equalsIgnoreCase("Yes")){

				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "SR_V1R2_DS_Debug_Pwd_Enh", i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_4")){

					Login.LaunchIDE(GlobalConstants.sIDEPath);
					Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
					Thread.sleep(GlobalConstants.MinWait);
					
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					AutoItX x = new AutoItX();
					boolean b  = x.controlCommandIsEnabled(LoginElements.wDBConnection, "", LoginElements.bOK);
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);	


					if(b==true)
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is not saved for existing connection profile. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");
					}


				}



				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_22")){

					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);

					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password prompted while connecting again when already saved for the connection profile. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);
						System.out.println(sTestCaseID  + "failed");

					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
					}



				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_23")){

					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					DebugOperations.DebugObjectBrowser_Open();
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugOption("Shortcut");

					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password prompted while debugging when already saved for the connection profile. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);
						System.out.println(sTestCaseID  + "failed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
					}


				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_5")||sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_6")||
						sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_7")||sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_8")){

					switch(sTestCaseID)
					{
					case "PTS_TOR.280.001_Functional_valid_7":

						DebugOperations.DebugOption("Toolbar");
						if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password prompted while debugging when already saved for the connection profile. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							System.out.println(sTestCaseID  + "failed");
							BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);

						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							System.out.println(sTestCaseID  + "passed");
						}
						break;

					case "PTS_TOR.280.001_Functional_valid_6":

						DebugOperations.DebugOption("ObjectBrowser");
						if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password prompted while debugging when already saved for the connection profile. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							System.out.println(sTestCaseID  + "failed");


						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							System.out.println(sTestCaseID  + "passed");
						}

						break;

					case "PTS_TOR.280.001_Functional_valid_5":

						DebugOperations.DebugOption("SQLViewer");
						if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password prompted while debugging when already saved for the connection profile. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							System.out.println(sTestCaseID  + "failed");


						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							System.out.println(sTestCaseID  + "passed");
						}

						break;

					case "PTS_TOR.280.001_Functional_valid_8":

						DebugOperations.DebugOption("Menu");
						if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password prompted while debugging when already saved for the connection profile. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							System.out.println(sTestCaseID  + "failed");


						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							System.out.println(sTestCaseID  + "passed");
						}

						break;

					default:	
						break;
					}


				}



				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_1"))
				{

					Login.LaunchIDE(GlobalConstants.sIDEPath);
					Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"NO");
					Thread.sleep(GlobalConstants.MaxWait);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);			

					if(BaseActions.ObjExists(LoginElements.wDBConnection, "",LoginElements.lstSavePassword )){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Save Password combo box is not available. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");

					}
				}
				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_2")) {

					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);

					sFlag = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.lstSavePassword);
					if(sFlag.equals("Do Not Save")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);

					}
					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Do Not Save Password is not selected by default in the combo box. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
					}
				}


				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_12")){      //Testcase Mapped PTS_TOR.280.001_Functional_valid_11

					Thread.sleep(GlobalConstants.MaxWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					AutoItX x = new AutoItX();
					boolean b  = x.controlCommandIsEnabled(LoginElements.wDBConnection, "", LoginElements.bOK);

					if(b==false)
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is already saved for existing connection profile. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");
					}
				}
				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_15")) {

					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					Thread.sleep(GlobalConstants.MedWait);     
					sFlag = BaseActions.ControlGetText(LoginElements.wDBConnection, "", LoginElements.lstSavePassword);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);

					if(sFlag.equals("Do Not Save"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Save Password option is selected in the combo box when not saved in connection profile.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");
					}
				}



				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_13")){

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					DebugOperations.DebugObjectBrowser_Open();
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugOption("Shortcut");



					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is not prompted while debugging when not saved in connection profile. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");
					}
				}

				if( sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_14")){
					{
						Thread.sleep(GlobalConstants.MinWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);

						sFlag = BaseActions.ControlGetText(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugComboBox);
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);


						if(!sFlag.equals("Permanently"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							System.out.println(sTestCaseID  + "passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Save Password option is selected in the combo box when not saved in connection profile.Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							System.out.println(sTestCaseID  + "failed");
						}
					}
				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_24")){

					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MedWait);


					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");

					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password not prompted while connecting again although it was not saved for the connection profile. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");


					}

				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_Invalid_6")){

					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.SetText(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugpassword, "WrongPassword");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugOKButton);
					Thread.sleep(GlobalConstants.MedWait);

					if(BaseActions.WinExists(DebugElements.wInvalidDebugPassword)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						BaseActions.Click(DebugElements.wInvalidDebugPassword, "", DebugElements.bInvalidOK);
						BaseActions.Winwait(DebugElements.sDebugPasswordWindow);
						BaseActions.SetText(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugpassword, sPassword);
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugComboBox);
						UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugOKButton);
						System.out.println(sTestCaseID  + "passed");

					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Did not prompt for correct password when wrong password was entered for the connection profile. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");

					}

				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_25")) {

					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugObjectBrowser_Open();
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugOption("Shortcut");

					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);
						System.out.println(sTestCaseID  + "passed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password not prompted while debugging although it was not saved for the connection profile. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");

					}
				}


				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_27")){

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Winwait(DebugElements.sDebugPasswordWindow);
					BaseActions.SetText(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugpassword, sPassword);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugComboBox);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugOKButton);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					DebugOperations.DebugObjectBrowser_Open();
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugOption("Shortcut");
					BaseActions.SetText(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugpassword, sPassword);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugComboBox);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugOKButton);
					Thread.sleep(GlobalConstants.MaxWait);
					DebugOperations.DebugOption("Shortcut");

					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "",DebugElements.sDebugCancelButton );
						System.out.println(sTestCaseID  + "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug window did not prompt for password when password not saved while debugging.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");
					}


				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_Invalid_2")){

					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugOption("Shortcut");
					BaseActions.SetText(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugpassword, "WrongPassword");
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "",DebugElements.bOK );
					Thread.sleep(GlobalConstants.MedWait);

					if(BaseActions.WinExists(DebugElements.wInvalidDebugPassword))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						BaseActions.Click(DebugElements.wInvalidDebugPassword, "",DebugElements.bInvalidOK );
						Thread.sleep(GlobalConstants.MinWait);
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "",DebugElements.sDebugCancelButton );
						System.out.println(sTestCaseID  + "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug window did not prompt for correct password when wrong password entered while debugging.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_20")){

					DebugOperations.DebugOption("Shortcut");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.SetText(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugpassword, sPassword);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugComboBox);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugOKButton);
					Thread.sleep(GlobalConstants.MaxWait);
					DebugOperations.DebugOption("Shortcut");


					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						Thread.sleep(GlobalConstants.MedWait);
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "",DebugElements.sDebugCancelButton );
						System.out.println(sTestCaseID  + "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug window did not prompt for password when password not saved while debugging.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");
					}

				}
				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_21")) {

					Thread.sleep(GlobalConstants.MaxWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					AutoItX x = new AutoItX();
					boolean b  = x.controlCommandIsEnabled(LoginElements.wDBConnection, "", LoginElements.bOK);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);

					if(b==false)
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is saved on for the existing connection although it was not saved while debugging. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");
					}
				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_17")){

					DebugOperations.DebugOption("Shortcut");
					BaseActions.Winwait(DebugElements.sDebugPasswordWindow);
					BaseActions.SetText(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugpassword, sPassword);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugComboBox);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugOKButton);
					Thread.sleep(GlobalConstants.MaxWait);
					DebugOperations.DebugOption("Shortcut");


					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug window again prompts for password although saved while debugging.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
						System.out.println(sTestCaseID  + "failed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
					}

				}
				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_18")) {

					Thread.sleep(GlobalConstants.MaxWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);

					AutoItX x = new AutoItX();
					boolean b  = x.controlCommandIsEnabled(LoginElements.wDBConnection, "", LoginElements.bOK);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);

					if(b==true)
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is not saved for the existing connection although saved while debugging. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");
					}
				}



				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_26")){

					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.removeConnection();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.lstSavePassword);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 2);
					Thread.sleep(GlobalConstants.MaxWait);
					ObjectBrowserPane.objectBrowserExpansion("Single");
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					DebugOperations.DebugObjectBrowser_Open();
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugOption("Shortcut");
					BaseActions.Winwait(DebugElements.sDebugPasswordWindow);
					BaseActions.SetText(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugpassword, sPassword);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugComboBox);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugOKButton);
					Thread.sleep(GlobalConstants.MaxWait);
					DebugOperations.DebugOption("Shortcut");

					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug window again prompts for password although saved while debugging.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
						System.out.println(sTestCaseID  + "failed");

					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
					}


				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_Invalid_1")){

					Thread.sleep(GlobalConstants.MaxWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					BaseActions.Winwait(LoginElements.wDBConnection);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					BaseActions.Winwait(LoginElements.wDBConnection);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.sConnectionName);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);

					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName , "TestConnection");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.sPassword);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);

					BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sPassword , "WrongPassword");

					BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
					Thread.sleep(GlobalConstants.MedWait);

					if(BaseActions.WinExists(LoginElements.wWrongPassword)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						BaseActions.Click(LoginElements.wWrongPassword, "", LoginElements.bWrongPswdOk);
						Thread.sleep(GlobalConstants.MedWait);
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
						System.out.println(sTestCaseID  + "passed");
					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Connection window doesnot prompt for correct password.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
						System.out.println(sTestCaseID  + "failed");
					}

				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_28")){

					Thread.sleep(GlobalConstants.MaxWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);

					ObjectBrowserPane.sslLogin("PERMANENT");

					Thread.sleep(GlobalConstants.MedWait);

					BaseActions.Winwait(LoginElements.wIDEWindow);

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					Thread.sleep(GlobalConstants.MaxWait);
					ObjectBrowserPane.objectBrowserRefresh("Double");       // Create one schema "AUTO" for the given connection and a function to debug 
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugOption("Shortcut");

					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password prompted while debugging although it was already saved.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						Thread.sleep(GlobalConstants.MedWait);
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);
						System.out.println(sTestCaseID  + "passed");
					}
				}

				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_Invalid_7")){                 // Testcase mapped PTS_TOR.280.001_Functional_Invalid_3

					Thread.sleep(GlobalConstants.MaxWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.objectBrowserRefresh("Double");
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					ObjectBrowserPane.disconnectDB();
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);


					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password prompted while connecting again although password is already saved for the connection profile. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);

					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID  + "passed");
					}
				}
				if(sTestCaseID.equals("PTS_TOR.280.001_Functional_valid_30")) {

					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.objectBrowserRefresh("Double");       // Create one schema "AUTO" for the given connection and a function to debug 
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.DebugOption("Shortcut");

					if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password prompted while debugging although it was already saved.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID  + "failed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						Thread.sleep(GlobalConstants.MedWait);
						BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);
						System.out.println(sTestCaseID  + "passed");
					}
				}


			}
		}






		for(int i=1;i<=27;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}

}




