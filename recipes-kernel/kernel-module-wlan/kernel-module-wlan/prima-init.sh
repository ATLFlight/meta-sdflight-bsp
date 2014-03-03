#!/bin/bash
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
##
## prima-init.sh is auto-generated and is used to bring up the WCN36{6,8}0 chip
## on Linux. It depends on udev to load the required firmware
###############################################################################

MOUNT=/bin/mount

QC_FW_DEVICE=/dev/mmcblk0p1	# eMMC partition that contains the firmware
QC_FW_MOUNT_POINT=/firmware	# Where to mount the eMMC partition
QC_FW_SUBDIR=image		# The subdir in eMMC partition where the fw is
QC_FW_WIFI_FILE_NAME=wcnss	# The root of thw WCN fw filename
QC_FW_WIFI_FILE_EXT=mdt		# The extension of the WCN fw filename
QC_FW_TRIGGER_DEVICE=/dev/wcnss_wlan # The device to trigger for fw download
QC_FW_DEST_DIR=/lib/firmware	     # Where to copy the fw files

qcFwImgDir=${QC_FW_MOUNT_POINT}/${QC_FW_SUBDIR} # Where to copy fw files from

##
## checkFilesAndPartitions:
##    Make sure the firmwware is available at the right place
##
checkFilesAndPartitions () {
    wifiMdtSrcFile=${qcFwImgDir}/${QC_FW_WIFI_FILE_NAME}.${QC_FW_WIFI_FILE_EXT}
    wifiMdtDstFile=${QC_FW_DEST_DIR}/${QC_FW_WIFI_FILE_NAME}.${QC_FW_WIFI_FILE_EXT}

    if [ -f ${wifiMdtDstFile} ]
    then
       echo "INFO: Found existing firmware files. Skipping copying from eMMC"
       return 0
    fi

    # Mount the firmware partition if not already mounted
    if [ ! -d ${QC_FW_MOUNT_POINT} ]
    then
        /bin/mkdir -p ${QC_FW_MOUNT_POINT}
    fi

    if [ ! -d ${qcFwImgDir} ]
    then
        echo "INFO: Mounting firmware partition..."
        ${MOUNT} -t vfat ${QC_FW_DEVICE} ${QC_FW_MOUNT_POINT}
    else
        echo "INFO: Skipping mounting the firmware partition"
    fi

    if [ ! -e ${wifiMdtSrcFile} ]
    then
        echo "ERROR: Firmware not found or mount failed"
        return 1
    fi
    # Copy the wcnss* files from eMMC partition to /lib/firmware
    /bin/cp ${qcFwImgDir}/${QC_FW_WIFI_FILE_NAME}* ${QC_FW_DEST_DIR}

    if [ ! -f ${wifiMdtDstFile} ]
    then
        echo "ERROR: Firmware  not found. Giving up"
        exit 1
    fi
    return 0
}

##
## loadKernelModules:
##    Insert the needed kernel modules 
##
loadKernelModules () {
   retVal=0
   cfgModule=`grep cfg80211 /proc/modules`
   cfgModule=${#cfgModule}
   if [ $cfgModule -eq 0 ]
   then
      echo "INFO: Loading kernel module: cfg80211 ..."
      modprobe cfg80211 > /dev/null 2>&1
      retVal=$?
   fi
   if [[ $retVal -ne 0 ]]
   then
      echo "ERROR: Loading cfg80211 failed"
      return $retVal
   fi

   wlanModule=`grep wlan /proc/modules`
   wlanModule=${#wlanModule}
   if [ $wlanModule -eq 0 ]
   then
      echo "INFO: Loading kernel module: wlan ..."
      modprobe wlan > /dev/null 2>&1
      retVal=$?
   fi
   return $retVal
}
checkFilesAndPartitions
status=$?

if [ $status -ne 0 ]
then
    echo "ERROR: Aborting wlan bringup"
    exit 1
fi


echo 1 > "${QC_FW_TRIGGER_DEVICE}" > /dev/null 2>&1
sleep 1

loadKernelModules
