# Data Studio Introduction

Notice: DataStudio will not maintenance from 2023.08.23,  we recommend use DataKit + WebDS plugin.

## Version introduction

This version is version 5.0.0 of opengauss Data Studio, which mainly provides the following functions:

- Manage / create database objects (functions, stored procedures, tables, views, sequences, triggers, etc.)
- Execute SQL statements or SQL scripts
- Create, execute, and debug functions or stored procedures
- Table data addition, deletion, modification and query
- Import / export table data
- Display / export DDL
- SQL assistant, formatting, execution history

## Feature introduction

- Manage / create database objects

  Support the management / creation of database, schema, function, stored procedure, table, column, index, constraint, view, appearance, sequence, synonym, trigger, tablespace, user / role and other database objects

- Execute SQL statement or SQL script

  Supports the execution of SQL statements or SQL scripts

- Create, execute and debug functions / stored procedures

  It supports the creation, execution and debugging of functions / stored procedures. In the debugging process, stack and variable information can be displayed, and operations such as adding, deleting, enabling and disabling breakpoints are supported

- Table data addition, deletion, modification and query

  Support the addition, deletion, modification and query of visual operation table data

- Import / export table data

  It supports the import and export of table data. The supported file formats include excel, CSV, txt and binary files

- Import / export connection profile

  Supports the import and export of connection configuration files

- Display / export DDL

  Support to display / export DDL of various database objects such as tables, functions / stored procedures, views, sequences, synonyms, etc

- SQL assistant, formatting, execution history

  It provides functions such as SQL assistant, editor, intelligent SQL prompt, formatting, historical SQL record, etc

- security management

  Support SSL secure network connection, user authority management, password management and other functions to ensure the security of the database in the management layer, application layer, system layer and network layer.

## Precautions for version use

- OpenGauss Data Studio is the only official client tool of openGauss database. Its built-in jdbc driver of openGauss is used together with openGauss database and cannot be used as a client tool of other databases
- OpenGauss Data Studio is a Java application. When using it, make sure to configure the Java 11 + running environment

# Source code compilation Guide

Specific steps of Building Data Studio binary package through source code

- ## Preconditions

  1. Download and install jdk11 http://jdk.java.net/archive/  and configure `Java_ Home` environment variable. JDK recommended version 11.0.2

  2. Download and install maven x. And configure `M2_ Home` environment variable. 

     Maven recommends version 3.8.3  https://archive.apache.org/dist/maven/maven-3/3.8.3/binaries/apache-maven-3.8.3-bin.zip

  3. Adoption [https://gluonhq.com/products/javafx/](https://gitee.com/link?target=https%3A%2F%2Fgluonhq.com%2Fproducts%2Fjavafx%2F)  Download SDK 17.0.2 to any local directory and unzip it to javafx-17.0.2

  4. Adoption [https://downloads.efxclipse.bestsolution.at/p2-repos/openjfx.p2-17.0.2.zip](https://gitee.com/link?target=https%3A%2F%2Fdownloads.efxclipse.bestsolution.at%2Fp2-repos%2Fopenjfx.p2-17.0.2.zip)  Download openjfx p2-17.0.2. Zip and unzip to any local directory.

  5. Configure POM file properties configuration information javafx.home and url.openjfx information .

     ```xml
     <javafx.home>local_directory\javafx-sdk-17.0.2</javafx.home>
     <url.openjfx>file:\\\local_directory\openjfx.p2-17.0.2</url.openjfx>
     ```
     
     Example:
     
     Download javafx-sdk-17.0.2 and openjfx P2-17.0.2 unzip to disk D of the local directory，Configure .
     
     ```xml
     <javafx.home>D:\javafx-sdk-17.0.2</javafx.home>
     <url.openjfx>file:\\\D:\openjfx.p2-17.0.2</url.openjfx>
     ```
     
     

- ## Source Compilation

  1. Enter the src directory of Data Studio source code through git bash command line:

  2. Use mvn - version command to check and confirm Maven and JDK version information.

     ```
     $ mvn -version
     Apache Maven 3.8.4 (9b656c72d54e5bacbed989b64718c159fe39b537)
     Maven home: D:\tool\apache-maven-3.8.3 Java version: 11, vendor: Oracle Corporation, runtime: D:\tool\openjdk-11\jdk-11 Default locale: zh_CN, platform encoding: GBK OS name: "windows server 2016", version: "10.0", arch: "amd64", family: "windows"
     ```

  3. Run compiled script

     ```
     cd ${Data_Studio_code}\code\datastudio\src
     sh copyExternalsToBuild.sh
     mvn clean package -Dmaven.test.skip=true
     ```

   4. The location of the generated  installation package is:

      ```
      ${Data_Studio_code}\code\datastudio\build
      ```



# Eclipse RCP development considerations

After eclipse imports the project, open org Opengauss Mppdbide Mppdbide Product file, click launch an eclipse application to run the startup project.

If the project fails to start, configure the current running environment and run it after the following operations.

Click  run-> run configurations - > plug ins - > Add required plug ins - > validate plug ins - > apply - > run in eclipse menu to start the project

# Participating Contributions

**Participating Contributions**

As an openGauss user, you can assist the openGauss community in a variety of ways. See [Community Contribution] for ways to participate in community contributions. https://opengauss.org/zh/contribution.html ), Here is a simple list of some ways for reference.

**Special Interest Groups**

OpenGauss brings together people of common interest to form different special interest groups (SIGs). Currently existing SIGs see [SIG List](https://opengauss.org/zh/contribution.html)。

We welcome and encourage you to join an existing SIG or to create a new SIG, as described in the [SIG Management Guide]( https://opengauss.org/zh/contribution.html ).

**Mailing Lists and Tasks**

Welcome to actively help users solve problems in [mailing list]( https://opengauss.org/zh/community/mails.html ) and issue tasks (including [Code Warehouse Tasks](https://gitee.com/organizations/opengauss/issues) ) Questions raised. In addition, we welcome your question. These will help the openGauss community grow better.

**Documentation**

Not only can you contribute to the community by submitting code, but we also welcome your feedback on problems, difficulties, or suggestions for improving the usability and integrity of your documents. For example, problems in obtaining software or documentation, and difficulties in using the system. Welcome to pay attention to and improve the documentation module for the openGauss community.

**IRC**

OpenGauss also opens channels at IRC as an additional channel for community support and interaction. See  [openGauss IRC](https://opengauss.org/zh/community/onlineCommunication.html) for details.

# Open source documentation should have a corresponding document license

This document follows [Knowledge Sharing License Agreement CC 4.0](https://creativecommons.org/licenses/by/4.0/).