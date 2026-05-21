# Introduction to DataStudio

Note: DataStudio is no longer maintained since August 25, 2023. All its functions have been transferred to the WebDS plug-in of DataKit.

## Version Overview

This version is openGauss DataStudio 5.1.0 and provides the following functions:

- Database objects management and creation (functions, stored procedures, tables, views, sequences, and triggers)
- SQL statements or SQL scripts execution
- Creation, execution, and debugging of functions and stored procedures
- Addition, deletion, modification, and query of table data
- Table data import and export
- DDL data display and export
- SQL assistant, formatting, execution history tracking

## Features

- Database objects management and creation

  You can manage or create database objects, including databases, schemas, functions, stored procedures, tables, columns, indexes, constraints, views, foreign tables, sequences, synonyms, triggers, tablespaces, users, and roles.

- SQL statements or SQL scripts execution

  You can run SQL statements or SQL scripts.

- Creation, execution, and debugging of functions and stored procedures

  DataStudio allows you to create, execute, and debug functions or stored procedures. During debugging, stack and variable information is displayed. Adding, deleting, enabling, and disabling breakpoints are supported.

- Addition, deletion, modification, and query of table data

  You can add, delete, modify, and query table data in a visualized manner.

- Table data import and export

  Table data can be imported from or exported to Excel, CSV, TXT, and binary files.

- Connection configuration file import and export

  Connection configuration files can be imported and exported.

- DDL data display and export

  DataStudio supports DDL display and export for various database objects, including tables, functions, stored procedures, views, sequences, and synonyms.

- SQL assistant, formatting, execution history tracking

  DataStudio provides SQL assistant features such as intelligent SQL completion, code formatting, and history tracking in the SQL editor.

- Security management

  DataStudio ensures database security across management, application, system, and network layers through SSL secure connections, user privilege management, and password policies.

## Important Notes

- openGauss DataStudio is the dedicated official client for openGauss databases. It includes a built-in openGauss JDBC driver and is designed solely for use with openGauss; it does not support other database platforms.
- Since openGauss DataStudio is a Java application, ensure that Java 11 or a newer version is installed.

# Source Code Compilation Guide

This section describes how to build a DataStudio binary package from source.

- ## Prerequisites

  1. Download and install JDK 11 at [JDK Download](http://jdk.java.net/archive/). Then, configure the `JAVA_HOME` environment variable. JDK 11.0.2 is recommended.
  
  2. Download and install Apache Maven 3.x, and configure the `M2_HOME` environment variable.
  
     [Maven 3.8.3](https://archive.apache.org/dist/maven/maven-3/3.8.3/binaries/apache-maven-3.8.3-bin.zip) is recommended.
  
  3. Download [JavaFX SDK 17.0.2](https://gluonhq.com/products/javafx/) to the local PC and decompress it to any directory.
  
  4. Download [openjfx.p2-17.0.2.zip](https://downloads.efxclipse.bestsolution.at/p2-repos/openjfx.p2-17.0.2.zip).
  
     Then, decompress it to any local directory.
  
  5. Configure the `javafx.home` and `url.openjfx` properties in the POM file.
  
     ```xml
     <javafx.home>Local_path\javafx-sdk-17.0.2</javafx.home>
     <url.openjfx>file:\\\Local_path\openjfx.p2-17.0.2</url.openjfx>
     ```
     
     Example:
     
     Decompress the downloaded `javafx-sdk-17.0.2` and `openjfx.p2-17.0.2` packages to the local drive D and configure the POM file.
     
     ```xml
     <javafx.home>D:\javafx-sdk-17.0.2</javafx.home>
     <url.openjfx>file:\\\D:\openjfx.p2-17.0.2</url.openjfx>
     ```

- ## Building from Source

  1. Run the `git bash` command to go to the `src` directory of DataStudio.

  2. Run the `mvn -version` command to check the Maven and JDK versions.

     ```shell
     $ mvn -version
      Apache Maven 3.8.4 (9b656c72d54e5bacbed989b64718c159fe39b537)
      Maven home: D:\tool\apache-maven-3.8.3
      Java version: 11, vendor: Oracle Corporation, runtime: D:\tool\openjdk-11\jdk-11
      Default locale: zh_CN, platform encoding: GBK
      OS name: "windows server 2016", version: "10.0", arch: "amd64", family: "windows"
     ```

  3. Run the compilation script.

     ```shell
     cd ${Data_Studio_code}\code\datastudio\src
     sh copyExternalsToBuild.sh
     mvn clean package -Dmaven.test.skip=true
     ```

  4. Check the location of the generated installation package.

     ```shell
     ${Data_Studio_code}\code\datastudio\build
     ```

# Precautions for Eclipse RCP Development

After importing the project to Eclipse, open the `mppdbide.product` file of the `org.opengauss.mppdbide.repository` project and click `Launch an Eclipse application` to start the project.

If the project fails to be started, perform the following operations to configure the current operating environment and then run the project again:

On the Eclipse menu, choose `run` > `run configurations` > `Plug-ins` > `Add Required Plug-ins` > `validate Plug-ins` > `Apply` > `Run`.

# Contributions

**Community**

As an openGauss user, you can contribute to the openGauss community in multiple ways. For details about how to contribute to the community, see [Contribution](https://opengauss.org/en/contribution/). Here, some methods are listed for reference.

**Special Interest Groups (SIGs)**

openGauss brings together people of common interest to form different SIGs. For details about existing SIGs, see the [SIG list](https://opengauss.org/en/contribution/).

You are welcome to join an existing SIG or create a SIG. For details about how to create a SIG, see [Special Interest Group (SIG)](https://opengauss.org/en/contribution/).

**Mail List and Issues**

You are welcome to actively help users solve problems raised in the [mail list](https://opengauss.org/en/community/mails/) and issues \(including [code repository issues](https://gitee.com/organizations/opengauss/issues)\). In addition, you can commit an issue. All these will help the openGauss community develop better.

**Documents**

You can contribute to the community by committing code. We also welcome your feedback on problems and difficulties, or suggestions on improving the usability and integrity of documents, for example, problems encountered when obtaining software or documents and difficulties encountered when using the system. You are welcome to pay attention to and help us improve the documentation module of the openGauss community.

**IRC**

openGauss has also set up an IRC channel as a supplementary venue for community support and interaction. For details, see [openGauss IRC](https://opengauss.org/en/community/onlineCommunication/).

# Document Licenses

This document complies with the [Creative Commons Attribution 4.0 International (CC BY 4.0) Deed](https://creativecommons.org/licenses/by/4.0/).
