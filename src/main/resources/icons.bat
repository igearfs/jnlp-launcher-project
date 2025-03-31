@echo off
setlocal enabledelayedexpansion

:: Specify the folder containing the icons
set ICONS_DIR=icons

:: Specify the name of the text file
set CONFIG_FILE=icons_list.txt

:: Start creating the plain text file
echo Generating icon list...

:: Loop through each PNG file in the icons folder
for %%F in (%ICONS_DIR%\*.png) do (
    echo %%~nxF >> %CONFIG_FILE%
)

echo Icon list has been generated at "%CONFIG_FILE%".

endlocal
