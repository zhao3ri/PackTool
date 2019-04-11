
@ECHO OFF
Echo Auto-sign Created By Dave Da illest 1 
Echo Update.zip is now being signed and will be renamed to update_signed.zip

java -jar signapk.jar key.x509.pem key.pk8 %1 %2

Echo Signing Complete 
 
Pause
EXIT
