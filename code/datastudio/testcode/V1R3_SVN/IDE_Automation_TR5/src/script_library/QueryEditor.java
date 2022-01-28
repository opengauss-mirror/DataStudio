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
TITLE - QUERY EDITOR
DESCRIPTION - FUNCTIONS WITH RESPECT TO QUERY EDITOR
MODIFICATION HISTORY - 
TEST CASES COVERED - NA
 *************************************************************************/
package script_library;

import java.awt.event.KeyEvent;

import object_repository.ConsoleResultElements;
import object_repository.ExecQueryElements;
import object_repository.ExpQueryElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.ObjectBrowserElements;
import object_repository.PlanElements;
import object_repository.TablePropertyElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import autoitx4java.AutoItX;

public class QueryEditor {

	public static void SetQuery(String sQuery) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: SetQuery()
	DESCRIPTION	 		: Function to set the query in Query Editor
	IN PARAMETERS		: sQuery{Input Query to be entered in Query Editor}
	RETURN PARAMETERS	: None
		 *************************************************************************/
		AutoItX x = new AutoItX();
		BaseActions.ClearConsole("TERMINAL");
		x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
		//Selecting the query for execution using key board actions
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sQuery);	
	}

	public static void SetMultiLineQuery(String[] sQuery) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: SetQuery()
	DESCRIPTION	 		: Function to set the query in Query Editor
	IN PARAMETERS		: sQuery{Input Query to be entered in Query Editor}
	RETURN PARAMETERS	: None
		 *************************************************************************/
		AutoItX x = new AutoItX();
		BaseActions.ClearConsole("Basic");
		x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
		//Selecting the query for execution using key board actions
		QueryEditor.ClearEditor();
		int iSize = sQuery.length;
		for(int i=0;i<iSize;i++)
			QueryEditor.SetFunction(sQuery[i]);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
	}


	public static void SetFunction(String sLine) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: SetFunction()
		DESCRIPTION	 		: Function to set the multiline function
		IN PARAMETERS		: sLine{Input Line for the functions}
		RETURN PARAMETERS	: None
		 *************************************************************************/
		AutoItX x = new AutoItX();
		x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
		x.send(sLine);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}

	public static void SmokePreCondition() throws Exception{
		/*************************************************************************
		FUNCTION NAME		: SmokePreCondition()
		DESCRIPTION	 		: Function to create a function ide smoke
		IN PARAMETERS		: None
		 *************************************************************************/
		QueryEditor.DropDatabase();
		QueryEditor.SingleQueryExe("DROP FUNCTION auto.auto1;", "Valid");
		ObjectBrowserPane.DropSchema("auto");
		ObjectBrowserPane.CreateSchema("auto");
		ObjectBrowserPane.CreateFunctionProcedure("auto","auto1");
	}

	public static void AutoFillPre() throws Exception{
		/*************************************************************************
		FUNCTION NAME		: AutoFillPre()
		DESCRIPTION	 		: Function to make auto fill precondition
		IN PARAMETERS		: None
		 *************************************************************************/
		ObjectBrowserPane.DropTable("autoschema", "emp");
		ObjectBrowserPane.DropSchema("autoschema");
		ObjectBrowserPane.CreateSchema("autoschema");
		ObjectBrowserPane.CreateTable("autoschema", "emp");
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);
		UtilityFunctions.KeyPress(KeyEvent.VK_F5, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F5, 1);
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);
	}


	public static void ExecuteButton() throws Exception
	{

		//BaseActions.Click(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sExeButton);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}


	public static String SingleQueryExe(String sQuery, String sQueryType) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: SingleQueryExe()
		DESCRIPTION	 		: Function to execute single query from Query Editor
		IN PARAMETERS		: sQuery{Input Query to be entered in Query Editor}
							: sQueryType{Type of Query - Valid/Invalid}
		RETURN PARAMETERS	: sFlag{returns Success if query execution is success}
							: sConsoleOutput(returns console output if execution fails)
		 *************************************************************************/
		String sFlag,sConsoleOutput;
		Thread.sleep(GlobalConstants.MinWait);
		/*BaseActions.ClearConsole("Basic");
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
		Thread.sleep(GlobalConstants.MinWait);*/
		QueryEditor.SetQuery(sQuery);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		if(sQuery.contains("DATABASE"))
			Thread.sleep(6000);
		if(sQuery.contains("SCHEMA")||sQuery.contains("TABLE"))
			Thread.sleep(2000);
		sConsoleOutput = QueryResult.ReadConsoleOutput("TERMINAL");
		if (sQueryType.equals("Valid"))
		{
			if(sConsoleOutput.contains("Executed Successfully.."))
				sFlag = "Success";
			else
				sFlag = sConsoleOutput;
			return sFlag;
		}
		else
			return sConsoleOutput;
	}

	public static void QueryFormat(String sQuery,String sFormatType) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: QueryFormat()
	DESCRIPTION	 		: Function to format Query based on user input
	IN PARAMETERS		: sQuery{Input Query to be entered in Query Editor}
						: sFormatType{Type of formatting to be performed}
	RETURN PARAMETERS	: None
		 *************************************************************************/
		QueryEditor.SetQuery(sQuery);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		Thread.sleep(GlobalConstants.MedWait);
		switch(sFormatType)
		{
		case "Enter" :
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		case "Shortcut" :
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
		case "Menu" :
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_E, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_E, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
		case "Toolbar" :
			BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sToolbarControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
					ObjectBrowserElements.iToolbarFormatxcord, ObjectBrowserElements.iToolbarFormatycord);
		}
		Thread.sleep(GlobalConstants.MedWait);
	}

	public static String MultipleQueryExe(String sQuery,String sQueryType,String sExeType, String sQueryCount,String sQuerySelection) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: MultipleQueryExe()
	DESCRIPTION	 		: Function to execute multiple queries
	IN PARAMETERS		: sQuery{Input Query to be entered in Query Editor}
						: sQueryType{Type of Query - Valid/Invalid}
	 					: sExeType{Type of execution to be performed}
	 					: sQueryCount{Number of queries to be executed}
	 					: sQuerySelection{Type of Query Selection - Full/Partial/None}
	RETURN PARAMETERS	: sFlag{returns Success if query execution is success}
						: sConsoleOutput(returns console output if execution fails)
		 *************************************************************************/
		AutoItX x = new AutoItX();
		String sFlag=null;
		int iQueryCount = Integer.valueOf(sQueryCount);
		BaseActions.ClearConsole("Basic");
		BaseActions.SetText(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, sQuery);
		if(sQuerySelection.equals("Full"))
		{
			x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		}
		else if(sQuerySelection.equals("Partial"))
		{
			//x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
			BaseActions.Click(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
			Thread.sleep(GlobalConstants.MedWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
			Thread.sleep(GlobalConstants.MedWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
			Thread.sleep(GlobalConstants.MedWait);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
			Thread.sleep(GlobalConstants.MedWait);
		}
		else if(sQuerySelection.equals("Error"))
		{
			PlanCost.PlanCostClick();
			BaseActions.Check(PlanElements.wExceutionTitle, "", "Button1");
			BaseActions.Click(PlanElements.wExceutionTitle, "",PlanElements.sOkButtonControlID);
			/*BaseActions.MouseClick(PlanElements.wToolbarTitle, "", PlanElements.sToolbarControlID, PlanElements.sbutton, PlanElements.iclick, PlanElements.ixcord, PlanElements.iycord);
			BaseActions.Check(PlanElements.wExceutionTitle, "", "Button3");
			BaseActions.Click(PlanElements.wExceutionTitle, "",PlanElements.sOkButtonControlID);*/
			Thread.sleep(GlobalConstants.MedWait);
		}
		else
		{
			x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
		}

		if (sExeType.equals("ToolBar"))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_X, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_X, 1);
		}
		else
		{
			BaseActions.Click(ExecQueryElements.wSQLTerminal,"",ExecQueryElements.sExeButton);
		}
		Thread.sleep(GlobalConstants.MedWait);
		if (sQueryType.equals("Error"))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
			String sConsoleOutput = QueryResult.ReadConsoleOutput("TERMINAL");
			sFlag = sConsoleOutput;
		}
		else
		{
			String sConsoleOutput = QueryResult.ReadConsoleOutput("TERMINAL");
			if (sQueryType.equals("Valid"))
			{
				if(sConsoleOutput.contains("Executed Successfully.."))
				{
					int LastIndex = 0;
					int Count = 0;
					String sTarget = "Executed Successfully..";
					while (LastIndex != -1)
					{
						LastIndex = sConsoleOutput.indexOf(sTarget, LastIndex);
						if(LastIndex!=-1)
						{
							Count++;
							LastIndex += sTarget.length();	
						}
					}
					if(Count >= iQueryCount)
						sFlag = "Success";
					else
						sFlag = sConsoleOutput;
				}
				else
					sFlag = sConsoleOutput;
			}
			else if(sQueryType.equals("Continuous"))
			{
				//BaseActions.Click(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
				BaseActions.MouseClick(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor, "left", 1, 350, 10);
				Thread.sleep(GlobalConstants.MedWait);
				x.send(sQuery);
				Thread.sleep(GlobalConstants.MedWait);
				BaseActions.Click(ExecQueryElements.wSQLTerminal,"",ExecQueryElements.sExeButton);
				Thread.sleep(GlobalConstants.MedWait);
				sConsoleOutput = QueryResult.ReadConsoleOutput("TERMINAL");
				if(sConsoleOutput.contains("Execution failed."))
					sFlag = sConsoleOutput;
				else
					sFlag = "Success";
			}
			else
				sFlag = sConsoleOutput;	
		}
		return sFlag;
	}

	public static String QueryFormatValidation(String sExpectedQuery, String sQuery) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: QueryFormatValidation()
	DESCRIPTION	 		: Function to format Query based on user input
	IN PARAMETERS		: sQuery{Input Query to be entered in Query Editor}
						: sFormatType{Type of formatting to be performed}
	RETURN PARAMETERS	: sFlag{returns Mismatch if there is a mismatch}
		 *************************************************************************/	
		String sActualFormatedQuery, sFlag=null;
		AutoItX x = new AutoItX();
		x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
		x.controlClick(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor,"left",2,34,8);
		UtilityFunctions.KeyPress(KeyEvent.VK_END, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_END, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_END, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_END, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_END, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_END, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
		sActualFormatedQuery = UtilityFunctions.GetClipBoard();
		//for (int FieldIndex=0; FieldIndex<sExpectedQuery.length; FieldIndex++)
		//{
		//if (sActualFormatedQuery.contains(sExpectedQuery[FieldIndex]))
		if (sActualFormatedQuery.equals(sExpectedQuery))
		{
		}
		else
		{
			sFlag = "Mismatch";
		}
		//}
		return sFlag;
	}	

	public static String AutoSuggestFillValidation() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: AutoSuggestFillValidation()
	DESCRIPTION	 		: Function to get the query after Auto Suggest and Auto Fill
	IN PARAMETERS		: None
	RETURN PARAMETERS	: sActualQuery{returns the actual query after auto suggest/auto fill}
		 *************************************************************************/
		String sActualQuery;
		sActualQuery = UtilityFunctions.GetClipBoard();
		return sActualQuery;
	}

	public static void ClearEditor() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: AutoSuggestFillValidation()
	DESCRIPTION	 		: Function to get the query after Auto Suggest and Auto Fill
	IN PARAMETERS		: None
	RETURN PARAMETERS	: sActualQuery{returns the actual query after auto suggest/auto fill}
		 *************************************************************************/
		AutoItX x = new AutoItX();
		x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
	}

	public static void DropDatabase() throws Exception
	{
		/*************************************************************************
	FUNCTION NAME : DropDatabase()
	DESCRIPTION : Function to drop all the databases other than postgres
	IN PARAMETERS : None
	RETURN PARAMETERS : 
		 *************************************************************************/
		AutoItX x = new AutoItX();
		String sQuery= "select datname from pg_database where datname not in ('template1','template0','postgres');";
		QueryEditor.SingleQueryExe(sQuery, "Normal");
		BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iResultxcord, ConsoleResultElements.iResultycord);
		BaseActions.MouseClick("Data Studio", "", "ToolbarWindow326", "left", 1, 559, 10);
		String DBName = x.clipGet(); 
		String DBNameSpliter[] = DBName.split("\n");
		for(String SplitedValues: DBNameSpliter){
			sQuery = "DROP DATABASE "+SplitedValues;
			QueryEditor.SingleQueryExe(sQuery, "Normal");
		}
	}
	public static void SaveQuery(String sOption, String sFileName, String sButton) throws Exception
	{
		/*************************************************************************
	FUNCTION NAME : SaveQuery()
	DESCRIPTION : Function to Save Query via different methods
	IN PARAMETERS : None
	RETURN PARAMETERS : 
		 *************************************************************************/
		AutoItX x = new AutoItX();
		sFileName = GlobalConstants.sSaveSQLPath+sFileName;
		switch(sOption)
		{
		default:
			break;
		case "TOOLBAR":
		{
			BaseActions.MouseClick(ExpQueryElements.wToolbarSQLWindow, "", ExpQueryElements.sToolbarSQLControlID, ExpQueryElements.sButton, ExpQueryElements.nclicks, ExpQueryElements.iToolbarSQLExportxcord, ExpQueryElements.iToolbarSQLExportycord);
			break;
		}
		case "MENU": 
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_F,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_F,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_S,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_S,1);
			
			break;
		}
		case "SHORTCUT": 
		{ 
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_S,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_S,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
			break;
		}
		case "CONTEXTMENU": 
		{
			BaseActions.Focus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_S,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_S,1);
			break;
		}
		} 
		Thread.sleep(GlobalConstants.MinWait);
		if(BaseActions.WinExists(ConsoleResultElements.wDisclaimerWindow))
		{
			BaseActions.Click(ConsoleResultElements.wDisclaimerWindow, "", ConsoleResultElements.bOk);
		}
		UtilityFunctions.KeyPress(127, 1);
		UtilityFunctions.KeyRelease(127, 1);
		BaseActions.SetText(ExpQueryElements.wSQLSaveasWindow, "", ExpQueryElements.sSQLSaveasControlID, sFileName);
		if(sButton.equalsIgnoreCase("SAVE"))
		{
			BaseActions.Click(ExpQueryElements.wSQLSaveasWindow, "", ExpQueryElements.sSaveButton);
		} else
			if(sButton.equalsIgnoreCase("OVERWRITE"))
			{
				BaseActions.Click(ExpQueryElements.wSQLSaveasWindow, "", ExpQueryElements.sSaveButton);
				Thread.sleep(GlobalConstants.MedWait);
				if(x.winExists("File Overwrite Confirmation."))
				{
					UtilityFunctions.KeyPress(10, 1);
					UtilityFunctions.KeyRelease(10, 1);
				}	
			} else
				if(sButton.equalsIgnoreCase("CANCEL"))
				{
					BaseActions.Click(ExpQueryElements.wSQLSaveasWindow, "", ExpQueryElements.sCancelButton);
				} else
				{
					sButton.equalsIgnoreCase("NOTHING");
				}
		Thread.sleep(GlobalConstants.MedWait);
	}

	public static void OpenQuery(String sOption, String sFileType, String sFileName, String sButton, String sAppendorOverwrite) throws Exception
	{
		/*************************************************************************
	FUNCTION NAME : OpenQuery()
	DESCRIPTION : Function to Open Query via different methods
	IN PARAMETERS : None
	RETURN PARAMETERS : 
		 *************************************************************************/
		if(sFileType.equals("NEW"))
		{
			sFileName = (new StringBuilder(String.valueOf(GlobalConstants.sSaveSQLPath))).append(sFileName).toString();
		} else
		{
			sFileName = (new StringBuilder(String.valueOf(GlobalConstants.sOpenSQLPath))).append(sFileName).toString();
		}
		switch(sOption)
		{
		default:
			break;
		case "TOOLBAR": 
		{
			BaseActions.MouseClick(ExpQueryElements.wToolbarSQLWindow, "", ExpQueryElements.sToolbarSQLControlID, ExpQueryElements.sButton, ExpQueryElements.nclicks, ExpQueryElements.iToolbarSQLOpenxcord, ExpQueryElements.iToolbarSQLOpenycord);
			break;
		}
		case "MENU": 
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_F,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_F,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_O,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_O,1);
			break;
		}
		case "SHORTCUT": 
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_O, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_O, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
			break;
		}
		case "CONTEXTMENU": 
		{
			BaseActions.Focus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_O, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_O, 1);
			break;
		}
		}
		BaseActions.Winwait("Open");
		//Thread.sleep(GlobalConstants.MedWait);
		BaseActions.SetText(ExpQueryElements.wFileOpenWindow, "", ExpQueryElements.sSQLOpenControlID, sFileName);
		if(sButton.equalsIgnoreCase("OPEN"))
		{
			BaseActions.Click(ExpQueryElements.wFileOpenWindow, "", ExpQueryElements.sOpenButton);
			Thread.sleep(GlobalConstants.MedWait.intValue());
			switch(sAppendorOverwrite)
			{
			default:
				break;
			case "NOTHING": 
				break;
			case "APPEND": 
			{
				BaseActions.Click(ExpQueryElements.wSQLAppendWindow, "", ExpQueryElements.sAppendButton);
				break;
			}
			case "OVERWRITE": 
			{
				BaseActions.Click(ExpQueryElements.wSQLAppendWindow, "", ExpQueryElements.sOverWriteButton);
				break;
			}
			}
		}
		else
		{
			BaseActions.Click(ExpQueryElements.wFileOpenWindow, "", ExpQueryElements.sCancelButton);
		}
	}
	public static void CancelQuery(String sOption) throws Exception
	{
		switch (sOption.toUpperCase())
		{
		case "BUTTON":BaseActions.Click(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sCancelButton);
		BaseActions.Winwait("Cancel Operation");
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		break;

		case "CONTEXTMENU":BaseActions.Focus(ExecQueryElements.wSQLTerminal, "",ExecQueryElements.sSQLEditor);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Winwait("Cancel Operation");
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		break;
		case "SHORTCUT":
			UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);

		}
	}
	public static void SelectConnection() throws Exception
	{
		//BaseActions.Focus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sConnection);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.MouseClick(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sConnCombo, ExecQueryElements.sButton,ExecQueryElements.iClick, ExecQueryElements.ixcord,ExecQueryElements.iycord);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}
	public static void SelectDBConnection() throws Exception
	{
		//BaseActions.Focus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sConnection);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.MouseClick(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sConnCombo, ExecQueryElements.sButton,ExecQueryElements.iClick, ExecQueryElements.ixcord,ExecQueryElements.iycord);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}

	public static void SQLEditorMinMax(String sOption) throws Exception
	{
		switch (sOption) {
		case "MIN":
			BaseActions.MouseClick("Data Studio", "", "ToolbarWindow3211", "left", 1, 10, 7);
			break;
		case "MAX":
			BaseActions.MouseClick("Data Studio", "", "ToolbarWindow3215", "left", 1, 10, 11);
			break;

		default:
			break;
		}

	}

	public static void CancelImportExport(String sOption) throws Exception
	{
		BaseActions.Focus("Data Studio", "", "msctls_progress321");
		BaseActions.MouseClick("Data Studio", "", "msctls_progress321", "left", 2, 65, 12);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sCancel);
		if(sOption.equalsIgnoreCase("Yes"))
			BaseActions.Click(ExecQueryElements.wCancelWindow, "", ExecQueryElements.sOKButton);
		else
			BaseActions.Click(ExecQueryElements.wCancelWindow, "", ExecQueryElements.sNoButton);


	}

	public static void AutoSuggestInvoke() throws Exception{
		/*************************************************************************
		FUNCTION NAME : AutoSuggestInvoke()
		DESCRIPTION : Function to invoke auto suggest by pressing CTRL and SPACE together
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
		Thread.sleep(GlobalConstants.MedWait);
	}

	public static void CloseActiveEditor() throws Exception{
		/*************************************************************************
		FUNCTION NAME : AutoSuggestCopy()
		DESCRIPTION : Function to copy the auto suggested values
		IN PARAMETERS : None
		RETURN PARAMETERS : sContents{returns auto suggested values}
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F4, 1);
	}
	public static String CopyEditor()
			throws Exception
			{
		AutoItX x = new AutoItX();
		x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
		return UtilityFunctions.GetClipBoard();
			}

	public static String AutoSuggestCopy() throws Exception{
		/*************************************************************************
		FUNCTION NAME : AutoSuggestCopy()
		DESCRIPTION : Function to copy the auto suggested values
		IN PARAMETERS : None
		RETURN PARAMETERS : sContents{returns auto suggested values}
		 *************************************************************************/
		AutoItX x = new AutoItX();
		String sContents;
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_U, 1);
		Thread.sleep(GlobalConstants.MedWait);
		sContents = (x.clipGet());
		return sContents;
	}
	public static void SetTerminalFocus() throws Exception{
		/*************************************************************************
		FUNCTION NAME : SetTerminalFocus()
		DESCRIPTION : Function to set focus on SQL Terminal
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		 *************************************************************************/
		AutoItX x = new AutoItX();
		x.controlClick(ExecQueryElements.wSQLTerminal, "",ExecQueryElements.sEditorHeader);
		x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
		x.controlClick(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
	}

	public static String objPropertyTerminalInvoke(String sQuery, String sType, int iTerminalNumber) throws Exception
	{
		Thread.sleep(1000);
		String sContent, sFlag;
		BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
		if(iTerminalNumber == 1)
			QueryEditor.SetQuery(sQuery);
		else
			MultipleTerminal.TerminalSetText(2, "autotableproperty.testtable");
		//BaseActions.ControlMouseClick("left", 552, 160, 1,50);
		//BaseActions.ControlMouseClick("left", 612, 135, 1,50);
		BaseActions.ControlMouseClick("left", 549, 128, 1,50);
		/*String [] sVals = sQuery.split("\\.");
	String sObjectName = sVals[1];*/
		if(sType.equals("Table"))
		{
			//if(BaseActions.WinExists(sObjectName+" Properties"))
			if(BaseActions.WinExists(TablePropertyElements.sPropertywindow))
				sFlag = "Pass";
			else
				sFlag = "Fail";
		}
		else
		{
			if(BaseActions.ObjExists("Data Studio","","[CLASS:SWT_Window0]"))
			{
				BaseActions.Click("Data Studio","","[CLASS:SWT_Window0]");
				sContent = UtilityFunctions.GetClipBoard();
				if(sContent.contains(sQuery))
					sFlag = "Pass";
				else
					sFlag = "Fail";
			}
			else
				sFlag="Fail";
		}
		return sFlag;

	}

	public static String objPropertyFunctionInvoke(String sType) throws Exception
	{
		BaseActions.CloseActiveTerminal();
		BaseActions.CloseActiveTerminal();
		ObjectBrowserPane.objPropertyFunctionOpen();
		String sFlag;
		AutoItX x = new AutoItX();
		if(sType.equals("Table"))
		{
			//Table Open from Function
			BaseActions.ControlMouseClick("left", 661, 302, 1,50);
			//BaseActions.ControlMouseClick("left", 752, 302, 1,50);
			BaseActions.SetAutoItOption("WinTitleMatchMode", "2");
			if(BaseActions.WinExists(TablePropertyElements.sPropertywindow))
				sFlag = "Pass";
			else
				sFlag = "Fail";
		}
		else
		{
			BaseActions.ControlMouseClick("left", 593, 251, 1,50);
			//BaseActions.ControlMouseClick("left", 670, 251, 1,50);
			if(BaseActions.ObjExists("Data Studio","","[CLASS:SWT_Window0]"))
				sFlag="Pass";
			else
				sFlag="Fail";
		}
		return sFlag;
	}

}
