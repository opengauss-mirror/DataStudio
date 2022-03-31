# Data Studio开源文档

# 1. 法律声明

**版权所有 © 2022 openGauss 社区。**

您对“本文档”的复制、使用、修改及分发受知识共享（Creative Commons）署名—相同方式共享4.0国际公共许可协议（以下简称“CC BY-SA 4.0”）的约束。为了方便用户理解，您可以通过访问https://creativecommons.org/licenses/by-sa/4.0/了解CC BY-SA 4.0的概要（但不是替代）。CC BY-SA 4.0的完整协议内容您可以访问如下网址获取：https://creativecommons.org/licenses/by-sa/4.0/legalcode。

**商标声明**

openGauss Data Studio为华为技术有限公司的商标。本文档提及的其他所有商标或注册商标，由各自的所有人拥有。

**免责声明**

本文档仅作为使用指导，除非适用法强制规定或者双方有明确书面约定, 华为技术有限公司对本文档中的所有陈述、信息和建议不做任何明示或默示的声明或保证，包括但不限于不侵权，时效性或满足特定目的的担保。

# 2. 发行说明

本文档是openGauss Data Studio 3.0.0版本的发行说明。

## 用户须知

openGauss是一款极致性能、安全、可靠的关系型（OLTP）开源数据库。openGauss Data Studio为openGauss社区发布的唯一官方客户端工具。采用协议“木兰宽松许可证”（Mulan PSL V2），用户可以自由复制、使用、修改、分发，不论修改与否。

openGauss Data Studo版本号采用点分位命名规则（X.Y.Z）模式，其中X表示架构变更版本，Y表示每年发布版本，Z表示补丁版本。正常每年会发布一个Y版本，重大架构或特性变更，发布一个新的X版本。openGauss Data Studio生命周期初步规划为3年。

## 版本介绍

本版本为openGauss Data Studio的3.0.0版本，主要提供如下功能：

- 管理/创建数据库对象（函数、存储过程、表、视图、序列、触发器等）
- 执行SQL语句或者SQL脚本
- 创建、执行、调试函数或存储过程
- 表数据增、删、改、查
- 导入/导出表数据
- 显示/导出DDL
- SQL助手、格式化、执行历史记录

## 特性介绍

- 管理/创建数据库对象

  支持管理/创建数据库、模式、函数、存储过程、表、列、索引、约束、视图、外表、序列、同义词、触发器、表空间、用户/角色等多种数据库对象

- 执行SQL语句或者SQL脚本

  支持执行SQL语句或SQL脚本

- 创建、执行、调试函数/存储过程

  支持创建、执行、调试函数/存储过程，在调试过程中，可显示堆栈、变量信息，支持断点的增、删、使能、禁用等操作

- 表数据增、删、改、查

  支持可视化操作表数据的增、删、改、查

- 导入/导出表数据

  支持表数据的导入和导出，支持的文件格式包括excel、csv、txt及二进制文件

- 导入/导出连接配置文件

  支持连接配置文件的导入和导出

- 显示/导出DDL

  支持显示/导出表、函数/存储过程、视图、序列、同义词等多种数据库对象的DDL

- SQL助手、格式化、执行历史记录

  提供SQL助手、编辑器智能SQL提示、格式化、历史SQL记录等功能

- 安全管理

  支持SSL安全网络连接、用户权限管理、密码管理等功能，保证数据库在管理层、应用层、系统层和网络层的安全性

## 版本使用注意事项

- openGauss Data Studio为openGauss数据库的唯一官方客户端工具，其内置openGauss的JDBC驱动，与openGauss数据库配套使用，不能作为其他数据库的客户端工具
- openGauss Data Studio为一个Java应用，在使用时需确保配置Java 8运行环境

## 已知问题

本版本是openGauss Data Studio第一个发布版本，无已知问题。

## CVE漏洞

本版本是openGauss Data Studio第一个发布版本，没有CVE披露漏洞。

