@echo off
if exist "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Results\Jubula_TestResults\Smoke_Load_Default_Schema" (
    echo "hi"
)else (
	mkdir "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Results\Jubula_TestResults\Smoke_Load_Default_Schema"
)

cd /d C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Scripts\Config_Files\
call Starttestsuite_Smoke_Load_Default_Schema.bat

cd "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Results\Jubula_TestResults\Smoke_Load_Default_Schema"
del *junit*.xml

cd /d C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Scripts\Config_Files\

java -jar XMLParser.jar
pause