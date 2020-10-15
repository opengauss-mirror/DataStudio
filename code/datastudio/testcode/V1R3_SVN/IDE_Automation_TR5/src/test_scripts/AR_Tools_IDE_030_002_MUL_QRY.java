package test_scripts;

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class AR_Tools_IDE_030_002_MUL_QRY {

	public static void main(String sARNumber) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.030.002 - Support multiple query execution from one sql editor
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Multiple_Query");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Multiple_Query");
		//Variable Declarations	
		String sFlag,sInputQuery,sStatus,sQueryType,sExeType,iQueryCount,sTestCaseID,sQuerySelection,sExecute;
		
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
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1 );
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
				sQueryType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,4);
				sExeType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,5);
				iQueryCount=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,6);
				sQuerySelection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,7);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				sFlag = QueryEditor.MultipleQueryExe(sInputQuery,sQueryType,sExeType,iQueryCount,sQuerySelection);
				
				/*************************************************************************
				Test Cases Covered
				1. SDV_FUNCVAL_PLIDE_QUERYEXEC_MULTIPLEQUERY_001
				2. SDV_FUNCVAL_PLIDE_MULTIPLEPLSQLFUNC_Y_002
				3. SDV_FUNCVAL_PLIDE_QUERYEXEC_TOOLBAR_003
				4. SDV_FUNCVAL_PLIDE_QUERYEXEC_ADDANDCHECK_004
				5. SDV_FIA_PLIDE_QUERYEXEC_PLSQL_FUNCSINGLEEDITOR_027
				*************************************************************************/
				if(sTestCaseID.equals("SDV_FUNCVAL_PLIDE_QUERYEXEC_MULTIPLEQUERY_001")|| sTestCaseID.equals("SDV_FUNCVAL_PLIDE_MULTIPLEPLSQLFUNC_Y_002")||sTestCaseID.equals("SDV_FUNCVAL_PLIDE_QUERYEXEC_TOOLBAR_003")||sTestCaseID.equals("SDV_FUNCVAL_PLIDE_QUERYEXEC_ADDANDCHECK_004")||sTestCaseID.equals("SDV_FIA_PLIDE_QUERYEXEC_PLSQL_FUNCSINGLEEDITOR_027"))
				{
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error occured while executing multiple queries. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				/*************************************************************************
				Test Cases Covered
				6.  SDV_FUNCERR_PLIDE_QUERYEXEC_WITHOUTSELECT_005
				7.  SDV_FUNCVAL_PLIDE_MULTIPLEPLSQLFUNC_Y_028
				8.  SDV_FUNCVAL_PLIDE_QUERYEXEC_MULTIPLEQUERY_003
				9.  SDV_FIA_PLIDE_QUERYEXEC_DBCONNECTION_025
				*************************************************************************/
				if(sTestCaseID.equals("SDV_FUNCERR_PLIDE_QUERYEXEC_WITHOUTSELECT_005")|| sTestCaseID.equals("SDV_FUNCVAL_PLIDE_MULTIPLEPLSQLFUNC_Y_028")||sTestCaseID.equals("SDV_FUNCVAL_PLIDE_QUERYEXEC_MULTIPLEQUERY_003")||sTestCaseID.equals("SDV_FIA_PLIDE_QUERYEXEC_DBCONNECTION_025"))
				{
					if(sFlag.equals("Success"))
					{
						Thread.sleep(GlobalConstants.MinWait);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error occured while executing multiple queries. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				/*************************************************************************
				Test Cases Covered
				10.  SDV_PERF_PLIDE_QUERYEXEC_BULKQUERY_0011
				*************************************************************************/
				if(sTestCaseID.equals("SDV_PERF_PLIDE_QUERYEXEC_BULKQUERY_0011"))
				{
					String sConsoleOutput = QueryResult.ReadConsoleOutput("TERMINAL");
					if(sConsoleOutput.contains("Execution failed."))
						sFlag = sConsoleOutput;
					else
						sFlag = "Success";
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error occured while executing multiple queries. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				
				/*************************************************************************
				Test Cases Covered
				11. SDV_FUNCERR_PLIDE_QUERYEXEC_SYNTAXERR_006
				12. SDV_FUNCERR_PLIDE_QUERYEXEC_PARTIALSELECT_007
				*************************************************************************/
				if(sTestCaseID.equals("SDV_FUNCERR_PLIDE_QUERYEXEC_SYNTAXERR_006")||sTestCaseID.equals("SDV_FUNCERR_PLIDE_QUERYEXEC_PARTIALSELECT_007"))
				{
					if(sFlag.contains("ERROR: syntax error"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");	
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error message is not getting displayed for partial/invalid query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				/*************************************************************************
				Test Cases Covered
				13. SDV_SECU_PLIDE_QUERYEXEC_NOTACCESS_0012
				*************************************************************************/
				if(sTestCaseID.equals("SDV_SECU_PLIDE_QUERYEXEC_NOTACCESS_0012"))
				{
					if(sFlag.contains("permission denied"))
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
				/*************************************************************************
				Test Cases Covered
				14. SDV_FIA_PLIDE_QUERYEXEC_ERRORMSG_017
				*************************************************************************/
				if(sTestCaseID.equals("SDV_FIA_PLIDE_QUERYEXEC_ERRORMSG_017"))
				{
					if(sFlag.contains("[ERROR] Error while executing Execution Plan and Cost for multiple queries.Only single Query should be selected to see Execution Plan and Costs.."))
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Execution Plan For Multiple Queries error window is not displayed. Please refer screenshot "+sTestCaseID+".jpg");
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
		//UtilityFunctions.SaveResult(ResultExcel,"Multiple_Query");
	}//end of main
}//end of class

