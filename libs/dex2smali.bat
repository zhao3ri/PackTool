@echo off
set PATH=%CD%;%PATH%;
java -jar "%~dp0\baksmali.jar" -o %1 %2 