@echo off

rem Build the jpackage installer

setlocal

rem Get the Wix path, project build directory, and other arguments passed by Maven



rem Set the PATH variable to include Wix binaries
set PATH=%PATH%;wix314-binaries

rem Run the jpackage command with the specified arguments
%3\bin\jpackage --input jarfile --name "SyncSyndicate" --main-jar %2 --main-class com.igearfs.jnlp.Main --type exe --runtime-image jre --vendor "In-Game Event, A Red Flag Syndicate LLC" --app-version "1.0.1" --description "SyncSyndicate: Unified JNLP Connectivity" --license-file LICENSE.txt --install-dir "SyncSyndicate" --verbose --win-menu --win-shortcut  --app-content "4.5.2,javafx-sdk-17.0.14,jnlp_cache,data"

endlocal
