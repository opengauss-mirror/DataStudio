set SOURCE=..\..\..
set DEST=.\

::0 Delete the old folders
rmdir /Q /S %DEST%\com.huawei.mppdbide.repository\tools
rmdir /Q /S ..\docs
rmdir /Q /S %DEST%\..\build
rmdir /Q /S %DEST%\db_assistant

rmdir /Q /S %DEST%\com.huawei.mppdbide.presentation\src-test\com\huawei\mppdbide\test-libs
mkdir %DEST%\com.huawei.mppdbide.presentation\src-test\com\huawei\mppdbide\test-libs
xcopy %SOURCE%\buildtools\DS_CommonTestJars %DEST%\com.huawei.mppdbide.presentation\src-test\.
xcopy %SOURCE%\buildtools\DS_CommonTestJars\MockRunner %DEST%\com.huawei.mppdbide.presentation\src-test\.
xcopy %SOURCE%\buildtools\DS_CommonTestJars\easyMock %DEST%\com.huawei.mppdbide.presentation\src-test\.

::1. Copy the external files from platform, 3rd Src to respective src folders
xcopy %SOURCE%\3rd_src\apache-commons-collections\commons-collections4-4.4.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\apache-poi\poi-4.1.1.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\apache-poi\poi-ooxml-4.1.1.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\apache-poi\poi-ooxml-schemas-4.1.1.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\apache-xml-beans\xmlbeans-3.0.2.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\apache-commons-compress\commons-compress-1.21.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\apache-jakarta-commons-math\commons-math3-3.6.1.jar %DEST%\Common-collection\. /Y /S /E
xcopy %SOURCE%\3rd_src\commons-csv\commons-csv-1.7.jar %DEST%\commons-csv\. /Y /S /E
xcopy %SOURCE%\3rd_src\jsql-parser\jsqlparser-3.2.jar %DEST%\JSQLParser\. /Y /S /E
xcopy %SOURCE%\3rd_src\jquery\jquery.min.js %DEST%\..\db_assistant\js\. /Y /S /E
xcopy %SOURCE%\3rd_src\antlr\antlr4-runtime-4.7.2.jar %DEST%\com.huawei.mppdbide.parser\. /Y /S /E
xcopy %SOURCE%\3rd_src\apache-log4j\log4j-api-2.13.2.jar %DEST%\com.huawei.mppdbide.utils\. /Y /S /E
xcopy %SOURCE%\3rd_src\apache-log4j\log4j-core-2.13.2.jar %DEST%\com.huawei.mppdbide.utils\. /Y /S /E
xcopy %SOURCE%\3rd_src\gson\gson-2.8.6.jar %DEST%\com.huawei.mppdbide.bl\. /Y /S /E
xcopy %SOURCE%\3rd_src\gson\gson-2.8.6.jar %DEST%\com.huawei.mppdbide.explainplan\. /Y /S /E
xcopy %SOURCE%\3rd_src\gson\gson-2.8.6.jar %DEST%\com.huawei.mppdbide.view\. /Y /S /E
xcopy %SOURCE%\3rd_src\google\guice-4.2.0.jar %DEST%\com.huawei.mppdbide.view\. /Y /S /E
xcopy %SOURCE%\3rd_src\google\guava-30.1.1-jre.jar %DEST%\com.huawei.mppdbide.view\. /Y /S /E
xcopy %SOURCE%\platform\Gauss200\gs_jdbc\gsjdbc4.jar %DEST%\com.huawei.dbdriver.jdbc.gauss\. /Y /S /E

mkdir %DEST%\com.huawei.mppdbide.repository\tools
mkdir %DEST%\db_assistant

xcopy %DEST%\..\db_assistant %DEST%\db_assistant /Y /E
xcopy %SOURCE%\code\datastudio\src\StartDataStudio.bat %DEST%\com.huawei.mppdbide.repository\tools\.
mkdir ..\docs
xcopy %SOURCE%\information\datastudio ..\docs\. /Y /S /E
mkdir %DEST%\..\build
