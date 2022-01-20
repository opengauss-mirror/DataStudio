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
 
package object_repository;

public class CreateTableWizardElements {

	//create table wizard elements
	public static String wCreateTitle ="Data Studio";
	public static String sCreateControlID = "SysTreeView321";
	public static String sColumnsID = "SysListView321";
	
	//General tab Elements	
	public static String wTitle = "Create New table";
	public static String sGenControlID = "SWT_Window02";
	public static String sTableControlID = "Edit3";
	public static String sTableName = "TestTable"; 
	public static int xGencord=25;  
	public static int yGencord=13;

	//General tab table space elements
	public static String sTSpaceControlID = "ComboBox2";
	//public static String sTSpaceComboControlID = "ComboBox2";
	public static int xTcord=214;  
	public static int yTcord=12;

	//General tab table description Elements
	public static String sDescControlID = "SWT_Window04";
	public static String sDescKey = "Create a table with table name as TestTable and Description of table text box having very large text(3000 chars) and text containing special characters."
			+"Check in the SQL_Preview tab whether it is correct."
			+ "Create table with this name and then check in the MPP Gauss whether same SQL command has been executed as listed in SQL Preview Tab.";

	//General Tab quoted option elements
	public static String sQuotedControlID = "Button4" ;

	public static String wDescTitle= "Update Table Description";
	public static String resetDescControlID = "SWT_Window03";
	
	//general tab table type elements
	public static String sTableTypeControlID = "ComboBox3" ;
	
	//Fill Factor Elements
	
	public static String sfillFactorcontrolID = "Edit4";
	//Button elements
	public static String bFinishcontrolID = "Button1";
	public static String bNextcontrolID = "Button2";
	public static String bBackcontrolID = "Button3";
	public static String bEditControlID = "Button14";
	public static String bCancelControlID = "Button13";
	public static String bUpControlID = "Button15";
	public static String bDownControlID = "Button16";
	public static String bUpdateControlID = "Button12";
	public static String bDeleteControlID = "Button13";
	public static String cifexsists = "Button5";
	public static String cOids = "Button6";
	public static String cColumnOrentation = "Button7";
	public static String bColumnAddcontrolID = "Button12";
	public static String bTableAddcontrolID = "Button31";
	
	//Tab Control elements
	public static String sTabControlID = "SysTabControl321";

	//Column tab elements
	public static String sCloumnControlID ="Edit5";
	public static String sCloumnQuotedID ="Button11";
	public static String sTypeDescription = "Static12";

	//SQL Preview Elements
	public static String spreviewControlID = "SysTabControl321";
	public static String sButton="left";
	public static int nclicks=1;
	public static int xprecord=508;  
	public static int yprecord=13;
	public static String sPreCopyControlID = "SWT_Window024";

	//Array Dimension Elements	
	public static String sarrayControlID = "Edit6";

	//Data type Elements
	public static String sDatatypeControlID = "ComboBox5";
	public static String sDatatypeSchemaControlID = "ComboBox4";

	//Precision Elements
	public static String sPrecisionControlID = "Edit7";

	//Scale Elements
	public static String sScaleControlID = "Edit8";
	
	//column constraint Elements
	public static String sNotnullControlID = "Button8";
	public static String sUniqueControlID = "Button9";
	public static String sDefaultControlID = "SWT_Window010";
	public static String sCheckControlID = "SWT_Window011";
	public static String sColumnQuoteControlID = "Button10";
	public static String sAvailableColumnsConstraint= "SysHeader325";
	
	//Data Distribution Elements
	public static String sdataTypeControlID = "ComboBox6";
	public static String sHashColumn="SysHeader322";
	public static String bSelectHashColumn= "Button17";
	public static int xDataCord=162;  
	public static int yDataCord=14;
	
	//***********TABLE Constraint Elements**********
	public static String stableConstraintControlID = "ComboBox8";
	public static String sConstraintNameControlID = "Edit10";
	public static String sSelectColumn = "Button25";
	public static String sAvailableColumns = "SysHeader325";
	public static String sConstraintExpressionControlID= "SWT_Window017";
	public static String sConstraints = "SysListView327";
	public static String bConstraintAdd = "Button27";
	public static String bConstraintEdit = "Button28";
	public static String bConstraintDelete = "Button26";
	public static String bSelectColumn = "Button19";
	public static String bDeselectColumn = "Button20";
	public static int xTableCord=257;  
	public static int yTableCord=13;
	public static String availableConstraint="SysHeader327";
	public static String selectColumnConstraint="Button19";
	//******* Constraint Button Elements***************************
	public static String bconstraintAdd = "Button27";
	public static String bconstraintEdit = "Button28";
	public static String bconstraintDelete = "Button26";
	//************* Unique Constriant Button Elements*************
	
	public static String buniqueAdd = "Button19";
	public static String buniqueRemove = "Button20";
	
	//************* Unique Constriant Avaliable column Elements*************
	
	public static String sControlID = "SysListView325";
	public static int xUniqueCord = 104;
	public static int yUniqueCord = 34;
	
	//******************** TableSpace elements **************************
	
	public static String stableSpaceControlID = "ComboBox7";
	
	//********** Fill factor elements***********************
	
	public static String stableFillFactor = "Edit9";
	
	public static String cDefereable = "Button23";
	public static String cIntiallyDeferred = "Button24";
	
	//********** Indexes Tab elements***********************
	
	public static String wRenameIndexWindow = "Rename Index";
	public static String sIndexName = "Edit11";
	public static int xIndexCord=343;  
	public static int yIndexCord=11;
	public static String sIndexControlID = "SysTabControl321";
	public static String sPartialIndex = "SWT_Window021";
	public static String bUniqueIndex = "Button29";
	public static String sAccessCombo = "ComboBox9";
	public static String sTablespaceCombo = "ComboBox10";
	public static String sFillFactor = "Edit12";
	public static String sUserDefinedExp = "SWT_Window020";
	public static String sAvailableColumnIndex = "SysHeader328";
	public static String bSelectIndex = "Button30";
	public static String bDeselectIndex = "Button31";
	public static String bIndexAdd = "Button34";
	public static String bIndexDelete = "Button35";
	public static String bIndexEdit = "Button36";
	public static String sIndexDefinition="SysHeader3210";
	public static String sChangeColumnOrder="SysHeader329";
	public static String bUpIndex="Button32";
	public static String bDownIndex="Button33";
	
	// Error messages
	public static String sEditError = "A column is currently being edited. Complete or cancel the Edit operation to perform other operations.";
	
	//Drop table 
	
	public static String wDropTable = "Drop Table";
	public static String wproperty="SWT_Window016";
	
}
