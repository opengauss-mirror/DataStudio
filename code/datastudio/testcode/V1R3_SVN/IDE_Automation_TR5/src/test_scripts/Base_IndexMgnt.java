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

import object_repository.ConsoleResultElements;
import object_repository.CreateTableWizardElements;
import object_repository.GlobalConstants;
import object_repository.TablespaceElements;
import script_library.CreateTableWizardFunctions;
import script_library.ObjectBrowserPane;
import script_library.QueryEditor;
import script_library.QueryResult;
import script_library.TablespaceFunctions;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import autoitx4java.AutoItX;

public class Base_IndexMgnt {

	public static void main(String sARNumber) throws Exception {

		//Creating the Test Result File for Reporting
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest","Base_IndexMgnt");
		//Creating the Test Result File for TMSS
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest","Base_IndexMgnt");
		//Variable Declarations	
		String sTestCaseID,sExecute,sFlag,sFlag1,sStatus;
		//Getting the total number of test cases from data sheet
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);

		String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
		
		Thread.sleep(GlobalConstants.MedWait);
		TablespaceFunctions.tablespace_Navigation();
		Thread.sleep(GlobalConstants.MinWait);
		TablespaceFunctions.openTableSpace();
		BaseActions.Winwait(TablespaceElements.wTablespaceTitle);
		TablespaceFunctions.tablespaceCreation("NAME", "test");
		TablespaceFunctions.tablespaceCreation("LOCATION", "/home/"+sUserName+"/test/demo1/demo2/DEMO3/AUTO1/AUto2/AUTO3");
		Thread.sleep(GlobalConstants.MedWait);
		TablespaceFunctions.maxSize("1000", 'M');
		TablespaceFunctions.button("OK");
		BaseActions.Winwait("Tablespace Successfully Created");
		UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);

		for(int i=1;i<=iRowCount;i++)
		{
			//Validate the Execute flag from data sheet and execute the test case based on the Execute flag
			sExecute=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i,2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);

			if(sExecute.equalsIgnoreCase("Yes")){

				sTestCaseID=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "Base_IndexMgnt", i,1);
				UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,3,"Yes");

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateColumn_111_812")){

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Test Col Umn", "Yes");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);

					if(sFlag.contains("Test Col Umn")&&sFlag1.contains("[INFO] Table created successfully.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create column quoted option. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateColumn_110_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("Test Col Umn", "Yes");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);

					if(sFlag.contains("Test Col Umn")&&sFlag1.contains("[INFO] Table created successfully.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create column quoted option. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateColumn_109_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("TestColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("_TestColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("TestColumn1", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppa", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);

					if(sFlag.contains("TestColumn")&&sFlag.contains("_TestColumn")&&sFlag.contains("TestColumn1")&&
							sFlag.contains("ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppa")&&
							sFlag1.contains("[INFO] Table created successfully.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create column with integer,underscore and long name. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");


				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateColumn_116_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("TestColumn1", "No");
					CreateTableWizardFunctions.DataType("CHAR");
					CreateTableWizardFunctions.Precision("4");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("TestColumn2", "No");
					CreateTableWizardFunctions.DataType("NUMERIC");
					CreateTableWizardFunctions.Precision("5");
					CreateTableWizardFunctions.Scale("4");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("TestColumn3", "No");
					CreateTableWizardFunctions.DataType("DECIMAL");
					CreateTableWizardFunctions.Precision("6");
					CreateTableWizardFunctions.Scale("5");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.AddCloumn("TestColumn4", "No");
					CreateTableWizardFunctions.DataType("VARCHAR");
					CreateTableWizardFunctions.Precision("9");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);

					if(sFlag.contains("TestColumn1 char(4)")&&sFlag.contains("TestColumn2 numeric(5,4)")&&sFlag.contains("TestColumn3 decimal(6,5)")&&
							sFlag.contains("TestColumn4 varchar(9)")&&sFlag1.contains("[INFO] Table created successfully.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create column with valid values for precision and scale. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateColumn_115_812")){

					AutoItX x = new AutoItX();
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("TestColumn1", "No");
					CreateTableWizardFunctions.DataType("VARCHAR");
					Thread.sleep(GlobalConstants.MinWait);
					boolean b1 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sScaleControlID);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.DataType("CHAR");
					Thread.sleep(GlobalConstants.MinWait);
					boolean b2 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sScaleControlID);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.DataType("BOOLEAN");
					Thread.sleep(GlobalConstants.MinWait);
					boolean b3 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sPrecisionControlID);
					boolean b4 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sScaleControlID);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.DataType("INTEGER");
					Thread.sleep(GlobalConstants.MinWait);
					boolean b5 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sPrecisionControlID);
					boolean b6 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sScaleControlID);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.DataType("TEXT");
					Thread.sleep(GlobalConstants.MinWait);
					boolean b7 = x.controlCommandIsEnabled(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sScaleControlID);
					Thread.sleep(GlobalConstants.MedWait);

					if(b1==false && b2==false && b3==false && b4==false && b5==false && b6==false && b7==false){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Precision and scale are editable for the datatypes which are not applicable. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_Create Constraints_123_812")){             //Test case mapped GaussIDE_FUNC_CreateIndex_141_812

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("TestColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("NEXT");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("NEXT");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.TableConstraint("UNIQUE");
					CreateTableWizardFunctions.ConstraintName("_UNIQUE11$aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumns);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					CreateTableWizardFunctions.selectConstraintColumn();
					CreateTableWizardFunctions.ConstraintButton("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableConstraint("CHECK");
					CreateTableWizardFunctions.ConstraintName("_CHECK11$Caaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", "SysListView326");
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", "Button25");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.ConstraintButton("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableConstraint("PRIMARY_KEY");
					CreateTableWizardFunctions.ConstraintName("_PRIMARY11$Paaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumns);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					CreateTableWizardFunctions.selectConstraintColumn();
					CreateTableWizardFunctions.ConstraintButton("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);

					if(sFlag.contains("CONSTRAINT _CHECK11$Caaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")&&sFlag.contains("CONSTRAINT _UNIQUE11$aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")&&
							sFlag.contains("CONSTRAINT _PRIMARY11$Paaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")&&
							sFlag1.contains("[INFO] Table created successfully.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create table with constraints containing special chars. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateColumn_112_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.ArrayDim("1");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.ArrayDim("2");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("ThirdColumn", "No");
					CreateTableWizardFunctions.ArrayDim("10");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("FourthColumn", "No");
					CreateTableWizardFunctions.ArrayDim("100");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					if(sFlag.contains("FirstColumn bigint[]")&&sFlag.contains("SecondColumn bigint[][]")&&
							sFlag.contains("ThirdColumn bigint[][][][][][][][][][]")&&
							sFlag.contains("FourthColumn")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create table with the given Array dimensions. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_Create Column_117_812")){  //Testcase mapped GaussIDE_FUNC_Create Column_118_812

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Columnconstarints("NOTNULL");
					CreateTableWizardFunctions.Columnconstarints("UNIQUE");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag.contains("FirstColumn bigint NOT NULL UNIQUE")&&
							sFlag1.contains("[INFO] Table created successfully.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}

					else{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create table with not null and unique column constraints. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateIndex_143_812")){      //Testcase mapped GaussIDE_FUNC_CreateIndex_147_812 and GaussIDE_SMOKE_CreateIndex_151_812

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("ThirdColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("FourthColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("FifthColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SixthColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sIndexControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					CreateTableWizardFunctions.indexName("index1");
					CreateTableWizardFunctions.selectAccessMethodIndex("btree");
					CreateTableWizardFunctions.indexFillFactor("10");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(1);
					CreateTableWizardFunctions.indexName("index2");
					CreateTableWizardFunctions.selectAccessMethodIndex("gin");
					CreateTableWizardFunctions.indexFillFactor("10");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(2);
					CreateTableWizardFunctions.indexName("index3");
					CreateTableWizardFunctions.selectAccessMethodIndex("gist");
					CreateTableWizardFunctions.indexFillFactor("10");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(3);
					CreateTableWizardFunctions.indexName("index4");
					CreateTableWizardFunctions.selectAccessMethodIndex("hash");
					CreateTableWizardFunctions.indexFillFactor("10");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(4);
					CreateTableWizardFunctions.indexName("index5");
					CreateTableWizardFunctions.selectAccessMethodIndex("psort");
					CreateTableWizardFunctions.indexFillFactor("10");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(5);
					CreateTableWizardFunctions.indexName("index6");
					CreateTableWizardFunctions.selectAccessMethodIndex("spgist");
					CreateTableWizardFunctions.indexFillFactor("10");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(6);
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bFinishcontrolID);

					if(BaseActions.WinExists(CreateTableWizardElements.wTitle)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create table with not null and unique column constraints. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateIndex_144_812")){          // user defined index "test" should be created

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sIndexControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					CreateTableWizardFunctions.indexName("index1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexTableSpace("test");
					CreateTableWizardFunctions.addColumnIndex(1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					CreateTableWizardFunctions.checkIndexObjectBrowser();

					if(sFlag.contains("FirstColumn")&&
							sFlag1.contains("[INFO] Table created successfully.") &&
							BaseActions.WinExists(CreateTableWizardElements.wRenameIndexWindow)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create table with index on user defined tablespaces. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateIndex_145_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sIndexControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					CreateTableWizardFunctions.indexName("index1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(1);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("index2");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(1);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("index3");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(2);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					CreateTableWizardFunctions.checkIndexObjectBrowser();

					if(sFlag.contains("FirstColumn")&&
							sFlag1.contains("[INFO] Table created successfully.") &&
							BaseActions.WinExists(CreateTableWizardElements.wRenameIndexWindow)){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create table with index on user defined tablespaces. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateIndex_142_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sIndexControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					CreateTableWizardFunctions.indexName("index1");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bUniqueIndex);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(1);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexName("index2");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(1);
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag1= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					boolean b1 = CreateTableWizardFunctions.checkIndexObjectBrowser();
					UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					boolean b2 = CreateTableWizardFunctions.checkIndexObjectBrowser();

					if(sFlag.contains("FirstColumn")&&sFlag.contains("SecondColumn")&&
							sFlag1.contains("[INFO] Table created successfully.") &&
							b1==true && b2==true){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create table with index on user defined tablespaces. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_EditIndex_169_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.SetText("Change Fill Factor", "", "Edit1", "10");
					BaseActions.Click("Change Fill Factor", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.SetText("Change Fill Factor", "", "Edit1", "100");
					BaseActions.Click("Change Fill Factor", "", "Button1");
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag.contains("[INFO] Changed 'index1' index fill factor to '10'.") &&
							sFlag.contains("[INFO] Changed 'index2' index fill factor to '100'.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to change fill factor of the indexes. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					BaseActions.ClearConsole("GLOBAL");

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_EditIndex_168_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Tablespace", "", "ComboBox1");
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Tablespace", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Tablespace", "", "ComboBox1");
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Tablespace", "", "Button1");
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag.contains("[INFO] Changed index1 index tablespace to pg_default .") &&
							sFlag.contains("[INFO] Changed index2 index tablespace to test .")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to change tablespace of the indexes. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					BaseActions.ClearConsole("GLOBAL");
				}



				if(sTestCaseID.equals("GaussIDE_SMOKE_DropIndex_157_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					BaseActions.Winwait("Drop Index");
					BaseActions.Click("Drop Index", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					BaseActions.Winwait("Drop Index");
					BaseActions.Click("Drop Index", "", "Button1");
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag.contains("successfully dropped.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to drop the indexes. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateINdex_025_716_986_812")){   //Test case mapped GaussIDE_SMOKE_EditIndex_154_812,  GaussIDE_FUNC_CreateINdex_256_151_550_568_812, GaussIDE_FUNC_CcrtRenameIndex_170_812

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sIndexControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					CreateTableWizardFunctions.indexName("index1");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bUniqueIndex);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(1);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
					BaseActions.Winwait(CreateTableWizardElements.wRenameIndexWindow);
					BaseActions.SetText(CreateTableWizardElements.wRenameIndexWindow, "", "SWT_Window03", "IndexRenamed");
					BaseActions.Click(CreateTableWizardElements.wRenameIndexWindow, "", "Button1");
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Tablespace", "", "ComboBox1");
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Tablespace", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.SetText("Change Fill Factor", "", "Edit1", "10");
					BaseActions.Click("Change Fill Factor", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.indexIconNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					BaseActions.Winwait("Drop Index");
					BaseActions.Click("Drop Index", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Renamed index1 index to IndexRenamed.") &&
							sFlag.contains("[INFO] Changed indexrenamed index tablespace to pg_default .") &&
							sFlag.contains("[INFO] Changed 'indexrenamed' index fill factor to '10'.") &&
							sFlag.contains("successfully dropped.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to change fill factor of the indexes. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}
					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_Create Column_120_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.DataType("TEXT");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.DataType("TEXT");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					BaseActions.Winwait("Table: auto.testtable, Column:firstcolumn");
					BaseActions.SetText("Table: auto.testtable, Column:firstcolumn", "", "SWT_Window03", "Default Value");
					BaseActions.Click("Table: auto.testtable, Column:firstcolumn", "", "Button1");
					BaseActions.Click("Table: auto.testtable, Column:firstcolumn", "", "Button2");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Setting column default value of firstcolumn column successful ")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set default value of the column with quoted string box enabled. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					BaseActions.ClearConsole("GLOBAL");

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_Create Column_121_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					BaseActions.Winwait("Table: auto.testtable, Column:secondcolumn");
					BaseActions.SetText("Table: auto.testtable, Column:secondcolumn", "", "SWT_Window03", "Default Value");
					BaseActions.Click("Table: auto.testtable, Column:secondcolumn", "", "Button2");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Setting column default value of secondcolumn column successful ")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to set default value of the column with quoted string box disabled. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");

					}

					else {

						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 2);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 2);
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");

					}
					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateIndex_149_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.MouseClick(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sIndexControlID, "left", 1, CreateTableWizardElements.xIndexCord, CreateTableWizardElements.yIndexCord);
					CreateTableWizardFunctions.indexName("index1");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sPartialIndex, "FirstColumn>5");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.addColumnIndex(1);
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("[INFO] Table created successfully.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create table with partial index. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_Create Constraints_130_812")){   //Test case mapped GaussIDE_FUNC_Create Constraints_131_812, GaussIDE_FUNC_Create Constraints_129_812 and GaussIDE_FUNC_Create Constraints_132_812

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("ThirdColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("FourthColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("FifthColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.Button("NEXT");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableConstraint("PRIMARY_KEY");

					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumns);

					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bSelectColumn);

					for(int j=0; j<4;j++){

						Thread.sleep(GlobalConstants.MedWait);
						BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumns);
						UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
						BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bSelectColumn);

					}
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.constraintTableSpace("test");
					CreateTableWizardFunctions.tableFillFactor("10");
					CreateTableWizardFunctions.tableDeffered("DEFERABLE");
					CreateTableWizardFunctions.tableDeffered("INDEFERABLE");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintAdd);
					sFlag1 = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MedWait);

					if(sFlag.contains("[INFO] Table created successfully.") && 
							sFlag1.contains("PRIMARY KEY (FirstColumn, SecondColumn, ThirdColumn, FourthColumn, FifthColumn)WITH (fillfactor=10)USING INDEX TABLESPACE test DEFERRABLE INITIALLY DEFERRED")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create table with Unique constraints. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_Create Constraints_126_812")){          //Test case mapped GaussIDE_FUNC_Create Constraints_125_812,GaussIDE_FUNC_Create Constraints_127_812 and GaussIDE_FUNC_Create Constraints_128_812

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("ThirdColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("FourthColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("FifthColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.Button("NEXT");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableConstraint("UNIQUE");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumns);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bSelectColumn);

					for(int j=0; j<4;j++){

						Thread.sleep(GlobalConstants.MedWait);
						BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumns);
						UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
						UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
						Thread.sleep(GlobalConstants.MedWait);
						BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bSelectColumn);

					}

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.constraintTableSpace("test");
					CreateTableWizardFunctions.tableFillFactor("10");
					CreateTableWizardFunctions.tableDeffered("DEFERABLE");
					CreateTableWizardFunctions.tableDeffered("INDEFERABLE");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintAdd);
					sFlag1 = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MedWait);

					if(sFlag.contains("[INFO] Table created successfully.") && 
							sFlag1.contains("UNIQUE (FirstColumn, SecondColumn, ThirdColumn, FourthColumn, FifthColumn)WITH (fillfactor=10)USING INDEX TABLESPACE test DEFERRABLE INITIALLY DEFERRED")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Added constraints are not getting reflected in the constraints window.. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");

				}

				if(sTestCaseID.equals("GaussIDE_SMOKE_CreateConstraints_152_812")){         //Test case mapped GaussIDE_FUNC_CreateINdex_256_151_550_812, GaussIDE_FUNCERR_constraints_183_812

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.DataDistribution("REPLICATION");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableConstraint("CHECK");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sConstraintNameControlID, "CHECK_CONSTRAINT");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", "SysHeader326");
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", "Button25");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintAdd);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableConstraint("UNIQUE");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sConstraintNameControlID, "UNIQUE_CONSTRAINT");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumns);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bSelectColumn);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintAdd);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.TableConstraint("PRIMARY_KEY");
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.SetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sConstraintNameControlID, "PRIMARY_KEY_CONSTRAINT");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sAvailableColumns);
					Thread.sleep(GlobalConstants.MedWait);
					UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
					Thread.sleep(GlobalConstants.MedWait);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bSelectColumn);
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.bconstraintAdd);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MinWait);
					if(sFlag.contains("[INFO] Table created successfully.") && 
							sFlag1.contains("CONSTRAINT CHECK_CONSTRAINT CHECK ( SecondColumn )") && 
							sFlag1.contains("CONSTRAINT UNIQUE_CONSTRAINT UNIQUE (SecondColumn)") &&
							sFlag1.contains("CONSTRAINT PRIMARY_KEY_CONSTRAINT PRIMARY KEY (SecondColumn)")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to add all the possible constraints to the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateINdex_025_719_812")){             //Test case mapped GaussIDE_FUNC_CcrtRenameIndex_177_812, GaussIDE_FUNC_CreateINdex_256_151_812, GaussIDE_SMOKE_EditColumn_153_812, GaussIDE_SMOKE_DropColumn_156_812

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
					BaseActions.Winwait("Rename Column");
					BaseActions.SetText("Rename Column", "", "SWT_Window03", "FirstColumnRenamed");
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					BaseActions.Winwait("Toggle Not Null Property");
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					BaseActions.Winwait("Change Data Type");
					BaseActions.Click("Change Data Type", "", "ComboBox2");
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Data Type", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					BaseActions.Winwait("Drop Column");
					BaseActions.Click("Drop Column", "", "Button1");
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MedWait);

					if(sFlag.contains("Renamed") && 
							sFlag.contains("Toggling NOT NULL property of firstcolumnrenamed column successfu") &&
							sFlag.contains("Changing data type for the column 'firstcolumnrenamed' in the table \"auto\".\"testtable\" is successful.") &&
							sFlag.contains("Drop Column \"testtable\".\"firstcolumnrenamed\" Successful.")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to add all the possible constraints to the table. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					BaseActions.ClearConsole("GLOBAL");

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateINdex_025_716_986_385_812")){    //Test case mapped GaussIDE_SMOKE_DropConstraints_158_812, GaussIDE_SMOKE_EditConstraint_155_812, GaussIDE_FUNC_CcrtRenameIndex_171_812, GaussIDE_FUNC_CreateINdex_257_812

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
					BaseActions.Winwait("Rename Constraint");
					BaseActions.SetText("Rename Constraint", "", "SWT_Window03", "Renamed_Check_Constraint");
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
					BaseActions.Winwait("Drop Constraint");
					BaseActions.Click("Drop Constraint", "", "Button1");
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MedWait);

					if(sFlag.contains("Renamed") && 
							sFlag.contains("Dropping")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to rename and drop the constraints. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_SMOKE_CreateColumn_150_812")){     //Test case mapped GaussIDE_FUNC_CreateColumn_114_812, GaussIDE_FUNC_EditColumn_167_812

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					CreateTableWizardFunctions.DataType("INTEGER");
					String firstDesc = BaseActions.ControlGetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTypeDescription);
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					CreateTableWizardFunctions.DataType("VARCHAR");
					String secondDesc = BaseActions.ControlGetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTypeDescription);
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait); 
					CreateTableWizardFunctions.AddCloumn("ThirdColumn", "No");
					CreateTableWizardFunctions.DataType("DECIMAL");
					String thirdDesc = BaseActions.ControlGetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTypeDescription);
					CreateTableWizardFunctions.Button("ADD");
					CreateTableWizardFunctions.AddCloumn("FourthColumn", "No");
					CreateTableWizardFunctions.DataType("TEXT");
					String fourthDesc = BaseActions.ControlGetText(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sTypeDescription);
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MinWait);
					CreateTableWizardFunctions.Button("NEXT");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.DataDistribution("REPLICATION");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1 = CreateTableWizardFunctions.SQLPreviewCopy();
					CreateTableWizardFunctions.Button("FINISH");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					Thread.sleep(GlobalConstants.MedWait);
					if(sFlag.contains("[INFO] Table created successfully.") && 
							sFlag1.contains("FirstColumn integer") && firstDesc.equals("-2 billion to 2 billion integer, 4-byte storage") &&
							sFlag1.contains("SecondColumn varchar") && secondDesc.equals("varchar(length), non-blank-padded string, variable storage length") &&
							sFlag1.contains("ThirdColumn decimal") && thirdDesc.equals("numeric(precision, decimal), arbitrary precision number") &&
							sFlag1.contains("FourthColumn text") && fourthDesc.equals("variable-length string, no limit specified")){

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to add columns of all the data types. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_EditColumn_164_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					BaseActions.Winwait("Change Data Type");
					BaseActions.Click("Change Data Type", "", "ComboBox2");
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Data Type", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					BaseActions.Winwait("Change Data Type");
					BaseActions.Click("Change Data Type", "", "ComboBox2");
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Data Type", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					BaseActions.Winwait("Change Data Type");
					BaseActions.Click("Change Data Type", "", "ComboBox2");
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Data Type", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_F, 2);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F, 2);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					BaseActions.Winwait("Change Data Type");
					BaseActions.Click("Change Data Type", "", "ComboBox2");
					UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Data Type", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe("insert into auto.testtable values ('a','b','c','d');", "valid");
					sFlag1= QueryResult.ReadConsoleOutput("TERMINAL");
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains(" Changing data type for the column 'firstcolumn' in the table \"auto\".\"testtable\" is successful.") && 
							sFlag.contains("Changing data type for the column 'secondcolumn' in the table \"auto\".\"testtable\" is successful.") && 
							sFlag.contains("Changing data type for the column 'thirdcolumn' in the table \"auto\".\"testtable\" is successful.") &&
							sFlag.contains("Changing data type for the column 'fourthcolumn' in the table \"auto\".\"testtable\" is successful.")&&
							sFlag1.contains("Executed Successfully..."))
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to add columns of all the data types. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", CreateTableWizardElements.sTableName);
					BaseActions.ClearConsole("GLOBAL");
				}

				if(sTestCaseID.equals("GaussIDE_FUNC_EditIndex_169_691_812")){

					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe("create table auto.t1 ( f1 int ) DISTRIBUTE BY REPLICATION;", "valid");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe("create index idx on auto.t1(f1 );", "valid");
					Thread.sleep(GlobalConstants.MedWait);
					QueryEditor.SingleQueryExe("Alter table auto.t1 rename f1 to c1;", "valid");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					BaseActions.Winwait("Toggle Not Null Property");
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.autoColumnsNavigation();
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
					BaseActions.Winwait("Change Data Type");
					BaseActions.Click("Change Data Type", "", "ComboBox2");
					UtilityFunctions.KeyPress(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_T, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
					BaseActions.Click("Change Data Type", "", "Button1");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag1= QueryResult.ReadConsoleOutput("TERMINAL");
					sFlag= QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("Toggling NOT NULL property of c1 column successful") && 
							sFlag.contains("Changing data type for the column 'c1' in the table \"auto\".\"t1\" is successful."))
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to add columns of all the data types. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", "t1");
					BaseActions.ClearConsole("GLOBAL");

				}

				if(sTestCaseID.equals("GaussIDE_FUNC_CreateColumn_113_812")){

					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.openCreateTableWizard();
					BaseActions.Winwait(CreateTableWizardElements.wTitle);
					CreateTableWizardFunctions.TableName(CreateTableWizardElements.sTableName, "No");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("NEXT");
					CreateTableWizardFunctions.AddCloumn("FirstColumn", "No");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeSchemaControlID);
					CreateTableWizardFunctions.DataType("INTEGER");
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("SecondColumn", "No");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeSchemaControlID);
					UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("ThirdColumn", "No");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeSchemaControlID);
					UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.AddCloumn("FourthColumn", "No");
					BaseActions.Click(CreateTableWizardElements.wTitle, "", CreateTableWizardElements.sDatatypeSchemaControlID);
					CreateTableWizardFunctions.Button("ADD");
					Thread.sleep(GlobalConstants.MedWait);
					sFlag = CreateTableWizardFunctions.SQLPreviewCopy();
					Thread.sleep(GlobalConstants.MedWait);
					CreateTableWizardFunctions.Button("FINISH");
					sFlag1 = QueryResult.ReadConsoleOutput("GLOBAL");
					if(sFlag.contains("FirstColumn integer") && sFlag.contains("SecondColumn pg_catalog._abstime") &&
							sFlag.contains("ThirdColumn information_schema._pg_foreign_data_wrappers") &&
							sFlag.contains("FourthColumn bigint") &&
							sFlag1.contains("Table created successfully."))
					{

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Passed");
						System.out.println(sTestCaseID + "passed");
					}

					else {

						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,4,"Failed");
						UtilityFunctions.WriteToExcel(ResultExcel,sARNumber,i+2,5,"Unable to create table for different schemas. Please refer screenshot "+sTestCaseID+".jpg");
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						System.out.println(sTestCaseID + "failed");
					}

					ObjectBrowserPane.DropTable("auto", "testtable");
					BaseActions.ClearConsole("GLOBAL");

				}
				
			}

		}
		
		Thread.sleep(GlobalConstants.MedWait);
		QueryEditor.SingleQueryExe("DROP tablespace test", "Valid");

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
