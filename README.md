# Data Studio介绍

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
- openGauss Data Studio为一个Java应用，在使用时需确保配置Java 11+运行环境

# 源码编译指导

通过源码构建Data Studio二进制包的具体步骤

- ## 前置条件

  1. 下载安装JDK11，并配置`JAVA_HOME`环境变量
  2. 下载安装apache maven3.x，并配置`M2_HOME`环境变量
  3. 通过`https://download2.gluonhq.com/openjfx/11.0.2/openjfx-11.0.2_windows-x64_bin-sdk.zip`下载javafx sdk依赖包，并配置`javafx_home`环境变量，值为${javafx-sdk-11.0.2}

- ## 源码编译

  通过命令行进入Data Studio源码src目录，并运行脚本文件

  ```shell
  cd ${Data_Studio_code}\code\datastudio\src
  .\copyExternalsToBuild.bat(windows) 或者 sh copyExternalsToBuild.sh(linux)
  mvn clean package -Dmaven.test.skip=true
  ```

  生成的安装包位置为：

  ```shell
  ${Data_Studio_code}\code\datastudio\build
  ```

# 参与贡献

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

# 开源的资料文档应附有对应的文档许可证

本文档遵循[知识共享许可协议CC 4.0](https://creativecommons.org/licenses/by/4.0/) (http://creativecommons.org/Licenses/by/4.0/)。

