REM Copyright (c) 2026 Huawei Technologies Co., Ltd.
REM DataStudio is licensed under Mulan PSL v2.
REM You can use this software according to the terms and conditions of the Mulan PSL v2.
REM You may obtain a copy of Mulan PSL v2 at:
REM          http://license.coscl.org.cn/MulanPSL2
REM THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
REM EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
REM MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
REM See the Mulan PSL v2 for more details.

@ECHO OFF
IF NOT DEFINED IS_MINIMIZED set IS_MINIMIZED=1 && start "" /min "%~dpnx0" %* && exit
SET PATH=%PATH%;%SystemRoot%\system32\WindowsPowerShell\v1.0;C:\Windows\winsxs\x86_microsoft-windows-w..ommand-line-utility_31bf3856ad364e35_6.1.7600.16385_none_a1802b822e2a878c;
SETLOCAL ENABLEEXTENSIONS
ECHO Checking for OS Name..............
For /F "usebackq tokens=2 delims=:" %%i in (`systeminfo /FO "LIST" ^| Findstr /B /C:"OS Name:"`) DO SET OS_NAME=%%i
Echo %OS_NAME%

SET OS_LANG=ENG
FOR /F "tokens=2 delims==" %%G in ('wmic os get OSLanguage /Value') DO SET OS_LANG=%%G
	IF /I "%OS_LANG%" EQU "1033" (
		SET LANG=ENG
	)
	IF /I "%OS_LANG%" EQU "4" (
		SET LANG=CHZ
	)
	IF /I "%OS_LANG%" EQU "31748" (
		SET LANG=CHZ
	)
	IF /I "%OS_LANG%" EQU "1028" (
		SET LANG=CHZ
	)
	IF /I "%OS_LANG%" EQU "2052" (
		SET LANG=CHZ
	)
	IF /I "%OS_LANG%" EQU "3076" (
		SET LANG=CHZ
	)
	IF /I "%OS_LANG%" EQU "4100" (
		SET LANG=CHZ
	)
	IF /I "%OS_LANG%" EQU "5124" (
		SET LANG=CHZ
	)

