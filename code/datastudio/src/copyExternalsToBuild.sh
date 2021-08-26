#!/bin/bash
SOURCE=../../..
DEST=./

#::0 Delete the old folders
rm -rf $DEST/com.huawei.mppdbide.repository/tools
rm -rf ../docs
rm -rf $DEST/../build
rm -rf $DEST/db_assistant

rm -rf $DEST\com.huawei.mppdbide.presentation\src-test\*.jar
cp $SOURCE/buildtools/DS_CommonTestJars $DEST/com.huawei.mppdbide.presentation/src-test/.
cp $SOURCE%/buildtools/DS_CommonTestJars/MockRunner $DEST/com.huawei.mppdbide.presentation/src-test/.
cp $SOURCE/buildtools/DS_CommonTestJars/easyMock $DEST/com.huawei.mppdbide.presentation/src-test/.

#::1. Copy the external files from platform, 3rd Src to respective src folders
cp $SOURCE/3rd_src/apache-commons-collections/commons-collections4-4.4.jar $DEST/Common-collection/.
cp $SOURCE/3rd_src/apache-poi/poi-4.1.1.jar $DEST/Common-collection/.
cp $SOURCE/3rd_src/apache-poi/poi-ooxml-4.1.1.jar $DEST/Common-collection/.
cp $SOURCE/3rd_src/apache-poi/poi-ooxml-schemas-4.1.1.jar $DEST/Common-collection/.
cp $SOURCE/3rd_src/apache-xml-beans/xmlbeans-3.0.2.jar $DEST/Common-collection/.
cp $SOURCE/3rd_src/apache-commons-compress/commons-compress-1.21.jar $DEST/Common-collection/.
cp $SOURCE/3rd_src/apache-jakarta-commons-math/commons-math3-3.6.1.jar $DEST/Common-collection/.
cp $SOURCE/3rd_src/commons-csv/commons-csv-1.7.jar $DEST/commons-csv/. 
cp $SOURCE/3rd_src/jsql-parser/jsqlparser-3.2.jar $DEST/JSQLParser/.
cp $SOURCE/3rd_src/jquery/jquery.min.js $DEST/../db_assistant/js/.
cp $SOURCE/3rd_src/antlr/antlr4-runtime-4.7.2.jar $DEST/com.huawei.mppdbide.parser/.
cp $SOURCE/3rd_src/antlr/antlr4-runtime-4.7.2.jar $DEST/com.huawei.mppdbide.staticcheck/.
cp $SOURCE/3rd_src/apache-log4j/log4j-api-2.13.2.jar $DEST/com.huawei.mppdbide.utils/.
cp $SOURCE/3rd_src/apache-log4j/log4j-core-2.13.2.jar $DEST/com.huawei.mppdbide.utils/.
cp $SOURCE/3rd_src/gson/gson-2.8.6.jar $DEST/com.huawei.mppdbide.bl/.
cp $SOURCE/3rd_src/gson/gson-2.8.6.jar $DEST/com.huawei.mppdbide.explainplan/.
cp $SOURCE/3rd_src/gson/gson-2.8.6.jar $DEST/com.huawei.mppdbide.view/.
cp $SOURCE/3rd_src/gson/gson-2.8.6.jar $DEST/com.huawei.mppdbide.staticcheck/.
cp $SOURCE/3rd_src/google/guice-4.2.0.jar $DEST/com.huawei.mppdbide.view/.
cp $SOURCE/3rd_src/google/guava-30.1.1-jre.jar $DEST/com.huawei.mppdbide.view/.
cp $SOURCE/platform/Gauss200/gs_jdbc/gsjdbc4.jar $DEST/com.huawei.dbdriver.jdbc.gauss/.

mkdir $DEST/com.huawei.mppdbide.repository/tools
mkdir $DEST/db_assistant
mkdir $DEST/com.huawei.mppdbide.repository/tools/win/FileUtil

cp -r $DEST/../db_assistant/ $DEST/db_assistant/
cp -r $SOURCE/platform/FileUtil/* $DEST/com.huawei.mppdbide.repository/tools/win/FileUtil/.
cp $SOURCE/code/datastudio/src/StartDataStudio.bat $DEST/com.huawei.mppdbide.repository/tools/.
mkdir ../docs
cp -r $SOURCE/information/datastudio/* ../docs/. 
mkdir $DEST/../build
