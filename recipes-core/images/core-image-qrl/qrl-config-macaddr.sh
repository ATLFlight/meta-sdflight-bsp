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
    echo "Configure MAC address of specified interface to specified value"
    echo "usage: $prog -h -i <wlan|eth> -m <aa:bb:cc:dd:ee:ff|auto|random>"
    echo "       -h:  Help"
    echo "       -i:  wlan=Set WLAN MAC, eth=Set ethernet MAC, bt=Set BT MAC"
    echo "       -m:  aa:bb:cc:dd:ee:ff=Set MAC address to specified value"
    echo "            auto=Set MAC address correctly from the appropriate file"
    echo "            random=Set MAC address to a randomly generated value"
    exit 1
}

optIntf=
optMAC=
optRunOnce=1			# By default we always force. But
				# scripts may call us with -r to skip
				# on subsequent invocations 
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
	-m)
	    shift 
	    optMAC=$1
	    [ $# -le 0 ] && usage "[ERROR] Not enough arguments"
	    if expr "$optMAC" : '-.*' > /dev/null; then
		usage "Error: Missing value for -m"
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
[ -z $optMAC ]  && usage "[ERROR] Missing MAC address"

configMACAddr $optIntf $optMAC

