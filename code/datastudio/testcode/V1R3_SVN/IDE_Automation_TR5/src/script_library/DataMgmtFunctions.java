package script_library;

import java.awt.event.KeyEvent;

import autoitx4java.AutoItX;
import object_repository.DataMgntElements;
import object_repository.GlobalConstants;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class DataMgmtFunctions {

	public static void connectToMulitpleDBFirsttime() throws Exception {
		BaseActions.MouseClick(DataMgntElements.wTitle, "",DataMgntElements.sControlID, DataMgntElements.sButton, DataMgntElements.nclicks,
				DataMgntElements.xcord, DataMgntElements.ycord);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		//UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		//UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
		BaseActions.SetText("", "", "", "Gaussdba@Mpp");
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);


	}

	public static void connectToMulitpleDB() throws Exception
	{
		BaseActions.MouseClick(DataMgntElements.wTitle, "",DataMgntElements.sControlID, DataMgntElements.sButton, DataMgntElements.nclicks,
				DataMgntElements.xcord, DataMgntElements.ycord);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
	}


	public static String openDBProperty() throws Exception
	{
		AutoItX x = new AutoItX();
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_O,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_O, 1);
		/*BaseActions.Focus(DataMgntElements.wTitle, "", DataMgntElements.sPControlID);
		BaseActions.MouseClick(DataMgntElements.wTitle, "",DataMgntElements.sPControlID, DataMgntElements.sButton, DataMgntElements.nProclicks,DataMgntElements.xpcord, DataMgntElements.ypcord);
		*/Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_C, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C, 1);
		Thread.sleep(GlobalConstants.MedWait);
		String scopy =x.clipGet();
		return scopy;
	}

	public static void closeDBProperty() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
		//BaseActions.MouseClick(DataMgntElements.wTitle, "", DataMgntElements.sprocloseID,  DataMgntElements.sButton, DataMgntElements.nProclicks, DataMgntElements.xpecord, DataMgntElements.ypecord);
	}

	public static void closeTableProperty() throws Exception
	{
		//BaseActions.MouseClick(DataMgntElements.wTitle, "", DataMgntElements.sprocloseID,  DataMgntElements.sButton, DataMgntElements.nProclicks,208,11);
		UtilityFunctions.KeyPress(KeyEvent.VK_SHIFT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_F4, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_SHIFT, 1);
	}


}
