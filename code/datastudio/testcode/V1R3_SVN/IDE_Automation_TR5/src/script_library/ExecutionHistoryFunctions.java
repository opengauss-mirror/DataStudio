package script_library;

import java.awt.event.KeyEvent;

import autoitx4java.AutoItX;
import object_repository.GlobalConstants;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class ExecutionHistoryFunctions {

	public static void openExeHistory(int iTerminalNumber) throws Exception
	{
		if(iTerminalNumber==1)
		{
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click("Data Studio", "", "Button3");
		}
		else
		{
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, iTerminalNumber-1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, iTerminalNumber-1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 3);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 3);
			Thread.sleep(GlobalConstants.MedWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}
		
	}

	public static void closeExeHistory() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
	}

	public static void exeHistoryOperations(String sSelection) throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		Thread.sleep(GlobalConstants.MinWait);
		switch (sSelection) {
		case "LOAD": 	
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;

		case "LOADANDCLOSE":

			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;

		case "DELETE":

			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 2);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			BaseActions.Winwait("Delete SQL");
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;

		case "DELETEALL":

			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 3);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 3);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			BaseActions.Winwait("Delete SQL");
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);

			break;

		case "PIN":

			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 4);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 4);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;

		case "UNPIN":

			UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, 5);
			UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, 5);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
			break;

		default:
			break;
		}
	}

	public static String copyExeHistoryQuery(int irownumber) throws Exception
	{
		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		if(irownumber==1)
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
		}
		else
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, irownumber-1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, irownumber-1);
			Thread.sleep(GlobalConstants.MinWait);
		}
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_Y, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Y, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		Thread.sleep(GlobalConstants.MedWait);
		String clipGet = x.clipGet();

		return clipGet;
	}


	public static void selectExeHistoryQuery(int irownumber) throws Exception
	{
		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		if(irownumber==1)
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
		}
		else
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, irownumber-1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, irownumber-1);
			Thread.sleep(GlobalConstants.MinWait);
		}

	}

	public static String copyQueryExeHistoryTray(String sConnectionname,int irownumber) throws Exception
	{
		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		Thread.sleep(GlobalConstants.MinWait);
		if(irownumber==1)
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
			Thread.sleep(GlobalConstants.MinWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_UP, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 1);
		}
		else
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, irownumber-1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, irownumber-1);
			Thread.sleep(GlobalConstants.MedWait);

			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
			Thread.sleep(GlobalConstants.MinWait);
		}

		BaseActions.MouseClick("SQL Execution History - "+sConnectionname, "", "SWT_Window03", "left", 3, 10, 8);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		String clipGet = x.clipGet();

		return clipGet;
	}

}
