#!/bin/sh
###############################################################################
# Author: Rahul Anand (ranand@codeaurora.org)
# 
# This script copies the firmware from a target-specific location to
## where the kernel expects it (/lib/firmware)
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
