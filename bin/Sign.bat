
@ECHO OFF
Echo Auto-sign Created By Dave Da illest 1 
Echo Update.zip is now being signed and will be renamed to update_signed.zip

java -jar "%~dp0\signapk.jar" "%~dp0\key.x509.pem" "%~dp0\key.pk8" %1 %2

Echo Signing Complete 
 
Pause
EXIT
