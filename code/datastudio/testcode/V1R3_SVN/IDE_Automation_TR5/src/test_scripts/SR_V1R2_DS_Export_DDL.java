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
import object_repository.GlobalConstants;
import object_repository.ObjectBrowserElements;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import script_library.ViewFunctions;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;


public class SR_V1R2_DS_Export_DDL {

	public static void main(String sARNumber) throws Exception {


		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","SR_V1R2_DS_Export_DDL");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","SR_V1R2_DS_Export_DDL");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		for(int i=1;i<=iRowCount;i++)
		{

			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);

			if(sExecute.equalsIgnoreCase("Yes")){

				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "SR_V1R2_DS_Export_DDL", i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_021")){            //Testcase mapped SDV_FUN_VAL_DS_Export_DDL_020

					File f = new File(GlobalConstants.exportDDlPath);
				for(File file: f.listFiles()) 
						if (!file.isDirectory()) 
							file.delete();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.createSchemaObjectBrowser("TestSchema1");
					String sPath = GlobalConstants.exportDDlPath + "TestSchema1.sql";
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.exportDDL(sPath, "No");

					Thread.sleep(GlobalConstants.MaxWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "testschema1");

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.dropSchemaObjectBrowser();

					if(sFlag.contains("Successfully exported") && sFlag1.contains("testschema1")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to export DDL, exported file doesn't contain DDL of schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}


				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_022")){          

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.CreateSchema("TestSchema2");
					String sPath = GlobalConstants.exportDDlPath + "TestSchema2.sql";
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.exportDDL(sPath, "No");

					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					String sFlag1 = UtilityFunctions.searchFile(sPath, "testschema2");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.dropSchemaObjectBrowser();

					if(sFlag.contains("Successfully exported") && sFlag1.contains("testschema2"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL, exported file doesn't contain DDL of schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_025")){

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.createSchemaObjectBrowser("createmaximumallowedschemanamechecktheschemaddlissavedinsqlform");
					String sPath = GlobalConstants.exportDDlPath + "testschema3.sql";
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.exportDDL(sPath, "No");

					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					String sFlag1 = UtilityFunctions.searchFile(sPath, "createmaximumallowedschemanamechecktheschemaddlissavedinsqlform");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C,1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.dropSchemaObjectBrowser();

					if(sFlag.contains("Successfully exported") && sFlag1.contains("createmaximumallowedschemanamechecktheschemaddlissavedinsqlform"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for maximum allowed schema name, exported file doesn't contain DDL of schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_026")){

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.CreateSchema("\"test schema with space,special chars **@@##^^!!\"");
					String sPath = GlobalConstants.exportDDlPath + "testschema4.sql";
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.exportDDL(sPath, "No");

					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					String sFlag1 = UtilityFunctions.searchFile(sPath, "test schema with space,special chars **@@##^^!!");


					if(sFlag.contains("Successfully exported") && sFlag1.contains("test schema with space,special chars **@@##^^!!"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for schema with Space, init caps and special characters, exported file doesn't contain DDL of schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_027")){

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.renameSchemaObjectBrowser("testschemarenamed");
					String sPath = GlobalConstants.exportDDlPath + "testschemarenamed.sql";
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.exportDDL(sPath, "No");

					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					String sFlag1 = UtilityFunctions.searchFile(sPath, "testschemarenamed");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("testschemarenamed"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL, exported file doesn't contain DDL of schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_012")){

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.RenameSchema("testschemarenamed", "testschemarenamedterminal");
					String sPath = GlobalConstants.exportDDlPath + "testschemarenamedterminal.sql";
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					ObjectBrowserPane.exportDDL(sPath, "No");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					if(sFlag.contains("Export process interrupted")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Export DDL works when schema is renamed and doesn't exist. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_011")){

					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.DropSchema("testschemarenamedterminal");
					String sPath = GlobalConstants.exportDDlPath + "testschemadropped.sql";
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					ObjectBrowserPane.exportDDL(sPath, "No");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.ObjectBrowserRefresh();

					if(sFlag.contains("Export process interrupted")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Export DDL works when schema is dropped and doesn't exist. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_023")){

					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.CreateSchema("commented --schema with comment");
					String sPath = GlobalConstants.exportDDlPath + "commented.sql";
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.exportDDL(sPath, "No");

					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE SCHEMA");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C,1);
					Thread.sleep(GlobalConstants.MinWait);
					ObjectBrowserPane.dropSchemaObjectBrowser();

					if(sFlag.contains("Successfully exported") && sFlag1.contains("commented"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for a schema with comments. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_002")){         //Testcase mapped SDV_FUN_VAL_DS_Export_DDL_001

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
					String sPath = GlobalConstants.exportDDlPath + "allcombinations.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE");
					String sFlag2 = UtilityFunctions.searchFile(sPath, "DISTRIBUTE");
					String sFlag3 = UtilityFunctions.searchFile(sPath, "COMMENT ON TABLE");
					String sFlag4 = UtilityFunctions.searchFile(sPath, "INDEX");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("ALLCOMBINATIONS") && 
							sFlag2.contains("HASH") && sFlag3.contains("Table with all combinations") &&
							sFlag4.contains("index")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for table with all possible combinations. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_004")){         //Testcase mapped SDV_FUN_VAL_DS_Export_DDL_003

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C,2);
					String sPath = GlobalConstants.exportDDlPath + "columnstore.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "orientation");
					String sFlag2 = UtilityFunctions.searchFile(sPath, "VALUES (3, 4)");
					String sFlag3 = UtilityFunctions.searchFile(sPath, "VALUES (7, 8)");
					String sFlag4 = UtilityFunctions.searchFile(sPath, "VALUES (9, 10)");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("orientation=column") && 
							sFlag2.contains("INSERT INTO columnstore VALUES (3, 4);") && sFlag3.contains("INSERT INTO columnstore VALUES (7, 8);") &&
							sFlag4.contains("INSERT INTO columnstore VALUES (9, 10);")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed"); 
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for column store table with data. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_008")){

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C,3);
					String sPath = GlobalConstants.exportDDlPath + "compressed.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE TABLE");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("compressedtable"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for compressed table, exported file doesn't contain DDL of the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_009")){

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_R,1);
					String sPath = GlobalConstants.exportDDlPath + "replication.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL"); 

					String sFlag1 = UtilityFunctions.searchFile(sPath, "DISTRIBUTE");
					String sFlag2 = UtilityFunctions.searchFile(sPath, "VALUES (9, 10)");
					String sFlag3 = UtilityFunctions.searchFile(sPath, "VALUES (7, 8)");
					String sFlag4 = UtilityFunctions.searchFile(sPath, "VALUES (5, 6)");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("DISTRIBUTE BY REPLICATION") && 
							sFlag2.contains("INSERT INTO replication VALUES (9, 10);") && sFlag3.contains("INSERT INTO replication VALUES (7, 8);") &&
							sFlag4.contains("INSERT INTO replication VALUES (5, 6);")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for Replication table with data. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_012")){            //Testcase mapped SDV_FUN_VAL_DS_Export_DDL_010 and SDV_FUN_VAL_DS_Export_DDL_011

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_M, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_M,1);
					String sPath = GlobalConstants.exportDDlPath + "maxvolume.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE TABLE");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("maxcolumns"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for table with maximum columns and huge volume of data, exported file doesn't contain DDL of the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_013")){

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C,1);
					String sPath = GlobalConstants.exportDDlPath + "chinesedata.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE TABLE");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("chinesedata"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for table with chinese dataa. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_014")){

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S,1);
					String sPath = GlobalConstants.exportDDlPath + "specialcharacters.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE TABLE");
					String sFlag2 = UtilityFunctions.searchFile(sPath, "VALUES ('%%%%%%%%%%%%%%%%%%', '^^^^^^^^^^^^^^^^^^^^^')");
					String sFlag3 = UtilityFunctions.searchFile(sPath, "VALUES ('!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!', '$$$$$$$$$$$$$$$$$$$$')");
					String sFlag4 = UtilityFunctions.searchFile(sPath, "VALUES ('@@@@@@@@@@@@', '**********')");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("specialcharacters") && 
							sFlag2.contains("INSERT INTO specialcharacters VALUES ('%%%%%%%%%%%%%%%%%%', '^^^^^^^^^^^^^^^^^^^^^');") && sFlag3.contains("INSERT INTO specialcharacters VALUES ('!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!', '$$$$$$$$$$$$$$$$$$$$');") &&
							sFlag4.contains("INSERT INTO specialcharacters VALUES ('@@@@@@@@@@@@', '**********');")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for table with special characters. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_015")){

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D,1);
					String sPath = GlobalConstants.exportDDlPath + "diffdatatype.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL"); 

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE TABLE");
					String sFlag2 = UtilityFunctions.searchFile(sPath, "VALUES (3, true, 'ghi', 'c')");
					String sFlag3 = UtilityFunctions.searchFile(sPath, "VALUES (5, true, 'mno', 'e')");
					String sFlag4 = UtilityFunctions.searchFile(sPath, "VALUES (4, false, 'jkl', 'd')");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("diffdatatype") && 
							sFlag2.contains("INSERT INTO diffdatatype VALUES (3, true, 'ghi', 'c');") && sFlag3.contains("INSERT INTO diffdatatype VALUES (5, true, 'mno', 'e');") &&
							sFlag4.contains("INSERT INTO diffdatatype VALUES (4, false, 'jkl', 'd');")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed"); 
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for table with different data types. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_016")){     //Testcase mapped SDV_FUN_VAL_DS_Export_DDL_019

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_M, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_M,2);
					String sPath = GlobalConstants.exportDDlPath + "maxdatalength.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL"); 

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE TABLE");
					String sFlag2 = UtilityFunctions.searchFile(sPath, "VALUES (9223372036854775806)");
					String sFlag3 = UtilityFunctions.searchFile(sPath, "VALUES (9223372036854775807)");


