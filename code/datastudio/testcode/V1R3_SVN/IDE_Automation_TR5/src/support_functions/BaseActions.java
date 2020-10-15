/*************************************************************************
TITLE - BASE ACTIONS
DESCRIPTION - OVERRIDDEN FUNCTIONS WITH RESPECT TO AUTOIT 
AUTHORS - AWX321824
CREATED DATE - 16-NOV-2015
LAST UPDATED DATE - 16-NOV-2015
MODIFICATION HISTORY - 
TEST CASES COVERED - NA
 *************************************************************************/
package support_functions;

import java.awt.event.KeyEvent;

import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import script_library.QueryEditor;
import autoitx4java.AutoItX;

public class BaseActions {

	public static void SetText(String title, String text, String controlId,String key) throws Exception {
		AutoItX x = new AutoItX();
		x.controlFocus(title, "", controlId);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DELETE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_A, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DELETE, 1);
		Thread.sleep(GlobalConstants.MinWait);
		x.send(key);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void Click(String title, String text, String controlID) throws Exception {

		AutoItX x = new AutoItX();
		x.controlClick(title, text, controlID);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void MouseClick(String title, String text, String controlID,String button,int clicks,int a,int b) throws Exception
	{
		AutoItX x = new AutoItX();
		x.controlClick(title, text, controlID, button, clicks, a, b);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static void ClearConsole(String sConsoleType) throws Exception {
		Thread.sleep(GlobalConstants.MedWait);
		if(sConsoleType.equals("GLOBAL"))
		{
			//Navigate to Global Console
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 2);
		}
		else
		{
			//Navigate to Terminal Console
			QueryEditor.SetTerminalFocus();
			UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 2);
			UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 1);
		}
		UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
	} 
	/*public static void ClearConsole(String sType) throws Exception {
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
		if(sType.equals("Debug"))
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sDebugMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
		else
			BaseActions.MouseClick(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sMouseClick, ConsoleResultElements.sMouseButton, ConsoleResultElements.iClick, ConsoleResultElements.iConsolexcord, ConsoleResultElements.iConsoleycord);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click(ConsoleResultElements.wConsoleResult, "",ConsoleResultElements.sConsoleClear);
		Thread.sleep(GlobalConstants.MinWait);
	}    */

	public static boolean WinExists(String sWindowName) throws Exception
	{
		AutoItX x = new AutoItX();
		boolean bExists = x.winExists(sWindowName);
		return bExists;
	}

	public static void WinClose(String sWindowName) throws Exception
	{
		AutoItX x = new AutoItX();
		x.winClose(sWindowName);
	}

	public static void Winwait(String sWindowName) throws Exception
	{
		AutoItX x = new AutoItX();
		x.winWaitActive(sWindowName);
	}


	public static void Check(String title, String text, String controlID){

		AutoItX x = new AutoItX();
		x.controlCommandCheck(title, text, controlID);

	}

	public static boolean ObjExists(String title, String text, String controlID)
	{
		AutoItX x = new AutoItX();
		boolean sFlag;
		sFlag = x.controlCommandIsVisible(title, text, controlID);
		return sFlag;
	}

	public static void Focus(String title, String text, String controlID) throws Exception {

		AutoItX x = new AutoItX();
		x.controlFocus(title, text, controlID);
		Thread.sleep(GlobalConstants.MinWait);
	}

	public static String ControlGetText(String sWindowName, String Text,String ControlID) throws Exception
	{
		AutoItX x = new AutoItX();
		String sControlText = x.controlGetText(sWindowName, Text, ControlID);
		return sControlText;
	}

	public static String WinGetText(String sWindowName) throws Exception
	{
		AutoItX x = new AutoItX();
		String sWinText = x.winGetText(sWindowName);
		return sWinText;
	}
	public static void WinActivate(String sWindowName) throws Exception
	{
		AutoItX x = new AutoItX();
		x.winActivate(sWindowName);
	}

	public static void SetAutoItOption(String sOption, String sParam) throws Exception {
		AutoItX x = new AutoItX();
		x.autoItSetOption(sOption, sParam);
	}
	public static String WinGetTitle(String sWindowName) throws Exception
	{
		AutoItX x = new AutoItX();
		String sWinTitle = x.winGetTitle(sWindowName);
		return sWinTitle;
	}
	public static void CloseActiveTerminal() throws Exception {
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F4, 1);
	}
	public static void ControlMouseClick(String sButton,int iXcord, int iYcord, int iClicks, int iSpeed) throws Exception
	{
		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		Thread.sleep(GlobalConstants.ModWait);
		x.mouseClick(sButton,iXcord,iYcord, iClicks,iSpeed);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		Thread.sleep(GlobalConstants.ModWait);
	}
	
	public static void CreateNewConnection() throws Exception {
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_N, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_N, 1);
		Thread.sleep(GlobalConstants.MedWait);
		}
}



