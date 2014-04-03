#!/bin/sh
#################################################################################################
## Author: Christopher Wingert
## 
## This script configures bluetooth during kernel booting sequence.
##
## v0.1: Basic draft
#################################################################################################
SCRIPT_VERSION="v0.1"
echo "BT Init Script: $SCRIPT_VERSION"

# Turn Bluetooth Power Off then On
echo 0 > /sys/class/rfkill/rfkill0/state
sleep 1
echo 1 > /sys/class/rfkill/rfkill0/state
sleep 1

/usr/bin/ath3k_hciattach -n /dev/ttyHS0 ath3k 3000000 &

echo "Done."


