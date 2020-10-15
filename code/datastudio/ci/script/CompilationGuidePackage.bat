cd ../../build
mkdir Package_64

xcopy "*-win32.win32.x86_64.zip" "Package_64"
del "*-win32.win32.x86_64.zip"

cd ../Package_64
ren *x86_64.zip Data_Studio_64.zip