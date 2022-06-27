ci_path=$PWD
info_path=$PWD/../../../../information/datastudio
code_path=$PWD/../../src
echo $ci_path
echo $code_path
time_stamp=$(date -d "today" +"%Y-%m-%d %H:%M:%S")
application_name="Data Studio"
commit_id=$(git rev-parse HEAD)
cd $code_path
sh copyExternalsToBuild.sh
sleep 2
mvn -B -f pom.xml -nsu clean package -Dmaven.test.skip=true
sleep 5

echo "Packaging Datastudio files"
cd $code_path/../build
ls -l
win32=$(ls -l $code_path/../build | grep -c win32.x86.zip)
win64=$(ls -l $code_path/../build | grep -c win32.x86_64.zip)
linux_x86=$(ls -l $code_path/../build | grep -c gtk.x86_64.zip)
linux_aarch=$(ls -l $code_path/../build | grep -c gtk.aarch64.zip)
echo $win32
echo $win64
echo $linux_x86
echo $linux_aarch

if [ "$win64" != 0 ];
then
    echo "windows 32 bit package is available";
    mkdir -p Package_win_64
    mv *win32.x86_64.zip  Package_win_64/Data_Studio_win_64.zip
    sleep 5
    cd Package_win_64 
    unzip Data_Studio_win_64.zip
    rm -rf Data_Studio_win_64.zip
    cd "Data Studio"
    rm *.p2bu
    find ./ -name ckeditor -type d | xargs rm -rf
    find -path '*/linux*' -delete
    echo -e "{\"application_name\":\""$application_name"\", \"compiled_time\":\""$time_stamp"\", \"commit_id\":\""$commit_id"\"}" > version.json
	cp -rf $info_path/openGauss* .
	cp -rf "$info_path/Data Studio 3.0.0 Open Source Software Notice.doc" .
    cd ..
    zip -r  DataStudio_win_64.zip "Data Studio"
    sha256sum DataStudio_win_64.zip > DataStudio_win_64.zip.sha256.txt
    rm -rf "Data Studio"
    chmod a+x  *
    mv * ../
    cd ..
    rm -rf Package_win_64
else
    echo "Windows 64 bit package is not found in build folder"
fi








