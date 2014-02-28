#!/bin/sh

t=/etc/network/

afw=/mnt/system/etc/firmware/ath6k/AR6004/hw3.0/
lfw=/lib/firmware/ath6k/AR6004/hw3.0/

out=/etc/network/eth0-init.sh

if [ ! -f $t/macaddress.done ]; then
   mkdir -p /mnt/system
   mount /dev/mmcblk0p12 /mnt/system/
   mkdir -p $lfw
   cp $afw/softmac.bin $lfw
   cp $afw/fw.ram.bin $lfw
   cp $afw/bdata.bin_sdio $lfw/bdata.bin
   umount /mnt/system

   mkdir -p /mnt/userdata
   mount /dev/mmcblk0p13 /mnt/userdata/
   cp /mnt/userdata/eth0.sh $t
   umount /mnt/userdata

   MAC=`cat $t/eth0.sh  | grep hwaddr | sed 's/.*hwaddr //g'`
   echo "ifconfig eth0 down" > $out
   echo "ifconfig eth0 hw ether $MAC" >> $out
   chmod 700 $out

   echo 1 > $t/macaddress.done
fi

. $out



exit

# Start from scratch
rm /etc/network/macaddress.done
rm -fr /lib/firmware/ath6k
rm /etc/network/eth0-init.sh


