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

import org.apache.commons.logging.Log;

import autoitx4java.AutoItX;
import object_repository.ConsoleResultElements;
import object_repository.DebugElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import script_library.DebugOperations;
import script_library.Login;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_Debug_Pswd {
	
public static void main(String sARNumber) throws Exception {
		
		//Creating the Test Result File for Reporting
				String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_Debug_Pswd");
				//Creating the Test Result File for TMSS
				String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_Debug_Pswd");
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
					
						sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "SR_V1R2_DS_Debug_Pswd", i,1);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
						
						
						if(sTestCaseID.equals("SDV_FUN_VAL_DS_DB_PWD_001"))
						{
							//Launching the IDE Tool Application
							Login.LaunchIDE(GlobalConstants.sIDEPath);
							//Login to IDE
							Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"NO");
							Thread.sleep(GlobalConstants.MaxWait);
							Thread.sleep(GlobalConstants.MedWait);
							UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
							UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
							Thread.sleep(GlobalConstants.MinWait);
							UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
						
							AutoItX x = new AutoItX();
							boolean b  = x.controlCommandIsEnabled(LoginElements.wDBConnection, "", LoginElements.bOK);
							BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);	
							
							
							if(b==false)
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is already saved for existing connection profile. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}
						
						if(sTestCaseID.equals("SDV_FUN_VAL_DS_DB_PWD_002"))
						{
							
							UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
							UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
							DebugOperations.DebugObjectBrowser_Open();
							Thread.sleep(GlobalConstants.MedWait);
							DebugOperations.DebugOption("Shortcut");
							Thread.sleep(GlobalConstants.MedWait);
							  
								
							if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is not prompted while debugging when not saved in connection profile. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
							}
							
							if(sTestCaseID.equals("SDV_FUN_VAL_DS_DB_PWD_003"))
							{
								
								UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
								UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
								     
								 sFlag = BaseActions.ControlGetText(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugComboBox);
								 BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);
								 
								  
								if(!sFlag.equals("Permanently"))
								{
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
								}
								else
								{
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Save Password option is selected in the combo box when not saved in connection profile.Please refer screenshot "+sTestCaseID+".jpg");
									UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
								}
							}
						
					
						if(sTestCaseID.equals("SDV_FUN_VAL_DS_DB_PWD_006"))  //Testcase Mapped SDV_FUN_VAL_DS_DB_PWD_005
						{
							
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
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug window again prompts for password when saved once.Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
								BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
								
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							}
							
							}
							if(sTestCaseID.equals("SDV_FUN_VAL_DS_DB_PWD_007")) {
							
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
							
							if(b==true)
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is not saved on for the existing connection when saved while debugging. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
							}
						
						
						
						if(sTestCaseID.equals("SDV_FUN_VAL_DS_DB_PWD_009"))  //Testcase Mapped SDV_FUN_VAL_DS_DB_PWD_008
						{
							
							Login.LaunchIDE(GlobalConstants.sIDEPath);
							Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"NO");
							Thread.sleep(GlobalConstants.MaxWait);
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
							UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
							UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
							UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
							BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugOKButton);
							Thread.sleep(GlobalConstants.MaxWait);
								
								Thread.sleep(GlobalConstants.MedWait);
								DebugOperations.DebugOption("Shortcut");
							
							if(BaseActions.WinExists(DebugElements.sDebugPasswordWindow))
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
								BaseActions.Click(DebugElements.sDebugPasswordWindow, "", DebugElements.sDebugCancelButton);
							}
							else
							{
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
								UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Debug password is not prompted even when not saved while debugging. Please refer screenshot "+sTestCaseID+".jpg");
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
							}
							
							if(sTestCaseID.equals("SDV_FUN_VAL_DS_DB_PWD_010")) {
								
								Thread.sleep(GlobalConstants.MedWait);
								UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
								UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
								UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
								UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
								BaseActions.Winwait(LoginElements.wDBConnection);
								UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
								UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
								
								AutoItX x = new AutoItX();
								boolean b  = x.controlCommandIsEnabled(LoginElements.wDBConnection, "", LoginElements.bOK);
								Thread.sleep(GlobalConstants.MinWait);
								BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
								
								if(b==false)
								{
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
								}
								else
								{
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
									UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Password is not saved on for the existing connection when saved while debugging. Please refer screenshot "+sTestCaseID+".jpg");
									UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
								}
								
								
							}
						}
						
					
					
				}
				
				for(int i=1;i<=7;i++)
				{
					sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
					sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
					String sFinalStatus = sTestCaseID+" "+sStatus;
					if(!sStatus.isEmpty())
						UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
				}

}
}