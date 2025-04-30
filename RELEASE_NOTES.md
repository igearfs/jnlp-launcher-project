V 1.0.1
* Moved the jnlp_entries file into the projects data folder
* Fixed the icon null issue with changing icon not saving correctly

v 1.3.1
* Added in logging for exceptions thrown for better reporting issues.
* Fixed issues with app not loading jnlp files correctly. Incorrect placement of files in windows.
* All files for read/write are now in windows appdata/roaming/SyncSyndicate

v 1.4.1
* Added notification that you did not save before launching or closing the application
* Fixed spinner popup when launcher is clicked and on application launch.

v 1.5.1
* Added clear cache button so you can download new jar's and update
* Added the ability to add in username/password for auto login when you launch.

V 1.6.1
Fixing build issue for each os. Have windows and mac. Next Linux

v 1.7.1
Adds webstart if it is not at the end of the url.
Icon defaults to Rocket if not selected
Fixed UI order on right pane and creation.
Cancel button removed from save confirmation.
System printouts reduced to logger.debug and logger.error. (not all of them)