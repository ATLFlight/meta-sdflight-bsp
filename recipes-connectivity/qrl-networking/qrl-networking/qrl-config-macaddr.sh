#!/bin/sh
###############################################################################
# Author: Rahul Anand (ranand@codeaurora.org)
# 
# This script sets the MAC address as specfied
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

QRL_COMMON_INCS_DIR=/usr/local/qr-linux
# Include common files
. ${QRL_COMMON_INCS_DIR}/qrl-mac-fw-inc.sh

usage() {
    prog=`basename $0` 1>&2
    echo "$prog : $*"
    echo "Configure MAC address of specified interface to specified value"
    echo "usage: $prog -h -i <wlan|eth|bt> -m <aa:bb:cc:dd:ee:ff|auto|random>"
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

