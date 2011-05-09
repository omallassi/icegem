
This is a short description of how to compile and run the monitoring tool.

1. How to compile

- Go to icegem\icegem-cache-utils directory
- Run the command

mvn clean assembly:assembly


2. How to start and stop

- Go to directory icegem\icegem-cache-utils\target
- Unpack the archive icegem-cache-utils-<version>.zip
- Go into the unpacked folder icegem-cache-utils-<version>
- Check the mail options in the file mail.properties
- Check the monitor tool options in the file monitoring.properties
- Start locator: .\start-locator.bat
- Start the first server in the separate console: .\run-server.bat
- Start the second server in the separate console: .\run-server.bat
- Start the monitoring tool in the separate console: .\run-monitoring-tool.bat
- After finishing your test stop the locator: .\stop-locator.bat