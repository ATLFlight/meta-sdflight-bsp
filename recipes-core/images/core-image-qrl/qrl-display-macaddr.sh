#!/bin/sh
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This script sets the MAC address as specfied
###############################################################################

QRL_COMMON_INCS_DIR=/usr/local/qr-linux
# Include common files
. ${QRL_COMMON_INCS_DIR}/qrl-mac-fw-inc.sh

usage() {
    prog=`basename $0` 1>&2
    echo "$prog : $*"
    echo "Display MAC address of specified interface"
    echo "usage: $prog -h -i <wlan|eth|bt>"
    echo "       -h:  Help"
    echo "       -i:  wlan=Display WLAN MAC, eth=Display ethernet MAC, bt=Display BT MAC"
    exit 1
}

optIntf=
optMAC=
optRunOnce=1			# By default we always force. But
				# scripts may call us with -r to skip
				# on subsequent invocations 

mountPartition ${QRL_PARTITION_NAME_PERSIST}
if [ $? -gt 0 ]
then
    echo "[ERROR] Mounting partition ${QRL_PARTITION_NAME_PERSIST}"
    return 1
fi
    

while [ "$1" != "" ]
do
    case $1 in 
	-i)
	    shift 
	    [ $# -le 0 ] && usage "[ERROR] Not enough arguments"
	    optIntf=$1
	    if expr "$optIntf" : '-.*' > /dev/null; then
		usage "[ERROR] Missing value for -i"
	    fi
	    if [ ${optIntf} != "wlan" ] && [ ${optIntf} != "eth" ] && [ ${optIntf} != "bt" ]
	    then
		usage "[ERROR] -i only takes wlan, bt, or eth"
	    fi
	    ;;
	-r)
	    optRunOnce=0
	    ;;
	-h)
	    usage "Help"
	    ;;
	*)
	    usage "Unknown argument $1"
	    break
	    ;;
    esac
    shift
done
[ -z $optIntf ] && usage "[ERROR] Missing interface"

displayMACAddr $optIntf 

