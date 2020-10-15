/*************************************************************************
TITLE - PLAN AND COST
DESCRIPTION - FUNCTIONS WITH RESPECT PLAN AND COST WINDOW
AUTHORS - AWX321822
CREATED DATE - 22-NOV-2015
LAST UPDATED DATE - 14-DEC-2015
*************************************************************************/

package script_library;


import java.awt.Robot;
import java.awt.event.KeyEvent;

import autoitx4java.AutoItX;
import object_repository.ConsoleResultElements;
import object_repository.GlobalConstants;
import object_repository.PlanElements;
import support_functions.BaseActions;
import support_functions.UtilityFunctions;

public class PlanCost {


	public static void PlanCostClick() throws Exception{
		/*************************************************************************
		FUNCTION NAME : PlanCostClick()
		DESCRIPTION : Function to click on Toolbar icon for Plan and Cost
		IN PARAMETERS : None
		RETURN PARAMETERS : None
		*************************************************************************/
		QueryEditor.SelectConnection();
		Thread.sleep(GlobalConstants.MedWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_TAB, 2);
		UtilityFunctions.KeyRelease(KeyEvent.VK_TAB, 2);
		Thread.sleep(GlobalConstants.MinWait);
		UtilityFunctions.KeyPress(KeyEvent.VK_ENTER, 1);
		UtilityFunctions.KeyRelease(KeyEvent.VK_ENTER, 1);
		}

	public static void ExecutionPlan(String sFormatType,String sExecutionPlan) throws Exception {
	/*************************************************************************
	FUNCTION NAME		: ExecutionPlan()
	DESCRIPTION	 		: Function to select the opted for Plan,Cost and Format Type
	IN PARAMETERS		: sFormatType {Format type of the Plan and Cost}
	RETURN PARAMETERS	: sExecutionPlan {Type of Execution Plan}
	*************************************************************************/
		AutoItX x = new AutoItX();
		x.controlFocus("Execution Plan", "", "SWT_Window01");
		Thread.sleep(GlobalConstants.MedWait);
		String[] sExePlan = sExecutionPlan.split(",");
		for(int i=0;i<sExePlan.length;i++)
		{
			if(sExePlan[i].equals("ANALYSE"))
				BaseActions.Check(PlanElements.wExceutionTitle, "", "Button1");
			if(sExePlan[i].equals("VERBOSE"))
				BaseActions.Check(PlanElements.wExceutionTitle, "", "Button2");
			if(sExePlan[i].equals("COSTS"))
				BaseActions.Check(PlanElements.wExceutionTitle, "", "Button3");
			if(sExePlan[i].equals("BUFFERS"))
				BaseActions.Check(PlanElements.wExceutionTitle, "", "Button4");
			if(sExePlan[i].equals("TIMING"))
				BaseActions.Check(PlanElements.wExceutionTitle, "", "Button5");
		}
		switch (sFormatType) {
		case "TEXT":
			BaseActions.Click(PlanElements.wExceutionTitle, "",PlanElements.sTextButtonControlID);
			break;
		case "XML":
			BaseActions.Click(PlanElements.wExceutionTitle, "",PlanElements.sXMLButtonControlID);
			break;
		case "JSON":
			BaseActions.Click(PlanElements.wExceutionTitle, "",PlanElements.sJSONButtonControlID);
			break;
		case "YAML":
			BaseActions.Click(PlanElements.wExceutionTitle, "",PlanElements.sYAMLButtonControlID);
			break;
		}
		BaseActions.Click(PlanElements.wExceutionTitle, "",PlanElements.sOkButtonControlID);
		Thread.sleep(GlobalConstants.MedWait);
	}

	public static String PlanCostValidation(String sFormatType, String sValidationMessage) throws Exception{
	/*************************************************************************
	FUNCTION NAME		: PlanCostValidation()
	DESCRIPTION	 		: Function to select the opted for Plan,Cost and Format Type
	IN PARAMETERS		: sFormatType {Format type of the Plan and Cost}
	RETURN PARAMETERS	: sValidationMessage {Expected Output to be validated against Actual }
	*************************************************************************/
		AutoItX x = new AutoItX();
		Robot r = new Robot();
		int i=0;
		String sFlag = null;
		String sConsoleOutput;
		QueryResult.ReadConsoleOutput("TERMINAL");
		/*//BaseActions.Click(ConsoleResultElements.wConsoleResult, "", ConsoleResultElements.sConsolePane);
		Thread.sleep(GlobalConstants.MinWait);
		r.keyPress(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_A);
		r.keyRelease(KeyEvent.VK_CONTROL);
		r.keyRelease(KeyEvent.VK_A);
		r.keyPress(KeyEvent.VK_CONTROL);
		r.keyPress(KeyEvent.VK_C);
		r.keyRelease(KeyEvent.VK_CONTROL);
		r.keyRelease(KeyEvent.VK_C);*/
		Thread.sleep(GlobalConstants.MedWait);
		sConsoleOutput = QueryResult.ReadConsoleOutput("TERMINAL");
		String[] sMessages = sValidationMessage.split(",");
		Thread.sleep(GlobalConstants.MedWait);
		for(i=0;i<sMessages.length;i++)
		{
			if(sConsoleOutput.contains(sMessages[i]))
			{}	
			else
				sFlag = "Fail";
		}
		if(sFlag == "Fail")
			return sConsoleOutput;
		else
			return "Success";
	}
}