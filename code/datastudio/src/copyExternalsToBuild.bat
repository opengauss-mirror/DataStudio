set SOURCE=..\..\..
set DEST=.\
set TEST=..\testcode\LLT\

call mvn dependency:copy-dependencies -f %SOURCE%\3rd_src\pom.xml
::0 Delete the old folders
rmdir /Q /S %DEST%\org.opengauss.mppdbide.repository\tools
rmdir /Q /S ..\docs
rmdir /Q /S %DEST%\..\build
rmdir /Q /S %DEST%\db_assistant

xcopy %SOURCE%\platform\Gauss200\gs_jdbc\gsjdbc4.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\junit-4.11.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-logging-1.0.4.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\oro-2.0.8.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\jdom-2.0.2.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-jdbc-2.0.6.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-core-2.0.6.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\xml-apis-1.0.b2.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\cglib-nodep-3.3.0.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\easymock-4.3.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\objenesis-3.2.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-codec-1.15.jar %DEST%\org.opengauss.mppdbide.presentation\src-test\.
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar  %DEST%\org.opengauss.mppdbide.presentation\src-test\.

::1. Copy the external files from platform, 3rd Src to respective src folders
xcopy %SOURCE%\3rd_src\target\dependency\commons-collections4-4.4.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\poi-4.1.2.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\poi-ooxml-4.1.2.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\poi-ooxml-schemas-4.1.2.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\xmlbeans-3.0.2.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\commons-compress-1.21.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\commons-math3-3.6.1.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\commons-csv-1.7.jar %DEST%\commons-csv\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\jsqlparser-3.2.jar %DEST%\JSQLParser\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\antlr4-runtime-4.9.3.jar %DEST%\org.opengauss.mppdbide.parser\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\log4j-api-2.17.1.jar %DEST%\org.opengauss.mppdbide.utils\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\log4j-core-2.17.1.jar %DEST%\org.opengauss.mppdbide.utils\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar %DEST%\org.opengauss.mppdbide.bl\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\commons-lang3-3.12.0.jar %DEST%\org.opengauss.mppdbide.bl\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar %DEST%\org.opengauss.mppdbide.explainplan\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar %DEST%\org.opengauss.mppdbide.view\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\guice-4.2.0.jar %DEST%\org.opengauss.mppdbide.view\. /Y /S /E
xcopy %SOURCE%\3rd_src\target\dependency\guava-30.1.1-jre.jar %DEST%\org.opengauss.mppdbide.view\. /Y /S /E
xcopy %SOURCE%\platform\Gauss200\gs_jdbc\gsjdbc4.jar %DEST%\org.opengauss.dbdriver.jdbc.gauss\. /Y /S /E

rmdir /Q /S %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib
mkdir %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib
xcopy %SOURCE%\3rd_src\target\dependency\commons-logging-1.0.4.jar %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\oro-2.0.8.jar %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jdom-2.0.2.jar %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\xml-apis-1.0.b2.jar %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\easymock-4.3.jar %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar  %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-jdbc-2.0.6.jar  %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-core-2.0.6.jar  %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-codec-1.15.jar %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.osgi-3.9.1.v20130814-1242.jar  %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.
xcopy %SOURCE%\platform\Gauss200\gs_jdbc\gsjdbc4.jar %TEST%\org.opengauss.mppdbide.adapter.test.fragment\lib\.

rmdir /Q /S %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib
mkdir %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib
xcopy %SOURCE%\3rd_src\target\dependency\commons-logging-1.0.4.jar %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\oro-2.0.8.jar  %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jdom-2.0.2.jar  %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\xml-apis-1.0.b2.jar  %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar  %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\easymock-4.3.jar  %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar  %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar  %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-jdbc-2.0.6.jar %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-core-2.0.6.jar %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\junit-4.11.jar %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-io-2.11.0.jar %TEST%\org.opengauss.mppdbide.bl.debug.test.fragment\lib\.

rmdir /Q /S %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib
mkdir %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib
xcopy %SOURCE%\3rd_src\target\dependency\commons-logging-1.0.4.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\oro-2.0.8.jar  %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jdom-2.0.2.jar  %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jsqlparser-3.2.jar  %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\xml-apis-1.0.b2.jar  %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar  %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\easymock-4.3.jar  %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar  %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar  %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\junit-4.11.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jsqlparser-3.2.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.osgi-3.9.1.v20130814-1242.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-io-2.11.0.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-api-mockito-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-module-testng-common-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-api-mockito-common-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-api-support-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-module-testng-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-core-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-api-easymock-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-classloading-base-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-module-junit4-legacy-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-module-javaagent-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-module-junit4-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-module-junit4-common-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-module-junit4-rule-agent-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-module-testng-agent-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\powermock-reflect-1.6.6.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\bsh-2.0b4.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\cglib-nodep-3.3.0.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\javassist-3.20.0-GA.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jcommander-1.27.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockito-all-1.10.19.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\objenesis-3.2.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\snakeyaml-1.30.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\platform\Gauss200\gs_jdbc\gsjdbc4.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.
xcopy %SOURCE%\platform\Gauss200\gs_jdbc\mockrunner-jdbc.jar %TEST%\org.opengauss.mppdbide.bl.test.fragment\lib\.

