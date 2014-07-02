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

PATH="/bin/:$PATH"
QRL_PARTITION_NAME_PERSIST=persist
QRL_COMMON_INCS_DIR=/usr/local/qr-linux

# Include common files
. ${QRL_COMMON_INCS_DIR}/qrl-mac-fw-inc.sh

PERSIST_PATH=/mnt/$QRL_PARTITION_NAME_PERSIST
FW_PATH=/lib/firmware/ar3k/1020201

FW1=PS_ASIC.pst
FW2=RamPatch.txt
FW3=ar3kbdaddr.pst

SP1=$PERSIST_PATH/$FW1
SP2=$PERSIST_PATH/$FW2
SP3=$PERSIST_PATH/$FW3

DP1=$FW_PATH/$FW1
DP2=$FW_PATH/$FW2
DP3=$FW_PATH/$FW3

COPY=0

if [ ! -f $DP1 -o ! -f $DP2 -o ! -f $DP3 ]; then
   echo "[WARNING] Could not find bluetooth firmware files, trying to copy from persist"
   mountPartition $QRL_PARTITION_NAME_PERSIST
   mkdir -p $FW_PATH
   cp -f $SP1 $DP1 2> /dev/null
   cp -f $SP2 $DP2 2> /dev/null
   cp -f $SP3 $DP3 2> /dev/null
   COPY=1
fi

if [ ! -f $DP1 -o ! -f $DP2 -o ! -f $DP3 ]; then
   echo "[WARNING] Could not copy bluetooth firmware files, continuing anyway"
else
   if [ $COPY = 1 ]; then
      echo "[NOTICE] Firmare copied"
   fi
fi


# Turn Bluetooth Power Off then On
echo 0 > /sys/class/rfkill/rfkill0/state
sleep 1
echo 1 > /sys/class/rfkill/rfkill0/state
sleep 1

/usr/bin/ath3k_hciattach -n /dev/ttyHS0 ath3k 3000000 &

echo "Done."


