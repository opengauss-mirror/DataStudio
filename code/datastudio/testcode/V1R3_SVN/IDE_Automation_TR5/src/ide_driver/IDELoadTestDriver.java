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
package ide_driver;

import java.awt.event.KeyEvent;
import java.io.File;

import object_repository.GlobalConstants;
import script_library.DebugOperations;
import script_library.EditTableDataFunctions;
import script_library.ExecutionHistoryFunctions;
import script_library.Login;
import script_library.MultipleTerminal;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import test_scripts.Reliability_Script;

public class IDELoadTestDriver {

	public static void main(String args[]) throws Exception {
		System.out.println(UtilityFunctions.CurrentDateTime("Execution Started: "));
		//Loading required DLL for AutoIT
		System.load(GlobalConstants.sJacobDLL);
		// Creating the Test Result File for Reporting
		String sResultExcel = "IDE_Load_Test_Data.xlsx";
		//Launching the IDE Tool Application
		//Login.LaunchIDE(GlobalConstants.sIDEPath);
		//Getting Login Credentials from IDE_Smoke_Test_Data file and
		String sConnection = UtilityFunctions.GetExcelCellValue(sResultExcel, "IDELogin", 1, 0);
		String sHost = UtilityFunctions.GetExcelCellValue(sResultExcel, "IDELogin", 1, 1);
		String sHostPort = UtilityFunctions.GetExcelCellValue(sResultExcel, "IDELogin", 1, 2);
		String sDBName = UtilityFunctions.GetExcelCellValue(sResultExcel, "IDELogin", 1, 3);
		String sUserName = UtilityFunctions.GetExcelCellValue(sResultExcel, "IDELogin", 1, 4);
		String sPassword = UtilityFunctions.GetExcelCellValue(sResultExcel, "IDELogin", 1, 5);
		//Login into IDE Tool
		//Login.IDELogin(sConnection, sHost, sHostPort, sDBName, sUserName,sPassword,"PERMENANT");
		int iRowCount = UtilityFunctions.GetRowCount(sResultExcel,"Load_Test_Data");
		String sTextResultFile = "IDE_Load_Results.txt";

		for(int i=1;i<=iRowCount;i++)
		{
			String sScenario = UtilityFunctions.GetExcelCellValue(sResultExcel, "Load_Test_Data", i, 1);
			String sExecute = UtilityFunctions.GetExcelCellValue(sResultExcel, "Load_Test_Data", i, 4);
			if (sScenario.equals("New_Features") && sExecute.equals("Yes"))
			{
				for(int j=1;j<=1000;j++)
				{
					UtilityFunctions.WriteToText(sTextResultFile, UtilityFunctions.CurrentDateTime("Create_Table Execution Started: "));
					Thread.sleep(GlobalConstants.MedWait);
					String sFileName = GlobalConstants.sCsvImportPath+"employee.csv";
					//Load Query Execution
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "Import_load.sql", "OPEN", "OVERWRITE");
					Thread.sleep(GlobalConstants.ModWait);
					QueryEditor.ExecuteButton();
					UtilityFunctions.GetCPUPercentage("Load Sql Execution"+i);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ClearEditor();
					//selecting from SQL execution History
					UtilityFunctions.GetCPUPercentage("Load Sql Execution"+i);
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.SelectTerminal(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("LOADANDCLOSE");
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					//importing from csv file
					UtilityFunctions.GetCPUPercentage("Load Sql Execution"+i);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation_Load();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.TableImport(sFileName, "OPEN");
					Thread.sleep(GlobalConstants.MedWait);
					//importing from SQL File
					UtilityFunctions.GetCPUPercentage("Load Sql Execution"+i);
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "Employee_DDL.sql", "OPEN", "OVERWRITE");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.SingleQueryExe("select * from employee;", "Valid");
					Thread.sleep(GlobalConstants.MinWait);
					//exporting the data to csv file
					UtilityFunctions.GetCPUPercentage("Load Sql Execution"+i);
					QueryResult.CurrentExport();
					File file = new File(GlobalConstants.sCsvExportPath+"ExportResult.csv");
					if(file.exists())
						file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					QueryResult.SaveCsv(GlobalConstants.sCsvExportPath+"ExportResult.csv");
					Thread.sleep(GlobalConstants.MedWait);
					//view creation
					UtilityFunctions.GetCPUPercentage("Load Sql Execution"+i);
					QueryEditor.SingleQueryExe("Create or replace view auto.viewname1 as select * from employee;", "Valid");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.TruncateTable(140, 117);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					//Canceling the Query
					QueryEditor.SetFunction("select count(*) from auto.auto_largedata");
					UtilityFunctions.GetCPUPercentage("Load Sql Execution"+i);
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.ExecuteButton();
					Thread.sleep(GlobalConstants.MinWait);
					QueryEditor.CancelQuery("SHORTCUT");
					QueryEditor.ClearEditor();
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.openExeHistory(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.selectExeHistoryQuery(1);
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.exeHistoryOperations("DELETEALL");
					Thread.sleep(GlobalConstants.MinWait);
					ExecutionHistoryFunctions.closeExeHistory();
					//Edit table data
					UtilityFunctions.GetCPUPercentage("Load Sql Execution"+i);
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick("Data Studio", "", "SysTreeView321", "left", 1, 140, 117);
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editTableWindow();
					Thread.sleep(GlobalConstants.MinWait);
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
					BaseActions.SetText("Data Studio", "", "SunAwtCanvas1", "1");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("POST");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.editDataOperations("COMMIT");
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditTableResult();
					Thread.sleep(GlobalConstants.MinWait);
					EditTableDataFunctions.closeEditData();
					//	Debug Operation
					UtilityFunctions.GetCPUPercentage("Load Sql Execution"+i);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
					Thread.sleep(GlobalConstants.MinWait);
					DebugOperations.DebugObjectBrowser_Open();
					Thread.sleep(GlobalConstants.MinWait);
					DebugOperations.SetBreakPoint(10);
					Thread.sleep(GlobalConstants.MinWait);
					DebugOperations.DebugSession();
					Thread.sleep(GlobalConstants.MinWait);
					DebugOperations.ClickContinue();
					Thread.sleep(GlobalConstants.MinWait);
					MultipleTerminal.CloseTerminal(2);
					Thread.sleep(GlobalConstants.MinWait);
					Login.DebugWindows();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.WriteToText(sTextResultFile, UtilityFunctions.CurrentDateTime("Create_Table Execution Completed: "));
				}
			}

			if (sScenario.equals("Single_Query_Execution") && sExecute.equals("Yes"))
			{ 
				UtilityFunctions.WriteToText(sTextResultFile, UtilityFunctions.CurrentDateTime("Create_Table Execution Started: "));
				Reliability_Script.Single_Query_Exe(sResultExcel, "Load_Test_Data","Single_Query_Exe");
				UtilityFunctions.WriteToText(sTextResultFile, UtilityFunctions.CurrentDateTime("Create_Table Execution Completed: "));
			} 
			if (sScenario.equals("Create_Table") && sExecute.equals("Yes"))
			{
				UtilityFunctions.WriteToText(sTextResultFile, UtilityFunctions.CurrentDateTime("Create_Table Execution Started: "));
				Reliability_Script.Drop_Table(sResultExcel,"Load_Test_Data");
				Reliability_Script.Create_Table(sResultExcel,"Load_Test_Data","Create_Table");
				UtilityFunctions.WriteToText(sTextResultFile, UtilityFunctions.CurrentDateTime("Create_Table Execution Completed: "));
			}
			if (sScenario.equals("Create_Function") && sExecute.equals("Yes"))
			{
				UtilityFunctions.WriteToText(sTextResultFile, UtilityFunctions.CurrentDateTime("Create_Function Execution Started: "));
				Reliability_Script.Drop_Function(sResultExcel, "Load_Test_Data");
				Reliability_Script.Create_Function(sResultExcel, "Load_Test_Data","Create_Function");
				UtilityFunctions.WriteToText(sTextResultFile, UtilityFunctions.CurrentDateTime("Create_Function Execution Completed: "));
			}
		}
		//Logout from IDE Tool after Execution
		//Login.IDELogout();
		System.out.println(UtilityFunctions.CurrentDateTime("Execution Completed: "));
	}
}
