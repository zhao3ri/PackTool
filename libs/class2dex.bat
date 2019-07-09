@echo off
set PATH=%CD%;%PATH%;
java -jar "%~dp0\dx.jar" --dex --output=%1 %2 