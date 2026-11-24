REM Copyright (c) 2026 Huawei Technologies Co., Ltd.
REM DataStudio is licensed under Mulan PSL v2.
REM You can use this software according to the terms and conditions of the Mulan PSL v2.
REM You may obtain a copy of Mulan PSL v2 at:
REM          http://license.coscl.org.cn/MulanPSL2
REM THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
REM EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
REM MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
REM See the Mulan PSL v2 for more details.

cd ../../build
mkdir Package_64

xcopy "*-win32.win32.x86_64.zip" "Package_64"
del "*-win32.win32.x86_64.zip"

cd ../Package_64
ren *x86_64.zip Data_Studio_64.zip