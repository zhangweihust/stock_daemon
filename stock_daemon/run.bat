@echo off

REM 备份
set LAST_CD=%cd%


REM 进去
set BASE_DIR=%~dp0
%BASE_DIR:~0,2%
cd %BASE_DIR%



if '%1=='## goto ENVSET

SET LIBDIR=libs

SET CLSNAME=com.zhangwei.stock.main.Main

SET CLSPATH=.
FOR %%c IN (%LIBDIR%\*.jar) DO CALL %0 ## %%c

GOTO RUN

:RUN

java -cp %CLSPATH% %CLSNAME% D:

goto END

:ENVSET

set CLSPATH=%CLSPATH%;%2

goto END

:END



REM 出去（恢复）
%LAST_CD:~0,2%
cd %LAST_CD%