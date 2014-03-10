#!/bin/bash
### BEGIN INIT INFO
# Provides:        install_proprietary_binaries  
# Required-Start:    
# Required-Stop:     
# Should-Start:      
# Should-Stop:       
# Default-Start:     2 
# Default-Stop:      0
# Short-Description: Install proprietary binaries.
# Description:       This script installs proprietary binaries necessary for operation of QR Linux.
### END INIT INFO
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This script installs proprietary SW
##
###############################################################################

if [ ! -e /etc/init.d/installed_proprietary_binaries ]; then
    MOUNT=/bin/mount
    GREP=/bin/grep
    MKDIR=/bin/mkdir 

    QC_FW_DEVICE=/dev/mmcblk0p1	# eMMC partition that contains the firmware
    QC_FW_MOUNT_POINT=/firmware	# Where to mount the eMMC partition
    QC_PKG_SUBDIR=deb		# The subdir in eMMC partition where pkgs are

    qcPkgDir=${QC_FW_MOUNT_POINT}/${QC_PKG_SUBDIR} # Where to install the pkgs from

    ##
    ## checkFilesAndPartitions:
    ##    Make sure the packages is available at the right place
    ##

    ##
    ## isMounted:
    ##    Check if the device $1, is mounted at $2
    ##    
    isMounted () {
	local device=$1
	local mntPoint=$2
	${MOUNT} | ${GREP} "${device} on ${mntPoint} "
	return $?
    }

    mountPartition () {
	local device=$1
	local mntPoint=$2

	isMounted ${device} ${mntPoint} 
	if [ $? -eq 0 ]
	then
	    echo "[INFO] Already mounted, skipping."
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
	${MOUNT} -t vfat ${device} ${mntPoint}
    }

    ##
    ## Install any other pkgs, if any
    ## 
    checkInstallPkgs () {
	echo "In checkInstallPkgs"
	mountPartition ${QC_FW_DEVICE} ${QC_FW_MOUNT_POINT} || {
	    echo "[ERROR] Error mounting the partition ${QC_FW_DEVICE}"
	    #return 1
	}
	pkgDir=${QC_FW_MOUNT_POINT}/${QC_PKG_SUBDIR}
	if [ ! -d ${pkgDir} ]
	then
	    echo "[WARNING] Pkg directory ${pkgDir} doesn't exist"
	    return 1
	fi
	export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/insserv
	cd ${pkgDir}
	dpkg --install testtools_*.deb
	dpkg --install reboot2fastboot_*.deb
	dpkg --install diag_*.deb
	dpkg --install libxml0_*.deb
	dpkg --install libdsutils1_*.deb
	dpkg --install libconfigdb0_*.deb
	dpkg --install qmi_*.deb
	dpkg --install qmi-framework_*.deb
	dpkg --install thermal_*.deb
	dpkg --install mp-decision_*.deb

	# (
	# 	cd ${pkgDir}
	# 	cmd="apt-get install "
	# 	for pkg in `ls *deb`
	# 	do
	# 	    cmd="${cmd} ${pkg/_*/}"
	# 	    echo $cmd
	# 	done
	# )
    }

    checkInstallPkgs
    touch /etc/init.d/installed_proprietary_binaries
fi
