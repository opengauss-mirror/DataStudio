package script_library;

import java.awt.event.KeyEvent;
import object_repository.GlobalConstants;
import object_repository.LoginElements;
import object_repository.PreferencesElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;
import autoitx4java.AutoItX;

public class SyntaxColoring {
	
	
	public static String LaunchSyntaxColoring() throws Exception
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
			return "Success";
		}
		else
			return "Failure";
	}
	
	public static void ColorSelection(String sColorType, String sSyntaxType, String sApplyButton, String sOKCancelButton, String sRestartButton) throws Exception
	{
		int iCount=1;
		if(sColorType.equals("RESTORE"))
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bRestoreDefaults);
		else
		{
			switch (sSyntaxType)
			{
			case "SINGLELINECOMMENT":
				BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bSingleLineComment);
				iCount=1;
				break;
			case "DEFAULT":
				BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bDefault);
				iCount=1;
				break;
			case "UNRESERVED":
				BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bUnreserved);
				iCount=1;
				break;
			case "RESERVED":
				BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bReserved);
				iCount=2;
				break;
			case "TYPE":
				BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bType);
				iCount=4;
				break;
			case "PREDICATE":
				BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bPredicate);
				iCount=5;
				break;
			case "CONSTANTS":
				BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bConstants);
				iCount=6;
				break;
			case "STRINGS":
				BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bStrings);
				iCount=1;
				break;
			}
			Thread.sleep(GlobalConstants.MinWait);
			if(sColorType.equals("SAME"))
			{
				UtilityFunctions.KeyPress(KeyEvent.VK_CONTROL,1);
				UtilityFunctions.KeyPress(KeyEvent.VK_HOME,1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_CONTROL,1);
				UtilityFunctions.KeyRelease(KeyEvent.VK_HOME,1);
			}
			else
			{
				UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT, iCount);
				UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT, iCount);
			}
			
			Thread.sleep(GlobalConstants.MedWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_SPACE, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_SPACE, 1);
			Thread.sleep(GlobalConstants.MedWait);
			UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}
		
		
		if(sApplyButton.equals("APPLY"))
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bApply);
		
		if(sOKCancelButton.equals("OK"))
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bOK);
		else if(sOKCancelButton.equals("CANCEL"))
			BaseActions.Click(PreferencesElements.wPreferences,"",PreferencesElements.bCancel);
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
	
	public static String ColorValidation(String sColorType, String sSyntaxType) throws Exception
	{
		float color=0.1f;
		AutoItX x = new AutoItX();
		int iYpixel = 135;
		String sFlag = "Fail";
		//Set the KeyWord and Get the Color
		switch (sSyntaxType)
		{
		case "SINGLELINECOMMENT":
			QueryEditor.SetQuery("--SINGLELINECOMMENT");
			color = x.pixelGetColor(373,iYpixel);
			break;
		case "DEFAULT":
			QueryEditor.SetQuery("GREEKROMAN");
			color = x.pixelGetColor(373,iYpixel);
			break;
		case "UNRESERVED":
			QueryEditor.SetQuery("ACTION");
			color = x.pixelGetColor(373,iYpixel);
			break;
		case "RESERVED":
			QueryEditor.SetQuery("CREATE");
			color = x.pixelGetColor(385,iYpixel);
			break;
		case "TYPE":
			QueryEditor.SetQuery("FLOAT");
			color = x.pixelGetColor(373,iYpixel);
			break;
		case "PREDICATE":
			QueryEditor.SetQuery("@@@");
			color = x.pixelGetColor(371,iYpixel);
			break;
		case "CONSTANTS":
			QueryEditor.SetQuery("BINARY");
			color = x.pixelGetColor(375,iYpixel);
			break;
		case "STRINGS":
			QueryEditor.SetQuery("'DATASTUDIO'");
			color = x.pixelGetColor(395,iYpixel);
			break;
		}
		//validate the color
		if(sColorType.equals("RESTORE"))
		{
			switch (sSyntaxType)
			{
			case "SINGLELINECOMMENT":
				if(color==4227200.0)
					sFlag="Pass";
				break;
			case "DEFAULT":
				if(color==0.0)
					sFlag="Pass";
				break;
			case "UNRESERVED":
				if(color==1.3119878E7)
					sFlag="Pass";
				break;
			case "RESERVED":
				if(color==9864784.0)
					sFlag="Pass";
				break;
			case "TYPE":
				if(color==7444981.0)
					sFlag="Pass";
				break;
			case "PREDICATE":
				if(color==1.4168717E7)
					sFlag="Pass";
				break;
			case "CONSTANTS":
				if(color==6402037.0)
					sFlag="Pass";
				break;
			case "STRINGS":
				if(color==9393919.0)
					sFlag="Pass";
				break;
			}
		}
		else if(sColorType.equals("CUSTOM"))
		{
			switch (sSyntaxType)
			{
			case "SINGLELINECOMMENT":
				if(color==1.2632256E7)
					sFlag="Pass";
				break;
			case "DEFAULT":
				if(color==8421376.0)
					sFlag="Pass";
				break;
			case "UNRESERVED":
				if(color==1.5005824E7)
					sFlag="Pass";
				break;
			case "RESERVED":
				if(color==1.1661952E7)
					sFlag="Pass";
				break;
			case "TYPE":
				if(color==1.0085621E7)
					sFlag="Pass";
				break;
			case "PREDICATE":
				if(color==5345530.0)
					sFlag="Pass";
				break;
			case "CONSTANTS":
				if(color==1.5579893E7)
					sFlag="Pass";
				break;
			case "STRINGS":
				if(color==9393824.0)
					sFlag="Pass";
				break;
			}
		}
		else if(sColorType.equals("SAME"))
		{
			switch (sSyntaxType)
			{
			case "SINGLELINECOMMENT":
				if(color==1.6744576E7)
					sFlag="Pass";
				break;
			case "DEFAULT":
				if(color==1.6744576E7)
					sFlag="Pass";
				break;
			case "UNRESERVED":
				if(color==1.49792E7)
					sFlag="Pass";
				break;
			case "RESERVED":
				if(color==1.439296E7)
					sFlag="Pass";
				break;
			case "TYPE":
				if(color==1.5579893E7)
					sFlag="Pass";
				break;
			case "PREDICATE":
				if(color==1.5569085E7)
					sFlag="Pass";
				break;
			case "CONSTANTS":
				if(color==1.5579893E7)
					sFlag="Pass";
				break;
			case "STRINGS":
				if(color==1.439296E7)
					sFlag="Pass";
				break;
			}
		}
		return sFlag;
	}

}
