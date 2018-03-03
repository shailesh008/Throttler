# Throttler

HttpWebServer.java is a plain http web server which can work standalone.
1. Throttler.java running the script.sh file and finding out the processm running on the current operating System.
2. Determining which process using what amount of OS resources IO/Memory/CPU Usage etc.
3. After finding the resource usage the response is send through server to which androip app is connected.
4. Response send in the for of JSON.





---------------------------------------------------------------------------------------------------------
Application Objective:------
Tool to monitor/limit process Memory/I.O/Network resource similar to AWS concept “pay as you go”.
Tool connected via Android app to show notification once process resource exceeds threshold.

