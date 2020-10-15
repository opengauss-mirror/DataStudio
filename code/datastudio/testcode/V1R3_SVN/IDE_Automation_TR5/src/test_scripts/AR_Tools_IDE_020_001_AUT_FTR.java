/************************************************************************************************
TITLE - AUTOFILL & AUTOSUGGESTION
DESCRIPTION - THIS PROGRAM COVERS THE BELOW TEST SCRIPTS
1.SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_002 --> Auto fill operation is available in schema.
2.SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_005 --> Auto fill operation is available in function/procedure.
3.SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_003 --> Auto fill operation is available in table.
4.SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_016     --> Test auto filing of table names when schema is accessed and 
											   no table is created under that schema.
5.SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_007 --> Auto suggest operation is available in database.
6.SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_011 --> Auto suggest operation is available in procedure/function.
7.SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_009 --> Auto suggest operation is available in table
8.SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_008 --> Auto suggest operation is available in schema.
9.SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_023     --> Auto suggest operation is available in invalid database.
10.SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_024    --> Auto suggest operation is available in invalid schema.
11.SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_025    --> Auto suggest operation is available in invalid table name.
 **************************************************************************************************/

package test_scripts;

import java.awt.event.KeyEvent;

import object_repository.ConsoleResultElements;
import object_repository.ExecQueryElements;
import object_repository.GlobalConstants;
import script_library.QueryEditor;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class AR_Tools_IDE_020_001_AUT_FTR {

	public static void main(String sARNumber) throws Exception{

		//Scripts to Test AR.Tools.IDE.020.001 - Auto fill, auto suggest feature in SQL editor
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Auto_Fill_Suggest");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Auto_Fill_Suggest");
		//Variable Declarations	
		String sInputQuery,sStatus,sTestCaseID,sExecute,sActualQuery,sExpectedQuery;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Precondition
		//QueryEditor.AutoFillPre();
		//Logout from IDE Tool after Execution
		/*Login.IDELogout();
		//Launching the IDE Tool Application
		Login.LaunchIDE(GlobalConstants.sIDEPath);
		//Getting Login Credentials from IDE_Smoke_Test_Data file and
		String sConnection = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 0);
		String sHost = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 1);
		String sHostPort = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 2);
		String sDBName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 3);
		String sUserName = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 4);
		String sPassword = UtilityFunctions.GetExcelCellValue(GlobalConstants.sSmokeTestDataFile, "IDELogin", 1, 5);
		//Login into IDE Tool
		Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword);*/
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			if(sExecute.equals("Yes"))
			{			
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
				sExpectedQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,4);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				
				/*************************************************************************
				Test Cases Covered
				1. SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_002
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_002"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.trim().equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Actual Query After Autofill is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test Cases Covered
				2. SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_005
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_005"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Actual Query After Autofill is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test Cases Covered
				3. SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_003
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_003"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Actual Query After Autofill is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				/*************************************************************************
				Test Cases Covered
				4. SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_016
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_016"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Actual Query After Autofill is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				/*************************************************************************
				Test Cases Covered
				5. SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_007
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_007"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 9);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 9);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.equals(sExpectedQuery))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Actual Query After Auto Suggestion is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
				}
				/*************************************************************************
				Test Cases Covered
				6. SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_011
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_011"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.equals(sExpectedQuery))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Actual Query After Auto Suggestion is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
				}

				/*************************************************************************
				Test Cases Covered
				7. SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_009
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_009"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 7);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 7);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.equals(sExpectedQuery))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Actual Query After Auto Suggestion is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
				}

				/*************************************************************************
				Test Cases Covered
				8. SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_008
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNVAL_PLIDE_ENHANCE_AUTOFLL_008"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.equals(sExpectedQuery))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Actual Query After Auto Suggestion is not matching with Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
				}

				/*************************************************************************
				Test Cases Covered
				9. SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_023
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_023"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Mismatch in the Actual and Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test Cases Covered
				10. SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_024
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_024"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Mismatch in the Actual and Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test Cases Covered
				11. SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_025
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNERR_PLIDE_ENHANCE_AUTOFLL_025"))
				{
					BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sInputQuery);
					sActualQuery=QueryEditor.AutoSuggestFillValidation();
					if(sActualQuery.equals(sExpectedQuery))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Mismatch in the Actual and Expected Query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
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
		//Save the Excel result to HTML
	//	UtilityFunctions.SaveResult(ResultExcel,"Auto_Fill_Suggest");
	}//end of main
}//end of class
