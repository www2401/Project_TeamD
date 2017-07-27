#!/bin/bash

##
## Connect to Android-x86 VM.
## http://www.android-x86.org/documents/virtualboxhowto
## http://www.android-x86.org/documents/debug-howto
##
## The platform-tools directory must be on the PATH for this script to work.
##

IP=$(sudo nmap -sn "$(hostname -I | cut -d' ' -f1)/24" | grep -B 2 "Cadmus Computer Systems" | head -n 1 | cut -d' ' -f5)
PORT=5555

adb kill-server
adb connect $IP:$PORT

echo "Be sure to enable 'Stay awake' in 'Developer options'. This will prevent the screen from falling asleep."
echo "'Developer options' may be enabled by selecting 'About tablet' in 'Settings' and repeatedly pressing the 'Build number' item."