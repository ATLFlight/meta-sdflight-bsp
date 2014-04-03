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
QRL_COMMON_INCS_DIR=/usr/local/qr-linux
# Include common files
. ${QRL_COMMON_INCS_DIR}/qrl-mac-fw-inc.sh
QRL_CHECK_FILE=${QRL_COMMON_INCS_DIR}/.qrl_installed_binaries
QRL_PKG_SUBDIR=deb

if [ -e ${QRL_CHECK_FILE} ] 
then
    exit 0
fi

##
## Install pkgs, if any
## 
checkInstallPkgs () {
    mountPartition ${QRL_PARTITION_NAME_CACHE}
    if [ $? -gt 0 ]
    then
        echo "[ERROR] Error mounting the partition ${QRL_PARTITION_NAME_CACHE}"
        return 0 # Don't fail if we can't install
    fi
    pkgDir=${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_CACHE}/${QRL_PKG_SUBDIR}
    if [ ! -d ${pkgDir} ]
    then
        echo "[WARNING] Pkg directory ${pkgDir} doesn't exist"
        return 0
    fi
    export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/insserv
    echo "[INFO] Installing pkgs..."
    for file in ${pkgDir}/*.deb
    do
      dpkg --install ${file} > /dev/null 2>&1
    done
}
echo "[INFO] Checking and installing remaining packages"
checkInstallPkgs
touch ${QRL_CHECK_FILE}
