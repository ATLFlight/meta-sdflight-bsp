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

# Include common files
/usr/local/qr-linux/qrl-config-macaddr.sh -i bt -m auto -r

PERSIST_PATH=/mnt/persist
FW_PATH=/lib/firmware/ar3k/1020201

FW1=PS_ASIC.pst
FW2=RamPatch.txt
FW3=ar3kbdaddr.pst

DP1=$FW_PATH/$FW1
DP2=$FW_PATH/$FW2
DP3=$FW_PATH/$FW3

if [ ! -f $DP1 -o ! -f $DP2 -o ! -f $DP3 ]; then
   echo "[ERROR] Bluetooth firmware not found"
   exit -1
fi

# Turn Bluetooth Power Off then On
echo 0 > /sys/class/rfkill/rfkill0/state
sleep 1
echo 1 > /sys/class/rfkill/rfkill0/state
sleep 1

/usr/bin/ath3k_hciattach -n /dev/ttyHS0 ath3k 3000000 &

echo "[INFO] Done."


