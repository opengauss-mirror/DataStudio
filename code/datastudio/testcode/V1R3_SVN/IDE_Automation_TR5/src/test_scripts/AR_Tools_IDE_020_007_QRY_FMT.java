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
TITLE - QUERY FORMAT
DESCRIPTION - THIS PROGRAM COVERS THE BELOW TEST SCRIPTS
1.SDV_FUNCVAL_PLIDE_SQLDEVOPE_FRMT_001-->Formatting the  query using Menu Option.
2.SDV_FUNCVAL_PLIDE_SQLDEVOPE_FRMT_002-->Formatting the  query using Menu Option.
3.SDV_FUNCVAL_PLIDE_SQLDEVOPE_FRMT_003-->Formatting the  query using Shortcut.
4.SDV_FUNCVAL_PLIDE_SQLDEVOPE_FRMT_004-->Formatting the  query using Shortcut.
5.SDV_FUNCVAL_PLIDE_SQLDEVOPE_FRMT_005-->Formatting the  query using ToolBar.
6.SDV_FUNCVAL_PLIDE_SQLDEVOPE_FRMT_006-->Formatting the  query using ToolBar.
7.SDV_FUNERRL_PLIDE_SQLDEVOPE_FRMT_015-->Formatting the  query using Enter.
8.SDV_FUNCVAL_PLIDE_SQLDEVOPE_FRMT_021-->Formatting the  query using Menu Option.
9.SDV_FUNERRL_PLIDE_SQLDEVOPE_FRMT_023-->Formatting the  query using Shortcut.
TEST CASES COVERED - REFER IDE_Functional_Test_Data.xlsx IN TEST DATA FOLDER
*************************************************************************/

package test_scripts;

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import script_library.QueryEditor;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class AR_Tools_IDE_020_007_QRY_FMT {
	public static void main(String sARNumber) throws Exception
	{
		//Scripts to Test AR.Tools.IDE.020.007 - Query Format,Validation check through Menu, Tool bar and Shortcut options
		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Query_Format");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Query_Format");
		//Variable Declarations
		String sFlag,sStatus,sTestCaseID,sInputQuery,sExecute,sFormatType,sExpectedQuery;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		//Loop to iterate through each Test Case in Test Data Sheet
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2 );
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				sFormatType=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,3);
				sInputQuery = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,4);
				sExpectedQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,5);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				QueryEditor.QueryFormat(sInputQuery,sFormatType);
				sFlag = QueryEditor.QueryFormatValidation(sExpectedQuery, sInputQuery);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
				//Logic to update the result excel and capture screenshot, for errors	
				if (sFlag == "Mismatch")
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Mismatch in Expected and Actual Formated Query. Please refer screenshot "+sTestCaseID+".jpg");
					UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
				}
				else
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
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
