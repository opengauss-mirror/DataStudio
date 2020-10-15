package script_library;

import java.awt.event.KeyEvent;

import object_repository.ExecQueryElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.ObjectBrowserElements;
import object_repository.SaveAsElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import autoitx4java.AutoItX;

public class MultipleTerminal {


	public static void OpenNewTerminal() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: OpenNewTerminal()
	DESCRIPTION	 		: Function to open new SQL Terminal
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		BaseActions.Click(LoginElements.wIDEWindow, "", ExecQueryElements.sConnCombo);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
		Thread.sleep(GlobalConstants.MedWait);
	}

	public static void OpenNewTerminalFromDB(int iDatabaseNumber) throws Exception{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.ObjectBrowserRefresh();
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, iDatabaseNumber-1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, iDatabaseNumber-1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_O, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_O, 1);

		/*UtilityFunctions.KeyPress(KeyEvent.VK_UP, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 3);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);*/
	}

	public static void SelectTerminal(int iTerminalNumber) throws Exception{
		/*************************************************************************
	FUNCTION NAME : SelectTerminal()
	DESCRIPTION : Function to set focus on specific SQL Terminal
	IN PARAMETERS : SQL Terminal Number
	RETURN PARAMETERS : None
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);

		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, iTerminalNumber-1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, iTerminalNumber-1);

		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 4);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 4);
	}

	public static void TerminalSetText(int iTerminalNumber, String sQuery) throws Exception{
		/*************************************************************************
		FUNCTION NAME : TerminalSetText()
		DESCRIPTION : Function to set text on specific SQL Terminal
		IN PARAMETERS : SQL Terminal Number
		RETURN PARAMETERS : None
		COMMENT: Use only for setting texts on terminals other than first terminal
		 *************************************************************************/
		AutoItX x = new AutoItX();
		SelectTerminal(iTerminalNumber);

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		Thread.sleep(GlobalConstants.MinWait);
		x.send(sQuery);
		Thread.sleep(GlobalConstants.MinWait);

	}

	public static void SetFunction(int iTerminalNumber, String sQuery) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: SetFunction()
		DESCRIPTION	 		: Function to set the multiline function
		IN PARAMETERS		: sLine{Input Line for the functions}
		RETURN PARAMETERS	: None
		 *************************************************************************/
		AutoItX x = new AutoItX();
		SelectTerminal(iTerminalNumber);
		Thread.sleep(GlobalConstants.MinWait);
		x.send(sQuery);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}

	public static void TerminalConsoleResultNavigation(int iTerminalNumber, String sTab) throws Exception
	{
		Thread.sleep(GlobalConstants.MinWait);
		SelectTerminal(iTerminalNumber);

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		if(sTab.equals("CONSOLE"))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 2);
		}
		else
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 2);
		}
	}

	public static String TerminalResultTabOperations(int iTerminalNumber,String sOperation) throws Exception
	{
		AutoItX x = new AutoItX();
		Thread.sleep(GlobalConstants.MinWait);
		TerminalConsoleResultNavigation(iTerminalNumber,"RESULT");
		Thread.sleep(GlobalConstants.MinWait);
		switch(sOperation)
		{
		case "COPY":
			//x.mouseClick("left", 455, 475, 1,50);
			x.mouseClick("left", 375, 375, 1,50);
			break;
		case "EXPORT ALL":
			//x.mouseClick("left", 475, 475, 1,50);
			x.mouseClick("left", 399, 372, 1,50);
			
			break;
		case "EXPORT CURRENT":	
			//x.mouseClick("left", 495, 475, 1,50);
			x.mouseClick("left", 424, 374, 1,50);
			break;
		default :
			break;
		}
		if(sOperation.equals("COPY"))
		{
			String sResultOutput = x.clipGet();
			return sResultOutput;
		}
		else
		{
			if(BaseActions.WinExists(SaveAsElements.wDisclaimer))
			{
				BaseActions.Click(SaveAsElements.wDisclaimer,"",SaveAsElements.bDisclaimerOk );
				if(x.winExists(SaveAsElements.sSave))
					return "Success";
				else
					return "Fail";
			}
			return "No Disclaimer Window is displayed";
		}

	}

	public static void TerminalOperations(int iterminalnumber,String sSelection) throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1 );
		UtilityFunctions.KeyPress(KeyEvent.VK_S,1 );
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		switch (sSelection) {
		case "CLOSE" : 
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,iterminalnumber-1 );
			UtilityFunctions.KeyPress(KeyEvent.VK_S,iterminalnumber-1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1 );
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU,1 );
			UtilityFunctions.KeyPress(KeyEvent.VK_C,1 );
			UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
			break;
		case "CLOSE_OTHERS" :
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,iterminalnumber-1 );
			UtilityFunctions.KeyPress(KeyEvent.VK_S,iterminalnumber-1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1 );
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU,1 );
			UtilityFunctions.KeyPress(KeyEvent.VK_O,1 );
			UtilityFunctions.KeyRelease(KeyEvent.VK_O, 1);
			break;
		case "CLOSE_ALL" :
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,iterminalnumber-1 );
			UtilityFunctions.KeyPress(KeyEvent.VK_S,iterminalnumber-1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1 );
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU,1 );
			UtilityFunctions.KeyPress(KeyEvent.VK_A,1 );
			UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
			break;

		default :
			break;

		}
	}

	public static void CloseTerminal(int iTerminalNumber) throws Exception {
		SelectTerminal(iTerminalNumber);
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F4, 1);
	}


	public static void TerminalConsoleClear(int iTerminalNumber) throws Exception{
		/*************************************************************************
	FUNCTION NAME : TerminalSetText()
	DESCRIPTION : Function to set text on specific SQL Terminal
	IN PARAMETERS : SQL Terminal Number
	RETURN PARAMETERS : None
	COMMENT: Use only for setting texts on terminals other than first terminal
		 *************************************************************************/
		SelectTerminal(iTerminalNumber);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 2);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
	}

	public static void TerminalEditorClear(int iTerminalNumber) throws Exception{
		MultipleTerminal.SelectTerminal(iTerminalNumber);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
	}


	public static String TerminalConsoleCopy(int iTerminalNumber) throws Exception{
		/*************************************************************************
	FUNCTION NAME : TerminalConsoleCopy()
	DESCRIPTION : Function to copy specific SQL Terminal console content
	IN PARAMETERS : SQL Terminal Number
	RETURN PARAMETERS : None
	COMMENT: Use only for setting texts on terminals other than first terminal
		 *************************************************************************/
		AutoItX x = new AutoItX();	
		SelectTerminal(iTerminalNumber);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 2);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
		Thread.sleep(GlobalConstants.MedWait);
		String sConsoleOutput = x.clipGet();
		return sConsoleOutput;
	}

	public static void TerminalSetQuery(int iTerminalNumber, String sQuery) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: TerminalSetQuery()
	DESCRIPTION	 		: Function to set the query in Query Editor
	IN PARAMETERS		: sQuery{Input Query to be entered in Query Editor}
	RETURN PARAMETERS	: None
		 *************************************************************************/
		AutoItX x = new AutoItX();
		TerminalConsoleClear(iTerminalNumber);
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 2);
		//Selecting the query for execution using key board actions
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
		Thread.sleep(GlobalConstants.MedWait);
		x.send(sQuery);
		Thread.sleep(GlobalConstants.MedWait);
	}
}
