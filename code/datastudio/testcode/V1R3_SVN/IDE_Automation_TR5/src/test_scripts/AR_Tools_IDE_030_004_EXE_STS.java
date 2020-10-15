/*************************************************************************
TITLE - STATUS_BAR AND QUERY EXECUTION
DESCRIPTION - THIS PROGRAM COVERS THE BELOW TEST SCRIPTS
1.SDV_FUNCVAL_QUERYEXECSTATUS_STATUS_001--> Query execution using single query
2.SDV_FUNCVAL_QQUERYEXECSTATUS_MULTIPLEQUERY_002-->Query execution using multiple query
3.SDV_USAB_QUERYEXECSTATUS_PROPERMESSAGE_007-->Proper status message in console.
4.SDV_FIA_QUERYEXECSTATUS_TIME AND STATUS_008-->Execution status in status bar.
5.SDV_FUNCERR_QUERYEXECSTATUS_INVALIDQUERY_003-->Execution status in status bar for invalid query.
6.SDV_SECU_QUERYEXECSTATUS_NOACCESS_007-->Execution status for not accessible query 
 *************************************************************************/


package test_scripts;

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import script_library.QueryEditor;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class AR_Tools_IDE_030_004_EXE_STS {
	public static void main(String sARNumber) throws Exception{
		//Scripts to Test AR.Tools.IDE.030.004 - Query execution status on status bar
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Execution_Status");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Execution_Status");
		//Variable Declarations	
		String sFlag,sStatus,sInputQuery,sQueryType,sTestCaseID,sExecute;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
				sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,4);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				
				/*************************************************************************
				Test Cases Covered
				1. SDV_FUNCVAL_QUERYEXECSTATUS_STATUS_001
				*************************************************************************/
				if(sTestCaseID.equals("SDV_FUNCVAL_QUERYEXECSTATUS_STATUS_001"))
				{
					sFlag = QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error occured while executing query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				/*************************************************************************
				Test Cases Covered
				2. SDV_FUNCVAL_QQUERYEXECSTATUS_MULTIPLEQUERY_002
				3. SDV_FIA_QUERYEXECSTATUS_TIME AND STATUS_008
				*************************************************************************/
				if(sTestCaseID.equals("SDV_FUNCVAL_QQUERYEXECSTATUS_MULTIPLEQUERY_002")||sTestCaseID.equals("SDV_FIA_QUERYEXECSTATUS_TIME_AND_STATUS_008"))
				{
					sFlag = QueryEditor.MultipleQueryExe(sInputQuery,sQueryType,"Regular","3","Full");
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.ScrollUp("Data Studio", "", "SWT_Window018", 4);
						UtilityFunctions.ScrollDown("Data Studio", "", "SWT_Window018", 4);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error occured while executing query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				/*************************************************************************
				Test Cases Covered
				4. SDV_FUNCERR_QUERYEXECSTATUS_INVALIDQUERY_003
				*************************************************************************/
				if(sTestCaseID.equals("SDV_FUNCERR_QUERYEXECSTATUS_INVALIDQUERY_003"))
				{
					sFlag = QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					if(sFlag.contains("ERROR: syntax error"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");	
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error message is not getting displayed for invalid query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				/*************************************************************************
				Test Cases Covered
				5. SDV_USAB_QUERYEXECSTATUS_PROPERMESSAGE_007
				*************************************************************************/
				if(sTestCaseID.equals("SDV_USAB_QUERYEXECSTATUS_PROPERMESSAGE_007"))
				{
					sFlag = QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					if(sFlag.contains("does not exist"))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Proper error message is not displayed for a query with invalid schema/table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				/*************************************************************************
				Test Cases Covered
				6. SDV_SECU_QUERYEXECSTATUS_NOACCESS_007
				*************************************************************************/
				if(sTestCaseID.equals("SDV_SECU_QUERYEXECSTATUS_NOACCESS_007"))
				{
					sFlag = QueryEditor.SingleQueryExe(sInputQuery,sQueryType);
					if(sFlag.contains("ERROR: permission denied"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");	
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error message is not getting displayed for schema with no access. Please refer screenshot "+sTestCaseID+".jpg");
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
		//UtilityFunctions.SaveResult(ResultExcel,"Execution_Status");
	}//end of main
}//end of class