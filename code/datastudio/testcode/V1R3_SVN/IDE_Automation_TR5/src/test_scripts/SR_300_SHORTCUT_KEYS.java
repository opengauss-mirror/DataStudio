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

import object_repository.ConsoleResultElements;
import object_repository.ExpQueryElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.PreferencesElements;
import script_library.Login;
import script_library.ShortCut_Mapper;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_300_SHORTCUT_KEYS {
	public static void main(String sARNumber) throws Exception
	{
		//Scripts to Test Short Cut,Validation through preference window
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Shortcut_Mapper");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Shortcut_Mapper");
		//Variable Declarations
		String sTestCaseID,sExecute,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Loop to iterate through each Test Case in Test Data Sheet
		String sCommandName1,sBinding1,sCommandName2,sBinding2;
		String sWindow="Fail",sHeaderFlag="Fail",sBindingFlag="Fail",sRestartFlag="Fail",sShortCutFlag="Fail",sShortCutFlag1="Fail",sShortCutFlag2="Fail",sShortCutFlag3="Fail";
		String sConflictFlag="Fail",sConflictFlag1="Fail",sConflictFlag2="Fail";
		//Launch Preference window
		ShortCut_Mapper.LaunchShortCutMapper();
		ShortCut_Mapper.UpdateShortCut(0,"RESTORE","","APPLY","OK","Yes");
		
		/**SDV_FUN_VAL_DS_Shortkey_001, SDV_FUN_VAL_DS_Shortkey_002,SDV_FUN_VAL_DS_Shortkey_004**/
		
		sWindow = ShortCut_Mapper.LaunchShortCutMapper();
		if(sWindow.equals("Success"))
		{
			/**SDV_FUN_VAL_DS_Shortkey_003**/
			if(BaseActions.ObjExists(PreferencesElements.wPreferences,"",PreferencesElements.hShortCutsHeader))
				sHeaderFlag="Pass";
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Shortkey_003", ResultExcel);
				sHeaderFlag="Fail";
			}
			
			/**SDV_FUN_VAL_DS_Shortkey_005**/
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB,1);
			sCommandName1 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.eCommandName);
			sBinding1 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.eBinding);
			ShortCut_Mapper.UpdateShortCut(0,"MODIFY","","","","");
			sCommandName2 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.eCommandName);
			sBinding2 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.eBinding);
			
			if(sCommandName1.equals(sCommandName2)||sBinding1.equals(sBinding2))
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Shortkey_005", ResultExcel);
				sBindingFlag="Fail";
			}
			else
				sBindingFlag="Pass";
			

			/**SDV_FUN_VAL_DS_Shortkey_007**/
			ShortCut_Mapper.LaunchShortCutMapper();
			ShortCut_Mapper.UpdateShortCut(0,"MODIFY","OPTION1","APPLY","OK","");
			if(BaseActions.WinExists(PreferencesElements.wDSRestart))
			{
				BaseActions.Click(PreferencesElements.wDSRestart,"",PreferencesElements.bYes);
				sRestartFlag="Pass";
			}
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Shortkey_007", ResultExcel);
				sRestartFlag="Fail";
			}	
			
			/**SDV_FUN_VAL_DS_Shortkey_008**/
			ShortCut_Mapper.LaunchShortCutMapper();
			ShortCut_Mapper.UpdateShortCut(0,"MODIFY","OPTION1","APPLY","OK","Yes");
			//try newly added short cut key
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_K,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_K,1);
			if(BaseActions.WinExists(LoginElements.wDBConnection))
			{
				sShortCutFlag="Pass";
				BaseActions.Click(LoginElements.wDBConnection,"",LoginElements.bCancel);
			}
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Shortkey_008", ResultExcel);
				sShortCutFlag="Fail";
			}
			
			/**SDV_FUN_VAL_DS_Shortkey_009,SDV_FUN_VAL_DS_Shortkey_010,SDV_FUN_VAL_DS_Shortkey_011,SDV_FUN_VAL_DS_Shortkey_012**/
			ShortCut_Mapper.LaunchShortCutMapper();
			ShortCut_Mapper.UpdateShortCut(0,"MODIFY","OPTION2","APPLY","","");
			ShortCut_Mapper.UpdateShortCut(12,"MODIFY","OPTION1","APPLY","OK","Yes");
			//try newly added short cut key
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_K,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_K,1);
			if(BaseActions.WinExists(ExpQueryElements.wSQLOpenWindow))
			{
				sShortCutFlag1="Pass";
				BaseActions.Click(ExpQueryElements.wSQLOpenWindow,"",ExpQueryElements.sCancelButton);
			}
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Shortkey_009", ResultExcel);
				sShortCutFlag1="Fail";
			}
			
			/**SDV_FUN_VAL_DS_Shortkey_013**/
			ShortCut_Mapper.LaunchShortCutMapper();
			ShortCut_Mapper.UpdateShortCut(0,"RESTORE","","APPLY","OK","Yes");
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_N,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_N,1);
			if(BaseActions.WinExists(LoginElements.wDBConnection))
			{
				sShortCutFlag2="Pass";
				BaseActions.Click(LoginElements.wDBConnection,"",LoginElements.bCancel);
			}
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Shortkey_013", ResultExcel);
				sShortCutFlag2="Fail";
			}
			
			/**SDV_FUN_VAL_DS_Shortkey_014**/
			ShortCut_Mapper.LaunchShortCutMapper();
			ShortCut_Mapper.UpdateShortCut(0,"MODIFY","OPTION1","","CANCEL","");
			Login.LaunchIDE(GlobalConstants.sIDEPath);
			String sConnection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
			String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
			String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
			String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
			String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
			String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
			Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
			Thread.sleep(GlobalConstants.MaxWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_K,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_K,1);
			if(BaseActions.WinExists(LoginElements.wDBConnection))
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Shortkey_014", ResultExcel);
				BaseActions.Click(LoginElements.wDBConnection,"",LoginElements.bCancel);
				sShortCutFlag3="Fail";
			}
			else
			{
				sShortCutFlag3="Pass";
			}
			
			
			/**SDV_FUN_VAL_DS_Shortkey_015,SDV_FUN_VAL_DS_Shortkey_016**/
			ShortCut_Mapper.LaunchShortCutMapper();
			ShortCut_Mapper.UpdateShortCut(0,"MODIFY","OPTION1","APPLY","","");
			ShortCut_Mapper.UpdateShortCut(12,"MODIFY","OPTION1","APPLY","OK","Yes");
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_K,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_K,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
			if(BaseActions.WinExists(LoginElements.wDBConnection))
			{
				sConflictFlag1="Fail";
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Shortkey_015", ResultExcel);
				BaseActions.Click(LoginElements.wDBConnection,"",LoginElements.bCancel);
			}
			else
			{
				sConflictFlag1="Pass";
			}
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_K,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_K,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
			if(BaseActions.WinExists(ExpQueryElements.wSQLOpenWindow))
			{
				sConflictFlag2="Pass";
				BaseActions.Click(ExpQueryElements.wSQLOpenWindow,"",ExpQueryElements.sCancelButton);
			}
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Shortkey_015", ResultExcel);
				sConflictFlag2="Fail";
			}
			
			if(sConflictFlag1.equals("Pass")&&sConflictFlag2.equals("Pass"))
				sConflictFlag="Pass";
			else
				sConflictFlag="Fail";
			
			ShortCut_Mapper.LaunchShortCutMapper();
			ShortCut_Mapper.UpdateShortCut(0,"RESTORE","","APPLY","OK","Yes");
			
		}	
		else
			UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Shortkey_001", ResultExcel);	
			
			
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2 );
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_001")||sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_002")||sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_004"))
				{
					if(sWindow.equals("Success"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Preference Window is not opened. Please refer screenshot "+sTestCaseID+".jpg");	
					}
				}
				
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_003"))
				{
					if(sHeaderFlag.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Shortcut table headers are not displayed. Please refer screenshot "+sTestCaseID+".jpg");	
					}
				}
				
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_005"))
				{
					if(sBindingFlag.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Shortcut key deatils are not populated upon clicking modify button. Please refer screenshot "+sTestCaseID+".jpg");	
					}
				}
				
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_007"))
				{
					if(sRestartFlag.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Restart window is not displayed after Shortcut key modifcation. Please refer screenshot "+sTestCaseID+".jpg");	
					}
				}
				
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_008"))
				{
					if(sShortCutFlag.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Newly added short cut key is not working as expected. Please refer screenshot "+sTestCaseID+".jpg");	
					}
				}
				
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_009")||sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_010")||sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_011")||sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_012"))
				{
					if(sShortCutFlag1.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Shortcut key does not work as expected on multiple key changes. Please refer screenshot "+sTestCaseID+".jpg");	
					}
				}
				
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_013"))
				{
					if(sShortCutFlag2.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Restore default feature is not working as expected. Please refer screenshot "+sTestCaseID+".jpg");	
					}
				}
				
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_014"))
				{
					if(sShortCutFlag3.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"CANCEL button is not working as expected. Please refer screenshot "+sTestCaseID+".jpg");	
					}
				}
				
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_015")||sTestCaseID.equals("SDV_FUN_VAL_DS_Shortkey_016"))
				{
					if(sConflictFlag.equals("Pass"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Conflict of Shortcut keys are not working as expected. Please refer screenshot "+sTestCaseID+".jpg");	
					}
				}
			}//end of if loop for Execute flag
		}//end of for loop
		//Generate text file report for TMSS integration
		for(int i=1;i<=iRowCount;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}//end of main
}//end of class
				
				
