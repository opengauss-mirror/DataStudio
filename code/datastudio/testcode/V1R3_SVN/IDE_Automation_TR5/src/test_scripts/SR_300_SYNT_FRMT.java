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

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import object_repository.PreferencesElements;
import script_library.Login;
import script_library.SyntaxColoring;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_300_SYNT_FRMT {
	public static void main(String sARNumber) throws Exception
	{
		//Scripts to Test Syntax Format,Validation through preference window
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Syntax_Format");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Syntax_Format");
		//Variable Declarations
		String sTestCaseID,sExecute,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Loop to iterate through each Test Case in Test Data Sheet
		String sKeyWord1,sKeyWord2,sKeyWord3,sKeyWord4,sKeyWord5,sKeyWord6,sKeyWord7,sKeyWord8;
		String sValidFlag1,sValidFlag2,sValidFlag3,sValidFlag4,sValidFlag5,sValidFlag6,sValidFlag7,sValidFlag8;
		String sWindow="Fail",sKeyWordFlag="Fail",sDefColValidFlag="Fail",sDefColValidFlag1="Fail",sDefColValidFlag2="Fail",sColorValidFlag="Fail",sSameColorValidFlag="Fail";
		//Launch Preference window

		/**SDV_FUN_VAL_DS_Syntax_001**/

		sWindow = SyntaxColoring.LaunchSyntaxColoring();
		if(sWindow.equals("Success"))
		{
			/**SDV_FUN_VAL_DS_Syntax_002**/

			sKeyWord1 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.sSingleLineComment);
			sKeyWord2 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.sDefault);
			sKeyWord3 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.sUnreserved);
			sKeyWord4 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.sReserved);
			sKeyWord5 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.sType);
			sKeyWord6 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.sPredicate);
			sKeyWord7 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.sConstants);
			sKeyWord8 = BaseActions.ControlGetText(PreferencesElements.wPreferences,"",PreferencesElements.sStrings);

			if(sKeyWord1.equals("Single Line Comment")&&sKeyWord2.equals("Default")&&sKeyWord3.equals("Unreserved Keyword")&&sKeyWord4.equals("Reserved Keyword")&&sKeyWord5.equals("Type")&&sKeyWord6.equals("Predicate")&&sKeyWord7.equals("Constants")&&sKeyWord8.equals("Strings"))
				sKeyWordFlag = "Pass";
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Syntax_002", ResultExcel);
				sKeyWordFlag = "Fail";
			}

			/**SDV_FUN_VAL_DS_Syntax_003, SDV_FUN_VAL_DS_Syntax_009**/
			//Set Custom Colors

			SyntaxColoring.ColorSelection("RESTORE","","","","");
			SyntaxColoring.ColorSelection("CUSTOM","SINGLELINECOMMENT","","","");
			SyntaxColoring.ColorSelection("CUSTOM","DEFAULT","","","");
			SyntaxColoring.ColorSelection("CUSTOM","UNRESERVED","","","");
			SyntaxColoring.ColorSelection("CUSTOM","RESERVED","","","");
			SyntaxColoring.ColorSelection("CUSTOM","TYPE","","","");
			SyntaxColoring.ColorSelection("CUSTOM","PREDICATE","","","");
			SyntaxColoring.ColorSelection("CUSTOM","CONSTANTS","","","");
			SyntaxColoring.ColorSelection("CUSTOM","STRINGS","APPLY","OK","Yes");

			//Validate Custom Colors

			sValidFlag1 = SyntaxColoring.ColorValidation("CUSTOM","SINGLELINECOMMENT");
			sValidFlag2 = SyntaxColoring.ColorValidation("CUSTOM","DEFAULT");
			sValidFlag3 = SyntaxColoring.ColorValidation("CUSTOM","UNRESERVED");
			sValidFlag4 = SyntaxColoring.ColorValidation("CUSTOM","RESERVED");
			sValidFlag5 = SyntaxColoring.ColorValidation("CUSTOM","TYPE");
			sValidFlag6 = SyntaxColoring.ColorValidation("CUSTOM","PREDICATE");
			sValidFlag7 = SyntaxColoring.ColorValidation("CUSTOM","CONSTANTS");
			sValidFlag8 = SyntaxColoring.ColorValidation("CUSTOM","STRINGS");

			if(sValidFlag1.equals("Pass")&&sValidFlag2.equals("Pass")&&sValidFlag3.equals("Pass")&&sValidFlag4.equals("Pass")&&sValidFlag5.equals("Pass")&&sValidFlag6.equals("Pass")&&sValidFlag7.equals("Pass")&&sValidFlag8.equals("Pass"))
				sColorValidFlag = "Pass";
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Syntax_003", ResultExcel);
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Syntax_009", ResultExcel);
				sColorValidFlag = "Fail";
			}	

			/**SDV_FUN_VAL_DS_Syntax_004**/
			SyntaxColoring.LaunchSyntaxColoring();

			//Set Same Color

			SyntaxColoring.ColorSelection("RESTORE","","","","");
			SyntaxColoring.ColorSelection("SAME","SINGLELINECOMMENT","","","");
			SyntaxColoring.ColorSelection("SAME","DEFAULT","","","");
			SyntaxColoring.ColorSelection("SAME","UNRESERVED","","","");
			SyntaxColoring.ColorSelection("SAME","RESERVED","","","");
			SyntaxColoring.ColorSelection("SAME","TYPE","","","");
			SyntaxColoring.ColorSelection("SAME","PREDICATE","","","");
			SyntaxColoring.ColorSelection("SAME","CONSTANTS","","","");
			SyntaxColoring.ColorSelection("SAME","STRINGS","APPLY","OK","Yes");

			//Validate Same Color

			sValidFlag1 = SyntaxColoring.ColorValidation("SAME","SINGLELINECOMMENT");
			sValidFlag2 = SyntaxColoring.ColorValidation("SAME","DEFAULT");
			sValidFlag3 = SyntaxColoring.ColorValidation("SAME","UNRESERVED");
			sValidFlag4 = SyntaxColoring.ColorValidation("SAME","RESERVED");
			sValidFlag5 = SyntaxColoring.ColorValidation("SAME","TYPE");
			sValidFlag6 = SyntaxColoring.ColorValidation("SAME","PREDICATE");
			sValidFlag7 = SyntaxColoring.ColorValidation("SAME","CONSTANTS");
			sValidFlag8 = SyntaxColoring.ColorValidation("SAME","STRINGS");

			if(sValidFlag1.equals("Pass")&&sValidFlag2.equals("Pass")&&sValidFlag3.equals("Pass")&&sValidFlag4.equals("Pass")&&sValidFlag5.equals("Pass")&&sValidFlag6.equals("Pass")&&sValidFlag7.equals("Pass")&&sValidFlag8.equals("Pass"))
				sSameColorValidFlag = "Pass";
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Syntax_004", ResultExcel);
				sSameColorValidFlag = "Fail";
			}

			/**SDV_FUN_VAL_DS_Syntax_005, SDV_FUN_VAL_DS_Syntax_006**/
			//Set Default Colors
			SyntaxColoring.LaunchSyntaxColoring();
			SyntaxColoring.ColorSelection("RESTORE","","APPLY","OK","Yes");
			//Validate Default Colors

			sValidFlag1 = SyntaxColoring.ColorValidation("RESTORE","SINGLELINECOMMENT");
			sValidFlag2 = SyntaxColoring.ColorValidation("RESTORE","DEFAULT");
			sValidFlag3 = SyntaxColoring.ColorValidation("RESTORE","UNRESERVED");
			sValidFlag4 = SyntaxColoring.ColorValidation("RESTORE","RESERVED");
			sValidFlag5 = SyntaxColoring.ColorValidation("RESTORE","TYPE");
			sValidFlag6 = SyntaxColoring.ColorValidation("RESTORE","PREDICATE");
			sValidFlag7 = SyntaxColoring.ColorValidation("RESTORE","CONSTANTS");
			sValidFlag8 = SyntaxColoring.ColorValidation("RESTORE","STRINGS");

			if(sValidFlag1.equals("Pass")&&sValidFlag2.equals("Pass")&&sValidFlag3.equals("Pass")&&sValidFlag4.equals("Pass")&&sValidFlag5.equals("Pass")&&sValidFlag6.equals("Pass")&&sValidFlag7.equals("Pass")&&sValidFlag8.equals("Pass"))
				sDefColValidFlag = "Pass";
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Syntax_005", ResultExcel);
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Syntax_006", ResultExcel);
				sDefColValidFlag = "Fail";
			}	

			/**SDV_FUN_VAL_DS_Syntax_011,SDV_FUN_INVAL_DS_Syntax_001**/
			//Set Default Colors
			SyntaxColoring.LaunchSyntaxColoring();
			SyntaxColoring.ColorSelection("RESTORE","","","","");
			SyntaxColoring.ColorSelection("CUSTOM","SINGLELINECOMMENT","","","");
			SyntaxColoring.ColorSelection("CUSTOM","DEFAULT","","","");
			SyntaxColoring.ColorSelection("CUSTOM","UNRESERVED","","","");
			SyntaxColoring.ColorSelection("CUSTOM","RESERVED","","","");
			SyntaxColoring.ColorSelection("CUSTOM","TYPE","","","");
			SyntaxColoring.ColorSelection("CUSTOM","PREDICATE","","","");
			SyntaxColoring.ColorSelection("CUSTOM","CONSTANTS","","","");
			SyntaxColoring.ColorSelection("CUSTOM","STRINGS","","CANCEL","");
			//Validate Default Colors

			sValidFlag1 = SyntaxColoring.ColorValidation("RESTORE","SINGLELINECOMMENT");
			sValidFlag2 = SyntaxColoring.ColorValidation("RESTORE","DEFAULT");
			sValidFlag3 = SyntaxColoring.ColorValidation("RESTORE","UNRESERVED");
			sValidFlag4 = SyntaxColoring.ColorValidation("RESTORE","RESERVED");
			sValidFlag5 = SyntaxColoring.ColorValidation("RESTORE","TYPE");
			sValidFlag6 = SyntaxColoring.ColorValidation("RESTORE","PREDICATE");
			sValidFlag7 = SyntaxColoring.ColorValidation("RESTORE","CONSTANTS");
			sValidFlag8 = SyntaxColoring.ColorValidation("RESTORE","STRINGS");

			if(sValidFlag1.equals("Pass")&&sValidFlag2.equals("Pass")&&sValidFlag3.equals("Pass")&&sValidFlag4.equals("Pass")&&sValidFlag5.equals("Pass")&&sValidFlag6.equals("Pass")&&sValidFlag7.equals("Pass")&&sValidFlag8.equals("Pass"))
				sDefColValidFlag1 = "Pass";
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Syntax_011", ResultExcel);
				UtilityFunctions.TakeScreenshot("SDV_FUN_INVAL_DS_Syntax_001", ResultExcel);
				sDefColValidFlag1 = "Fail";
			}
			/**SDV_FUN_INVAL_DS_Syntax_002**/
			//Set Default Colors
			SyntaxColoring.LaunchSyntaxColoring();
			SyntaxColoring.ColorSelection("RESTORE","","","","");
			SyntaxColoring.ColorSelection("CUSTOM","SINGLELINECOMMENT","","","");
			SyntaxColoring.ColorSelection("CUSTOM","DEFAULT","","","");
			SyntaxColoring.ColorSelection("CUSTOM","UNRESERVED","","","");
			SyntaxColoring.ColorSelection("CUSTOM","RESERVED","","","");
			SyntaxColoring.ColorSelection("CUSTOM","TYPE","","","");
			SyntaxColoring.ColorSelection("CUSTOM","PREDICATE","","","");
			SyntaxColoring.ColorSelection("CUSTOM","CONSTANTS","","","");
			SyntaxColoring.ColorSelection("CUSTOM","STRINGS","","","");
			Login.LaunchIDE(GlobalConstants.sIDEPath);
			String sConnection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
			String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
			String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
			String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
			String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
			String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
			Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");//********
			Thread.sleep(GlobalConstants.MaxWait);
			sValidFlag1 = SyntaxColoring.ColorValidation("RESTORE","SINGLELINECOMMENT");
			sValidFlag2 = SyntaxColoring.ColorValidation("RESTORE","DEFAULT");
			sValidFlag3 = SyntaxColoring.ColorValidation("RESTORE","UNRESERVED");
			sValidFlag4 = SyntaxColoring.ColorValidation("RESTORE","RESERVED");
			sValidFlag5 = SyntaxColoring.ColorValidation("RESTORE","TYPE");
			sValidFlag6 = SyntaxColoring.ColorValidation("RESTORE","PREDICATE");
			sValidFlag7 = SyntaxColoring.ColorValidation("RESTORE","CONSTANTS");
			sValidFlag8 = SyntaxColoring.ColorValidation("RESTORE","STRINGS");

			if(sValidFlag1.equals("Pass")&&sValidFlag2.equals("Pass")&&sValidFlag3.equals("Pass")&&sValidFlag4.equals("Pass")&&sValidFlag5.equals("Pass")&&sValidFlag6.equals("Pass")&&sValidFlag7.equals("Pass")&&sValidFlag8.equals("Pass"))
				sDefColValidFlag2 = "Pass";
			else
			{
				UtilityFunctions.TakeScreenshot("SDV_FUN_INVAL_DS_Syntax_002", ResultExcel);
				sDefColValidFlag2 = "Fail";
			}
		}
		else
			UtilityFunctions.TakeScreenshot("SDV_FUN_VAL_DS_Syntax_001", ResultExcel);

		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2 );
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Syntax_001"))
				{
					if(sWindow.equals("Success"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Preference Window is not opened. Please refer screenshot "+sTestCaseID+".jpg");	
					}
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Syntax_002"))
				{
					if(sKeyWordFlag.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Preference Window is not opened. Please refer screenshot "+sTestCaseID+".jpg");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Syntax_003")||sTestCaseID.equals("SDV_FUN_VAL_DS_Syntax_009"))
				{
					if(sColorValidFlag.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Preference Window is not opened. Please refer screenshot "+sTestCaseID+".jpg");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Syntax_004"))
				{
					if(sSameColorValidFlag.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Preference Window is not opened. Please refer screenshot "+sTestCaseID+".jpg");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Syntax_005")||sTestCaseID.equals("SDV_FUN_VAL_DS_Syntax_006"))
				{
					if(sDefColValidFlag.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Preference Window is not opened. Please refer screenshot "+sTestCaseID+".jpg");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}
				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Syntax_011")||sTestCaseID.equals("SDV_FUN_INVAL_DS_Syntax_001"))
				{
					if(sDefColValidFlag1.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Preference Window is not opened. Please refer screenshot "+sTestCaseID+".jpg");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}
				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Syntax_002"))
				{
					if(sDefColValidFlag2.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Preference Window is not opened. Please refer screenshot "+sTestCaseID+".jpg");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
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

