#!/bin/bash
################################################################################
## Copyright (c) 2014,2015 The Linux Foundation.
## All rights reserved.
## 
## Redistribution and use in source and binary forms, with or without
## modification, are permitted (subject to the limitations in the
## disclaimer below) provided that the following conditions are met:
## 
## * Redistributions of source code must retain the above copyright
##   notice, this list of conditions and the following disclaimer.
## 
## * Redistributions in binary form must reproduce the above copyright
##   notice, this list of conditions and the following disclaimer in the
##   documentation and/or other materials provided with the distribution.
## 
## * Neither the name of the Linux Foundation nor the names of its
##   contributors may be used to endorse or promote products derived from
##   this software without specific prior written permission.
## 
## NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE
## GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
## HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
## WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
## MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
## DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
## LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
## CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
## SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
## BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
## WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
## OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
## IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
## 
################################################################################
currentMode=""
softapDetails=""

function usage() {
    echo "usage: wificonfig [-gh] [-s <softap | station>"
    echo " "
    echo "options:"
    echo "   -h   show this usage"
    echo "   -g   get the current mode"
    echo "   -s   set the current mode (softap or station)"
}

# Each mode has its own qca6234.cfg file, and the symlink, qca6234.cfg.lnk
# determines which one is active.
if [ -h /etc/network/interfaces.d/qca6234.cfg.lnk ]; then
    link=$(basename `readlink /etc/network/interfaces.d/qca6234.cfg.lnk`)
    if [ "$link" == ".qca6234.cfg.station" ]; then
        currentMode="station"
    elif [ "$link" == ".qca6234.cfg.softap" ]; then
        currentMode="softap"
        pswd=`grep -e "^wpa_passphrase=" /etc/hostapd.conf`
        ssid=`grep -e "^ssid=" /etc/hostapd.conf`
        softapDetails="[ $ssid, $pswd ]"
    fi
fi

if [ "$1" == "-h" ]; then
   usage

elif [ "$1" == "-s" ]; then

    # We're setting the mode

    good=false

    # Only switch to specified mode, if current mode isn't already set to it.
    if [ "$currentMode" != "$2" ]; then
         if [ "$2" == "softap" ]; then
             echo "Configuring for softap mode"

             # Set the qca6234.cfg.lnk to the softap variant (e.g. .qca6234.cfg.softap)
             rm -f /etc/network/interfaces.d/qca6234.cfg.lnk
             ln -s /etc/network/interfaces.d/.qca6234.cfg.softap /etc/network/interfaces.d/qca6234.cfg.lnk

             # Soft ap mode requires dnsmasq service, so configure it to start 
             # up at boot.
             update-rc.d dnsmasq defaults > /dev/null
             good=true
         elif [ "$2" == "station" ]; then
             echo "Configuring for station mode"

             # Set the qca6234.cfg.lnk to the station variant (e.g. .qca6234.cfg.station)
             rm -f /etc/network/interfaces.d/qca6234.cfg.lnk
             ln -s /etc/network/interfaces.d/.qca6234.cfg.station /etc/network/interfaces.d/qca6234.cfg.lnk


             # For station mode, prevent dnsmasq from starting up at boot.
             update-rc.d -f dnsmasq remove > /dev/null
             good=true
         else
             # User specified a bogus mode so print the usage.
             echo "Illegal mode: ($2) specified"
             usage
             exit
         fi

         # If we succesfully set the mode, inform the user that they need to reboot to have them
         # take effect
         if ( $good ); then
             echo "Reboot for changes to take effect"
         fi
    else
        echo "Mode already set to $2"
    fi 

elif [ "$1" == "-g" ]; then

    # Getting the current mode

    echo "$currentMode $softapDetails"

else
    echo "Illegal option ($1) specified"
    usage
fi
