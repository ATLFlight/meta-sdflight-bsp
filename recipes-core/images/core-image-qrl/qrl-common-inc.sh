#
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This file contains common functions and variables included in many scripts
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
ls
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
