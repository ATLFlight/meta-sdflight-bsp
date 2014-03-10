#!/bin/sh

t=/etc/network/

if [ -f $t/macaddress.done ]; then
   # Nothing to do
   exit 0
fi


# First mount the right android partition to copy firmware
mkdir -p /mnt/system || {
    echo "[ERROR] Error creating mount point"
    exit 1
}

mount /dev/mmcblk0p12 /mnt/system/ || {
    echo "[ERROR] Error mounting the firmware parition"
    exit 1
}

#  Copy both versions of the firmware. It's ok for one
# of these to fail, in cases where either hw3.0 or hw1.3 dirs
# don't exist
afw=/mnt/system/etc/firmware/ath6k/AR6004/hw3.0/
lfw=/lib/firmware/ath6k/AR6004/hw3.0/
mkdir -p $lfw
cp $afw/softmac.bin $lfw
cp $afw/fw.ram.bin $lfw
cp $afw/bdata.bin_sdio $lfw/bdata.bin

afw=/mnt/system/etc/firmware/ath6k/AR6004/hw1.3/
lfw=/lib/firmware/ath6k/AR6004/hw1.3/
mkdir -p $lfw
cp $afw/softmac.bin $lfw
cp $afw/fw.ram.bin $lfw
cp $afw/bdata.bin_sdio $lfw/bdata.bin

umount /mnt/system

# Now get the ethernet MAC address from the Android
# userdata partition, and set that in our ethernet
# config scripts
mkdir -p /mnt/userdata || {
    echo "[ERROR] Error creating mount point"
    exit 1
}
mount /dev/mmcblk0p13 /mnt/userdata/ || {
    echo "[ERROR] Error mounting the userdata partition"
    exit 1
}

MAC=`cat /mnt/userdata/eth0.sh  | grep hwaddr | sed 's/.*hwaddr //g'`
umount /mnt/userdata
sed -i -e "s/@REPLACE_WITH_REAL_MAC_ADDR@/${MAC}/" /etc/network/if-pre-up.d/eth0-config-macaddr
echo 1 > $t/macaddress.done