rmdir /Q /S %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib
mkdir %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib
xcopy %SOURCE%\3rd_src\target\dependency\commons-logging-1.0.4.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jdom-2.0.2.jar  %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-jdbc-2.0.6.jar  %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-core-2.0.6.jar  %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\xml-apis-1.0.b2.jar  %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar  %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\platform\Gauss200\gs_jdbc\gsjdbc4.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar  %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jsqlparser-3.2.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\junit-4.11.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar  %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-io-2.11.0.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\objenesis-3.2.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\easymock-4.3.jar  %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\objenesis-3.2.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\oro-2.0.8.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.core.contenttype-3.4.100.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.core.jobs-3.5.100.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.core.runtime-3.7.0.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.e4.core.services-2.0.100.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.e4.core.di-1.8.100.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.equinox.app-1.3.100.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.equinox.common-3.6.0.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.equinox.preferences-3.6.1.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.equinox.registry-3.6.100.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.osgi-3.9.1.v20130814-1242.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\org.eclipse.osgi.services-3.5.100.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\runtime_registry_compatibility-4.3.1.jar %TEST%\org.opengauss.mppdbide.bl.windows.test.fragment\lib\.

rmdir /Q /S %TEST%\org.opengauss.mppdbide.editor.extension.test.fragment\lib
mkdir %TEST%\org.opengauss.mppdbide.editor.extension.test.fragment\lib
xcopy %SOURCE%\3rd_src\target\dependency\antlr4-runtime-4.9.3.jar %TEST%\org.opengauss.mppdbide.editor.extension.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar  %TEST%\org.opengauss.mppdbide.editor.extension.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-codec-1.15.jar %TEST%\org.opengauss.mppdbide.editor.extension.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar %TEST%\org.opengauss.mppdbide.editor.extension.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\junit-4.11.jar %TEST%\org.opengauss.mppdbide.editor.extension.test.fragment\lib\.

rmdir /Q /S %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib
mkdir %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib
xcopy %SOURCE%\3rd_src\target\dependency\commons-logging-1.0.4.jar %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\oro-2.0.8.jar %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jdom-2.0.2.jar  %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\xml-apis-1.0.b2.jar %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\easymock-4.3.jar %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar  %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-codec-1.15.jar %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\junit-4.11.jar %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-jdbc-2.0.6.jar %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-core-2.0.6.jar %TEST%\org.opengauss.mppdbide.explainplan.test.fragment\lib\.

rmdir /Q /S %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib
mkdir %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib
xcopy %SOURCE%\3rd_src\target\dependency\commons-logging-1.0.4.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\oro-2.0.8.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jdom-2.0.2.jar  %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\xml-apis-1.0.b2.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\easymock-4.3.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-codec-1.15.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\junit-4.11.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\antlr4-runtime-4.9.3.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-jdbc-2.0.6.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-core-2.0.6.jar %TEST%\org.opengauss.mppdbide.parser.test.fragment\lib\.

rmdir /Q /S %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib
mkdir %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib
xcopy %SOURCE%\3rd_src\target\dependency\commons-logging-1.0.4.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\oro-2.0.8.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jdom-2.0.2.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-jdbc-2.0.6.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-core-2.0.6.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\xml-apis-1.0.b2.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-collections4-4.4.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\platform\Gauss200\gs_jdbc\gsjdbc4.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jsqlparser-3.2.jar  %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-codec-1.15.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-io-2.11.0.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\poi-4.1.2.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\poi-ooxml-4.1.2.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\poi-ooxml-schemas-4.1.2.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\xmlbeans-3.0.2.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\junit-4.11.jar %TEST%\org.opengauss.mppdbide.presentation.windows.test.fragment\lib\.

rmdir /Q /S %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib
mkdir %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib
xcopy %SOURCE%\3rd_src\target\dependency\commons-logging-1.0.4.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\oro-2.0.8.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jdom-2.0.2.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-jdbc-2.0.6.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-core-2.0.6.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\xml-apis-1.0.b2.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\platform\Gauss200\gs_jdbc\gsjdbc4.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jsqlparser-3.2.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\junit-4.11.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-codec-1.15.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-io-2.11.0.jar %TEST%\org.opengauss.mppdbide.util.windows.test.fragment\lib\.

rmdir /Q /S %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib
mkdir %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib
xcopy %SOURCE%\3rd_src\target\dependency\commons-logging-1.0.4.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\oro-2.0.8.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jdom-2.0.2.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\xml-apis-1.0.b2.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\hamcrest-api-1.0.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\easymock-4.3.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\gson-2.8.6.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\log4j-1.2.15.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-codec-1.15.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\junit-4.11.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\jsqlparser-3.2.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\commons-io-2.11.0.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-jdbc-2.0.6.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.
xcopy %SOURCE%\3rd_src\target\dependency\mockrunner-core-2.0.6.jar %TEST%\org.opengauss.mppdbide.utils.test.fragment\lib\.

mkdir %DEST%\org.opengauss.mppdbide.repository\tools
mkdir %DEST%\db_assistant

xcopy %DEST%\..\db_assistant %DEST%\db_assistant /Y /E
xcopy %SOURCE%\code\datastudio\src\StartDataStudio.bat %DEST%\org.opengauss.mppdbide.repository\tools\.
mkdir ..\docs
xcopy %SOURCE%\information\datastudio ..\docs\. /Y /S /E
mkdir %DEST%\..\build