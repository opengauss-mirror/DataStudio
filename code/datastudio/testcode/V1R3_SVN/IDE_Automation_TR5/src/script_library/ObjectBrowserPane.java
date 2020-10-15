/*************************************************************************
TITLE - OBJECT BROWSER AND TOOLBAR
DESCRIPTION - FUNCTIONS WITH RESPECT TO OBJECT BROWSER AND TOOLBAR
AUTHORS - AWX321824
CREATED DATE - 16-NOV-2015
LAST UPDATED DATE - 16-NOV-2015
MODIFICATION HISTORY - 
TEST CASES COVERED - NA
 *************************************************************************/
package script_library;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import autoitx4java.AutoItX;
import object_repository.ConsoleResultElements;
import object_repository.CreateDBElements;
import object_repository.DataMgntElements;
import object_repository.ExecQueryElements;
import object_repository.ExpQueryElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.ObjectBrowserElements;
import object_repository.SSlElements;
import object_repository.SaveAsElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class ObjectBrowserPane {


	public static void ObjectBrowser() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: ObjectBrowser()
	DESCRIPTION	 		: Function to Navigate through the Object Browser
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
				ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);
		/*UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		 */
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);

		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);

		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

	}
	public static void BrowserExport() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: BrowserExport()
	DESCRIPTION	 		: Function to Export result from Object Browser
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_E, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_E, 2);

		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

		/*UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);*/
	}

	public static void BrowserImport() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: BrowserImport()
	DESCRIPTION	 		: Function to Export result from Object Browser
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
	}

	public static void reIndex() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);


	}

	public static void analyze() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
	}

	public static String HelpButton() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: HelpButton()
	DESCRIPTION	 		: Function to Click the Help button and to validation
	IN PARAMETERS		: None
	RETURN PARAMETERS	: sHelpFlag {True if help window is present, else False}
		 *************************************************************************/		
		String sHelpFlag;
		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_F1, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F1, 1);
		if(x.winExists("Help Document Does Not Exist"))
			sHelpFlag = "False";
		else
			sHelpFlag = "True";
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		return sHelpFlag;
	}
	public static String EmptyTableValidation() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: EmptyTableValidation()
	DESCRIPTION	 		: Function to validate the empty result table
	IN PARAMETERS		: None
	RETURN PARAMETERS	: sEmptyFlag {Pass if empty result, else Fail}
		 *************************************************************************/
		String sEmptyFlag;
		AutoItX x = new AutoItX();

		if(x.winExists("Internal Error")) 
			sEmptyFlag = "Fail";
		else
			sEmptyFlag = "Pass";
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		return sEmptyFlag;
	}
	public static void OpenProcedure() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: OpenProcedure()
	DESCRIPTION	 		: Function to open a procedure from Object Browser
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 12);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 12);

		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}


	public static String CreateDB(String sDBName) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: CreateDB()
	DESCRIPTION	 		: Function to Create a Database
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		String sCreateDBQuery = "CREATE DATABASE "+sDBName+";";
		String sFlag = QueryEditor.SingleQueryExe(sCreateDBQuery,"Valid");
		if(sFlag.equals("Success"))
			return "Success";
		else
			return "Failed";
	}
	public static String RenameDB(String sOldDBName,String sNewDBName) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: RenameDB()
		DESCRIPTION	 		: Function to Rename a Database
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		String sCreateDBQuery = "ALTER DATABASE "+sOldDBName+" RENAME TO "+sNewDBName+";";
		String sFlag = QueryEditor.SingleQueryExe(sCreateDBQuery,"Valid");
		if(sFlag.equals("Success"))
			return "Success";
		else
			return "Failed";
	}

	public static String RenameTable(String sOldDBName,String sNewDBName) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: RenameDB()
		DESCRIPTION	 		: Function to Rename a Database
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		String sCreateTableQuery = "ALTER TABLE "+sOldDBName+" RENAME TO "+sNewDBName+";";
		String sFlag = QueryEditor.SingleQueryExe(sCreateTableQuery,"Valid");
		if(sFlag.equals("Success"))
			return "Success";
		else
			return "Failed";
	}


	public static String RenameDBObjectBrowser(String sNewDBName) throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		BaseActions.SetText("Rename Database", "", "Edit1", sNewDBName);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MedWait);
		return QueryResult.ReadConsoleOutput("GLOBAL");
	}

	public static void vacuum() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_V, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_V, 2);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MedWait);
	}

	public static void setSchema() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 3);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		BaseActions.Winwait(ObjectBrowserElements.wSetSchema);
		BaseActions.Click(ObjectBrowserElements.wSetSchema, "", ObjectBrowserElements.dSetSchema);
		//UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		//UtilityFunctions.KeyRelease(KeyEvent.VK_A,1);
		//BaseActions.Click(TablePropertyElements.sSchemaTitle, "", TablePropertyElements.btnOK);


	}

	public static void RenameTable(String sNewTableName) throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_R,2 );
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 2);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		BaseActions.SetText("Rename Table", "", "Edit1", sNewTableName);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MedWait);
	}


	public static String DropDB(String sDBName) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: CreateDB()
		DESCRIPTION	 		: Function to Drop a Database
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		String sDropDBQuery = "DROP DATABASE "+sDBName+";";
		String sFlag = QueryEditor.SingleQueryExe(sDropDBQuery,"Valid");
		if(sFlag.equals("Success"))
			return "Success";
		else
			return "Failed";
	}

	public static String DropDBObjectBrowserNotConnected() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
		BaseActions.Winwait("Drop Database");
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);	
		Thread.sleep(GlobalConstants.MedWait);
		return QueryResult.ReadConsoleOutput("GLOBAL");
	}

	public static String DropDBObjectBrowserConnected() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_D, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D, 2);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);	
		BaseActions.Winwait("Drop Database");
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);	
		Thread.sleep(GlobalConstants.ModWait);
		return QueryResult.ReadConsoleOutput("GLOBAL");
	}


	public static String DropTableObjectBrowser() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
		BaseActions.Winwait("Drop Table");
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);	
		Thread.sleep(GlobalConstants.MedWait);
		return QueryResult.ReadConsoleOutput("GLOBAL");
	}


	public static String CreateTable(String sSchemaName, String sTableName) throws Exception{
		/*************************************************************************
		FUNCTION NAME : CreateTable()
		DESCRIPTION : Function to Create a Table
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		 *************************************************************************/
		String sCreateTableQuery;
		if(sTableName.contains("Unique"))
			sCreateTableQuery = "CREATE TABLE "+sSchemaName+"."+sTableName+"(auto1 bigint,auto2 bigint UNIQUE CHECK(123));";
		else if(sTableName.contains("employee"))
			sCreateTableQuery = "CREATE TABLE "+sSchemaName+"."+sTableName+"(empid bigint,salutation char,ename text,salary bigint,deptno bigint);";
		else
			sCreateTableQuery = "CREATE TABLE "+sSchemaName+"."+sTableName+"(empid bigint,ename text,salary bigint,deptno bigint);";
		String sFlag = QueryEditor.SingleQueryExe(sCreateTableQuery,"Valid");
		if(sFlag.equals("Success"))
		{ 
			if(sTableName.contains("Unique"))
			{
				String sConsoleOutput = QueryResult.ReadConsoleOutput("TERMINAL");
				if(sConsoleOutput.contains("[NOTICE] CREATE TABLE / UNIQUE will create implicit index"))
					sFlag = "Success";
				else
					sFlag = "Failed";
			}
			return sFlag;
		} 
		else
			return "Failed";
	}

	public static void InsertTable(String sSchemaname,String sTableName,int records) throws Exception
	{	
		//String sFlag = null;
		for(int i=1;i<=records;i++)
		{
			String sInsertquery= "INSERT INTO "+sSchemaname+"."+sTableName+" VALUES ("+i+",'"+sTableName+"',"+i+10001+","+i+10+");";
			BaseActions.SetText(DataMgntElements.wTitle, "", DataMgntElements.sInsertControlID, sInsertquery);
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}



	}
	public static String AlterTable(String sSchemaname,String sOldTableName, String sNewTableName) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: RenameTable()
		DESCRIPTION	 		: Function to Rename a Table
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None

		 *************************************************************************/
		String sAlterTableQuery = "ALTER TABLE "+sSchemaname+"."+sOldTableName+" RENAME TO "+sNewTableName+";";
		String sFlag = QueryEditor.SingleQueryExe(sAlterTableQuery,"Valid");
		if(sFlag.equals("Success"))
			return "Success";
		else
			return "Failed";
	}

	public static String DropTable(String sSchemaName, String sTableName) throws Exception{

		/*************************************************************************
		FUNCTION NAME		: DropTable()
		DESCRIPTION	 		: Function to Drop a Table
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		String sDropTableQuery = "DROP TABLE "+sSchemaName+"."+sTableName+";";
		String sFlag = QueryEditor.SingleQueryExe(sDropTableQuery,"Valid");
		if(sFlag.equals("Success"))
			return "Success";
		else
			return "Failed";
	}
	public static String CreateSchema(String sSchemaName) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: CreateDB()
		DESCRIPTION	 		: Function to Create a Database
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		AutoItX x = new AutoItX();
		String sCreateSchemaQuery = "CREATE SCHEMA "+sSchemaName+";";
		String sFlag = QueryEditor.SingleQueryExe(sCreateSchemaQuery,"Valid");
		x.controlFocus(ExecQueryElements.wSQLTerminal, "", ExecQueryElements.sSQLEditor);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		Thread.sleep(GlobalConstants.MedWait);
		if(sFlag.equals("Success"))
			return "Success";
		else
			return "Failed";
	}

	public static String RenameSchema(String sOldSchemaName,String sNewSchemaName) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: RenameSchema()
		DESCRIPTION	 		: Function to Rename a Schema
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		String sCreateDBQuery = "ALTER SCHEMA "+sOldSchemaName+" RENAME TO "+sNewSchemaName+";";
		String sFlag = QueryEditor.SingleQueryExe(sCreateDBQuery,"Valid");
		if(sFlag.equals("Success"))
			return "Success";
		else
			return "Failed";
	}

	public static void CreateFunctionProcedure(String sSchemaName, String sFunctionName) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: CreateFunctionProcedure()
		DESCRIPTION	 		: Creates a Function/Procedure
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		ArrayList<String> lstStatus = new ArrayList<String>();
		QueryEditor.ClearEditor();
		BaseActions.ClearConsole("Basic");
		if(sSchemaName.equals("public"))
			lstStatus.add("CREATE OR REPLACE FUNCTION "+sFunctionName+"()");
		else
			lstStatus.add("CREATE OR REPLACE FUNCTION "+sSchemaName+"."+sFunctionName+"()");
		lstStatus.add("RETURNS integer");
		lstStatus.add("LANGUAGE plpgsql");
		lstStatus.add("AS $$");
		lstStatus.add("DECLARE");
		lstStatus.add("m int;");
		lstStatus.add("BEGIN");
		lstStatus.add("m := 5;");
		lstStatus.add("m := m+1;");
		lstStatus.add("m := m+1;");
		lstStatus.add("m := m+1;");
		lstStatus.add("m := m+1;");
		lstStatus.add("return m;");
		lstStatus.add("end $$");
		int iSize = lstStatus.size();
		for(int i=0;i<iSize;i++)
		{	
			String sLine = lstStatus.get(i);
			QueryEditor.SetFunction(sLine);
		}
		BaseActions.Click(ExecQueryElements.wSQLTerminal,"",ExecQueryElements.sExeButton);
		//QueryEditor.ClearEditor();
	}
	public static String DropSchema(String sSchemaName) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: CreateDB()
	DESCRIPTION	 		: Function to Drop a Database
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		String sDropSchemaQuery = "DROP SCHEMA "+sSchemaName+";";
		String sFlag = QueryEditor.SingleQueryExe(sDropSchemaQuery,"Valid");
		if(sFlag.equals("Success"))
			return "Success";
		else
			return "Failed";
	}

	public static void ObjectBrowserRefresh() throws Exception{ //added ALT+Q
		/*************************************************************************
	FUNCTION NAME		: CreateDB()
	DESCRIPTION	 		: Function to Drop a Database
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);
		UtilityFunctions.KeyPress(KeyEvent.VK_F5, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F5, 1);
		Thread.sleep(3000);
	}

	public static void TableRefresh() throws Exception
	{
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, ObjectBrowserElements.xTableRefreshcord, ObjectBrowserElements.yTableRefreshcord);
		UtilityFunctions.KeyPress(KeyEvent.VK_F5, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F5, 1);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void TableExport(String sPath,String sButton) throws Exception
	{
		AutoItX x = new AutoItX();

		/*UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_UP, 5);
		UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 5);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);*/
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_E, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_E, 2);

		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
		BaseActions.SetText(ExpQueryElements.wSQLSaveasWindow, "",ExpQueryElements.sSQLSaveasControlID ,sPath);
		if(sButton.equalsIgnoreCase("SAVE"))
		{
			BaseActions.Click(ExpQueryElements.wSQLSaveasWindow, "",ExpQueryElements.sSaveButton);
			if(x.winExists("File Overwrite Confirmation"))
			{
				UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			}
		}

		else
		{
			BaseActions.Click(ExpQueryElements.wSQLSaveasWindow, "",ExpQueryElements.sCancelButton);
		}
	}

	public static void TableImport(String sPath,String sButton) throws Exception
	{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.SetText(ExpQueryElements.wSQLOpenWindow, "",ExpQueryElements.sSQLOpenControlID ,sPath);
		if(sButton.equalsIgnoreCase("OPEN"))
			BaseActions.Click(ExpQueryElements.wSQLOpenWindow, "",ExpQueryElements.sOpenButton);
		else
			BaseActions.Click(ExpQueryElements.wSQLOpenWindow, "",ExpQueryElements.sCancelButton);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.Winwait("Data Imported Successfully");
		UtilityFunctions.KeyPress(KeyEvent.VK_ESCAPE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ESCAPE, 1);
	}

	public static void TableImportWithoutWait(String sPath,String sButton) throws Exception
	{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.SetText(ExpQueryElements.wSQLOpenWindow, "",ExpQueryElements.sSQLOpenControlID ,sPath);
		if(sButton.equalsIgnoreCase("OPEN"))
			BaseActions.Click(ExpQueryElements.wSQLOpenWindow, "",ExpQueryElements.sOpenButton);
		else
			BaseActions.Click(ExpQueryElements.wSQLOpenWindow, "",ExpQueryElements.sCancelButton);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}

	public static void Auto_Import_Table_Navigation() throws Exception{
		/*************************************************************************
		FUNCTION NAME : CreateDB()
		DESCRIPTION : Function to Drop a Database
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		 *************************************************************************/
		objectBrowserRefresh("SINGLE");
		Thread.sleep(3500);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,3);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		Thread.sleep(GlobalConstants.MinWait);
	}


	public static void Auto_Table_Navigation() throws Exception{

		objectBrowserRefresh("SINGLE");
		Thread.sleep(3500);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void Auto_Table_Navigation_Load() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void Auto_Import_Navigation() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		objectBrowserRefresh("SINGLE");
		Thread.sleep(3500);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,3);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,2);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static String TruncateTable(int xPos, int yPos) throws Exception
	{
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,xPos,yPos);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_T, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_T, 3);
		/*UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 3);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);*/
		Thread.sleep(GlobalConstants.MedWait);
		if(BaseActions.WinExists(ExpQueryElements.wTruncateTableWindow))
		{
			BaseActions.Click(ExpQueryElements.wTruncateTableWindow,"",ExpQueryElements.bTruncateTableOK);
			String sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
			if(sFlag.contains("truncated"))
				return "Success";
			else
				return sFlag;
		}
		else
			return "Truncate Table window not displayed";
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
		BaseActions.MouseClick("Data Studio", "", "ToolbarWindow326", "left", 1, 424, 11);
		String DBName = x.clipGet(); 
		String DBNameSpliter[] = DBName.split("\n");
		for(String SplitedValues: DBNameSpliter){
			sQuery = "DROP DATABASE "+SplitedValues;
			QueryEditor.SingleQueryExe(sQuery, "Normal");
		}
	}

	public static void objectBrowserRefresh(String sConnType) throws Exception
	{
		switch(sConnType.toUpperCase())
		{
		case "SINGLE":
			BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
					ObjectBrowserElements.xRefreshcord, ObjectBrowserElements.yRefreshcord);
			UtilityFunctions.KeyPress(KeyEvent.VK_F5, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_F5, 1);
			Thread.sleep(3500);
			break;
		case "DOUBLE":	
			BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
					85, 27);
			UtilityFunctions.KeyPress(KeyEvent.VK_F5, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_F5, 1);
			Thread.sleep(3500);
			break;
		case "TRIPLE":	
			BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
					87, 43);
			UtilityFunctions.KeyPress(KeyEvent.VK_F5, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_F5, 1);
			Thread.sleep(3500);
			break;
		default:
			break;
		}
	}

	public static void objectBrowserExpansion(String sConnType) throws Exception
	{
		switch(sConnType.toUpperCase())
		{
		case "SINGLE":

			BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
					ObjectBrowserElements.excord, ObjectBrowserElements.eycord);
			break;

		case "DOUBLE":
			BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
					13,28);
			break;

		case "TRIPLE":
			BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
					12,46);
		default:
			break;
		}
	}
	public static void connectToMultipleDB(String sConnType) throws Exception
	{
		switch(sConnType.toUpperCase())
		{
		case "SINGLE":BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,83,28);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);

		/*BaseActions.SetText(ObjectBrowserElements.wPasswordTitle, "", ObjectBrowserElements.sPasswordControlID, sPassword);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);*/
		break;

		case "DOUBLE":BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,83,45);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
		/*BaseActions.SetText(ObjectBrowserElements.wPasswordTitle, "", ObjectBrowserElements.sPasswordControlID, sPassword);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);*/
		break;

		case "TRIPLE":BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,83,61);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
		/*BaseActions.SetText(ObjectBrowserElements.wPasswordTitle, "", ObjectBrowserElements.sPasswordControlID, sPassword);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);*/
		break;

		default:
			break;
		}
	}
	public static void connectToDB() throws Exception
	{
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "",ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
				ObjectBrowserElements.xDBcord, ObjectBrowserElements.yDBcord);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		/*UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);*/
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
		/*BaseActions.SetText(ObjectBrowserElements.wPasswordTitle, "", ObjectBrowserElements.sPasswordControlID, sPassword);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);*/
	}

	public static void disconnectDB() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}
	public static void newConnection() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
	}

	public static void removeConnection() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		BaseActions.Winwait("Remove server Confirmation");
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}

	public static void tablePropertyFunctionOpen() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static String DropIndex(String sSchemaName, String sIndexName) throws Exception{

		/*************************************************************************
		FUNCTION NAME		: DropTable()
		DESCRIPTION	 		: Function to Drop a Table
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		String sDropTableQuery = "DROP INDEX "+sSchemaName+"."+sIndexName+";";
		String sFlag = QueryEditor.SingleQueryExe(sDropTableQuery,"Valid");
		if(sFlag.equals("Success"))
			return "Success";
		else
			return "Failed";
	}

	public static void AutoSuggestSchemaOpen() throws Exception{
		/*************************************************************************
		FUNCTION NAME : AutoSuggestSchemaOpen()
		DESCRIPTION : Function to Navigate through the Object Browser
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);

		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
				ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);

		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 7);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 7);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}
	public static void disconnectDB(int dbNumber) throws Exception{
		/*************************************************************************
		FUNCTION NAME : BrowserExport()
		DESCRIPTION : Function to disconnect a db from Object Browser
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
				ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, dbNumber);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, dbNumber);

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 2);
	}
	public static void objPropertyFunctionOpen() throws Exception{
		/*************************************************************************
		FUNCTION NAME : objPropertyFunctionOpen()
		DESCRIPTION : Function to Navigate through the Object Browser
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);

		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
				ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);

		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 9);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 9);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}
	public static void ObjectBrowserSchemaOpen(int iSchemaNumber) throws Exception{
		/*************************************************************************
		FUNCTION NAME : ObjectBrowserSchemaOPen()
		DESCRIPTION : Function to Navigate to a specific schema
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
				ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);

		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);

		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, iSchemaNumber);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, iSchemaNumber);

		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}
	public static void UnReservedKeywords() throws Exception
	{
		ArrayList<String> lstStatus = new ArrayList<String>();
		lstStatus.add("ABORT");
		lstStatus.add("ABSOLUTE");
		lstStatus.add("ACCESS");
		lstStatus.add("ACCOUNT");
		lstStatus.add("ACTION");
		lstStatus.add("ADD");
		lstStatus.add("ADMIN");
		lstStatus.add("AFTER");
		lstStatus.add("AGGREGATE");
		lstStatus.add("ALSO");
		lstStatus.add("ALTER");
		lstStatus.add("ALWAYS");
		lstStatus.add("APP");
		lstStatus.add("XMLELEMENT");
		lstStatus.add("XMLEXISTS");
		lstStatus.add("XMLFOREST");
		lstStatus.add("XMLPI");
		int iSize = lstStatus.size();
		for(int i=0;i<iSize;i++)
		{ 
			String sLine = lstStatus.get(i);
			QueryEditor.SetFunction(sLine);
		}
	}
	public static void Predicate() throws Exception
	{
		ArrayList<String> lstStatus = new ArrayList<String>();
		lstStatus.add("BETWEEN");
		lstStatus.add("BIGINT");
		lstStatus.add("BINARY_DOUBLE");
		lstStatus.add("AUTHORIZATION");
		lstStatus.add("BINARY");
		lstStatus.add("COLLATION");
		lstStatus.add("..");
		lstStatus.add(":=");
		lstStatus.add("=>");
		lstStatus.add("=");
		lstStatus.add("~<=~");
		lstStatus.add("~>=~");
		lstStatus.add("~>~");
		lstStatus.add("&<|");
		lstStatus.add("|&>");
		lstStatus.add("@@@");
		lstStatus.add("-|-");
		int iSize = lstStatus.size();
		for(int i=0;i<iSize;i++)
		{ 
			String sLine = lstStatus.get(i);
			QueryEditor.SetFunction(sLine);
		}
	}
	public static void TableSmokeImport(String sPath,String sButton) throws Exception
	{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_I, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_I, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.SetText(ExpQueryElements.wFileOpenWindow, "",ExpQueryElements.sSQLOpenControlID ,sPath);
		if(sButton.equalsIgnoreCase("OPEN"))
			BaseActions.Click(ExpQueryElements.wFileOpenWindow, "",ExpQueryElements.sOpenButton);
		else
			BaseActions.Click(ExpQueryElements.wFileOpenWindow, "",ExpQueryElements.sCancelButton);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}
	public static void ReservedKeywords() throws Exception
	{
		ArrayList<String> lstStatus = new ArrayList<String>();
		lstStatus.add("ANALYZE");
		lstStatus.add("AND");
		lstStatus.add("ANY");
		lstStatus.add("ARRAY");
		lstStatus.add("AS");
		lstStatus.add("ASC");
		lstStatus.add("JOIN");
		lstStatus.add("LEFT");
		lstStatus.add("LIKE");
		lstStatus.add("NATURAL");
		lstStatus.add("NOTNULL");
		lstStatus.add("OUTER");
		lstStatus.add("OVER");
		int iSize = lstStatus.size();
		for(int i=0;i<iSize;i++)
		{ 
			String sLine = lstStatus.get(i);
			QueryEditor.SetFunction(sLine);
		}
	}

	public static String createDBObjectBrowser(String sDBName,
			String sEncoding, String sConnect) throws Exception {
		ObjectBrowserPane.objectBrowserRefresh("SINGLE");
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);

		Thread.sleep(GlobalConstants.MinWait);

		BaseActions.SetText("Create Database", "", "Edit1", sDBName);


		BaseActions.MouseClick("Create Database", "", "ComboBox1", "left", 1, 72, 14);

		if(sEncoding.equalsIgnoreCase("UTF-8")){

			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

		}

		else if (sEncoding.equalsIgnoreCase("GBK")){

			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);

		}

		else if (sEncoding.equalsIgnoreCase("LATIN1")){

			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 3);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 3);
		}


		else if(sEncoding.equalsIgnoreCase("SQL_ASCII")){

			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 4);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 4);

		}

		else {

			BaseActions.SetText("Create Database", "", "Edit2", sEncoding);

		}

		Thread.sleep(GlobalConstants.MedWait);
		if (sConnect.equalsIgnoreCase("Yes")) {

			BaseActions.Click("Create Database", "", "Button1");
		}

		Thread.sleep(GlobalConstants.MaxWait);

		BaseActions.Click("Create Database", "", "Button2");
		Thread.sleep(GlobalConstants.MedWait);
		QueryResult.ReadConsoleOutput("GLOBAL");

		return QueryResult.ReadConsoleOutput("GLOBAL");

	}
	public static String RenameDBObjectBrowser(String sNewDBName, String sConnect) throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		BaseActions.SetText("Rename Database", "", "Edit1", sNewDBName);
		if(sConnect.equalsIgnoreCase("Yes")){

			BaseActions.Click(CreateDBElements.wRenameDatabase, "", CreateDBElements.bConnect);
		}
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click(CreateDBElements.wRenameDatabase, "", CreateDBElements.bOK);
		BaseActions.Winwait(CreateDBElements.wRenameDatabase);
		BaseActions.Click(CreateDBElements.wRenameDatabase, "", CreateDBElements.bRenameOK);
		return QueryResult.ReadConsoleOutput("GLOBAL");
	}

	public static void newConnectionDetails(String sConnection, String sHost, String sHostPort, String sDBName, String sUserName, String sPassword) throws Exception
	{
		BaseActions.Winwait(LoginElements.wDBConnection);
		BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bClear);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName, sConnection);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHost, sHost);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHostPort, sHostPort);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sDBName, sDBName);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sUsername, sUserName);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sPassword, sPassword);

	}
	public static void removeConnectionWindow() throws Exception
	{
		BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bRemoveConnection);
		BaseActions.Winwait(LoginElements.wRemoveConnection);
		BaseActions.Click(LoginElements.wRemoveConnection, "", LoginElements.bRemoveConnectionYes);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
	}

	public static void sslLogin(String sSavePassword) throws Exception{

		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sConnectionName, SSlElements.sConnectionName);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHost, SSlElements.sHost);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sHostPort, SSlElements.sHostPort);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sDBName, SSlElements.sDBName);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sUsername, SSlElements.sUsername);
		BaseActions.SetText(LoginElements.wDBConnection, "", LoginElements.sPassword, SSlElements.sPassword);

		BaseActions.Click(LoginElements.wDBConnection, "",LoginElements.lstSavePassword );
		switch(sSavePassword)
		{
		case "NO":
			UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
			break;
		case "SESSION":
			UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
			break;
		case "PERMANENT":
			UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
			break;
		default :
			break;
		}

		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

		BaseActions.Click(LoginElements.wDBConnection, "", SSlElements.bEnableSSL);

		BaseActions.SetText(LoginElements.wDBConnection, "", SSlElements.sSSLPassword, SSlElements.sSSLPasswordValue);

		BaseActions.Click(LoginElements.wDBConnection, "", SSlElements.bTrustButton);

		Thread.sleep(GlobalConstants.MedWait);

		BaseActions.SetText("Open", "", SSlElements.sUploadField, SSlElements.sTrustFileLocation);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

		Thread.sleep(GlobalConstants.MedWait);

		BaseActions.Click(LoginElements.wDBConnection, "", SSlElements.bClientButton);
		Thread.sleep(GlobalConstants.MinWait);

		BaseActions.SetText("Open", "", SSlElements.sUploadField, SSlElements.sClientFileLocation);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bOK);
	}

	public static void disconnectAll() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);

	}
	public static void removeTempConnectionWindow() throws Exception
	{
		BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bRemoveConnection);
		BaseActions.Winwait(LoginElements.wRemoveTempConnection);
		BaseActions.Click(LoginElements.wRemoveTempConnection, "", LoginElements.bRemoveConnectionYes);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click(LoginElements.wDBConnection, "", LoginElements.bCancel);
	}

	public static String createSchemaObjectBrowser(String sSchemaName) throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C,1);
		BaseActions.Winwait(ObjectBrowserElements.wCreateSchema);
		BaseActions.SetText(ObjectBrowserElements.wCreateSchema, "", ObjectBrowserElements.sShemaName, sSchemaName);
		BaseActions.Click(ObjectBrowserElements.wCreateSchema, "", ObjectBrowserElements.bSchemaOK);
		Thread.sleep(GlobalConstants.MedWait);
		String sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
		if(sFlag.contains("SUCCESFUll")){

			return "success";
		}
		else {

			return "failed";
		}
	}

	public static String exportDDL(String sPath, String sData) throws Exception{

		String sFlag = null;
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU,1);

		if(sData.equalsIgnoreCase("No")){

			UtilityFunctions.KeyPress(KeyEvent.VK_E, 3);
			UtilityFunctions.KeyRelease(KeyEvent.VK_E, 3);
		}
		else {

			UtilityFunctions.KeyPress(KeyEvent.VK_E, 4);
			UtilityFunctions.KeyRelease(KeyEvent.VK_E, 4);
		}
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		if(BaseActions.WinExists(SaveAsElements.wDisclaimer))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}
		BaseActions.Winwait("Save As");
		BaseActions.SetText("Save As", "", "Edit1", sPath);
		BaseActions.Click("Save As", "", "Button1");
		Thread.sleep(GlobalConstants.MaxWait);

		if(BaseActions.WinExists(ObjectBrowserElements.wExportFinish)){

			BaseActions.Click(ObjectBrowserElements.wExportFinish, "", ObjectBrowserElements.bExportFinishOK);
			sFlag = "Success";
		}
		else if(BaseActions.WinExists(ObjectBrowserElements.wExportFailed)) {


			BaseActions.Click(ObjectBrowserElements.wExportFailed, "", ObjectBrowserElements.bExportFailedOK);
			sFlag = "Failed";
		}
		return sFlag;

	}

	public static void dropSchemaObjectBrowser() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
		BaseActions.Winwait(ObjectBrowserElements.wDropSchema);
		BaseActions.Click(ObjectBrowserElements.wDropSchema, "", ObjectBrowserElements.bDropSchemaOK);
	}

	public static void schemaDDLNavigation() throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		ObjectBrowserPane.ObjectBrowserRefresh();
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyPress(KeyEvent.VK_U, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_U, 1);
	}
	public static void renameSchemaObjectBrowser(String sNewSchemaName) throws Exception{

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		BaseActions.Winwait(ObjectBrowserElements.wRenameSchema);
		BaseActions.SetText(ObjectBrowserElements.wRenameSchema, "", ObjectBrowserElements.bRenameSchemaOK, sNewSchemaName);
		BaseActions.Click(ObjectBrowserElements.wRenameSchema, "", ObjectBrowserElements.bRenameSchemaOK);
	}
}

