#!/bin/bash
### BEGIN INIT INFO
# Provides:        install_binaries  
# Required-Start:    
# Required-Stop:     
# Should-Start:      
# Should-Stop:       
# Default-Start:     2 
# Default-Stop:      0
# Short-Description: Install binaries.
# Description:       This script installs binaries necessary for operation of QR Linux.
### END INIT INFO
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This script installs packages needed for QR-Linux bsp support
##
###############################################################################
CHECK_FILE=/var/lib/dpkg/.qrl_installed_binaries

if [ -e ${CHECK_FILE} ] 
then
    exit 0
fi


MOUNT=/bin/mount
GREP=/bin/grep
MKDIR=/bin/mkdir 

QC_FW_DEVICE=/dev/mmcblk0p14 # eMMC partition that contains the firmware
QC_FW_MOUNT_POINT=/persist	# Where to mount the eMMC partition
QC_PKG_SUBDIR=deb		# The subdir in eMMC partition where pkgs are

qcPkgDir=${QC_FW_MOUNT_POINT}/${QC_PKG_SUBDIR} # Where to install the pkgs from

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
## mountPartition:
##   Mount the partition for the binaries 
##    
mountPartition () {
    local device=$1
    local mntPoint=$2

    isMounted ${device} ${mntPoint} 
    if [ $? -eq 0 ]
    then
        return 0
    fi

    # Mount the firmware partition if not already mounted
    if [ ! -d ${mntPoint} ]
    then
        ${MKDIR} -p ${mntPoint} || {
            echo "[ERROR] Error creating mount point: ${mntPoint}"
            return 1
        }
    fi

    echo "[INFO] Mounting the partition..."
    ${MOUNT} -t ext4 ${device} ${mntPoint}
}

##
## Install pkgs, if any
## 
checkInstallPkgs () {
    mountPartition ${QC_FW_DEVICE} ${QC_FW_MOUNT_POINT} || {
        echo "[ERROR] Error mounting the partition ${QC_FW_DEVICE}"
        return 0 # Don't fail if we can't install
    }
    pkgDir=${QC_FW_MOUNT_POINT}/${QC_PKG_SUBDIR}
    if [ ! -d ${pkgDir} ]
    then
        echo "[WARNING] Pkg directory ${pkgDir} doesn't exist"
        return 0
    fi
    export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/insserv
    cd ${pkgDir}
    echo "[INFO] Installing pkgs..."
    dpkg --install reboot2fastboot_*.deb > /dev/null 2>&1
    dpkg --install diag_*.deb > /dev/null 2>&1
    dpkg --install libxml0_*.deb > /dev/null 2>&1
    dpkg --install libdsutils1_*.deb > /dev/null 2>&1
    dpkg --install libconfigdb0_*.deb > /dev/null 2>&1
    dpkg --install qmi_*.deb > /dev/null 2>&1
    dpkg --install qmi-framework_*.deb > /dev/null 2>&1
    dpkg --install thermal_*.deb > /dev/null 2>&1
    dpkg --install mp-decision_*.deb > /dev/null 2>&1
}
echo "[INFO] Checking and installing remaining packages"
checkInstallPkgs
touch ${CHECK_FILE}
