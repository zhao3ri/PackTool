@echo off
set PATH=%CD%;%PATH%;
%~dp0\win\bin\java -jar "%~dp0\apktool.jar" %1 %2 %3 %4 %5 %6 %7 %8 %9
