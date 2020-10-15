package script_library;

import java.awt.event.KeyEvent;

import object_repository.GlobalConstants;
import object_repository.TablespaceElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class TablespaceFunctions {

	public static void tablespace_Navigation() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_ALT,1);
		UtilityFunctions.KeyPress(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_Q,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ALT,1);
		Thread.sleep(GlobalConstants.MinWait);
		ObjectBrowserPane.objectBrowserRefresh("SINGLE");
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,2);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_DOWN,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_DOWN,1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_RIGHT,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_RIGHT,1);
	}

	public static void openTableSpace() throws Exception {

		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU,1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_C,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_C,1);
	}

	public static void tablespaceCreation(String sOption,String sValue) throws Exception
	{
		if(sOption.equalsIgnoreCase("NAME"))
		{
			BaseActions.SetText(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceName, sValue);
			Thread.sleep(GlobalConstants.MinWait);
		}

		else if (sOption.equalsIgnoreCase("LOCATION"))
		{
			BaseActions.SetText(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceLocation, sValue);
			Thread.sleep(GlobalConstants.MinWait);
		}

		else if(sOption.equalsIgnoreCase("UNLIMITEDSIZE"))
		{
			BaseActions.Click(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceUnlimitedsize);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sOption.equalsIgnoreCase("FILESYSTEM"))
		{
			BaseActions.Focus(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceCombobox);

			switch (sValue) {
			case "HDFS":  UtilityFunctions.KeyPress(KeyEvent.VK_H,1);
			UtilityFunctions.KeyRelease(KeyEvent.VK_H,1);
			break;

			default:
				break;
			}
		}
		else if(sOption.equalsIgnoreCase("ADDRESS"))
		{
			BaseActions.SetText(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceAddress, sValue);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sOption.equalsIgnoreCase("FILEPATH"))
		{
			BaseActions.SetText(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceFilepath, sValue);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sOption.equalsIgnoreCase("STOREPATH"))
		{
			BaseActions.SetText(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceStorepath, sValue);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sOption.equalsIgnoreCase("SEQCOST"))
		{
			BaseActions.SetText(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceseqcost, sValue);
			Thread.sleep(GlobalConstants.MinWait);
		}
		else if(sOption.equalsIgnoreCase("RANCOST"))
		{
			BaseActions.SetText(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespacerancost, sValue);
			Thread.sleep(GlobalConstants.MinWait);
		}

	}

	public static void maxSize(String sValue, char sOption) throws Exception	
	{
		BaseActions.SetText(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespaceMaxsize, sValue);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Focus(TablespaceElements.wTablespaceTitle, "", TablespaceElements.sTablespacemaxsize);
		switch (sOption) {

		case 'K':  UtilityFunctions.KeyPress(KeyEvent.VK_K,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_K,1);
		Thread.sleep(GlobalConstants.MinWait);
		break;

		case 'M':  UtilityFunctions.KeyPress(KeyEvent.VK_M,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_M,1);
		Thread.sleep(GlobalConstants.MinWait);
		break;

		case 'G':  UtilityFunctions.KeyPress(KeyEvent.VK_G,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_G,1);
		Thread.sleep(GlobalConstants.MinWait);
		break;

		case 'T':  UtilityFunctions.KeyPress(KeyEvent.VK_T,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
		Thread.sleep(GlobalConstants.MinWait);
		break;

		case 'P':  UtilityFunctions.KeyPress(KeyEvent.VK_P,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_P,1);
		Thread.sleep(GlobalConstants.MinWait);
		break;

		default:
			break;
		}
	}

	public static void button(String sOption) throws Exception	
	{
		switch (sOption) {
		case "OK":
			BaseActions.Click(TablespaceElements.wTablespaceTitle, "", TablespaceElements.bOk);
			break;
		case "CANCEL":
			BaseActions.Click(TablespaceElements.wTablespaceTitle, "", TablespaceElements.bCancel);
			break;


		default:
			break;
		}
	}

	public static void showDDL() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 3);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 3);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
	}
	public static void renameTablespaceObjectBrowser(String sNewName) throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_R, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		BaseActions.Winwait(TablespaceElements.wRenameTitle);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.SetText(TablespaceElements.wRenameTitle, "", TablespaceElements.sRenameWindow, sNewName);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click(TablespaceElements.wRenameTitle, "", TablespaceElements.bOkRenameButton);
	}

	public static void setTableSpaceOption(String iRandomCost,String iseqCost) throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 1);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		BaseActions.Winwait(TablespaceElements.wTableTitle);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.SetText(TablespaceElements.wTableTitle, "", TablespaceElements.sRandomCost, iRandomCost);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.SetText(TablespaceElements.wTableTitle, "", TablespaceElements.sSeqcost, iseqCost);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click(TablespaceElements.wTableTitle, "", TablespaceElements.bOkSetButton);
	}

	public static void setTableMaxsize(String sValue, char sOption) throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_S, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_S, 2);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		BaseActions.Winwait(TablespaceElements.wMaxSizeTtile);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.SetText(TablespaceElements.wMaxSizeTtile, "", TablespaceElements.sSetMaxSize, sValue);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Focus(TablespaceElements.wMaxSizeTtile, "", TablespaceElements.sTablespacemaxsize);
		switch (sOption) {

		case 'K':  UtilityFunctions.KeyPress(KeyEvent.VK_K,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_K,1);
		Thread.sleep(GlobalConstants.MinWait);
		break;

		case 'M':  UtilityFunctions.KeyPress(KeyEvent.VK_M,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_M,1);
		Thread.sleep(GlobalConstants.MinWait);
		break;

		case 'G':  UtilityFunctions.KeyPress(KeyEvent.VK_G,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_G,1);
		Thread.sleep(GlobalConstants.MinWait);
		break;

		case 'T':  UtilityFunctions.KeyPress(KeyEvent.VK_T,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_T,1);
		Thread.sleep(GlobalConstants.MinWait);
		break;

		case 'P':  UtilityFunctions.KeyPress(KeyEvent.VK_P,1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_P,1);
		Thread.sleep(GlobalConstants.MinWait);
		break;

		default:
			break;
		}
		BaseActions.Click(TablespaceElements.wMaxSizeTtile, "", TablespaceElements.bOkMaxSizeButton);
	}
	public static void dropTableSpaceObjeBrowser() throws Exception
	{
		UtilityFunctions.KeyPress(KeyEvent.VK_CONTEXT_MENU, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_CONTEXT_MENU, 1);
		Thread.sleep(GlobalConstants.MinWait);	
		UtilityFunctions.KeyPress(KeyEvent.VK_D, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_D, 1);
		BaseActions.Winwait(TablespaceElements.wDropTitle);
		Thread.sleep(GlobalConstants.MinWait);
		BaseActions.Click(TablespaceElements.wDropTitle, "",TablespaceElements.bOkDropButton);
	}
}