## 源代码（待完善）

openGauss Data Studio代码仓：

## 参与贡献

**参与贡献**

作为openGauss用户，你可以通过多种方式协助openGauss社区。参与社区贡献的方法请参见[社区贡献](https://opengauss.org/zh/contribution.html)，这里简单列出部分方式供参考。

**特别兴趣小组**

openGauss将拥有共同兴趣的人们聚在一起，组成了不同的特别兴趣小组（SIG）。当前已有的SIG请参见[SIG列表](https://opengauss.org/zh/contribution.html)。

我们欢迎并鼓励你加入已有的SIG或创建新的SIG，创建方法请参见[SIG管理指南](https://opengauss.org/zh/contribution.html)。

**邮件列表和任务**

欢迎你积极地帮助用户解决在[邮件列表](https://opengauss.org/zh/community/mails.html)和issue任务（包括[代码仓任务](https://gitee.com/organizations/opengauss/issues)） 中提出的问题。另外，我们也欢迎你提出问题。这些都将帮助openGauss社区更好地发展。

**文档**

你不仅可以通过提交代码参与社区贡献，我们也欢迎你反馈遇到的问题、困难，或者对文档易用性、完整性的改进建议等。例如获取软件或文档过程中的问题，使用系统过程中的难点。欢迎关注并改进openGauss社区的文档模块。

**IRC**

openGauss也在IRC开辟了频道，作为提供社区支持和交互的额外渠道。详情请参见[openGauss IRC](https://opengauss.org/zh/community/onlineCommunication.html)。

## 致谢

我们忠心地感谢参与和协助openGauss Data Studio项目的所有成员。是你们的辛勤付出使得版本顺利发布，也为openGauss更好地发展提供可能。

# 3. 产品描述

本文档对openGauss Data Studio工具进行了详细描述，包括产品定位、系统架构分析、应用场景、运行环境、基本功能和特性等信息。

## 产品定位

openGauss Data Studio是openGauss社区发布的唯一官方客户端工具，支持用户通过图形化界面管理openGauss数据库中的对象，提升用户使用体验，并包含信创测试需要的部分功能。

## 系统架构

Data Studio是一个图形化的客户端工具，它通过JDBC驱动与openGauss数据库连接，采用C/S（Client/Server，即客户端服务器端）架构进行通信。

## 应用场景

- 直观的GUI让用户简单地管理openGauss数据库

## 运行环境

支持Windows平台，在使用时需确保配置Java 8运行环境

## 基本功能和特性

openGauss Data Studio作为强大的数据库管理工具，主要支持如下基本功能：

- 管理/创建数据库对象

  支持管理/创建数据库、模式、函数、存储过程、表、列、索引、约束、视图、外表、序列、同义词、触发器、表空间、用户/角色等多种数据库对象

- 执行SQL语句或者SQL脚本

  支持执行SQL语句或SQL脚本

- 创建、执行、调试函数/存储过程

  支持创建、执行、调试函数/存储过程，在调试过程中，可显示堆栈、变量信息，支持断点的增、删、使能、禁用等操作

- 表数据增、删、改、查

  支持可视化操作表数据的增、删、改、查

- 导入/导出表数据

  支持表数据的导入和导出，支持的文件格式包括excel、csv、txt及二进制文件

- 导入/导出连接配置文件

  支持连接配置文件的导入和导出

- 显示/导出DDL

  支持显示/导出表、函数/存储过程、视图、序列、同义词等多种数据库对象的DDL

- SQL助手、格式化、执行历史记录

  提供SQL助手、编辑器智能SQL提示、格式化、历史SQL记录等功能

- 安全管理

  支持SSL安全网络连接、用户权限管理、密码管理等功能，保证数据库在管理层、应用层、系统层和网络层的安全性

# 4. 技术白皮书

## 产品定位

openGauss Data Studio是openGauss社区发布的唯一官方客户端工具，支持用户通过图形化界面管理openGauss数据库中的对象，提升用户使用体验，并包含信创测试需要的部分功能。

## 应用场景

- 直观的GUI让用户简单地管理openGauss数据库

## 技术特点

openGauss Data Studio采用Eclipse RCP（Rich Client Platform，即富客户平台）框架进行开发。RCP是eclipse进化的产物，是Eclipse组织向用户提供的强大的开放性开发平台。用户基于RCP框架能够创建自己的基于Eclipse的应用程序。因此openGauss Data Studio的界面与Eclipse非常相近。

## 软件架构

Data Studio是一个图形化的客户端工具，它通过JDBC驱动与openGauss数据库连接，采用C/S（Client/Server，即客户端服务器端）架构进行通信。

## 部署方案

- openGauss Data Studio支持部署在Windows平台， 其通过JDBC驱动与openGauss数据库连接。
- openGauss支持单机部署和一主多备部署两种部署形态。Data Studio对openGauss的部署形态不做限制，但是由于openGauss备机只提供读服务，不能进行写操作，因此建议用Data Studio连接openGauss的主机。
- 由于Data Studio本质是通过JDBC远程连接openGauss数据库，因此在数据库端需配置可远程访问：（1）配置postgresql.conf文件的listen_addresses参数；（2）配置pg_hba.conf文件的ip白名单。
- 由于openGauss限制初始用户不能远程登录，因此在Data Studio中不能使用初始用户连接openGauss数据库。
- Data Studio会根据Windows用户在其安装路径下创建UserData目录，用以存放用户数据，并保存用户个性化设置，因此可以用不同的Windows用户同时运行Data Studio工具，但正常运行的前提是需保证Windows用户对Data Studio目录具有所有权限。

## 软硬件配置要求

- Data Studio部署在Windows平台，需配置Java 8运行环境
- openGauss数据库无强制要求，可部署在openEuler或者CentOS操作系统上，可正常使用即可

# 5. 特性描述

openGauss Data Studio作为强大的数据库管理工具，主要支持如下基本功能：

- 管理/创建数据库对象

  支持管理/创建数据库、模式、函数、存储过程、表、列、索引、约束、视图、外表、序列、同义词、触发器、表空间、用户/角色等多种数据库对象

- 执行SQL语句或者SQL脚本

  支持执行SQL语句或SQL脚本

- 创建、执行、调试函数/存储过程

  支持创建、执行、调试函数/存储过程，在调试过程中，可显示堆栈、变量信息，支持断点的增、删、使能、禁用等操作

- 表数据增、删、改、查

  支持可视化操作表数据的增、删、改、查

- 导入/导出表数据

  支持表数据的导入和导出，支持的文件格式包括excel、csv、txt及二进制文件

- 导入/导出连接配置文件

  支持连接配置文件的导入和导出

- 显示/导出DDL

  支持显示/导出表、函数/存储过程、视图、序列、同义词等多种数据库对象的DDL

- SQL助手、格式化、执行历史记录

  提供SQL助手、编辑器智能SQL提示、格式化、历史SQL记录等功能

- 安全管理

  支持SSL安全网络连接、用户权限管理、密码管理等功能，保证数据库在管理层、应用层、系统层和网络层的安全性

# 6. 编译指导书(需更新依赖下载的地址)

本文档介绍了通过源码构建Data Studio开发者环境的具体步骤，以帮助开发者快速构建Data Studio开发环境。

- ## 编译准备

  编译环境：eclipse RCP 2021-12版本

  前置条件：已安装jdk11和apache maven 3

- ## 源码编译

  ### (1) 下载源码

  在码云上下载Data Studio的源码

  ### (2) 下载依赖软件（需更新依赖下载地址）

  在eclipse中下载项目依赖的软件

  ```
  菜单栏help -> Install New Software...
  ```

  ![image-20220119212810688](.\photo\image-20220119212810688.png)

  在Work with对应的文本框中输入下列依赖所在的地址：

  ```
  http://mirrors.ustc.edu.cn/eclipse/nattable/releases/1.6.0/repository/
  
  http://mirrors.ustc.edu.cn/eclipse/tools/gef/updates/releases/5.1.0_gef-master_5509/
  
  http://mirrors.ustc.edu.cn/eclipse/efxclipse/updates-released/3.5.0/site/
  
  http://mirrors.ustc.edu.cn/eclipse/releases/2019-06/201906191000/
  ```

  ### (3) 导入Maven工程

  ```
  菜单栏File -> import... -> Maven -> Existing Maven Projects -> Next
  ```

  选择Data Studio源码所在的根目录，并导入项目

  示例中源码根目录为D:\code\openGaussDataStudio\DataStudio-master

  ![image-20220122113134187](.\photo\image-20220122113134187.png)

  ### (4) 执行脚本，编译源码

  通过命令行进入Data Studio源码src目录，并运行脚本文件

  ```
  cd ${Data_Studio_code}\code\datastudio\src
  .\copyExternalsToBuild.bat(windows) 或者 sh copyExternalsToBuild.sh(linux)
  mvn clean package -Dmaven.test.skip=true
  ```

  ${Data_Studio_code}表示源码根目录，示例中为D:\code\openGaussDataStudio\DataStudio-master

  ![image-20220120231819579](.\photo\image-20220120231819579.png)

  ![image-20220120232701958](.\photo\image-20220120232701958.png)

  生成的安装包位置为：

  ```
  ${Data_Studio_code}\code\datastudio\build
  ```

  ![image-20220120233132405](.\photo\image-20220120233132405.png)

  至此可编译出Data Studio的二进制包

  ### (5) eclipse设置

  ```
  步骤1：Window -> Preferences -> (Search for api) -> select API Baseines
  设置Missing API baseline为ignore并点击Apply
  ```

  ![image-20220122113920300](.\photo\image-20220122113920300.png)

  ```
  步骤2：Window -> Preferences -> (Search for error) -> select(Java -> Compiler -> Errors/Warnings)
  选择Select Deprecated and restrictedAPI并设置Forbidden reference(access rules)为ignore
  ```

  ![image-20220122114515808](.\photo\image-20220122114515808.png)

  ```
  步骤3：Window -> Preferences -> Maven -> Errors/Warnings
  设置Plugin execution not covered by lifecycle configuration为ignore
  ```

  ![image-20220122114845303](.\photo\image-20220122114845303.png)

  eclipse设置后选择Project -> Clean...，对项目进行清理，并重新build

### (6) 启动Data Studio应用

```
在Data Studio源码src目录下，进入子目录com.huawei.mppdbide.repository，打开mppdbide.product文件(即对应的文件路径：${Data_Studio_code}\code\datastudio\src\com.huawei.mppdbide.repository\mppdbide.product)，点击Launch an Eclipse application,即可启动Data Studio应用程序
```

![image-20220122115533357](.\photo\image-20220122115533357.png)

启动成功后，会弹出如下的Data Studio界面

![image-20220122143410964](.\photo\image-20220122143410964.png)

若启动失败，可尝试添加必要的依赖

```
Run -> Run Configurations -> Plug-ins -> Add Required Plug-ins -> Validate Plug-ins
```

![image-20220122144127110](.\photo\image-20220122144127110.png)

弹出如下对话框，表示依赖安装成功

![image-20220122144222514](.\photo\image-20220122144222514.png)

点击OK,并点击Run即可启动Data Studio界面。

至此，可通过源码构建Data Studio的开发环境，若需对源码进行调试，选择Debug模式即可。

# 7. 快速入门

Data Studio通过JDBC驱动与数据库远程连接，因此，在用Data Studio连接数据库前，需在数据库端配置可远程访问：

（1）配置postgresql.conf文件的listen_addresses参数；

（2）配置pg_hba.conf文件的ip白名单。

由于openGauss数据库限制初始用户不能远程登录，因此需创建用于远程连接的用户。

通过源代码或者安装包方式，启动Data Studio界面后，输入名称、主机、端口号、数据库、用户名、密码等信息，点击确定，即可远程连接数据库。

连接分为SSL连接和non-SSL连接两种，由于默认启用SSL连接，因此若选择non-SSL连接，则取消勾选启用SSL即可；若选择SSL连接，则需在SSL页签中配置相应的证书。

![image-20220122145707000](.\photo\image-20220122145707000.png)

连接成功后，即可显示如下所示的界面，下面简述Data Studio的界面布局。

![image-20220122150738880](.\photo\image-20220122150738880.png)

- 标记1所示为菜单栏
- 标记2所示为工具栏
- 标记3所示对对象浏览器窗口，显示数据库对象信息，包括数据库、模式、表空间 、用户/角色等信息
- 标记4所示为编辑器，用于输入SQL语句
- 标记5所示为视图窗口，可输出相应的日志信息，或显示查询结果等
- 标记6所示为SQL助手，可查询相应的SQL语法，并可与编辑器中输入的SQL进行联动提示

Data Studio作为一个强大的客户端工具，其可方便地管理数据库对象。用户界面简洁明了，所见即所得，相信用户可以快速熟练地使用Data Studio管理openGauss数据库对象，更多功能和使用可参考用户手册。

提示：用户手册在安装包路径下的docs目录中，支持中英文两种语言。点击`帮助 -> 用户手册`或者按下快捷键F1也可快速打开用户手册。

# 8. 安装指南

Data Studio二进制包发布在openGauss官网上，详见[下载链接](https://opengauss.org/zh/download.html)

Data Studio工具免安装，下载后解压，双击Data Studio.exe即可用。

Data Studio发布包结构如下图所示

![image-20220122154329176](.\photo\image-20220122154329176.png)

各文件(夹)说明如下：

| 文件(夹)名                                        | 说明                                                         |
| ------------------------------------------------- | ------------------------------------------------------------ |
| configuration                                     | 包含应用启动信息和所需Eclipse插件路径信息。                  |
| db_assistant                                      | 包含“SQL助手”功能相关的文件。                                |
| docs                                              | 包含中英文用户手册，其详细介绍了如何使用Data Studio工具。    |
| features                                          | 包含Eclipse（如富客户端协议GUI）和Data Studio特性。          |
| p2                                                | p2包含的文件用于提供和管理基于Eclipse和Equinox的应用。       |
| plugins                                           | 包含必须的Eclipse和Data Studio插件。                         |
| tools                                             | 包含Data Studio的依赖工具。                                  |
| UserData                                          | 包含每个使用Data Studio的OS用户各自的文件夹。<br/>每个用户目录下<br>Autosave：包含自动保存的查询和函数/过程信息。<br/>config：包含Data Studio的内部文件夹。<br/>Logs：包含日志文件，保存Data Studio相关的日志信息。<br/>Preferences：包含Preferences.prefs文件，内容为自定义的首选项。<br/>Profile：包含connection.properties文件、SQL执行历史、Profiles.txt文件，用于管理Data Studio中的连接信息。 |
| artifacts.xml                                     | 包含产品编译版本信息。                                       |
| changelog.txt                                     | 包含当前版本的详细变更信息。                                 |
| Data Studio 2.1.2 Open Source Software Notice.doc | 包含所用开源软件及其许可证信息。                             |
| Data Studio.exe                                   | 支持连接服务器并执行各种操作，如管理数据库对象、编辑或执行PL/SQL程序。 |
| Data Studio.ini                                   | 包含Data Studio工具运行时的配置信息。                        |
| Data Studioc.exe                                  | 支持通过命令行启动Data Studio。                              |
| DataStudio.bat                                    | 支持在Windows操作系统中连接数据库，执行各种操作。            |
| openGauss Data Studio授权协议.docx                | 本授权协议(“本协议”)适用于openGauss社区 (“本社区”)研发的openGauss Data Studio（“本软件”）。 |
| readme.txt                                        | 包含当前版本的功能和修复的问题。                             |
| version.json                                      | 包含元数据信息文件。                                         |

# 9. 开发者指南

Data Studio采用eclipse RCP框架进行开发，代码各个模块封装为eclipse-plugin，各模块之间的主要功能如下图所示，其中带有test的项目是测试工程。

![image-20220209202912322](.\photo\image-20220209202912322.png)

view工程是Data Studio的UI界面显示工程，Application.e4xmi文件是Data Studio的UI设计文件，其描述了Data Studio的UI布局，包括菜单栏、工具栏、对象浏览器、状态栏等UI信息。

Application.e4xmi与Data Studio界面的对应关系如下图所示，详细描述如下表所示。

![image-20220209204638298](.\photo\image-20220209204638298.png)

| Application                  | UI界面                                                       |
| ---------------------------- | ------------------------------------------------------------ |
| 标记1处的Main Menu           | 标记2处的菜单栏，包含文件、编辑、运行、调试、设置、帮助这六个菜单 |
| 标记3处的Part Stack          | 标记4处的对象浏览器，树型结构，显示数据库对象，包括模式、表空间、用户/角色等 |
| 标记5处的Part Sash Container | 标记6处的编辑器界面                                          |
| 标记7处的Part                | 标记8处的SQL助手                                             |
| 标记9处的Window Trim-Top     | 标记10处的上面的工具栏                                       |
| 标记11处的Window Trim-Bottom | 标记12处的状态栏                                             |

Application.e4xmi文件中涉及的UI组件描述如下：

| UI组件              | 描述                                                         |
| ------------------- | ------------------------------------------------------------ |
| Trimmed Window      | UI主窗口，至少需要一个                                       |
| Part Sash Container | Part的窗扇容器，可以垂直或水平显示所有的Part                 |
| Part Stack          | Part的栈容器，同一时刻有且仅有栈顶Part会显示                 |
| Part                | 允许用户导航和修改数据的用户界面组件，可以存放UI控件（如View、Comppsite、下拉菜单、工具栏、关联菜单等） |

view工程中plugin.xml文件描述了UI扩展点，其中使用了初始化对象和对象浏览器右键菜单两种扩展。

- 初始化扩展：

  ![image-20220209211006563](.\photo\image-20220209211006563.png)

  从plugin.xml配置文件中，我们可以看到其绑定了lifeCycleURI，并且处理类为LifeCycleManager，通过源代码可以看到，@PostContextCreate后其注册了监听器用于监听应用程序启动后的处理函数AppStartupCompleteEventHandler，此处即可以看到DataStudio的启动过程，包括处理启动参数、读取默认配置、SQL终端、SQL历史记录等功能。

- 对象浏览器右键菜单扩展

  ![image-20220209211249261](.\photo\image-20220209211249261.png)

  该示例定义了数据库对象（Database）右键菜单中含有断开连接（disconnect）

RCP框架提供了一些可见的组件，例如Menu（菜单）、Toorbar（工具栏）、Part、Windows等，还包括一些不可见的组件，例如Handler（处理程序）、Command（命令）和key binding(快捷键绑定)。

Command和Handler通常是联合使用的，当某一事件触发命令Command时，对应的处理程序Handler自动被调用，Handler通常与响应类Class URI关联。通过@CanExecute注解的方法canExecute()判断是否可以执行响应类，如果为true，则执行响应类中@Execute注解的方法execute()。

下图中示意说明命令Command-New Node Connection与处理程序New Node Connection关联，并与响应类NewDbConnection相对应。

![image-20220210112850510](.\photo\image-20220210112850510.png)

![image-20220210113053115](.\photo\image-20220210113053115.png)

Data Studio中，当点击菜单栏、工具栏、或者右键菜单栏时，该点击事件即触发相应的Command，并调用对应的应用程序Handler，从而执行相应的响应类。通过@CanExecute注解的方法canExecute()判断是否可以执行响应类，如果为true，则执行响应类中@Execute注解的方法execute()。上述的调用关系如下图所示。

![image-20220210173745203](.\photo\image-20220210173745203.png)

- ## 如何增加一个菜单栏

  以菜单栏`文件->新建连接`为例：

  ![image-20220210114650368](.\photo\image-20220210114650368.png)

  ```
  在Main Menu -> Menu 增加Handled Menu Item，其与Command-New Node Connection相关联
  ```

  因此，当在菜单栏点击`文件->新建连接`时，会触发命令Command-New Node Connection，调用对应的Handler-New Node Connection，执行相应类NewDbConnection，调用DBConnectionDialog类的open()方法，将弹出如下的新建连接对话框

  ![image-20220210142108846](.\photo\image-20220210142108846.png)

  Handled Menu Item中的Icon URI对应图标的文件位置，Label（标签）、Mnemonics（助记符）、Tooltip（工具提示）对应的文字记录在文件bundle_zh_CN.properties（中文）或者bundle.properties（英文）。

  bundle_zh_CN.properties中的内容：

  ![image-20220210144531937](.\photo\image-20220210144531937.png)

  图中的编码为中文的unicode编码

  bundle.properties中的内容：

  ![image-20220210143540300](.\photo\image-20220210143540300.png)

  ![image-20220210151331971](.\photo\image-20220210151331971.png)

  Icon URI与上图标记1对应；label的内容与上图标记2对应；tooltip的内容与上图标记3对应；mnemonics的内容N表示助记符为N，当点击文件菜单时，直接按快捷键N即可弹出新建连接对话框。

  同时Ctrl+N快捷键也可弹出新建连接对话框，其在Binding Tables进行设置

  ![image-20220210151727674](.\photo\image-20220210151727674.png)

- ## 如何增加一个工具栏

  与增加菜单栏类似，仍以新建连接为例，介绍如何增加一个工具栏

  ![image-20220210152302853](.\photo\image-20220210152302853.png)

  ```
  在TrimBars -> Window Trim -> Toolbar 增加Handled Tool Item，其与Command-New Node Connection相关联
  ```

  与菜单栏类似，因此，当在工具栏点击新建连接（下图标记1所示）时，会触发命令Command-New Node Connection，调用对应的Handler-New Node Connection，执行相应类NewDbConnection，调用DBConnectionDialog类的open()方法，将弹出新建连接对话框

  ![image-20220210152616886](.\photo\image-20220210152616886.png)

  同Handled Menu Item，Handled Tool Item中的Icon URI对应图标的文件位置，Label（标签）、Tooltip（工具提示，与上图标记2对应）对应的文字记录在文件bundle_zh_CN.properties（中文）或者bundle.properties（英文）。

- ## 如何增加一个右键菜单

  在对象浏览器中，会以树型结构显示数据库对象，包括数据库、模式、名空间、函数/过程、普通表、视图、外表、序列、同义词、触发器、表空间、用户/角色等数据库对象，如下图所示

  ![image-20220210154034751](.\photo\image-20220210154034751.png)

  针对每个数据库对象，右键菜单会有一些对应的操作，以具体函数为例，右键菜单如下如所示

  ![image-20220210154209396](.\photo\image-20220210154209396.png)

  下面以右键菜单启动调试为例，讲述如何增加一个右键菜单

  （1）增加启动调试命令对应的Command、Handler、相应类Class URI

  Command

  ![image-20220210154738978](.\photo\image-20220210154738978.png)

  Handler

  ![image-20220210154846332](.\photo\image-20220210154846332.png)

  Class URI

  StartDebugHandler

  （2）增加Handled Menu Item

  ```
  在Application -> Snippets -> Part -> Menus -> Popup Menu 增加Handled Menu Item，其与Command-Start Debugging相关联
  ```

  ![image-20220210155249222](.\photo\image-20220210155249222.png)

  启动调试对应的Handled Menu Item 对应的Core Expression为

  ```
  com.huawei.objectbrowser.popupmenu.startdebug.id.expression
  ```

  （3）在plugin.xml文件中配置与DebugObjetcs即函数/存储过程对象相关联

  ![image-20220210155924265](.\photo\image-20220210155924265.png)

  在plugin.xml文件中，通过Handled Menu Item对应的Core Expression与DebugObjects关联起来，因此，对于函数/存储过程对象，右键菜单中含有启动调试命令。

  （4）在bundle.properties文件中增加菜单对应的文字说明

  与菜单栏中的Handled Menu Item一样，Handled Menu Item中的Icon URI对应图标的文件位置，Label（标签）、Mnemonics（助记符）、Tooltip（工具提示）对应的文字记录在文件bundle_zh_CN.properties（中文）或者bundle.properties（英文）。

  bundle_zh_CN.properties

  ![image-20220210161042596](.\photo\image-20220210161042596.png)

  bundle.properties

  ![image-20220210161103541](.\photo\image-20220210161103541.png)

  label的内容与函数/存储过程中的右键菜单图标记1对应；tooltip的内容与上图标记3对应

  我们注意到启动调试后有快键键F11表示，如图中标记2所示，该快键键定义在keybinding.xml文件中

  ![image-20220210171827335](.\photo\image-20220210171827335.png)

  这样通过commandid和defaultkey可将F11快捷键与启动调试命令绑定在一起。

至此，介绍了如何在Data Studio源码中增加菜单栏、工具栏及右键菜单。

Data Studio基于RCP框架进行开发，UI控件的实现逻辑是类似的，开发者可以参考上面所示的菜单栏、工具栏、右键菜单的实现过程，添加更多的UI，从而丰富Data Studio的功能。

# 10. FAQ

本文档是openGauss Data Studio常见问题指南。

- Q1: Forbid remote connection with initial user.

  ![image-20220122162542273](.\photo\image-20220122162542273.png)

  回答：Data Studio通过JDBC驱动与数据库远程连接，由于openGauss数据库限制初始用户不能远程登录，因此需创建新的用户，用于远程连接。

- Q2: Invalid username/passwor, login denied.

  ![image-20220122163107257](.\photo\image-20220122163107257.png)

  回答：用户名或者密码错误，请检查用户名或者密码是否正确，同时请注意pg_hba.conf文件中配置的认证方式是否正确。

- Q3: no pg_hba.conf entry for host “xx.xx.xx.xx”

  ![image-20220122163645768](.\photo\image-20220122163645768.png)

  回答：请配置pg_hba.conf文件的ip白名单，将客户端Data Studio所在的机器ip加入白名单中。

- Q4: Connection to xx.xx.xx.xx:xx refused.

  ![image-20220122164053976](.\photo\image-20220122164053976.png)

  回答：请确保数据库是否启动，ip和端口号是否正确，postgresql.conf文件的listen_addresses参数是否配置正确，该参数应配置为*或者数据库所在机器的具体ip。

- Q5: 启动时，发生未知内部错误.请联系Data Studio技术支持团队了解日志详情。

  ![image-20220122170627400](.\photo\image-20220122170627400.png)

  回答：请检查本地环境是否为java 8，并确定是否是使用openGauss社区的Data Studio工具连接openGauss数据库。

# 11. 术语表

| 术语 | 全称                       | 解释           |
| ---- | -------------------------- | -------------- |
| C/S  | Client/Server              | 客户端服务器端 |
| JDBC | Java Database Connectivity | Java数据库连接 |
| GUI  | Graphical User Interface   | 图形用户界面   |
| RCP  | Rich Client Platform       | 富客户平台     |

# 12. 开源的资料文档应附有对应的文档许可证

本文档遵循[知识共享许可协议CC 4.0](https://creativecommons.org/licenses/by/4.0/) (http://creativecommons.org/Licenses/by/4.0/)。

