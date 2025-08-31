Set WshShell = CreateObject("WScript.Shell")
WshShell.Run "target\app\bin\app.bat", 0, False
Set WshShell = Nothing