@echo off
if exist "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Results\Jubula_TestResults\Smoke_right_click_resultset" (
    echo "hi"
)else (
	mkdir "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Results\Jubula_TestResults\Smoke_right_click_resultset"
)

cd /d C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Scripts\Config_Files\
call Starttestsuite_Smoke_right_click_resultset.bat

cd "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Results\Jubula_TestResults\Smoke_right_click_resultset"
del *junit*.xml

cd /d C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Scripts\Config_Files\

java -jar XMLParser.jar
pause