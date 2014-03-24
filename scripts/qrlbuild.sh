#!/bin/bash
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This is the build script for the QR-Linux project
###############################################################################

MACHINE_IFC6410=ifc6410
MACHINE_SOM8064=som8064
DEFAULT_MACHINE=${MACHINE_IFC6410}

TOOLCHAIN_NAME=gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux

DPKG_CMD="dpkg-query"

optMachine=${DEFAULT_MACHINE}

#################################################################################################
## usage
#################################################################################################
usage ()
{
    cat << EOF
This scripts builds the QR-Linux kernel and other necessary images
USAGE: $0 options
OPTIONS:
   -h      Show this message
   -m      The machine to build (default: ${DEFAULT_MACHINE})
EOF
}

#################################################################################################
## handleCommandLine
#################################################################################################
handleCommandLine () {
   while getopts "hm:" o
   do
      case $o in
         h)
             usage
             exit 1
             ;;
          m)
              optMachine=$OPTARG
              ;;
          ?)
              usage
              exit
              ;;
      esac
   done
   if [[ ${optMachine} != ${MACHINE_IFC6410} && ${optMachine} != ${MACHINE_SOM8064} ]]
   then
      echo "[ERROR] Unsupported machine: ${optMachine}"
      exit 1
   fi
}

#################################################################################################
## checkRequiredPkgs
#################################################################################################
checkRequiredPkgs () {
   type ${DPKG_CMD} > /dev/null 2>&1 || {
      echo "[ERROR] ${DPKG_CMD} not found. Can't check for installed packages"
      return 1
   }

   pkgMissing=0
   pkgsMissing="Packages missing: "
   for pkg in diffstat texinfo gawk chrpath multistrap
   do
      ${DPKG_CMD} --status ${pkg} > /dev/null 2>&1
      if [ $? -ne 0 ]
      then
         pkgMissing=1
         pkgsMissing="${pkgsMissing} ${pkg}"
      fi
   done

   if [ ${pkgMissing} = 1 ]
   then
      echo "[ERROR] Check for required packages failed. ${pkgsMissing}"
      return 1
   fi
   return ${pkgMissing}
}

#################################################################################################
## main
#################################################################################################

handleCommandLine $@
echo "[INFO] Building machine: ${optMachine}"
checkRequiredPkgs || {
   echo "[ERROR] Can't build because of missing build tools"
   exit 1
}

if [ ! -d  ${TOOLCHAIN_NAME} ]
then
   echo "[INFO] Fetching toolchain"
   meta-qr-linux/scripts/linaro-fetch.sh
fi

source ./oe-init-build-env build
MACHINE=${optMachine} bitbake core-image-qrl

