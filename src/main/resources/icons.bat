
REM  Copyright (c) 2025. All rights reserved.
REM  This software is protected under the intellectual property laws of the United States and international copyright treaties.
REM  Unauthorized copying, modification, distribution, or reverse engineering of this software is strictly prohibited.
REM  By using this software, you agree to comply with the terms and conditions outlined in the license agreement provided with the product.
REM  Any use of the software outside the bounds of this agreement is subject to legal action.


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
