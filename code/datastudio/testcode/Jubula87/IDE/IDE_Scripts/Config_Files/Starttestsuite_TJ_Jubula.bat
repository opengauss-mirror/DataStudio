set JUBULA_DB_NAME=%1 
set TEST_JOB=%2
set EXECUTION_OUTPUT=%3

python GenerateXML.py %JUBULA_DB_NAME% %TEST_JOB%

cd /d C:\Program Files\jubula_8.7.1.046\ite
echo -----TESTSUITE STARTED-------- 

testexec.exe -c "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Scripts\Config_Files\TJ_UAT_File.xml"  -generate_monitoring_report -resultdir "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Results\Jubula_TestResults\UAT_Result\" -datadir "C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Test_Data" > %EXECUTION_OUTPUT% 2>&1
echo -----TESTSUITE ENDED-----------
cd /d C:\Project_DB_Tool_Automation_Suite\IDE\IDE_Scripts\Config_Files\