					if(sFlag.contains("Successfully exported") && sFlag1.contains("maxdatalength") && 
							sFlag2.contains("INSERT INTO maxdatalength VALUES (9223372036854775806);") && sFlag3.contains("INSERT INTO maxdatalength VALUES (9223372036854775807);")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed"); 
					}

					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for table with maximum data length. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_017")){

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_M, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_M,2);
					String sPath = GlobalConstants.exportDDlPath + "maxdatalength.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);

					if(BaseActions.WinExists(ObjectBrowserElements.wFileOverwrite)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						BaseActions.Click(ObjectBrowserElements.wFileOverwrite, "", ObjectBrowserElements.bFileOverwriteYes);
						Thread.sleep(GlobalConstants.MedWait);
						BaseActions.Click(ObjectBrowserElements.wExportFinish, "", ObjectBrowserElements.bExportFinishOK);
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Message is not shown for overwriting the file when exported again. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_018")){

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					String sPath = GlobalConstants.exportDDlPath + "tablewithspecialchars.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);

					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE TABLE");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("Table with Caps,Initchars,****"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for table with Capital letters, white spaces, special characters, etc. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_006")){

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					String sPath = GlobalConstants.exportDDlPath + "systemtable.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);

					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE TABLE");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("pg_aggregate"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for system table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_001")){

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 5);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 5);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 2);
					String sPath = GlobalConstants.exportDDlPath + "largetable1.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");

					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_F5, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F5, 1);

					String sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");

					if(sFlag1.contains("refreshed")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to perform other operations when export is in progress. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_008")){

					String sQuery = "INSERT INTO auto.auto_largedata VALUES (21,'a12','chn');";
					sFlag = QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag.equals("Success")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to perform other insert operation in the same table when export is in progress. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_009")){

					String sQuery = "INSERT INTO utl_raw.columnstore VALUES (13, 14);";
					sFlag = QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Winwait(ObjectBrowserElements.wExportFinish);
					BaseActions.Click(ObjectBrowserElements.wExportFinish, "",ObjectBrowserElements.bExportFinishOK );

					if(sFlag.equals("Success")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to perform other insert operation in other table when export is in progress. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_004")){

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E,1);
					String sPath = GlobalConstants.exportDDlPath + "emptytable.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);

					sFlag = UtilityFunctions.searchFile(sPath, "INSERT");



					if(!sFlag.contains("INSERT")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"INSERT query present in empty table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_003")){

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E,1);
					String sPath = GlobalConstants.exportDDlPath + "emptytable.csv";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);

					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");



					if(sFlag.contains(".sql")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Export DDL could be saved in a format other than sql. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_006")){

					ObjectBrowserPane.schemaDDLNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E,1);
					String sPath = GlobalConstants.exportDDlPath + "emptytable.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Winwait(ObjectBrowserElements.wFileOverwrite);
					BaseActions.Click(ObjectBrowserElements.wFileOverwrite, "", "Button2");


					if(!BaseActions.WinExists(ObjectBrowserElements.wExportFinish)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"File overwrite operation performed even after cancelling it. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_005")){

					ObjectBrowserPane.CreateTable("utl_raw", "droptable");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					ObjectBrowserPane.DropTable("utl_raw", "droptable");
					String sPath = GlobalConstants.exportDDlPath + "droppedtable.sql";
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_U,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D,2);
					sFlag = ObjectBrowserPane.exportDDL(sPath, "Yes");
					Thread.sleep(GlobalConstants.MedWait);

					if(!sFlag.equalsIgnoreCase("Failed")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Export DDL works for dropped table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);

					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						BaseActions.Click(ObjectBrowserElements.wExportFailed, "", ObjectBrowserElements.bExportFailedOK);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_030")){         //Testcase mapped SDV_FUN_VAL_DS_Export_DDL_029

					String sQuery = "CREATE or replace VIEW utl_raw.sameschemaview1 AS Select * from utl_raw.columnstore ;";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "sameschemaview1.sql";
					ObjectBrowserPane.schemaDDLNavigation();

					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S,1);
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("sameschemaview1"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view from a table in same schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_031")){        

					String sQuery = "CREATE or replace VIEW utl_raw.multipleschemaview1  AS Select * from pg_catalog.all_all_tables ;";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "multipleschemaview1.sql";
					ObjectBrowserPane.schemaDDLNavigation();

					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_M, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_M,1);
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("multipleschemaview1"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view from a table in other schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_032")){        

					String sQuery = "CREATE or replace VIEW utl_raw.sameschemaview2  AS Select * from utl_raw.sameschemaview1 ;";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "sameschemaview2.sql";
					ObjectBrowserPane.schemaDDLNavigation();

					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S,2);
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("sameschemaview2"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view from a view in same schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_033")){        

					String sQuery = "CREATE OR REPLACE VIEW utl_raw.multipleschemaview2  AS Select * from public.viewname ;";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "multipleschemaview2.sql";
					ObjectBrowserPane.schemaDDLNavigation();

					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_M, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_M,2);
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("multipleschemaview2"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view from a view in another schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_034")){        

					String sQuery = "CREATE OR REPLACE VIEW utl_raw.view_anotherview_anothertable  AS SELECT a.ename, b.dept FROM updatetable a, viewname b WHERE (a.eid = b.eid);";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "view_anotherview_anothertable.sql";
					ObjectBrowserPane.schemaDDLNavigation();

					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("view_anotherview_anothertable"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view from a view, table in another schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_037")){

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
					String sPath = GlobalConstants.exportDDlPath + "systemview.sql";
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MedWait);

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("all_all_tables"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view from a system view. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_038")){           //Test case mapped SDV_FUN_INVAL_DS_Export_DDL_014      

					String sQuery = "CREATE OR REPLACE view utl_raw.viewWithoutTable as Select 'Hello world';";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "viewwithouttable.sql";
					ObjectBrowserPane.schemaDDLNavigation();

					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,2);
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("viewwithouttable"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view created without table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_039")){        

					String sQuery = "Create or replace view utl_raw.columnsview as Select ename, eid from auto.auto_largedata;";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "columnsview.sql";
					ObjectBrowserPane.schemaDDLNavigation();

					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C,1);
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("columnsview"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view created with sub-set of columns. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_040")){        

					String sQuery = "CREATE or replace VIEW utl_raw." + "\"View with @special***chars!!#$&^%\"" + "AS SELECT * FROM auto.auto_largedata;";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "specialcharsview.sql";
					ObjectBrowserPane.schemaDDLNavigation();

					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("View with @special***chars!!#$&^%"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view created with special characters, init caps, white spaces,etc. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

					String dropQuery = "DROP VIEW utl_raw." + "\"View with @special***chars!!#$&^%\"";
					QueryEditor.SingleQueryExe(dropQuery,"Valid");
					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MedWait);


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_041")){        

					String sQuery = "CREATE OR REPLACE VIEW utl_raw.complexview AS SELECT a.ename,b.dept "
							+ "FROM auto.auto_largedata a , public.auto_largedata b WHERE a.eid = b.eid;";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "complexview.sql";
					ObjectBrowserPane.schemaDDLNavigation();

					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 2);			
					UtilityFunctions.KeyRelease(KeyEvent.VK_C,2);
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("complexview"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view created with complex query. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_042")){        

					String sQuery = "CREATE OR REPLACE VIEW utl_raw.chineseview /*'文件','文件*/ AS SELECT * FROM auto.auto_largedata;/*'文件','文件*/;";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "chineseview.sql";
					ObjectBrowserPane.schemaDDLNavigation();

					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C,1);
					ViewFunctions.exportDDL(sPath);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE VIEW");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("chineseview"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for view created with Chinese comments. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_016")){

					String sQuery = "ALTER view utl_raw.chineseview SET SCHEMA public;";
					String sDropQuery = "DROP view public.chineseview;";
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe(sDropQuery,"Valid");
					ObjectBrowserPane.ObjectBrowserRefresh();
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "movedview.sql";
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_U,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C,1);
					sFlag = ViewFunctions.exportDDL(sPath);
					Thread.sleep(GlobalConstants.MedWait);


					if(sFlag.equalsIgnoreCase("Failed")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Export DDL works although the view is not available in the schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_044")){


					Thread.sleep(GlobalConstants.MedWait);
					String sPath = GlobalConstants.exportDDlPath + "movedview.sql";
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P,2);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C,1);
					ViewFunctions.exportDDL(sPath);
					Thread.sleep(GlobalConstants.MedWait);

					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "Schema");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("Schema: public;"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Export DDL doesnot show the updated schema name on moving the view to another schema . Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_036")){

					ObjectBrowserPane.ObjectBrowserRefresh();
					String sQuery = "create temp view tempview as select * from public.largedata;";
					String sDropQuery = "drop view tempview;";
					String sPath = GlobalConstants.exportDDlPath + "tempview.sql";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P,2);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					ViewFunctions.exportDDL(sPath);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
					QueryEditor.SingleQueryExe(sDropQuery,"Valid");
					ObjectBrowserPane.ObjectBrowserRefresh();
					ViewFunctions.Temp_Schema_Delete();
					Thread.sleep(GlobalConstants.MedWait);
					ViewFunctions.Temp_Schema_Delete();


					if(sFlag.contains("Successfully exported"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for temp table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_017")){

					String sQuery = "create or replace view utl_raw.renameview as select * from public.auto_largedata;";
					String renameQuery = "ALTER VIEW utl_raw.renameview RENAME TO renamedview;";
					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);
					ObjectBrowserPane.ObjectBrowserRefresh();
					String sPath = GlobalConstants.exportDDlPath + "renamedview.sql";
					QueryEditor.SingleQueryExe(renameQuery, "Valid");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_U,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_R,1);
					sFlag = ViewFunctions.exportDDL(sPath);
					Thread.sleep(GlobalConstants.MedWait);

					if(sFlag.equalsIgnoreCase("Failed")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Export DDL works although the view is renamed and is not available in the schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_INVAL_DS_Export_DDL_015")){

					String sQuery = "Drop view utl_raw.renamedview;";

					QueryEditor.SingleQueryExe(sQuery,"Valid");
					Thread.sleep(GlobalConstants.MedWait);

					String sPath = GlobalConstants.exportDDlPath + "droppedview.sql";

					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.objectBrowserExpansion("SINGLE");
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
					Thread.sleep(GlobalConstants.MinWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_U,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_V, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_V,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_R,1);
					sFlag = ViewFunctions.exportDDL(sPath);
					Thread.sleep(GlobalConstants.MedWait);

					if(sFlag.equalsIgnoreCase("Failed")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Export DDL works although the view is dropped and is not available in the schema. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}

				}

				if(sTestCaseID.equals("SDV_FUN_VAL_DS_Export_DDL_047")){             //Test case mapped SDV_FUN_VAL_DS_Export_DDL_046

					UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
					ObjectBrowserPane.ObjectBrowserRefresh();
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 8);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 8);
					String sPath = GlobalConstants.exportDDlPath + "function.sql";
					ObjectBrowserPane.exportDDL(sPath, "Yes"); 
					Thread.sleep(GlobalConstants.MedWait);

					sFlag = QueryResult.ReadConsoleOutput("GLOBAL");

					String sFlag1 = UtilityFunctions.searchFile(sPath, "CREATE");

					if(sFlag.contains("Successfully exported") && sFlag1.contains("CREATE OR REPLACE FUNCTION auto.auto1()"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
					}
					else
					{
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+3,5,"Unable to export DDL for PLSQL Function/Procedure . Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}


				}
			}




		}





		for(int i=1;i<=44;i++)
		{
			sTestCaseID=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,2);
			sStatus=UtilityFunctions.GetExcelCellValue(ResultExcel,sARNumber,i+2,4);
			String sFinalStatus = sTestCaseID+" "+sStatus;
			if(!sStatus.isEmpty())
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
		}

	}

}
