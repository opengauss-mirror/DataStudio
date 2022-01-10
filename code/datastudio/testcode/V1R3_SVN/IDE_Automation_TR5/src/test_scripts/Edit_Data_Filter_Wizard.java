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
import object_repository.EditWindowElements;
import object_repository.GlobalConstants;
import script_library.EditTableDataFunctions;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class Edit_Data_Filter_Wizard {

	public static void main(String sARNumber) throws Exception
	{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Edit_Data_Filter_Wizard");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Edit_Data_Filter_Wizard");
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

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_009"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("select count (*) from autotable.rowtable;", "Normal");
					sFlag1 = QueryResult.EditCopyContent().replace("*", "");
					Thread.sleep(GlobalConstants.MinWait);
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
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("ROllBACK");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = EditTableDataFunctions.copyEditTableResultData();
					QueryEditor.SingleQueryExe("select count (*) from autotable.rowtable;", "Normal");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag3 = QueryResult.EditCopyContent().replace("*", "");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag1.matches(sFlag3) && sFlag2.contains("Status : Rollback Successful"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Retreive the result for row table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_006")) //Testcase Mapped SDV_FUNVAL_DS_Edit_data_filter_007,SDV_FUNVAL_DS_Edit_data_filter_008
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("select count (*) from autotable.ctable;", "Normal");
					sFlag1 = QueryResult.EditCopyContent().replace("*", "");
					Thread.sleep(GlobalConstants.MinWait);
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
					EditTableDataFunctions.Button("EXECUTE");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "B");
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "6");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "IT");
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("ROllBACK");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = EditTableDataFunctions.copyEditTableResultData();
					QueryEditor.SingleQueryExe("select count (*) from autotable.ctable;", "Normal");
					sFlag3 = QueryResult.EditCopyContent().replace("*", "");
					if(sFlag1.matches(sFlag3) && sFlag2.contains("Status : Rollback Successful"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Retreive the result for Column table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_007"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("select count (*) from autotable.ctable;", "Normal");
					sFlag1 = QueryResult.EditCopyContent().replace("*", "");
					Thread.sleep(GlobalConstants.MinWait);
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
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "B");
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "6");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "IT");
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("ROllBACK");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = EditTableDataFunctions.copyEditTableResultData();
					QueryEditor.SingleQueryExe("select count (*) from autotable.ctable;", "Normal");
					sFlag3 = QueryResult.EditCopyContent().replace("*", "");
					if(sFlag1.matches(sFlag3) && sFlag2.contains("Status : Rollback Successful"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to Retreive the result from the table in different schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_008"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("SELECT count (*) FROM autotable.ctable Where dept = 'IT';", "Normal");
					sFlag1 = QueryResult.EditCopyContent().replace("*", "");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 3);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("where", "dept = 'IT'");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("orderby", "eid");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "C");
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "7");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "IT");
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("ROllBACK");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = EditTableDataFunctions.copyEditTableResultData();
					QueryEditor.SingleQueryExe("SELECT count (*) FROM autotable.ctable Where dept = 'IT';", "Normal");
					sFlag3 = QueryResult.EditCopyContent().replace("*", "");
					if(sFlag1.matches(sFlag3) && sFlag2.contains("Status : Rollback Successful"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to edit select/where/orderby. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_020"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					QueryEditor.SingleQueryExe("SELECT count (*) FROM autotable.complex_table;", "Normal");
					sFlag1 = QueryResult.EditCopyContent().replace("*", "");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "*");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.editDataOperations("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SunAwtCanvas1", "left", 2,141,113 );//255, 97
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "600");
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "FF");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_UP,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP,1);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("ROllBACK");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = EditTableDataFunctions.copyEditTableResultData();
					QueryEditor.SingleQueryExe("SELECT count (*) FROM autotable.complex_table;", "Normal");
					sFlag3 = QueryResult.EditCopyContent().replace("*", "");
					if(sFlag1.matches(sFlag3) && sFlag2.contains("Status : Rollback Successful"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to edit for complex data type. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_010"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.autoTableNavigation();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = UtilityFunctions.GetClipBoard();
					EditTableDataFunctions.Button("CANCEL");
					if(sFlag1.contains("*"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"SELECT field is not showing * by default Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_011"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.autoTableNavigation();
					Thread.sleep(GlobalConstants.MinWait);	
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					EditTableDataFunctions.editTableWizard("Select", "--test文件");
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1=BaseActions.ControlGetText("Edit table data wizard", "", "Static6");
					if(sFlag1.matches("Please enter valid columns"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Chinese comments are allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_012"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "from schema1.emp1 union ALL");
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

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_013"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "rename eid as empid");
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5," String Operations are allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_014"))
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

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_015"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "eid+100 as empid");
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Math Operations are allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_016"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "max(eid)");
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Math Operations are allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_017")) //Testcase Mapped SDV_FUNVAL_DS_Edit_data_filter_018
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "current_database()");
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"INFO Functions are allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_019"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "eid as ename ,ename as eid");
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
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"column alias is allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Edit_data_filter_004"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "eid as ename ,ename as eid");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Where", "%");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 =BaseActions.ControlGetText("Edit table data wizard", "", "Static6");
					if(sFlag2.matches("Please enter valid columns"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Invalid characters are allowed.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Edit_data_filter_006"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWizard("Select", "'");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag2 =BaseActions.ControlGetText("Edit table data wizard", "", "Static6");
					if(sFlag2.matches("Please enter valid columns"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"user is able to open edit data window without entering any data in SELECT & WHERE field .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_USABILITY_DS_Edit_data_filter_001")) //Testcase Mapped SDV_RELIAB_RECOV_DS_Edit_data_filter_003
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.WinClose("Edit table data wizard");
					Thread.sleep(GlobalConstants.MinWait);
					if(BaseActions.WinExists("Edit table data wizard"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Edit table wizard is not closed .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}

				if(sTestCaseID.equals("SDV_FUNVAL_DS_Edit_data_filter_021")) // Test case Mapped SDV_FUNVAL_DS_Edit_data_filter_022
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					Thread.sleep(GlobalConstants.MinWait);	
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("CANCEL");
					if(BaseActions.WinExists("Edit table data wizard"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Edit table wizard is not closed .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Edit_data_filter_001")) //Testcase Mapped SDV_FUN_INVAL_DS_Edit_data_filter_002
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.ClearConsole("valid");
					Thread.sleep(GlobalConstants.MinWait);
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
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "Test");
					UtilityFunctions.KeyPress(KeyEvent.VK_LEFT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT,1);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);  		
					if(BaseActions.WinExists("Post Failed"))			//Modified Code for new build
					{
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE,1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE,1);
						Thread.sleep(GlobalConstants.MinWait);
						sFlag1= QueryResult.ReadConsoleOutput("GLOBAL").replace("\"", "");
						if(sFlag1.contains("[INFO] Query execution failed."))
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						}
						else
						{
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
							UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User is able to edit the system table.Please refer screenshot"+sTestCaseID+".jpg");
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Internal Error .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FIA_DS_Edit_data_filter_003")) //Testcase Mapped SDV_RELIAB_LOAD_DS_Edit_data_filter_001
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.TableImportWithoutWait(GlobalConstants.sCsvImportPath+"Largedata.csv", "OPEN");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 120, 224);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					if(BaseActions.WinExists("Edit table data wizard"))
					{
						Thread.sleep(GlobalConstants.MinWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						BaseActions.Winwait("Data Imported Successfully");
						Thread.sleep(GlobalConstants.MedWait);
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User is not able to open edit while import .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("SDV_FIA_DS_Edit_data_filter_002"))	
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					EditTableDataFunctions.autoTableNavigation();
					 UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					 UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					 Thread.sleep(GlobalConstants.MinWait);
					 ObjectBrowserPane.BrowserExport();
					 Thread.sleep(GlobalConstants.MedWait);
					 UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					 UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					 Thread.sleep(GlobalConstants.MedWait);
					 File file = new File(GlobalConstants.sCsvExportPath+"SDV_FIA_DS_Edit_data_filter_003.csv");
					 if(file.exists())
						 file.delete();
					 Thread.sleep(GlobalConstants.MedWait);
					 QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"SDV_FIA_DS_Edit_data_filter_003.csv");
					 Thread.sleep(GlobalConstants.MinWait);
					 BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 120, 224);
					 Thread.sleep(GlobalConstants.MedWait);
					 UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					 UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					 Thread.sleep(GlobalConstants.MinWait);
					 EditTableDataFunctions.editTableWindow();
					 Thread.sleep(GlobalConstants.MinWait);
					 if(BaseActions.WinExists("Edit table data wizard"))
					 {
						 Thread.sleep(GlobalConstants.MinWait);
						 UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						 UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						 BaseActions.Winwait("Data Exported Successfully");
						 Thread.sleep(GlobalConstants.MedWait);
						 UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						 UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						 UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					 }

					 else
					 {
						 UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						 UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User is not able to open edit while Export .Please refer screenshot"+sTestCaseID+".jpg");
						 UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					 }
					 QueryEditor.SingleQueryExe("truncate table autotable.auto_largedata;", "Valid");
					 Thread.sleep(GlobalConstants.MinWait);
				}



				if(sTestCaseID.equals("SDV_FIA_DS_Edit_data_filter_005"))	
				{
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.ClearConsole("GLOBAL"); //** Needs to be confirmed
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "12K_Lines.sql", "OPEN", "OVERWRITE");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,3);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("EXECUTE");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = EditTableDataFunctions.copyEditTableData().replace("\"", "");
					if(sFlag1.contains(" [INFO] SQL successfully loaded to SQL Terminal.")&&sFlag2.contains("SELECT * FROM autotable.ctable  ;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User is not able to perform Edit data table .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FIA_DS_Edit_data_filter_004"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.ClearConsole("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SaveQuery("SHORTCUT", "import.sql", "OVERWRITE");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,3);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("EXECUTE");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2 = EditTableDataFunctions.copyEditTableData().replace("\"", "");
					if(sFlag1.contains("[INFO] SQL successfully saved")&&sFlag2.contains("SELECT * FROM autotable.ctable  ;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User is not able to perform Edit data table .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FIA_DS_Edit_data_filter_006"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					BaseActions.ClearConsole("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,3);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("EXECUTE");
					sFlag2 = EditTableDataFunctions.copyEditTableData().replace("\"", "");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag1.contains("refreshed")&&sFlag2.contains("SELECT * FROM autotable.ctable  ;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User is not able to perform Edit data table after refresh.Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Edit_data_filter_003")) //need to modify this test case
				{
					UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");
					Thread.sleep(GlobalConstants.MedWait);
					EditTableDataFunctions.closeEditData();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					EditTableDataFunctions.autoTableNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,3);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("OK");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.Button("Execute");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= EditTableDataFunctions.copyEditTableData().replace("\"", "");
					if(sFlag1.contains("SELECT * FROM autotable.ctable"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"User is able to .Please refer screenshot"+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					//ObjectBrowserPane.RenameTable("autotable.coltable", "ctable");
					Thread.sleep(GlobalConstants.ModWait);
				}
			}
		}
		for(int i=1;i<=25;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}
	}

}