IF NOT EXIST """..\Data Studio.ini""" IF "%LANG%" == "ENG" ( 
	START /MIN CMD /c "powershell.exe [Reflection.Assembly]::LoadWithPartialName("""System.Windows.Forms""");[Windows.Forms.MessageBox]::show("""Data Studio.ini file does not exist""","""Data Studio""",0,48)>nul"
	Exit
	)

IF NOT EXIST """..\Data Studio.ini""" IF "%LANG%" == "CHZ" ( 
	START /MIN CMD /c "powershell.exe [Reflection.Assembly]::LoadWithPartialName("""System.Windows.Forms""");[Windows.Forms.MessageBox]::show("""Data Studio.ini �ļ������� ""","""Data Studio""",0,48)>nul"
	Exit
	)

ECHO Checking for Java Compatibility..............

:: Check Data Studio Version is 32 bit or 64 bit 
SET DS_BIT=0
FOR /F "tokens=*" %%G in ('findstr /C:"x86_64" "..\Data Studio.ini"') DO SET DS_VERSION=%%G
	IF /I "%DS_VERSION%" EQU "" (
		SET DS_BIT=32
		ECHO "DS VERSION is 32 Bit"
	) ELSE ( 
		SET DS_BIT=64
		ECHO "DS VERSION is 64 Bit"
	)

:: Step2 - Get the OS_VERSION (32 Bit or 64 Bit); 
SET OS_BIT=0
FOR /F "tokens=2 delims=:" %%G in ('Systeminfo ^| Findstr "x64-based"') DO SET OS_VERSION=64bit
	IF /I "%OS_VERSION%" EQU "64bit" (
		SET OS_BIT=64
		ECHO "OS VERSION is 64 Bit"
	) ELSE ( 
		SET OS_BIT=32
		ECHO "OS VERSION is 32 Bit"
	)


:JAVA_BIT_CHECK
	:: Step4 - Get the Java Version using wmic and check if it 64 Bit
	Echo Checking for Java 11 64 bit Version .................
	SET JAVA_BIT_64=0
	FOR /F "tokens=*" %%G in ('wmic product get name ^, version ^| findstr /B /C:"Java" ^| findstr "11" ^| findstr "64-bit"') DO SET JAVA_VERSION=%%G

	IF /I NOT "%JAVA_VERSION%" == "" (
			SET JAVA_BIT_64=64
			ECHO "Has Java 11 with 64 bit"
			GOTO JAVA11_32BIT_CHECK
			
	)
	

:JAVA11_32BIT_CHECK

	SET JAVA_BIT_32=0

	Echo Checking for Java 11 32 bit Version .................
	FOR /F "tokens=*" %%G in ('wmic product get name ^, version ^| findstr /B /C:"Java" ^| findstr "11" ^| findstr /V "64-bit" ^| findstr /V "Java\ Auto"') DO SET JAVA_VERSION1=%%G
	
	IF /I NOT "%JAVA_VERSION1%" == "" (
	
			SET JAVA_BIT_32=32
			REM ECHO %JAVA_BIT_32%
			ECHO "Has Java 11 with 32 bit"
			GOTO CHECK_LANG
		)
	
	
	IF %JAVA_BIT_64% == 0 IF %JAVA_BIT_32% == 0 (
		GOTO JAVA_LOWER_CHECK
	)
	GOTO CHECK_LANG
	
:JAVA_LOWER_CHECK
	SET JAVA_BIT=0
	Echo Checking for Java minimum version ..............
	

	FOR /F "tokens=*" %%G in ('wmic product get name ^, version ^| findstr /B /C:"Java" ^| findstr /V "11"') DO SET JAVA_LOWER_VERSION=%%G

		IF /I NOT "%JAVA_LOWER_VERSION%" == "" IF "%LANG%" == "ENG" (
			
			@echo off
			START /MIN CMD /c "powershell.exe [Reflection.Assembly]::LoadWithPartialName("""System.Windows.Forms""");[Windows.Forms.MessageBox]::show("""Data Studio is supported with minimum Java Version of 11. `n`nInstall Java version 11 in order to use Data Studio.""", """Unsupported Java Version""",0,48)>nul"
			GOTO EXIT
		) 
		IF /I NOT "%JAVA_LOWER_VERSION%" == "" IF "%LANG%" == "CHZ" (
			
			@echo off
			START /MIN CMD /c "powershell.exe [Reflection.Assembly]::LoadWithPartialName("""System.Windows.Forms""");[Windows.Forms.MessageBox]::show("""֧��Data Studio�����Java�汾Ϊ11��`n`n ʹ�� Data Studio ǰ�谲װ Java 11�� """, """ ��֧�ָ� Java �汾 """,0,48)>nul"
			GOTO EXIT
		) 
		

:CHECK_LANG


 echo Langauge is %LANG%

	IF /I "%LANG%" EQU "ENG" (
		GOTO PRINT_MSG_ENG
	) ELSE (
			GOTO PRINT_MSG_CHZ
			)
	TIMEOUT /T 5
	GOTO EXIT


	
:PRINT_MSG_ENG

::Invalid scenarios
:: Java 32 Bit and Java 64 bit is installed in machine	

		IF /I %JAVA_BIT_64% == %DS_BIT%  IF %OS_BIT% == 64 (
			
			Goto runDS	
			GOTO EXIT
		) 
		IF /I %JAVA_BIT_32% == %DS_BIT%  IF %OS_BIT% == 64 (
			Goto runDS	
			GOTO EXIT
		)
		IF /I %JAVA_BIT_32% == %DS_BIT% IF %OS_BIT% == 32 (
			GOTO runDS
			GOTO EXIT
		)


	IF %JAVA_BIT_64% == 64  IF %DS_BIT% == 32 IF %OS_BIT% == 64 (

			Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_64% Bit Data Studio %DS_BIT% Bit"
			START /MIN /B CMD /C "powershell.exe [Reflection.Assembly]::LoadWithPartialName("""System.Windows.Forms""");[Windows.Forms.MessageBox]::show("""You are attempting to run 32-bit Data Studio on: `n`n  . 64 bit OS `n . %OS_NAME%  `n . Java 11 64-bit JDK ^(Incompatible^) `n`n Please install Java 11 32-bit """, """Unsupported Java Version""",0,48)>nul"
			GOTO EXIT
	)		

	IF %JAVA_BIT_32% == 32  IF %DS_BIT% == 64 IF %OS_BIT% == 64 (

			Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_32% Bit Data Studio %DS_BIT% Bit"
			START /MIN CMD /C "powershell.exe [Reflection.Assembly]::LoadWithPartialName("""System.Windows.Forms""");[Windows.Forms.MessageBox]::show("""You are attempting to run 64-bit Data Studio on: `n`n  . 64 bit OS `n . %OS_NAME%  `n . Java 11 32-bit JDK ^(Incompatible^) `n`n Please install Java 11 64-bit """, """Unsupported Java Version""",0,48)>nul"
			GOTO EXIT
	)		 

	IF %JAVA_BIT_32% == 32  IF %DS_BIT% == 64 IF %OS_BIT% == 32 (

			Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_32% Bit Data Studio %DS_BIT% Bit"
			START /MIN CMD /C "powershell.exe [Reflection.Assembly]::LoadWithPartialName("""System.Windows.Forms""");[Windows.Forms.MessageBox]::show("""You are attempting to run 64-bit Data Studio on: `n`n  . 32 bit OS `n . %OS_NAME%  `n . DS Version 64 Bit ^(Incompatible^) `n`n Please install 32 bit DS """, """Unsupported DS Version""",0,48)>nul"
			GOTO EXIT
	)		 

	
	::Valid scenarios	

	IF %JAVA_BIT_64% == 64  IF %DS_BIT% == 64 IF %OS_BIT% == 64 (
			Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_64% Bit Data Studio %DS_BIT% Bit"
			GOTO runDS 
			GOTO EXIT
	)		

	IF %JAVA_BIT_32% == 32  IF %DS_BIT% == 32 IF %OS_BIT% == 32 (
			Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_32% Bit Data Studio %DS_BIT% Bit "
			GOTO runDS 
			GOTO EXIT
	) 
	IF %JAVA_BIT_32% == 32  IF %DS_BIT% == 32 IF %OS_BIT% == 64 (
		 	Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_32% Bit Data Studio %DS_BIT% Bit "
			GOTO runDS 
			GOTO EXIT
	) 	

:PRINT_MSG_CHZ

::Invalid scenarios
:: Java 32 Bit and Java 64 bit is installed in machine	

		IF /I %JAVA_BIT_64% == %DS_BIT%  IF %OS_BIT% == 64 (
			Goto runDS	
			GOTO EXIT
		) 
		IF /I %JAVA_BIT_32% == %DS_BIT%  IF %OS_BIT% == 64 (
			Goto runDS	
			GOTO EXIT
		)
		IF /I %JAVA_BIT_32% == %DS_BIT% IF %OS_BIT% == 32 (
			GOTO runDS
			GOTO EXIT
		)


	IF %JAVA_BIT_64% == 64  IF %DS_BIT% == 32 IF %OS_BIT% == 64 (
			
			Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_64% Bit Data Studio %DS_BIT% Bit"
			START /MIN /B CMD /C "powershell.exe [Reflection.Assembly]::LoadWithPartialName("""System.Windows.Forms""");[Windows.Forms.MessageBox]::show("""����ͼ�����»�������32λData Studio��`n`n  . 64 λ����ϵͳ `n . %OS_NAME%  `n . 64λJava 11 JDK ^��������^�� `n`n ��ʹ��64λ��Data Studio�� """, """��֧�ָ� Data Studio �汾""",0,48)>nul"
			GOTO EXIT
	)		

	IF %JAVA_BIT_32% == 32  IF %DS_BIT% == 64 IF %OS_BIT% == 64 (

			Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_32% Bit Data Studio %DS_BIT% Bit"
			START /MIN CMD /C "powershell.exe [Reflection.Assembly]::LoadWithPartialName("""System.Windows.Forms""");[Windows.Forms.MessageBox]::show("""����ͼ�����»�������64λData Studio�� `n`n  . 64λ����ϵͳ `n . %OS_NAME%  `n . 32λJava 11 JDK ^��������^��`n`n �밲װ64λJava 11�� """, """��֧�ָ� Java �汾""",0,48)>nul"
			GOTO EXIT
	)		 

	REM ADDED NEW SCENARIO FOR JAVA 32 BIT AND DS 64 BIT AND OS 32 BIT
	IF %JAVA_BIT_32% == 32  IF %DS_BIT% == 64 IF %OS_BIT% == 32 (

			Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_32% Bit Data Studio %DS_BIT% Bit"
			START /MIN CMD /C "powershell.exe [Reflection.Assembly]::LoadWithPartialName("""System.Windows.Forms""");[Windows.Forms.MessageBox]::show("""����ͼ�����»�������64λData Studio�� `n`n  . 32λ����ϵͳ `n . %OS_NAME%  `n . 32λJava 11 JDK ^��������^��`n`n ��ʹ��32λ��Data Studio�� """, """��֧�ָ� Data Studio �汾""",0,48)>nul"
			GOTO EXIT
	)		
	REM ADDED NEW SCENARIO
::Valid scenarios	

	IF %JAVA_BIT_64% == 64  IF %DS_BIT% == 64 IF %OS_BIT% == 64 (
			Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_64% Bit Data Studio %DS_BIT% Bit"
			GOTO runDS 
			GOTO EXIT
	)		

	IF %JAVA_BIT_32% == 32  IF %DS_BIT% == 32 IF %OS_BIT% == 32 (
		 	Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_32% Bit Data Studio %DS_BIT% Bit "
			GOTO runDS 
			GOTO EXIT
	) 
	IF %JAVA_BIT_32% == 32  IF %DS_BIT% == 32 IF %OS_BIT% == 64 (
		 	Echo "OS is %OS_BIT% , Java 11 %JAVA_BIT_32% Bit Data Studio %DS_BIT% Bit "
			GOTO runDS 
			GOTO EXIT
	) 	

:runDS
	Echo "Launching Data Studio"
	CD ..
	call START "" "Data Studio.exe" 
	TIMEOUT /T 5
	GOTO EXIT

@ECHO ON
:EXIT
Exit
	
	