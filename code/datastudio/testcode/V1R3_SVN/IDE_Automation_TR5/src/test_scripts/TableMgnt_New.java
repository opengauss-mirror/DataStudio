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
import java.io.IOException;

import autoitx4java.AutoItX;
import object_repository.CreateTableWizardElements;
import object_repository.EditWindowElements;
import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import object_repository.TablePropertyElements;
import script_library.CreateTableWizardFunctions;
import script_library.EditTableDataFunctions;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class TableMgnt_New {

	public static void main(String sARNumber) throws Exception {

		//Creating the Test Result File for Reporting
		String ResultExcel= UtilityFunctions.CreateResultFile("FunctionalTest", "TableMgnt_New");
		//Creating the Test Result File for TMSS
		String sTextResultFile= UtilityFunctions.CreateTextResultFile("FunctionalTest", "TableMgnt_New");
		//Variable Declarations	
		String sTestCaseID, sExecute, sFlag, sFlag1, sStatus, sInputQuery, sFlag2, 	sFlag3;
		//Getting the total number of test cases from data sheet
		int iRowCount=UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		//Loop to iterate through each Test Case in Test Data Sheet	
		for (int i = 1; i < iRowCount; i++) {

			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 2);
			if (sExecute.equals("Yes")) {

				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 1);
				sInputQuery=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 3);

				if (sTestCaseID.equals("GaussIDE_FUNC_CreateTable_093_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);

					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("FirstIndex");

					CreateTableWizardFunctions.addColumnIndex(1);
					CreateTableWizardFunctions.checkIndexDefinition();
					CreateTableWizardFunctions.indexName("SecondIndex");
					CreateTableWizardFunctions.addColumnIndex(2);
					CreateTableWizardFunctions.checkIndexDefinition();
					CreateTableWizardFunctions.indexName("ThirdIndex");
					CreateTableWizardFunctions.addColumnIndex(3);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.checkIndexDefinition();
					CreateTableWizardFunctions.indexName("FourthIndex");
					CreateTableWizardFunctions.addColumnIndex(4);
					CreateTableWizardFunctions.checkIndexDefinition();
					CreateTableWizardFunctions.indexName("FifthIndex");
					CreateTableWizardFunctions.addColumnIndex(5);
					CreateTableWizardFunctions.checkIndexDefinition();
					Thread.sleep(GlobalConstants.MedWait);

					sFlag=CreateTableWizardFunctions.SQLPreviewCopy();

					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);

					CreateTableWizardFunctions.deleteColumnIndex(1);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.deleteColumnIndex(2);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.deleteColumnIndex(3);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.deleteColumnIndex(4);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.deleteColumnIndex(5);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1=CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);


					if(sFlag.contains("FirstIndex") &&sFlag.contains("SecondIndex") &&sFlag.contains("ThirdIndex") 
							&&sFlag.contains("FourthIndex") &&sFlag.contains("FifthIndex") &&sFlag1.contains("FirstColumn") 
							&& sFlag2.contains("[INFO] Table created successfully."))
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println( sTestCaseID+ "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to add/delete the index. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "TestTable");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

				}

				if (sTestCaseID.equals("GaussIDE_FUNC_CreateTable_096_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.Button("Next");
					sFlag1=CreateTableWizardFunctions.SQLPreviewCopy();

					CreateTableWizardFunctions.Button("Back");
					CreateTableWizardFunctions.Button("Back");
					CreateTableWizardFunctions.Button("Back");
					CreateTableWizardFunctions.Button("Back");
					CreateTableWizardFunctions.Button("Back");
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.Button("Next");
					sFlag2=CreateTableWizardFunctions.SQLPreviewCopy();

					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);

					if(sFlag1.contains("FirstColumn") && sFlag2.contains("SecondColumn") && sFlag.contains("[INFO] Table created successfully."))
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to click Back/Next/Finish. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "TestTable");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if (sTestCaseID.equals("GaussIDE_Func_Val_CreateTable_244_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					sFlag=BaseActions.WinGetTitle(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);

					ObjectBrowserPane.Auto_Table_Navigation();
					EditTableDataFunctions.editTableWindow();
					sFlag1=	BaseActions.WinGetTitle(EditWindowElements.wEditWindow);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);

					ObjectBrowserPane.Auto_Table_Navigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					sFlag2=BaseActions.WinGetTitle(CreateTableWizardElements.wDropTable);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);

					if (sFlag.equals("Create New table")&& sFlag1.equals("Edit table data wizard") && sFlag2.equals("Drop Table")) {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID+ "passed");
					}

					else {
						System.out.println(sTestCaseID+"Fail");

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to add unique check constraints. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);	
					}

				}
				if (sTestCaseID.equals("GaussIDE_FUNC_CreateTable_090_070")) 
				{	
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");

					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.Button("ADD");

					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("FirstIndex");
					CreateTableWizardFunctions.addColumnIndex(1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					CreateTableWizardFunctions.indexName("SecondIndex");
					CreateTableWizardFunctions.addColumnIndex(2);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

					sFlag1=CreateTableWizardFunctions.SQLPreviewCopy(); 
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);

					if(sFlag1.contains("FirstColumn") && sFlag1.contains("SecondColumn") && sFlag1.contains("FirstIndex")&& sFlag1.contains("SecondIndex")&& sFlag.contains("[INFO] Table created successfully."))
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create index on 1 or more availble columns. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "TestTable");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");


				}
				if (sTestCaseID.equals("GaussIDE_FUNC_CreateTable_089_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("FirstIndex");
					BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sUserDefinedExp, "lower(FirstColumn)");
					CreateTableWizardFunctions.addColumnIndex(1);
					UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

					sFlag=CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);

					if(sFlag.contains("lower(FirstColumn)") &&sFlag1.contains("[INFO] Table created successfully."))
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create index with user defined expression. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "TestTable");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if (sTestCaseID.equals("GaussIDE_FUNC_CreateTable_091_070")) {

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");

					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.Button("ADD");

					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("FirstIndex");

					CreateTableWizardFunctions.changeColumnOrder(2);
					CreateTableWizardFunctions.indexName("SecondIndex");

					CreateTableWizardFunctions.changeColumnOrder(2);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag=CreateTableWizardFunctions.SQLPreviewCopy(); 
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");

					if (sFlag.contains("FirstIndex")&& sFlag.contains("SecondIndex") &&sFlag1.contains("[INFO] Table created successfully.") ) {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
						System.out.println( sTestCaseID+"passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,5,"unable to create table with DEFER/UNIQUE .Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "testtable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}
				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_074_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("c1", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("c2", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("c3", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("c4", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("c5", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.Button("NEXT");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableConstraint("PRIMARY_KEY");
					CreateTableWizardFunctions.addConstraint();

					sFlag=CreateTableWizardFunctions.SQLPreviewCopy();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xTableCord, CreateTableWizardElements.yTableCord);
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.deleteConstraint(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = CreateTableWizardFunctions.SQLPreviewCopy();
					System.out.println(sFlag1);
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2= QueryResult.ReadConsoleOutput("Global");
					if(sFlag.contains("PRIMARY KEY (c1, c2, c3, c4, c5)") && sFlag2.contains("[INFO] Table created successfully."))


					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID+"passed");

					}

					else {

						System.out.println("Fail");

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to add primary check constraints. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "testtable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");




				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateTable_069_070"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("c1", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("c2", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("c3", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("c4", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("c5", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.Button("NEXT");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableConstraint("UNIQUE");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addConstraint();
					Thread.sleep(GlobalConstants.MedWait);
					sFlag=CreateTableWizardFunctions.SQLPreviewCopy();
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xTableCord, CreateTableWizardElements.yTableCord);

					CreateTableWizardFunctions.deleteConstraint(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1 = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag2= QueryResult.ReadConsoleOutput("Global");

					if(sFlag.contains("UNIQUE (c1, c2, c3, c4, c5)")  && sFlag2.contains("[INFO] Table created successfully."))
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println( sTestCaseID+"passed");

					}

					else {
						System.out.println( sTestCaseID+"fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to add unique check constraints. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "testtable");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");	

				}

				if (sTestCaseID.equals("GaussIDE_FUNC_CreateTable_094_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);

					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("FirstIndex");
					CreateTableWizardFunctions.addColumnIndex(1);
					CreateTableWizardFunctions.checkIndexDefinition();
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bIndexEdit);
					CreateTableWizardFunctions.indexName("SecondIndex");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bIndexAdd);
					sFlag=CreateTableWizardFunctions.SQLPreviewCopy();
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					if (sFlag.contains("SecondIndex") && sFlag1.contains("[INFO] Table created successfully."))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println( sTestCaseID+ "passed");
					}

					else{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to rename the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "TestTable");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				//Mapped GaussIDE_FUNC_EditTable_116_070,GaussIDE_FUNC_EditTable_114_0,GaussIDE_FUNC_EditTable_114_070_156,GaussIDE_Func_InVal_CreateTbl_001_581	
				if(sTestCaseID.equals("GaussIDE_FuncInvalid_CreateTable_740_070"))

				{
					boolean b;
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName("*TestTable", "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.DataType("CHAR");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.SQLPreviewCopy();

					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag=BaseActions.WinGetText(CreateTableWizardElements.wTitle);

					b=BaseActions.WinExists(CreateTableWizardElements.wTitle);

					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName("auto_largedata", "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1=BaseActions.WinGetText(CreateTableWizardElements.wTitle);
					b=BaseActions.WinExists(CreateTableWizardElements.wTitle);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);

					if(sFlag.contains("ERROR: syntax error at or near \"TestTable\"") &&sFlag1.contains("ERROR: relation \"auto_largedata\" already exists")&& b==true)
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+4,4,"Passed");
						System.out.println(sTestCaseID+ "passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Table is created with invalid name. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if (sTestCaseID.equals("GaussIDE_FUNC_CreateTable_025_716_070")) {

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					ObjectBrowserPane.vacuum();

					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					if (sFlag.contains("successfully vacuumed")) {
						System.out.println(sTestCaseID+"passed");
					}

					else {
						System.out.println("Fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to vacuum the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);

					}

				}

				if (sTestCaseID.equals("GaussIDE_FUNC_EditTable_097_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.DataType("CHAR");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					ObjectBrowserPane.RenameTable("_TestTable2");
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MedWait);

					ObjectBrowserPane.RenameTable("TestTable$");
					sFlag2 = QueryResult.ReadConsoleOutput("GLOBAL");
					ObjectBrowserPane.Auto_Table_Navigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					ObjectBrowserPane.RenameTable("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
					sFlag3 = QueryResult.ReadConsoleOutput("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

					if(sFlag1.contains("[INFO] Renamed")
							&& sFlag2.contains("[INFO] Renamed")&& 
							sFlag3.contains("[INFO] Renamed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID+"passed");
					}
					else {
						System.out.println(sTestCaseID+"fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to rename the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if (sTestCaseID.equals("GaussIDE_FUNC_EditTable_104_070")) {

					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("FirstIndex");
					CreateTableWizardFunctions.addColumnIndex(1);
					CreateTableWizardFunctions.indexName("SecondIndex");
					CreateTableWizardFunctions.addColumnIndex(2);
					CreateTableWizardFunctions.indexName("ThirdIndex");
					CreateTableWizardFunctions.addColumnIndex(3);
					CreateTableWizardFunctions.SQLPreviewCopy();

					CreateTableWizardFunctions.Button("FINISH");

					Thread.sleep(GlobalConstants.MedWait);


					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.setSchema();
					Thread.sleep(GlobalConstants.MedWait);			
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
					BaseActions.Click(TablePropertyElements.sSchemaTitle, "", TablePropertyElements.btnOK);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					if (sFlag.contains("[INFO] Moved")) {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}
					else {
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set Schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto_import", "TestTable");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}


				if (sTestCaseID.equals("GaussIDE_FUNC_EditTable_102_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");

					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName("DescTable","No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.TableDesc("Create a table with table name as DescTable and Description of table text box having very large text");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);

					CreateTableWizardFunctions.indexName("FirstIndex");
					CreateTableWizardFunctions.addColumnIndex(1);
					CreateTableWizardFunctions.indexName("SecondIndex");
					CreateTableWizardFunctions.addColumnIndex(2);
					CreateTableWizardFunctions.indexName("ThirdIndex");
					CreateTableWizardFunctions.addColumnIndex(3);
					CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MedWait);

					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					CreateTableWizardFunctions.resetTableDesc("Create a table with table name as ResetDescTable and Description of table text box having very large text");


					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);

					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					if(UtilityFunctions.GetClipBoard().contains("ResetDescTable"))
					{
						Thread.sleep(GlobalConstants.MinWait);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
						Thread.sleep(GlobalConstants.MinWait);
						BaseActions.Focus(CreateTableWizardElements.wCreateTitle, "", CreateTableWizardElements.wproperty);
						UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_F4, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_F4, 1);
					}
					else {
						System.out.println(sTestCaseID+"Fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set table description. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "DescTable");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if (sTestCaseID.equals("GaussIDE_FUNC_EditTable_103_070")) 
				{

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.TableName("TestTable","No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);

					CreateTableWizardFunctions.indexName("FirstIndex");
					CreateTableWizardFunctions.addColumnIndex(1);
					CreateTableWizardFunctions.indexName("SecondIndex");
					CreateTableWizardFunctions.addColumnIndex(2);
					CreateTableWizardFunctions.indexName("ThirdIndex");
					CreateTableWizardFunctions.addColumnIndex(3);
					CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

					if(UtilityFunctions.GetClipBoard().contains("DEFAULT"))
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
						BaseActions.Focus(CreateTableWizardElements.wCreateTitle, "", CreateTableWizardElements.wproperty);
						UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
						UtilityFunctions.KeyPress(KeyEvent.VK_F4, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_F4, 1);

					}
					else {
						System.out.println(sTestCaseID+"Fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set table description. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "TestTable");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				//Mapped with "GaussIDE_FUNC_EditTable_111_070" & GaussIDE_FUNC_EditTable_115_070, GaussIDE_FUNC_CreateTable_113_070
				if (sTestCaseID.equals("GaussIDE_FUNC_EditTable_109_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.CreateSchema("sh1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					QueryEditor.SingleQueryExe("CREATE TABLE sh1.testtable1 (c1 bigint)","notreq");
					ObjectBrowserPane.InsertTable("sh1", "testtable1", 1);
					ObjectBrowserPane.CreateSchema("sh2");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);

					QueryEditor.SingleQueryExe("CREATE TABLE sh2.testtable2 (c2 bigint)","notreq");
					ObjectBrowserPane.InsertTable("sh2", "testtable2", 1);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);

					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.setSchema();
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 2);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					if (sFlag.contains("Moved")) {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {
						System.out.println(sTestCaseID+"Fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("sh1", "testtable1");
					ObjectBrowserPane.DropTable("sh2", "testtable2");
					ObjectBrowserPane.DropSchema("sh1");
					ObjectBrowserPane.DropSchema("sh2");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}

				if (sTestCaseID.equals("GaussIDE_FUNC_EditTable_110_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");

					ObjectBrowserPane.CreateSchema("sh1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					QueryEditor.SingleQueryExe("CREATE TABLE sh1.testtable1 (c1 bigint)","notreq");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

					ObjectBrowserPane.CreateSchema("sh2");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					QueryEditor.SingleQueryExe("CREATE TABLE sh2.testtable1 (c1 bigint)","notreq");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);

					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					ObjectBrowserPane.setSchema();
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 2);

					sFlag=BaseActions.WinGetText(ObjectBrowserElements.wSetSchema);

					if(sFlag.contains("ERROR: relation \"testtable1\" already exists in schema \"sh1\""))
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + " passed");
					}
					else {
						System.out.println( sTestCaseID+"Fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("sh1", "testtable1");
					ObjectBrowserPane.DropTable("sh2", "testtable1");
					ObjectBrowserPane.DropSchema("sh1");
					ObjectBrowserPane.DropSchema("sh2");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

				}
				//Mapped GaussIDE_FUNC_CreateTable_025_070GaussIDE_FUNC_CreateTable_026_070GaussIDE_FUNC_CreateTable_027_070
				if (sTestCaseID.equals("GaussIDE_FUNC_InVal_CreateTable_248_070")) 
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName("T2", "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("C2", "No");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.DataDistribution("REPLICATION");

					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();

					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName("T3", "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("C3", "No");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.DataDistribution("HASH");
					CreateTableWizardFunctions.availbleHashColumn();
					CreateTableWizardFunctions.selectHashColumn();

					sFlag1 = CreateTableWizardFunctions.SQLPreviewCopy();

					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);

					if (sFlag.contains("REPLICATION") && sFlag1.contains("HASH")) {
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}
					else {
						System.out.println( sTestCaseID+"Fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set data distribution. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "T2");
					ObjectBrowserPane.DropTable("auto", "T3");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

				}

				//Mapped with 	GaussIDE_FUNC_PropertyTable_117_070
				if (sTestCaseID.equals("GaussIDE_FUNC_VAL_CreateTable_225_089_070")) {

					AutoItX x = new AutoItX();
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName("T1", "No");
					CreateTableWizardFunctions.Button("Next");
					CreateTableWizardFunctions.AddCloumn("C1", "No");
					CreateTableWizardFunctions.Button("ADD");
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("I1");
					//need to add index method//
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.selectAccessMethodIndex("hash");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.addColumnIndex(1);
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.tablePropertyFunctionOpen();
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_H, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_H, 1);
					Thread.sleep(GlobalConstants.MinWait);

					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MinWait);

					sFlag=x.clipGet();

					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_F4, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F4, 1);
					ObjectBrowserPane.DropIndex("auto", "I1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					Thread.sleep(GlobalConstants.MedWait);

					ObjectBrowserPane.Auto_Table_Navigation();

					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					ObjectBrowserPane.analyze();
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.tablePropertyFunctionOpen();
					Thread.sleep(GlobalConstants.MinWait);

					UtilityFunctions.KeyPress(KeyEvent.VK_H, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_H, 1);
					Thread.sleep(GlobalConstants.MinWait);

					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1=x.clipGet();
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag.contains("Has Index::True") && sFlag1.contains("Has Index::False") ){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println( sTestCaseID+ "passed");
					}
					else {
						System.out.println( sTestCaseID+"Fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
					ObjectBrowserPane.DropTable("auto", "T1");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");


				}

				//Mapped GaussIDE_FUNC_CreateTable_120_070
				if (sTestCaseID.equals("GaussIDE_FUNC_CreateTable_119_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableName("Shaifali","No");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Col1", "No");
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.ConstraintName("test");
					CreateTableWizardFunctions.ConstraintExpression("col1>100");
					CreateTableWizardFunctions.ConstraintButton("ADD");
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.indexName("FirstIndex");
					CreateTableWizardFunctions.addColumnIndex(1);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTabControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("SecondIndex");

					Thread.sleep(GlobalConstants.MinWait);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTablespaceCombo);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					CreateTableWizardFunctions.addColumnIndex(1);
					sFlag1 = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");

					if (sFlag1.contains("TABLESPACE pg_default")) {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID+"passed");
					}
					else {
						System.out.println( sTestCaseID+"Fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set table description. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}	
					ObjectBrowserPane.DropTable("auto", "Shaifali");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}	

				//Mapped GaussIDE_FUNC_VAL_CreateTable_225_144_070
				if (sTestCaseID.equals("GaussIDE_FUNC_ViewTable_108_070")) {

					AutoItX x = new AutoItX();
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("c1", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);

					CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.Auto_Table_Navigation();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					sFlag=x.clipGet();
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag.contains("Normal"))
					{

						Thread.sleep(GlobalConstants.MinWait);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}
					else {
						System.out.println( sTestCaseID+"Fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set table description. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					ObjectBrowserPane.DropTable("auto", "TestTable");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");

				}

				if (sTestCaseID.equals("GaussIDE_FUNC_InVal_CreateTable_247_070")) {

					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i+2, 3, "Yes");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.CreateSchema("sh1");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
					QueryEditor.SingleQueryExe("CREATE TABLE sh1.testtable1 (col1 bigint)","notreq");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.objectBrowserRefresh("SINGLE"); 
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,2);

					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
					ObjectBrowserPane.setSchema();
					Thread.sleep(GlobalConstants.MedWait);			
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER,1);
					BaseActions.Click(TablePropertyElements.sSchemaTitle, "", TablePropertyElements.btnOK);
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					if (sFlag.contains("[INFO] Moved")) 
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID+"passed");

					}
					else{
						System.out.println( sTestCaseID+"Fail");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);

					}
					ObjectBrowserPane.DropTable("auto", "testtable1");
					ObjectBrowserPane.DropSchema("sh1");
					BaseActions.ClearConsole("GLOBAL");
					ObjectBrowserPane.objectBrowserRefresh("SINGLE");
				}	

			}

		}	

		for(int i=1;i<=22;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}	


	}	

}
