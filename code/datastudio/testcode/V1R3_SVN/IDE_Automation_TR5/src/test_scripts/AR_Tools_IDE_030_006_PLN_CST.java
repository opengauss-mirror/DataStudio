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
TITLE -EXECUTION PLAN AND COST
DESCRIPTION - THIS PROGRAM COVERS THE BELOW TEST SCRIPTS
1.SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001--> Plan and Cost for single Query Execution.
2.SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001_152_604-->Check plan and cost Verbose with text option.
3.SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001_152_604_882-->Check plan and cost  with JSON option. 
4.SDV_FUNCERR_QUERYEXECPLANCOST_INVALID_003-->Plan and Cost for Invalid Query.
5.SDV_SECU_QUERYEXECPLANCOST_NO ACCESS007-->Query execution in which user does not have access.
6.SDV_FUNCVAL_QUERYEXECPLANCOST_DIALOG_002-->Tool bar option for plan and cost with a dialog to select options.
7.SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001_152--.>Check plan and cost  dialog box option.
8.SDV_FUNCERR_QUERYEXECPLANCOST_MULTIPLEQUERY_004-->Query execution plan & cost for multiple query.
 *************************************************************************/

package test_scripts;

import java.awt.event.KeyEvent;

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import script_library.PlanCost;
import script_library.QueryEditor;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class AR_Tools_IDE_030_006_PLN_CST {


	public static void main(String sARNumber) throws Exception{
		//Scripts to Test AR.Tools.IDE.030.006 - Display query execution plan and cost
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Plan_Cost");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Plan_Cost");
		//Variable Declarations	
		String sFlag,sExecute,sStatus,sInputQuery,sExecutionPlan,sValidationMessage,sFormatType,sTestCaseID;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Loop to iterate through each Test Case in Test Data Sheet	
		QueryEditor.SingleQueryExe("select * from pg_am;", "Normal");
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
				sExecutionPlan=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,4);
				sFormatType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,5);
				sValidationMessage=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,7);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				QueryEditor.SetQuery(sInputQuery);
				PlanCost.PlanCostClick();
				PlanCost.ExecutionPlan(sFormatType,sExecutionPlan);
				/*************************************************************************
				Test Cases Covered
				1. SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001
				2. SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001_152_604
				3. SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001_152
				4. SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001_152_604_882
				5. SDV_FUNCVAL_QUERYEXECPLANCOST_DIALOG_002
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001")|| sTestCaseID.equals("SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001_152_604")||sTestCaseID.equals("SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001_152")||sTestCaseID.equals("SDV_FUNCVAL_QUERYEXECPLANCOST_QUERY_001_152_604_882")||sTestCaseID.equals("SDV_FUNCVAL_QUERYEXECPLANCOST_DIALOG_002"))
				{
					sFlag=PlanCost.PlanCostValidation(sFormatType, sValidationMessage);
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Discrepancy in the Execution Plan and Cost display in console. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test Cases Covered
				6. SDV_FUNCERR_QUERYEXECPLANCOST_INVALID_003
				7. SDV_SECU_QUERYEXECPLANCOST_NO_ACCESS007
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_FUNCERR_QUERYEXECPLANCOST_INVALID_003")|| sTestCaseID.equals("SDV_SECU_QUERYEXECPLANCOST_NO_ACCESS007"))
				{
					sFlag=PlanCost.PlanCostValidation(sFormatType, sValidationMessage);
					if(sFlag.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Expected Error message is not getting displayed for invalid query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				/*************************************************************************
				Test Cases Covered
				8. SDV_USAB_QUERYEXECPALNCOST_ERRORMESSAGE_008
				9. SDV_FUNCERR_QUERYEXECPLANCOST_MULTIPLEQUERY_004
				 *************************************************************************/
				if(sTestCaseID.equals("SDV_USAB_QUERYEXECPALNCOST_ERRORMESSAGE_008")||sTestCaseID.equals("SDV_FUNCERR_QUERYEXECPLANCOST_MULTIPLEQUERY_004"))
				{
					Thread.sleep(GlobalConstants.MedWait);
					if(BaseActions.WinExists("Execution Plan For Multiple Queries"))
					{	
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);	
						sFlag=PlanCost.PlanCostValidation(sFormatType, sValidationMessage);
						if(sFlag.contains("ERROR"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");							
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error window is not displayed to user while executing multiple queries with Execution Plan options selected. Please refer screenshot "+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Error window is not displayed to user while executing multiple queries with Execution Plan options selected. Please refer screenshot "+sTestCaseID+".jpg");
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
	}//end of main
}//end of class














