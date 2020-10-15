package script_library;

import java.awt.event.KeyEvent;

import object_repository.DebugElements;
import object_repository.ErrorElements;
import object_repository.ExecQueryElements;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.ObjectBrowserElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import autoitx4java.AutoItX;

public class DebugOperations {

	public static void DebugOption(String sType) throws Exception {
		AutoItX x = new AutoItX();
		switch (sType) {
		case "ObjectBrowser":
			BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
					150,80);
			Thread.sleep(500);
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "SQLViewer":
			x.winActivate(LoginElements.wIDEWindow);
			Thread.sleep(500);
			x.controlClick(LoginElements.wIDEWindow, "","SWT_Window0116");
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_UP, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "Menu":
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;
		case "Toolbar":
			BaseActions.MouseClick(DebugElements.wToolbarTitle,"",DebugElements.sDebugToolbar,DebugElements.sMouseButton,
					DebugElements.iButtonClick,DebugElements.iDebugxcord,DebugElements.iDebugycord);
			break;
		case "Shortcut":
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
			break;

		default :
			break;
		}
	}

	public static String GetCallStack() throws Exception {

		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_J, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_J, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		Thread.sleep(GlobalConstants.MedWait);
		String sCallStackOutput = x.clipGet();
		return sCallStackOutput;
	}

	public static String GetBreakpoint() throws Exception {

		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Y, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Y, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		Thread.sleep(GlobalConstants.MedWait);
		String sBreakpointOutput = x.clipGet();
		return sBreakpointOutput;
	}

	public static String GetVariables() throws Exception {

		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_K, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_K, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		Thread.sleep(GlobalConstants.MedWait);
		String sVariableOutput = x.clipGet();
		return sVariableOutput;
	}

	public static void ClearDebug() throws Exception
	{
		DebugOperations.TerminateDebugging();
		Thread.sleep(GlobalConstants.MedWait);
		DebugOperations.CheckboxClick(0);
		Thread.sleep(GlobalConstants.MedWait);
		DebugOperations.DeleteBreakpoint();
		Thread.sleep(GlobalConstants.MedWait);
		DebugOperations.CheckboxClick(0);
		Thread.sleep(GlobalConstants.MedWait);
		DebugOperations.DeleteBreakpoint();
		Thread.sleep(GlobalConstants.MedWait);
		DebugOperations.CheckboxClick(0);
		Thread.sleep(GlobalConstants.MedWait);
		DebugOperations.DeleteBreakpoint();
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.ClearConsole("Debug");
	}

	public static void LargeDataFunction() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: ObjectBrowser()
	DESCRIPTION	 		: Function to Navigate through the Object Browser
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		ObjectBrowserPane.ObjectBrowserRefresh();
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);


	}

	public static void DebugObjectBrowserMultipleDB(String sConnType) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: ObjectBrowser()
	DESCRIPTION	 		: Function to Navigate through the Object Browser
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		switch(sConnType.toUpperCase())
		{
		case "SINGLE":
			BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
					ObjectBrowserElements.xcord, ObjectBrowserElements.ycord);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);

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

			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);

			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;

		case "DOUBLE":
			BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,
					89,27);
			Thread.sleep(GlobalConstants.MinWait);
			//Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 6);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 6);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

			/*UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);

			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);

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

			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);

			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);*/
			break;
		default:
			break;
		}

	}

	public static void DebugObjectBrowser_Open() throws Exception{
		/*************************************************************************
		FUNCTION NAME		: ObjectBrowser()
		DESCRIPTION	 		: Function to Navigate through the Object Browser
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		ObjectBrowserPane.ObjectBrowserRefresh();
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}

	public static void DebugObjectBrowser_Close() throws Exception{
		/*************************************************************************
		FUNCTION NAME		: ObjectBrowser()
		DESCRIPTION	 		: Function to Navigate through the Object Browser
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		/*BaseActions.ClearConsole("Basic");
		Thread.sleep(GlobalConstants.MedWait);
		DebugOperations.TerminateDebugging();
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.MouseClick(ObjectBrowserElements.wTitle,"","SWT_Window06",ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks, 180, 12);
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,157,80);
		UtilityFunctions.KeyPress(KeyEvent.VK_LEFT, 8);
		UtilityFunctions.KeyRelease(KeyEvent.VK_LEFT, 8);
		DebugOperations.CheckboxClick(0);
		Thread.sleep(GlobalConstants.MedWait);
		DebugOperations.DeleteBreakpoint();
		Thread.sleep(GlobalConstants.MedWait);*/
		DebugOperations.TerminateDebugging();
		Thread.sleep(GlobalConstants.MedWait);
		Login.DebugWindows();
		Thread.sleep(GlobalConstants.MedWait);
		DebugOperations.CheckboxClick(0);
		Thread.sleep(GlobalConstants.MedWait);
		DebugOperations.DeleteBreakpoint();
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.ClearConsole("Basic");
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
		
		
	}


	public static void SetBreakPoint(int LineNumber) throws Exception {
		Thread.sleep(GlobalConstants.MinWait);
		switch (LineNumber) {
		case 6:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					//DebugElements.iClick, 6, 83);
					DebugElements.iClick, 6, 92);
			break;
		case 7:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					//DebugElements.iClick, 6,99);
					DebugElements.iClick, 6,110);
			break;
		case 8:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					//DebugElements.iClick, 6, 112);
					DebugElements.iClick, 6, 128);
			break;
		case 9:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					//DebugElements.iClick, 6, 127);
					DebugElements.iClick, 6, 144);
			break;
		case 10:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					//DebugElements.iClick, 6, 142);
					DebugElements.iClick, 6, 162);
			break;
		case 11:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					//DebugElements.iClick, 6, 157);
					DebugElements.iClick, 6, 177);
			break;
		case 12:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					//DebugElements.iClick, 6, 172);
					DebugElements.iClick, 6, 194);
			break;
		case 13:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 213);
			break;
		case 14:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 236);
			break;
		case 15:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 246);
			break;
			/*	case 16:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 17:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 18:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 19:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 20:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 21:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 22:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;*/

		default :
		}

	}
	public static void RemoveBreakPoint(int LineNumber) throws Exception {

		switch (LineNumber) {
		case 6:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 92);
			break;
		case 7:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6,110);
			break;
		case 8:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 128);
			break;
		case 9:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 144);
			break;
		case 10:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 162);
			break;
		case 11:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 177);
			break;
		case 12:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 194);
			break;
		case 13:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 213);
			break;
		case 14:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 236);
			break;
		case 15:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, 6, 246);
			break;
			/*	case 16:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 17:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 18:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 19:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 20:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 21:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;
		case 22:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",
					DebugElements.sDebugwindow, DebugElements.sMouseButton,
					DebugElements.iClick, x, y);
			break;*/

		default :
		}

	}
	public static void CheckboxClick(int CheckBoxNumber) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: CheckboxClick()
	DESCRIPTION	 		: Function to click on checkbox in breakpoint window
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		BaseActions.Focus(DebugElements.wDebugpane, "",DebugElements.sBreakpointHeader);
		Thread.sleep(GlobalConstants.MedWait);
		switch (CheckBoxNumber) {

		case 0:
			BaseActions.MouseClick(DebugElements.wDebugpane, "",DebugElements.sBreakpointHeader, DebugElements.sMouseButton, DebugElements.iCheckboxClick,12,12);
			Thread.sleep(GlobalConstants.MedWait);
			break;
		case 1:

			BaseActions.MouseClick(DebugElements.wDebugpane, "",DebugElements.sBreakpointwindow, DebugElements.sMouseButton, DebugElements.iCheckboxClick,12,36);
			Thread.sleep(GlobalConstants.MedWait);
			break;
		case 2:
			Thread.sleep(GlobalConstants.MedWait);
			BaseActions.MouseClick(DebugElements.wDebugpane, "",DebugElements.sBreakpointwindow, DebugElements.sMouseButton, DebugElements.iCheckboxClick,13,54);
			//Thread.sleep(GlobalConstants.MinWait);
			break;
		case 3:

			BaseActions.MouseClick(DebugElements.wDebugpane, "",DebugElements.sBreakpointwindow, DebugElements.sMouseButton, DebugElements.iCheckboxClick,12,73);
			Thread.sleep(GlobalConstants.MedWait);
			break;
		case 4:

			BaseActions.MouseClick(DebugElements.wDebugpane, "",DebugElements.sBreakpointwindow, DebugElements.sMouseButton, DebugElements.iCheckboxClick,12,94);
			Thread.sleep(GlobalConstants.MedWait);
			break;
		case 5:

			BaseActions.MouseClick(DebugElements.wDebugpane, "",DebugElements.sBreakpointwindow, DebugElements.sMouseButton, DebugElements.iCheckboxClick,12,111);
			Thread.sleep(GlobalConstants.MedWait);
			break;
		}	

	}
	public static void DeleteBreakpoint() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: DeleteBreakpoint()
	DESCRIPTION	 		: Function to click on DeleteBreakpoint in breakpoint window
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		BaseActions.Focus(DebugElements.wDebugpane, "", DebugElements.wDeleteBreakpoint);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.MouseClick(DebugElements.wDebugpane, "", DebugElements.wDeleteBreakpoint,DebugElements.wDeleteMouseButton,DebugElements.iButtonClick , DebugElements.iDeletexcord, DebugElements.iDeleteycord);
	}
	public static void DisableBreakPoint() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: DisableBreakPoint()
	DESCRIPTION	 		: Function to click on DisableBreakPoint in breakpoint window
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.MouseClick(DebugElements.wDebugpane, "", DebugElements.wDisableBreakpoint,DebugElements.wDisabledMouseButton,DebugElements.iButtonClick , DebugElements.iDisabledxcord, DebugElements.iDisabledycord);
		Thread.sleep(GlobalConstants.MedWait);
	}
	public static void EnableBreakPoint() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: EnableBreakPoint()
	DESCRIPTION	 		: Function to click on DisableBreakPoint in breakpoint window
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		BaseActions.MouseClick(DebugElements.wDebugpane, "", DebugElements.wDisableBreakpoint,DebugElements.sMouseButton,DebugElements.iButtonClick , DebugElements.ienabledxcord, DebugElements.ienabledycord);
		Thread.sleep(GlobalConstants.MedWait);
	}

	public static void DebugSession() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: DebugSession()
	DESCRIPTION	 		: Function to click on Debug in breakpoint window
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_D,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D,1);
	}
	public static String DebugConnection(String Password) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: DebugConnection()
		DESCRIPTION	 		: Function to Debug the Function.
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		/*BaseActions.SetText(DebugElements.sDebugConnection, "", DebugElements.sDebugpassword,Password);
		BaseActions.Click(DebugElements.sDebugConnection, "", DebugElements.sDebugOKButton);*/
		Thread.sleep(GlobalConstants.MedWait);
		if(BaseActions.WinExists(ErrorElements.wDebugConnectionError))
		{
			BaseActions.Click(ErrorElements.wDebugConnectionError,"",ErrorElements.bOK);
			return "DebugConnectionError";
		}
		else
			return "DebugConnectionSuccessful";
	}
	public static void ClickConsoleTab() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: ClickConsoleTab()
	DESCRIPTION	 		: Function to Click Console to see the results.
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.MouseClick(DebugElements.wDebugpane, "", DebugElements.Consoleclick, DebugElements.sMouseButton, DebugElements.iButtonClick, DebugElements.iConsoleclickxcord, DebugElements.iConsoleclickycord);
	}

	public static void ClickContinue() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: ClickContinue()
	DESCRIPTION	 		: Function to Click Continue .
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_F9,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F9,1);
		Thread.sleep(GlobalConstants.MedWait);

	}
	public static void StepIn() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: StepIn()
	DESCRIPTION	 		: Function to do StepIn Operation.
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_F7,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F7,1);
		Thread.sleep(GlobalConstants.MedWait);
	}
	public static void StepOut() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: StepOut()
	DESCRIPTION	 		: Function to do StepOut Operation.
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F7,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F7,1);
		Thread.sleep(GlobalConstants.MedWait);
	}
	public static void TerminateDebugging() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: TerminateDebugging()
	DESCRIPTION	 		: Function to Terminate Debugging Session.
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_F10,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F10,1);
	}
	public static void StepOver() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: StepOver()
	DESCRIPTION	 		: Function to perform Step Over Operation.
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_F8,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F8,1);
		Thread.sleep(GlobalConstants.MedWait);
	}
	public static void SetVariableClick() throws Exception{
		/*************************************************************************
	FUNCTION NAME		: SetVariableClick()
	DESCRIPTION	 		: Function to set Variable.
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.MouseClick(DebugElements.wDebugpane, "", DebugElements.SetVariableclick, DebugElements.sMouseButton, DebugElements.iClick, DebugElements.iSetVariablexcord, DebugElements.iSetVariableycord);
	}

	public static void SetVariableValue(String VariableValue) throws Exception{
		/*************************************************************************
	FUNCTION NAME		: SetVariableValue()
	DESCRIPTION	 		: Function to set Variable.
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
		 *************************************************************************/
		Thread.sleep(GlobalConstants.MedWait);
		AutoItX x = new AutoItX();
		BaseActions.SetText("Set Variable Value", "", "Edit1", VariableValue);
		x.controlClick("Set Variable Value", "", "Button1");
		Thread.sleep(GlobalConstants.MedWait);
	}

	public static void RemoveConnection() throws Exception{
		/*************************************************************************
		FUNCTION NAME : RemoveConnection()
		DESCRIPTION : Function to Remove Connection.
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		 *************************************************************************/
		BaseActions.MouseClick(LoginElements.wIDEWindow, "", DebugElements.ClickConnection, DebugElements.sMouseButton, DebugElements.iButtonClick, DebugElements.ClickConnectionxcord, DebugElements.ClickConnectionycord);
		BaseActions.Click(LoginElements.wIDEWindow, "", ExecQueryElements.sConnCombo);
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 2);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.Click("Remove server Confirmation", "", "Button1");

	}

	/*public static void RemoveConnection() throws Exception{
	 *//*************************************************************************
	FUNCTION NAME		: RemoveConnection()
	DESCRIPTION	 		: Function to Remove Connection.
	IN PARAMETERS		: None
	RETURN PARAMETERS	: None
	  *************************************************************************//*
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.MouseClick(DebugElements.wDebugpane, "", DebugElements.ClickConnection, DebugElements.sMouseButton, DebugElements.iButtonClick, DebugElements.ClickConnectionxcord, DebugElements.ClickConnectionycord);
		Thread.sleep(GlobalConstants.MedWait);
		BaseActions.MouseClick(DebugElements.wDebugpane, "", DebugElements.ClickDisconnectConnection, DebugElements.sMouseButton, DebugElements.iButtonClick, DebugElements.ClickDisconnectionxcord, DebugElements.ClickDisconnectionycord);
		BaseActions.Click("Remove server Confirmation", "", "Button1");
	}*/

	public static String DebugValidation() throws Exception{
		/*************************************************************************
		FUNCTION NAME		: DebugValidation
		DESCRIPTION	 		: Function to validate the console message after successful debug
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		String sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
		if(sFlag.contains("Connection profile 'Debug' connected successfully")&&(sFlag.contains("Debug started."))&&(sFlag.contains("Debugging completed."))&&(sFlag.contains("Executed Successfully")))
			return "DebugSuccess";
		else
			return "DebugFailed";
	}

	public static String DebugValidation(String Databasename) throws Exception{
		/*************************************************************************
		FUNCTION NAME		: DebugValidation
		DESCRIPTION	 		: Function to validate the console message after successful debug
		IN PARAMETERS		: None
		RETURN PARAMETERS	: None
		 *************************************************************************/
		String sFlag = QueryResult.ReadConsoleOutput("GLOBAL");
		if(sFlag.contains("Connection profile 'Debug' connected successfully")&&(sFlag.contains("Debug started."))&&(sFlag.contains("Debugging completed."))&&(sFlag.contains("Executed Successfully")))
			return "DebugSuccess";
		else
			return "DebugFailed";
	}

	public static void ObjectbrowserImport() throws Exception
	{
		//BaseActions.MouseClick(ObjectBrowserElements.wTitle, "", ObjectBrowserElements.sControlID, ObjectBrowserElements.sButton, ObjectBrowserElements.nclicks,74,8);
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
		Thread.sleep(GlobalConstants.MinWait);
	}
}