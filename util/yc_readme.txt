REM ####################################
REM #
REM # change the YIC_HOME first before use below script
REM #
REM ####################################

echo off
IF [%1] == [] GOTO argNotValid

SET YUIC_HOME=C:\Users\xho\workspace.7\three\yuicompressor


java -jar   %YUIC_HOME%\yuicompressor-2.4.8.jar %1 -o %2

goto end

:argNotValid
echo args empty, try like "yc.bat abc.js abc_min.js"

:end