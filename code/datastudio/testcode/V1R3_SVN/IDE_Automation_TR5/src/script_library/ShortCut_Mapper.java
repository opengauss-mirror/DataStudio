package script_library;

import java.awt.event.KeyEvent;

import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.PreferencesElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class ShortCut_Mapper {
	
	public static String LaunchShortCutMapper() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_G, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_G, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
		Thread.sleep(GlobalConstants.MedWait);
		if(BaseActions.WinExists(PreferencesElements.wPreferences))
		{
			UtilityFunctions.KeyPress(KeyEvent.VK_UP, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_UP, 2);
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bCancel);
			UtilityFunctions.KeyPress(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_G, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ALT, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_G, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_P, 1);
			UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, 2);
			UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, 2);
			Thread.sleep(GlobalConstants.ModWait);
			if(BaseActions.ObjExists(PreferencesElements.wPreferences,"",PreferencesElements.lstShortCuts))
				return "Success";
			else
				return "Failure";
		}
		else
			return "Failure";
	}
	
	public static void UpdateShortCut(int iCommandNumber,String sAction,String sOption,String sApplyButton, String sOKCancelButton, String sRestartButton) throws Exception
	{
		BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.lstShortCuts);
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_HOME, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_HOME, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN, iCommandNumber);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN, iCommandNumber);
		if(sAction.equals("RESTORE"))
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bRestoreShortCutDefaults);
		else if(sAction.equals("MODIFY"))
		{
			Thread.sleep(GlobalConstants.MedWait);
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bModify);
			if(sOption.equals("OPTION1"))
			{
				BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.eBinding);
				UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
				UtilityFunctions.KeyPress(KeyEvent.VK_K,1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_K,1);
			}
			else if(sOption.equals("OPTION2"))
			{
				BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.eBinding);
				UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
				UtilityFunctions.KeyPress(KeyEvent.VK_M,1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_M,1);
			}
		}
		else if(sAction.equals("UNBINDKEY"))
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bUnbindKey);
		
		if(sApplyButton.equals("APPLY"))
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bApplyShortCut);
		
		if(sOKCancelButton.equals("OK"))
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bOKShorCut);
		else if(sOKCancelButton.equals("CANCEL"))
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bCancelShortCut);
		Thread.sleep(GlobalConstants.MedWait);
		if(sRestartButton.equals("Yes"))
		{
			BaseActions.Click(PreferencesElements.wDSRestart,"",PreferencesElements.bYes);
			Thread.sleep(GlobalConstants.SuperMaxWait);
			BaseActions.WinActivate(LoginElements.wDBConnection);
			String sConnection=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 0);
			String sHost=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 1);
			String sHostPort=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 2);
			String sDBName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 3);
			String sUserName=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 4);
			String sPassword=UtilityFunctions.GetExcelCellValue(GlobalConstants.sFunctionalTestDataFile, "IDELogin", 1, 5);
			Login.IDELogin(sConnection,sHost,sHostPort,sDBName,sUserName,sPassword,"PERMENANT");
			Thread.sleep(GlobalConstants.ModWait);
		}
		else if(sRestartButton.equals("No"))
			BaseActions.Click(PreferencesElements.wDSRestart,"",PreferencesElements.bNo);
	}

}
