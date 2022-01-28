/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

/*************************************************************************
TITLE - OBJECT REPOSITORY OF PLAN/COST ELEMENT WINDOW
DESCRIPTION - COLLECTION OF EACH OBJECTS WITH THEIR IDENTIFIABLE-
PROPERTIES IN PLAN/COST ELEMENTS WINDOW
TEST CASES COVERED - NA
*************************************************************************/
package object_repository;

public class PlanElements {

	//Toolbar Co-Ordinates
	public static String wToolbarTitle="Data Studio";
	public static String sToolbarControlID="ToolbarWindow3219";
	public static String sbutton="left";
	public static int iclick=1;
	public static int ixcord=598;//333;
	public static int iycord=10;
	//Checkbox Co-Ordinates
	public static String wExceutionTitle="Execution Plan";
	public static String sExecutionControlID="Button1";
	//OK Button Object
	public static String sOkButtonControlID="Button11";
	//Format Type Objects
	public static String sTextButtonControlID="Button7";
	public static String sXMLButtonControlID="Button8";
	public static String sJSONButtonControlID="Button9";
	public static String sYAMLButtonControlID="Button10";
	//Execution Plan Objects
	public static String bAnalyse = "Button1";
	public static String bVerbose = "Button2";
	public static String bCosts = "Button3";
	public static String bBuffers = "Button4";
	public static String bTiming = "Button5";
	public static String bFormat = "Button6";

}