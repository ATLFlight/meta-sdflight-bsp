#!/bin/sh
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This script copies the firmware from a target-specific location to
## where the kernel expects it (/lib/firmware)
###############################################################################

QRL_COMMON_INCS_DIR=/usr/local/qr-linux
# Include common files
. ${QRL_COMMON_INCS_DIR}/qrl-mac-fw-inc.sh


usage() {
    prog=`basename $0` 1>&2
    echo "$prog : $*"
    echo "usage: $prog -h -f -m"
    echo "       -h: Help"
    echo "       -f: Copy firmware only"
    echo "       -m: Copy file for MAC address only"
    exit 1
}

optFWOnly=
optMACOnly=

while [ "$1" != "" ]
do
    case $1 in 
	-f)
	    optFWOnly=1
	    ;;
	-m)
	    optMACOnly=1
	    ;;
	-h)
	    usage ""
	    ;;
	*)
	    usage "Unknown argument $1"
	    break
	    ;;
    esac
    shift
done

if [ -z ${optMACOnly} ]
then
    copyFirmware
fi

if [ -z ${optFWOnly} ]
then
    copyMACAddr
fi
