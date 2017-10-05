echo off
REM ####################################
REM #
REM # gzip tool
REM #
REM ####################################

IF [%1] == [] GOTO argNotValid
IF [%2] == [] GOTO argNotValid

SET GZIP_HOME=D:\Km\NOC\apache-tomcat-8.0.46\webapps\ROOT\util\gzip


D:\Km\NOC\apache-tomcat-8.0.46\webapps\ROOT\util\gzip\gzip.exe -c  %1 > %2

goto end

:argNotValid
echo on
echo args empty, try like "zip.bat a.js a.ngjs"

:end