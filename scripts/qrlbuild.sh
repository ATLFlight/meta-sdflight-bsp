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
build_lk=0

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
   while getopts "hbm:" o
   do
      case $o in
         h)
             usage
             exit 1
             ;;
          b)
             build_lk=1
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
################################################################################
## buildBootloader
################################################################################
buildBootloader () {
  # Set up the bitbake environment
  if [ ! -d buildlk ]; then
    source oe-init-build-env buildlk
    grep -vEe "EXTERNAL_TOOLCHAIN|TCMODE" ../build/conf/local.conf > conf/local.conf
    cp  ../build/conf/bblayers.conf conf/bblayers.conf
  else
    source oe-init-build-env buildlk
  fi

  # Compile the cross compiler
  MACHINE=${optMachine} bitbake gcc-cross 

  # Compile the libgcc.a
  # Note there is an error during this phase, but the libgcc.a is copmiled and seems to work

  LIBGCCFILE=$BUILDDIR/tmp-eglibc/sysroots/x86_64-linux/usr/include/gcc-build-internal-cortexa8hf-vfp-neon-linux-gnueabi/arm-linux-gnueabi/libgcc/libgcc.a

  if [ ! -f $LIBGCCFILE ]; then
    set +e
    MACHINE=${optMachine} bitbake libgcc
    set -e
  fi

  if [ ! -f $LIBGCCFILE ]; then
    echo "Error generating libgcc.a"
    exit 1
  fi

  # Update the lk sources
  MACHINE=${optMachine} bitbake -c patch lk

  LKBOOTLOADERFILE=$BUILDDIR/tmp-eglibc/work/arm-linux-gnueabi/lk/1.0-r9/lk-1.0/build-msm8960/emmc_appsboot.mbn

  # Compile lk bootloader
  if [ ! -f $LKBOOTLOADERFILE ]; then
    (cd $BUILDDIR/tmp-eglibc/work/arm-linux-gnueabi/lk/1.0-r9/lk-1.0;
    export PATH=$BUILDDIR/tmp-eglibc/sysroots/x86_64-linux/usr/bin/cortexa8hf-vfp-neon-linux-gnueabi:$PATH;
    make -j 1 TOOLCHAIN_PREFIX='arm-linux-gnueabi-' msm8960 EMMC_BOOT=1 SIGNED_KERNEL=0 ENABLE_THUMB=false LIBGCC=$LIBGCCFILE)
  fi

  if [ ! -f $LKBOOTLOADERFILE ]; then
    echo "Error generating lk bootloader"
    exit 1
  else
    # Install in the proper place
    mkdir -p $BUILDDIR/../build/tmp-eglibc/deploy/images/${optMachine}/out
    cp $LKBOOTLOADERFILE $BUILDDIR/../build/tmp-eglibc/deploy/images/${optMachine}/out
  fi

  cd ..
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

if [[ $build_lk = 1 ]]
then
  buildBootloader
fi

source ./oe-init-build-env build
MACHINE=${optMachine} bitbake core-image-qrl

