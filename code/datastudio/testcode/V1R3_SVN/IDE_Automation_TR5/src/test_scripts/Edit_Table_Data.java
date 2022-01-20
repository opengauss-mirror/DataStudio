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

import object_repository.GlobalConstants;
import script_library.EditTableDataFunctions;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class Edit_Table_Data {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Edit_Table_Data");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Edit_Table_Data");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag1,sFlag2,sFlag3,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		//Loop to iterate through each Test Case in Test Data Sheet	
		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			if(sExecute.equals("Yes"))
			{			
				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,1);
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_Table_016"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					sFlag1 = "SELECT * FROM autotable.rowtable  ;";
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					sFlag2= EditTableDataFunctions.copyEditTableData().replace("\"", "");
					if(sFlag1.contains(sFlag2))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"The Query is not matching. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_Table_003")) //Testcase Covered SDV_NEW_DS_Edit_Table_017,SDV_FUNVAL_DS_Edit_Table_011
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("select count (*) from autotable.rowtable where empid=100;", "Normal");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = QueryResult.EditCopyContent();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "100");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					EditTableDataFunctions.editDataOperations("DELETE");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select count (*) from autotable.rowtable where empid=100;", "Normal");
					sFlag2 = QueryResult.EditCopyContent();
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.equals(sFlag2))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to edit the first column Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}
				if(sTestCaseID.equals("SDV_NEW_DS_Edit_Table_016")) //testcase covered SDV_FUNVAL_DS_Edit_Table_001_641_New_1
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("select count (*) from autotable.rowtable where empid=100;", "Normal");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = QueryResult.EditCopyContent();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "100");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "Z");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select count (*) from autotable.rowtable where empid=100;", "Normal");
					sFlag2 = QueryResult.EditCopyContent();
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.equals(sFlag2))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Data is updated without commiting the data Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_Table_005")) //Testcase covered SDV_FUNVAL_DS_Edit_Table_014,
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("select count (*) from autotable.rowtable;", "Normal");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "5");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "Z");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("COMMIT");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= EditTableDataFunctions.copyEditTableResultData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select * from autotable.rowtable where salutation ='Z';", "Normal");
					sFlag2 = QueryResult.CopyContent();
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("Status : Commit Successful")&&sFlag2.contains("Z"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"DML OPerations Failed.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("delete from autotable.rowtable where salutation ='Z';","Normal");
					Thread.sleep(GlobalConstants.MinWait);
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_Table_006"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 3);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "5");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "Z");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("COMMIT");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= EditTableDataFunctions.copyEditTableResultData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("Status : Commit Successful"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"DML OPerations Failed.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("delete from autotable.ctable where ename ='Z';","Normal");
					Thread.sleep(GlobalConstants.MinWait);
				}


				if(sTestCaseID.equals("SDV_NEW_DS_Edit_Table_019"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					EditTableDataFunctions.editTableWizard("ORDERBY", "salutation");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "5");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "Z");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("ROLLBACK");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= EditTableDataFunctions.copyEditTableResultData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("Status : Rollback Successful"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"unable to get the result with orderby clause.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_Table_009"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.systemTableNavigation();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("EXECUTE");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "Test");
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					EditTableDataFunctions.editDataOperations("POST");
					BaseActions.Winwait("Post Failed");
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE,1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
					if(sFlag1.contains("Your transactions for this connection are rolled back as query execution is failed."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to edit the system table.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_Table_012")) //Test case covered SDV_FUN_INVAL_DS_Edit_Table_001
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 5);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 5);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("EXECUTE");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "1");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "1");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("COMMIT");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= EditTableDataFunctions.copyEditTableResultData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select count (*) from autotable.zempty", "Normal");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 =QueryResult.CopyContent().trim();
					if(sFlag1.contains("Status : Commit Successful") && sFlag2.contains("1"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"unable to insert rows in empty table.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.TruncateTable(136,298);
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_Table_013"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("EXECUTE");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_UP,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP,1);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,250,58 );
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "1");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "Z");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,2);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "1");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "Z");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("COMMIT");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= EditTableDataFunctions.copyEditTableResultData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select count (*) from autotable.rowtable where salutation ='Z';", "Normal");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = QueryResult.CopyContent();
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("Status : Commit Successful")&&sFlag2.contains("2"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"unable to insert rows in the middle of the table.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_Table_015"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					sFlag1 = EditTableDataFunctions.copyEditTableData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("EDIT");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = EditTableDataFunctions.copyEditTableData();
					if(sFlag1.equals(sFlag2))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Edit.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Edit_Table_002"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.autoTableNavigation();
					Thread.sleep(GlobalConstants.MinWait);	
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "empid as ename,ename as salary");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 =BaseActions.ControlGetText("Edit table data wizard", "", "Static6");
					if(sFlag1.matches("Please enter valid columns"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Able to do DML operations on alias.Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
				}
				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Edit_Table_003"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "timestamp '2001-09-28 23:00' - interval '23 hours' AS RESULT, 21 * interval '1 day' AS RESULT;");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 =BaseActions.ControlGetText("Edit table data wizard", "", "Static6");
					if(sFlag2.matches("Please enter valid columns"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"String Manuplations are allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Edit_Table_004"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "empid from rowtable union select empid");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 =BaseActions.ControlGetText("Edit table data wizard", "", "Static6");
					if(sFlag2.matches("Please enter valid columns"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"SET Operations are allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Edit_Table_005"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");	
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					EditTableDataFunctions.editTableWizard("where", "empid in (select eid from autotable.ctable)");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "empid,eid");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					sFlag2 =BaseActions.ControlGetText("Edit table data wizard", "", "Static6");
					EditTableDataFunctions.Button("Cancel");
					if(sFlag2.matches("Please enter valid columns"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Sub queries are allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Edit_Table_006"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 3);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "5");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "Z");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("COMMIT");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= EditTableDataFunctions.copyEditTableResultData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select * from autotable.ctable where ename ='Z';", "Normal");
					sFlag2 = QueryResult.CopyContent();
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("Status : Commit Successful")&&sFlag2.contains("Z"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"DML Operations are not allowed on complex datatype.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					QueryEditor.SingleQueryExe("delete from autotable.ctable where ename ='Z';", "Normal");
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Edit_Table_007")) //Testcase covered SDV_FUNVAL_DS_Edit_Table_001_641_New_4
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "ZZZZZZZZZZZ");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					BaseActions.Winwait("Post Failed");
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE,1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1=QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("Your transactions for this connection are rolled back as query execution is failed."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"wrong data is allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_Table_001_641_New"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "Z");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					if(BaseActions.WinExists("Confirm Discard Posted changes"))
					{
						Thread.sleep(GlobalConstants.MinWait);
						BaseActions.Click("Confirm Discard Posted changes", "", "Button1");
						if(QueryResult.ReadConsoleOutput("GLOBAL").contains("[INFO] Rollback Successful"))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Rollback was not Successfull.Please refer screenshot"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"No pop-up to Discard the changes.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
			}
		}
		for(int i=1;i<=17;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}	
}