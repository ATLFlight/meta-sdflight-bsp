#
###############################################################################
# Author: Rahul Anand (ranand@codeaurora.org)
# 
# This file contains common functions and variables included in many scripts
#
# Copyright (c) 2014, The Linux Foundation.
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted (subject to the limitations in the
# disclaimer below) provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright
#   notice, this list of conditions and the following disclaimer.
#
# * Redistributions in binary form must reproduce the above copyright
#   notice, this list of conditions and the following disclaimer in the
#   documentation and/or other materials provided with the distribution.
#
# * Neither the name of the Linux Foundation nor the names of its
#   contributors may be used to endorse or promote products derived from
#   this software without specific prior written permission.
#
# NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE
# GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
# HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
# BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
# WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
# OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
# IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
# 
###############################################################################

MOUNT=/bin/mount
MKDIR=/bin/mkdir
GREP=/bin/grep

##
## Commonly used eMMC partition devices
## 
QRL_DEVICE_MODEM=/dev/mmcblk0p1  
QRL_DEVICE_ABOOT=/dev/mmcblk0p5  
QRL_DEVICE_BOOT=/dev/mmcblk0p7  
QRL_DEVICE_SYSTEM=/dev/mmcblk0p12  
QRL_DEVICE_USERDATA=/dev/mmcblk0p13  
QRL_DEVICE_PERSIST=/dev/mmcblk0p14  
QRL_DEVICE_CACHE=/dev/mmcblk0p15 

##
## Commonly used eMMC partition names and mountpoints
## 
QRL_PARTITION_NAME_MODEM=modem
QRL_PARTITION_NAME_ABOOT=aboot
QRL_PARTITION_NAME_BOOT=boot
QRL_PARTITION_NAME_SYSTEM=system
QRL_PARTITION_NAME_USERDATA=userdata
QRL_PARTITION_NAME_PERSIST=persist
QRL_PARTITION_NAME_CACHE=cache

##
## Other commonly used variables
## 
QRL_LIB_FIRMWARE=/lib/firmware
QRL_DEFAULT_MOUNTROOT=/mnt

##
## isMounted:
##    Check if the device $1, is mounted at $2
##    
isMounted () {
    local device=$1
    local mntPoint=$2
    ${MOUNT} | ${GREP} "${device} on ${mntPoint} " > /dev/null
    return $?
}

##
## doMount:
##   Mount the device $1 at $2, with FS type of $3
##    
doMount () {
    local device=$1
    local mntPoint=$2
    local fsType=$3

    isMounted ${device} ${mntPoint} 
    if [ $? -eq 0 ]
    then
        return 0
    fi

    # Mount the partition if not already mounted
    if [ ! -d ${mntPoint} ]
    then
        ${MKDIR} -p ${mntPoint} || {
            echo "[ERROR] Error creating mount point: ${mntPoint}"
            return 1
        }
    fi

    echo "[INFO] Mounting the partition ${device} at ${mntPoint}"
    ${MOUNT} -t ${fsType} ${device} ${mntPoint}
}

##
## mountPartition:
##   Mount the partition specified by name
##    
mountPartition () {
    local partitionName=$1

    device=
    fsType=ext4
    mntPoint=
    case ${partitionName} in
	${QRL_PARTITION_NAME_MODEM})
	    device=${QRL_DEVICE_MODEM}
	    mntPoint="${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_MODEM}"
	    fsType=vfat
	    ;;
	${QRL_PARTITION_NAME_SYSTEM})
	    device=${QRL_DEVICE_SYSTEM}
	    mntPoint="${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_SYSTEM}"
	    ;;
	${QRL_PARTITION_NAME_USERDATA})
	    device=${QRL_DEVICE_USERDATA}
	    mntPoint="${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_USERDATA}"
	    ;;
	${QRL_PARTITION_NAME_PERSIST})
	    device=${QRL_DEVICE_PERSIST}
	    mntPoint="${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_PERSIST}"
	    ;;
	${QRL_PARTITION_NAME_CACHE})
	    device=${QRL_DEVICE_CACHE}
	    mntPoint="${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_CACHE}"
	    ;;
	?)
	    echo "[ERROR] Mounting partition ${partitionName} not supported"
	    return 1
	    ;;
    esac
    doMount ${device} ${mntPoint} ${fsType}
}
	
##
## humanMACToNumber: Take a colon separated MAC address and remove the colons
## 
humanMACToNumber() {
    echo ${1} | sed 's/://g'
}
