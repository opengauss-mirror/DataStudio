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
import java.io.*;

import object_repository.*;
import script_library.*;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class SR_V1R2_DS_001_STR_2_SVE_OPN_SQL
{

	public SR_V1R2_DS_001_STR_2_SVE_OPN_SQL()
	{
	}

	public static void main(String sARNumber)
			throws Exception
			{
		String ResultExcel = UtilityFunctions.CreateResultFile("FunctionalTest", "Save_Open_SQL");
		String sTextResultFile = UtilityFunctions.CreateTextResultFile("FunctionalTest", "Save_Open_SQL");
		int iRowCount = UtilityFunctions.GetRowCount(GlobalConstants.sFunctionalTestDataFile, sARNumber);
		for(int i = 1; i <= iRowCount; i++)
		{
			String sExecute = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 2);
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
			if(sExecute.equals("Yes"))
			{
				String sTestCaseID = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 1);
				String sInputQuery = UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, sARNumber, i, 3);
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_valid_1PTS_SR.V1R2.DS.001_STR_2_Usability_Test_1"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 3, "Yes");
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.SetQuery(sInputQuery);
					File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("ToolBar.sql").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("TOOLBAR", "ToolBar.sql", "SAVE");
					String sToolbar;
					if(file.exists())
					{
						sToolbar = "Success";
					} else
					{
						sToolbar = "Failure";
					}
					file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("ShortCut.sql").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("SHORTCUT", "ShortCut.sql", "SAVE");
					String sShortCut;
					if(file.exists())
					{
						sShortCut = "Success";
					} else
					{
						sShortCut = "Failure";
					}
					file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("Menu.sql").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("MENU", "Menu.sql", "SAVE");
					String sMenu;
					if(file.exists())
					{
						sMenu = "Success";
					} else
					{
						sMenu = "Failure";
					}
					file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("ContextMenu.sql").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("CONTEXTMENU", "ContextMenu.sql", "SAVE");
					String sContextMenu;
					if(file.exists())
					{
						sContextMenu = "Success";
					} else
					{
						sContextMenu = "Failure";
					}
					if(sToolbar.equals("Success") && sShortCut.equals("Success") && sMenu.equals("Success") && sContextMenu.equals("Success"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Passed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 2, 5, (new StringBuilder("Error occured while saving query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Error occured while saving query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_valid_3"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.SetQuery(sInputQuery);
					File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("Overwrite.sql").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("SHORTCUT", "Overwrite.sql", "SAVE");
					QueryEditor.SaveQuery("SHORTCUT", "Overwrite.sql", "NOTHING");
					BaseActions.Click(ExpQueryElements.wSQLSaveasWindow, "", ExpQueryElements.sSaveButton);
					Thread.sleep(500);
					if(BaseActions.WinExists(ExpQueryElements.wFileOverwriteWindow))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						BaseActions.Click(ExpQueryElements.wFileOverwriteWindow, "", ExpQueryElements.bYesButton);
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Overwrite Confirmation pop up window is not displayed. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_valid_4"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					ObjectBrowserPane.CreateFunctionProcedure("public", "testauto");
					Thread.sleep(1000);
					File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("function.sql").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("SHORTCUT", "function.sql", "SAVE");
					if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully saved to"))
					{
						if(file.exists())
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Error occured while saving query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 4, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 4, 5, (new StringBuilder("Success message is not displayed on saving the query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_valid_5"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "pg_am.sql", "OPEN", "OVERWRITE");
					String sExpectedQuery = QueryEditor.CopyEditor();
					if(sExpectedQuery.equals("select * from pg_am;"))
					{
						if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully loaded to SQL Terminal."))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Success message is not displayed in Console. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Error occured while opening query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_valid_6"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.SetQuery(sInputQuery);
					String sFileName = (new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("SaveandUpdate.sql").toString();
					File file = new File(sFileName);
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("SHORTCUT", "SaveandUpdate.sql", "SAVE");
					Thread.sleep(GlobalConstants.MedWait.intValue());
					Writer output = new FileWriter(sFileName, true);
					output.append("select * from pg_aggregate;");
					output.append("\r\n");
					output.close();
					QueryEditor.OpenQuery("SHORTCUT", "NEW", "SaveandUpdate.sql", "OPEN", "OVERWRITE");
					String sExpectedQuery = QueryEditor.CopyEditor();
					if(sExpectedQuery.contains("select * from pg_aggregate;") || sExpectedQuery.contains("select * from pg_am;"))
					{
						if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully loaded to SQL Terminal."))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Success message is not displayed in Console. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Error occured while opening modified query file. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_valid_10"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "pg_am.sql", "OPEN", "NOTHING");
					if(BaseActions.WinExists(ExpQueryElements.wSQLAppendWindow))
					{
						UtilityFunctions.KeyPress(10, 1);
						UtilityFunctions.KeyRelease(10, 1);
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Append/Overwrite Window is not displayed. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_Invalid_1"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.SetQuery("");
					UtilityFunctions.KeyPress(17, 1);
					UtilityFunctions.KeyPress(83, 1);
					UtilityFunctions.KeyRelease(17, 1);
					UtilityFunctions.KeyRelease(83, 1);
					String sSave;
					if(BaseActions.WinExists(ExpQueryElements.wSQLSaveasWindow))
					{
						sSave = "Fail";
					} else
					{
						sSave = "Pass";
					}
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "blank.sql", "OPEN", "OVERWRITE");
					String sOpen;
					if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully loaded to SQL Terminal."))
					{
						sOpen = "Pass";
					} else
					{
						sOpen = "Fail";
					}
					if(sSave.equals("Pass") && sOpen.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Save/Open operations on blank sql failed. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_Invalid_2"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.SetQuery(sInputQuery);
					File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("junk.sql").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("SHORTCUT", "junk.sql", "SAVE");
					if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully saved to"))
					{
						if(file.exists())
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Error occured while saving query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Success message is not displayed on saving the query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_Invalid_3"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.SetQuery(sInputQuery);
					File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("invalid.txt").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("SHORTCUT", "invalid.txt", "SAVE");
					String sSave;
					if(BaseActions.WinExists(ExpQueryElements.wInvalidSQLWindow))
					{
						sSave = "Pass";
						BaseActions.Click(ExpQueryElements.wInvalidSQLWindow, "", ExpQueryElements.sSaveOKButton);
					} else
					{
						sSave = "Fail";
					}
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "invalid.txt", "OPEN", "OVERWRITE");
					String sOpen;
					if(BaseActions.WinExists(ExpQueryElements.wSQLAppendWindow))
					{
						sOpen = "Pass";
						BaseActions.Click(ExpQueryElements.wSQLAppendWindow, "", ExpQueryElements.sOpenOKButton);
					} else
					{
						sOpen = "Fail";
					}
					if(sSave.equals("Pass") && sOpen.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Save/Open operations on invalid extension files failed. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_Invalid_4"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.SetQuery(sInputQuery);
					QueryEditor.SaveQuery("SHORTCUT", "", "SAVE");
					String sSave;
					if(BaseActions.WinExists(ExpQueryElements.wSQLSaveasWindow))
					{
						sSave = "Pass";
						BaseActions.Click(ExpQueryElements.wSQLSaveasWindow, "", ExpQueryElements.sCancelButton);
					} else
					{
						sSave = "Fail";
					}
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "", "OPEN", "OVERWRITE");
					String sOpen;
					if(BaseActions.WinExists(ExpQueryElements.wFileOpenWindow))
					{
						sOpen = "Pass";
						BaseActions.Click(ExpQueryElements.wFileOpenWindow, "", ExpQueryElements.sCancelButton);
					} else
					{
						sOpen = "Fail";
					}
					if(sSave.equals("Pass") && sOpen.equals("Pass"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Save/Open operations on blank file names failed. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("SDV_V1R2_Save_Load_Functional_Valid_011"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "chinese_comments_open.sql", "OPEN", "OVERWRITE");
					String sExpectedQuery = QueryEditor.CopyEditor();
					if(sExpectedQuery.contains("建立客户地区中间表-去重set表"))
					{
						if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully loaded to SQL Terminal."))
						{
							File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("chinese_comments_save.sql").toString());
							if(file.exists())
							{
								file.delete();
							}
							QueryEditor.SaveQuery("SHORTCUT", "chinese_comments_save.sql", "SAVE");
							if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully saved to"))
							{
								if(file.exists())
								{
									UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
								} else
								{
									UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
									UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Error occured while saving query with Chinese comments. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
									UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
								}
							} else
							{
								UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
								UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Success message is not displayed on saving the query with Chinese comments. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Success message is not displayed in Console while opening query with Chinese comments. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Error occured while opening query with Chinese comments. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("SDV_V1R2_Save_Load_Functional_InValid_005"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.OpenQuery("SHORTCUT", "EXISTING", "not_exist.sql", "OPEN", "NOTHING");
					if(BaseActions.WinExists(ExpQueryElements.wSQLAppendWindow))
					{
						String sText = BaseActions.ControlGetText(ExpQueryElements.wSQLAppendWindow, "", "Static2");
						BaseActions.Click(ExpQueryElements.wSQLAppendWindow, "", ExpQueryElements.sOpenOKButton);
						if(sText.contains("Selected file does not exist. Please select correct file."))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
						}else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("File does not exist message in warning window is not displayed. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}

					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("File does not exist Window is not displayed. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}


				if(sTestCaseID.equals("SDV_V1R2_Save_Load_Functional_Valid_012"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					QueryEditor.SetQuery(sInputQuery);
					File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("µØÇøºÅ.sql").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("SHORTCUT", "µØÇøºÅ.sql", "SAVE");
					if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully saved to"))
					{
						QueryEditor.OpenQuery("SHORTCUT", "NEW", "µØÇøºÅ.sql", "OPEN", "OVERWRITE");
						String sExpectedQuery = QueryEditor.CopyEditor();
						if(sExpectedQuery.equals(sInputQuery))	
						{
							if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully loaded to SQL Terminal."))
							{
								UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
							} else
							{
								UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
								UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Success message is not displayed in Console. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
								UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
							}
						}else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Error occured while opening query with Chinese file name. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Error occured while saving query with Chinese file name. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}

				if(sTestCaseID.equals("PTS_SR.V1R2.DS.001_STR_2_Functional_valid_7SDV_V1R2_Save_Load_Functional_Valid_013SDV_V1R2_Save_Load_Functional_Valid_014"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 4, 3, "Yes");
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 5, 3, "Yes");
					QueryEditor.SetQuery(sInputQuery);
					File file = new File((new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append("SaveandLoad.sql").toString());
					if(file.exists())
					{
						file.delete();
					}
					QueryEditor.SaveQuery("SHORTCUT", "SaveandLoad.sql", "SAVE");
					QueryEditor.OpenQuery("SHORTCUT", "NEW", "SaveandLoad.sql", "OPEN", "OVERWRITE");
					String sExpectedQuery = QueryEditor.CopyEditor();
					if(sExpectedQuery.equals(sInputQuery))
					{
						if(QueryResult.ReadConsoleOutput("GLOBAL").contains("SQL successfully loaded to SQL Terminal."))
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Passed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 4, 4, "Passed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 5, 4, "Passed");
						} else
						{
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 4, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 5, 4, "Failed");
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Success message is not displayed in Console. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 4, 5, (new StringBuilder("Success message is not displayed in Console. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 4, 5, (new StringBuilder("Success message is not displayed in Console. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
							UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
						}
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 4, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 5, 4, "Failed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 5, (new StringBuilder("Error occured while opening query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 4, 5, (new StringBuilder("Error occured while opening query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 5, 5, (new StringBuilder("Error occured while opening query. Please refer screenshot ")).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					}
				}
				if(sTestCaseID.contains("PTS_SR.V1R2.DS.001_STR_2_Usability_Test_3"))
				{
					UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 3, 3, "Yes");
					Thread.sleep(GlobalConstants.MedWait);
					DebugOperations.RemoveConnection();
					QueryEditor.SetQuery(sInputQuery);
					BaseActions.MouseClick(ExpQueryElements.wToolbarSQLWindow, "", ExpQueryElements.sToolbarSQLControlID, ExpQueryElements.sButton, ExpQueryElements.nclicks, ExpQueryElements.iToolbarSQLExportxcord, ExpQueryElements.iToolbarSQLExportycord);
					Thread.sleep(GlobalConstants.MedWait);
					String sToolbarSave;
					if(BaseActions.WinExists(ExpQueryElements.wSQLSaveasWindow))
					{
						sToolbarSave = "Pass";
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 2);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 2);
					} else
					{
						sToolbarSave = "Fail";
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					Thread.sleep(GlobalConstants.MedWait);
					String sShortCutSave;
					if(BaseActions.WinExists(ExpQueryElements.wSQLSaveasWindow))
					{
						sShortCutSave = "Pass";
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 2);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 2);
					} else
					{
						sShortCutSave = "Fail";
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_F,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S,1);
					Thread.sleep(GlobalConstants.MedWait);
					String sMenuSave;
					if(BaseActions.WinExists(ExpQueryElements.wSQLSaveasWindow))
					{
						sMenuSave = "Pass";
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 2);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 2);
					} else
					{
						sMenuSave = "Fail";
					}
					BaseActions.Focus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
					Thread.sleep(GlobalConstants.MedWait);
					String sContextMenuSave;
					if(BaseActions.WinExists(ExpQueryElements.wSQLSaveasWindow))
					{
						sContextMenuSave = "Pass";
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 2);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 2);
					} else
					{
						sContextMenuSave = "Fail";
					}
					BaseActions.MouseClick(ExpQueryElements.wToolbarSQLWindow, "", ExpQueryElements.sToolbarSQLControlID, ExpQueryElements.sButton, ExpQueryElements.nclicks, ExpQueryElements.iToolbarSQLOpenxcord, ExpQueryElements.iToolbarSQLOpenycord);
					String sToolbarOpen;
					if(BaseActions.WinExists(ExpQueryElements.wFileOpenWindow))
					{
						sToolbarOpen = "Pass";
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 2);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 2);
					} else
					{
						sToolbarOpen = "Fail";
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_O, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_O, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
					Thread.sleep(GlobalConstants.MedWait);
					String sShortCutOpen;
					if(BaseActions.WinExists(ExpQueryElements.wFileOpenWindow))
					{
						sShortCutOpen = "Pass";
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 2);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 2);
					} else
					{
						sShortCutOpen = "Fail";
					}
					UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_F,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_F,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
					UtilityFunctions.KeyPress(KeyEvent.VK_O,1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_O,1);
					Thread.sleep(GlobalConstants.MedWait);
					String sMenuOpen;
					if(BaseActions.WinExists(ExpQueryElements.wFileOpenWindow))
					{
						sMenuOpen = "Pass";
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 2);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 2);
					} else
					{
						sMenuOpen = "Fail";
					}
					BaseActions.Focus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
					UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
					UtilityFunctions.KeyPress(KeyEvent.VK_O, 1);
					UtilityFunctions.KeyRelease(KeyEvent.VK_O, 1);
					String sContextMenuOpen;
					if(BaseActions.WinExists(ExpQueryElements.wFileOpenWindow))
					{
						sContextMenuOpen = "Pass";
						UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 2);
						UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 2);
					} else
					{
						sContextMenuOpen = "Fail";
					}
					System.out.println(sShortCutSave);
					System.out.println(sMenuSave);
					System.out.println(sToolbarSave);
					System.out.println(sContextMenuSave);
					System.out.println(sShortCutOpen);
					System.out.println(sMenuOpen);
					System.out.println(sToolbarOpen);
					System.out.println(sContextMenuOpen);
					if(sShortCutSave.equals("Fail") || sMenuSave.equals("Fail") || sToolbarSave.equals("Fail") || sContextMenuSave.equals("Fail") || sShortCutOpen.equals("Fail") || sMenuOpen.equals("Fail") || sToolbarOpen.equals("Fail") || sContextMenuOpen.equals("Fail"))
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 5, 4, "Passed");
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 5, 5, (new StringBuilder("Save/Open operations are enabled even after db is disconnected. Please refer scr" +
								"eenshot "
								)).append(sTestCaseID).append(".jpg").toString());
						UtilityFunctions.TakeScreenshot(sTestCaseID, ResultExcel);
					} else
					{
						UtilityFunctions.WriteToExcel(ResultExcel, sARNumber, i + 5, 4, "Failed");
					}
				}
			}
		}

		for(int i = 1; i <= 18; i++)
		{
			String sTestCaseID = UtilityFunctions.GetExcelCellValue(ResultExcel, sARNumber, i + 2, 2);
			String sStatus = UtilityFunctions.GetExcelCellValue(ResultExcel, sARNumber, i + 2, 4);
			String sFinalStatus = (new StringBuilder(String.valueOf(sTestCaseID))).append(" ").append(sStatus).toString();
			if(!sStatus.isEmpty())
			{
				UtilityFunctions.WriteToText(sTextResultFile, sFinalStatus);
			}
		}

			}
}
