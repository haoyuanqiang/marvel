rem Build the project
call mvn clean
call mvn package

rem Clean the directory for dist
rmdir /s/q dist
md dist

rem Copy Files
echo F | xcopy /Y marvel-discovery\target\marvel-discovery.jar dist\marvel-discovery.jar
echo F | xcopy /Y marvel-admin\target\marvel-admin.jar dist\marvel-admin.jar
echo F | xcopy /Y marvel-auth\target\marvel-auth.jar dist\marvel-auth.jar
echo F | xcopy /Y marvel-gateway\target\marvel-gateway.jar dist\marvel-gateway.jar
