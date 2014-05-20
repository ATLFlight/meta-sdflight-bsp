#!/bin/sh
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This file contains machine-specific functions to set the MAC addresses for
## various interfaces
###############################################################################

. ${QRL_COMMON_INCS_DIR}/qrl-common-inc.sh

# We check this file to bypass configuring MAC addr
QRL_CHECK_FILE=/usr/local/qr-linux/.qrl_eth_mac_done
 
QRL_DEFAULT_SOFTMAC_LOC=/lib/firmware/ath6k/AR6004/hw3.0
QRL_RAND_MAC_ROOT="40:D8:55:1B" # Fixed first 4 bytes of MAC address
				   # The last two (random) byte gets appended to this
QRL_ETH0_MAC_FILE=eth0.sh

QRL_FW_ROOTDIR=etc/firmware/
QRL_FW_SUBDIR_3_0=ath6k/AR6004/hw3.0 # The subdir in eMMC partition where the fw is

# Files to be copied. We list them as individual variables, and use a
# for loop to copy since this shell doesn't support arrays, and we need
# to rename one of the files
QRL_FW_WIFI_IN_FILE1=softmac.bin
QRL_FW_WIFI_IN_FILE2=fw.ram.bin
QRL_FW_WIFI_IN_FILE3=bdata.bin_sdio

QRL_FW_WIFI_OUT_FILE1=softmac.bin
QRL_FW_WIFI_OUT_FILE2=fw.ram.bin
QRL_FW_WIFI_OUT_FILE3=bdata.bin

QRL_FW_NUM_FILES=3

QRL_WIFI_MAC_FILE=${QRL_FW_WIFI_IN_FILE1}

globalEthMAC= 

##
## createSoftmacBin
##    Create a binary softmac file for use with ATH6K WLAN adapter.
##    Takes a colon separated hex MAC address, the location and name of
##    file to create
##    
createSoftmacBin() {
    local mac=$1
    local location=$2
    local name=$3

    file=${location}/${name}

    mac="$( humanMACToNumber ${mac} )"
    if [ -d ${location} ] && [ -w ${location} ]
    then
	if [ -e ${name} ]
	then
	    if [ -w ${name} ]
	    then
		echo "[WARNING] Overwriting existing ${file}"
	    else
		echo "[ERROR] File ${file} exists but is not writable"
		return 1
	    fi
	fi
    else
	echo "[ERROR] Can't write to location ${location}"
	return 1
    fi
    # Everything is ok, create the file
    echo ${mac} | xxd -r -p > ${file}
    echo "[INFO] Wrote file ${file}"
}

    
##
## getEthMACFromAndroid
##    Search the Android partitions and extract the ethernet address
##    Returns 0 or 1, and sets the globalEthMAC
##    
getEthMACFromAndroid() {
    macAddr=
    addrFile=
    addrFound=0
    # In Inforce 1.5 release, the ethernet address is in userdata
    mountPartition ${QRL_PARTITION_NAME_PERSIST}
    if [ $? -eq 0 ]
    then
	addrFile="${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_PERSIST}/${QRL_ETH0_MAC_FILE}"
	if [ -r ${addrFile} ]
	then
	    addrFound=1
	else
	    echo "[WARNING] Ethernet MAC address not found at ${addrFile}. Trying another location"
	fi
    else 
	echo "[WARNING] Couldn't mount partition ${QRL_PARTITION_NAME_PERSIST}. Trying another partition"
    fi

    # In Inforce 2.0 release, the ethernet address is in persist. If
    # not found already, look in persist for the ethernet address.
    if [ ${addrFound} -eq 0 ]
    then
    	mountPartition ${QRL_PARTITION_NAME_USERDATA}
	if [ $? -eq 0 ]
	then
	    addrFile="${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_USERDATA}/${QRL_ETH0_MAC_FILE}"
	    if [ -r ${addrFile} ]
	    then
		addrFound=1
	    else
		echo "[WARNING] Ethernet MAC address not found at ${addrFile}. Giving up"
		return 1
	    fi
	else 
	    echo "[ERROR] Couldn't mount partition ${QRL_PARTITION_NAME_USERDATA}"
	    return 1
	fi
    fi

    if [ ${addrFile} -eq 0 ]
    then
	echo "[ERROR] No MAC address found"
	return 1
    fi
    macAddr=`cat ${addrFile}  | grep hwaddr | sed 's/.*hwaddr //g'`
    globalEthMAC=${macAddr}
    return 0
}

##
## setEthMACPersistently: Write the specified ethernet address to a persistent file
## 
setEthMACPersistently() {
    macToUse=$1
    addrFile="${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_PERSIST}/${QRL_ETH0_MAC_FILE}"

    if [ ! -w ${addrFile} ]
    then
    	mountPartition ${QRL_PARTITION_NAME_PERSIST}
	if [ $? -eq 0 ]
	then
	    if [ ! -e ${addrFile} ]
	    then
		echo "hwaddr ${macToUse}" > ${addrFile}
		return $?
	    fi
	fi
    fi
    sed -i -e "s/hwaddr.*/hwaddr ${macToUse}/" ${addrFile}
    echo "[INFO] Wrote ${macToUse} to ${addrFile}"
}

    
##
## configMACAddr:
##    Function called by target-independent script to configure MAC address
##    Configure the MAC address for interface ($1) to value ($2)
##    For the IFC6410, we can set either the ethernet or WLAN MAC address.
##    The MAC address can be set either from the NV file, random value
##    or the specified : separated hex value
##    
configMACAddr() {
    if [  ${optRunOnce} -eq 0 ]
    then
	if [ -e ${QRL_CHECK_FILE} ]
	then
	    # Without the force option, we don't do anything if
	    # a specific file is present
	    return 0
	fi
    fi
    doConfigMACAddr $1 $2
    # If we couldn't configure the MAC successfully for eth, we'll set it
    # to a random value, so that ethernet always comes up
    if [ $? -eq 2 ]
    then
	if [ ${1} = "eth" ]
	then
	    echo "[INFO] Configuring a random MAC address instead"
	    doConfigMACAddr $1 random
	fi
    fi
}


doConfigMACAddr() {
    local interface=$1
    local macAddr=$2

    retVal=
    macToUse=
    case $interface in
	eth)
	    case $macAddr in
		auto)
		    getEthMACFromAndroid
		    if [ $? -eq 0 ]
		    then
			echo "[INFO] Read MAC from Android: ${globalEthMAC}"
			macToUse=${globalEthMAC}
		    else
			echo "[ERROR] Could not get MAC from Android partitions"
			return 2
		    fi
		    ;;
		random)
                    # Get a 2-digit random nunmber for changing the MAC address
		    mac=$(( 1+$(od -An -N2 -i /dev/random)%(100) ))
		    randMac="40:D8:55:1B:40:${mac}"
		    echo "[INFO] Generated random Ethernet MAC address: $randMac"
		    macToUse=${randMac}
		    ;;
		*)
		    macToUse=${macAddr}
		    ;;
	    esac
	    echo "[INFO] Setting system config to use Ethernet MAC addr ${macToUse}"
	    file=/etc/network/if-pre-up.d/eth0-config-macaddr
	    sed -i -e "s/hw ether.*/hw ether ${macToUse}/" ${file}
	    retVal=$?
	    # Disabling touching the persist partition
	    # setEthMACPersistently ${macToUse}
	    touch ${QRL_CHECK_FILE}
	    return $retVal
	    ;;
	wlan)
	    case $macAddr in
		auto)
		    echo "[INFO] Nothing to do"
		    return 0
		    ;;
		random)
                    # Get a 2-digit random nunmber for changing the MAC address
		    mac=$(( 1+$(od -An -N2 -i /dev/random)%(100) ))
		    randMac="${QRL_RAND_MAC_ROOT}:${mac}"
		    echo "[INFO] Generated random WLAN MAC address: $randMac"
		    macToUse=${randMac}
		    ;;
		*)
		    macToUse=${macAddr}
		    ;;
	    esac
	    echo "[INFO] Setting WLAN MAC addr to ${macToUse}"
	    createSoftmacBin ${macToUse} ${QRL_DEFAULT_SOFTMAC_LOC} ${QRL_WIFI_MAC_FILE}
	    ;;
	*)
	    echo "[ERROR] Don't undertand interface: $interface"
	    ;;
    esac
}

qrlFwSrcDir=${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_SYSTEM}/${QRL_FW_ROOTDIR}/${QRL_FW_SUBDIR_3_0}
qrlFwDestDir=${QRL_LIB_FIRMWARE}/${QRL_FW_SUBDIR_3_0}

##
## copyFirmware
##    Function called by target-independent script to copy firmware
##    Copies the firmware from target-specific source, to target-specfic location
##    under /lib/firmware
##    
copyFirmware() {

    mountPartition ${QRL_PARTITION_NAME_SYSTEM}
    if [ $? -gt 0 ]
    then
	echo "[ERROR] Mounting partition ${QRL_PARTITION_NAME_SYSTEM}"
	return 1
    fi
    
    srcFile=${qrlFwSrcDir}/${QRL_FW_WIFI_IN_FILE2}
    dstFile=${qrlFwDestDir}/${QRL_FW_WIFI_IN_FILE2}
    
    if [ ! -e ${srcFile} ]
    then
	echo "[ERROR] Firmware not found in ${QRL_FW_SUBDIR_3_0} or mount failed"
	continue
    fi
    
    mkdir -p ${qrlFwDestDir} || {
	echo "[ERROR] Could not create firwmare dir ${qrlFwDestDir}"
	return 0
    }

    # Found the firmware, copy each file to its destination
    for i in `seq 1 ${QRL_FW_NUM_FILES}`
    do
	eval /bin/cp ${qrlFwSrcDir}/\${QRL_FW_WIFI_IN_FILE${i}} ${qrlFwDestDir}/\${QRL_FW_WIFI_OUT_FILE${i}}
	retVal=$?
    done
    
    if [ ! -f ${dstFile} ]
    then
	echo "[ERROR] Firmware  not copied"
	return 1
    fi
    echo "[INFO] Copied firmware"
    return 0
}

##
## copyMACAddr
##    Function called by target-independent script to copy MAC Address
##    Copies the MAC address files to appropriate target-specific location
##    
copyMACAddr() {
    wifiMACDestFile=${qrlFwDestDir}/${QRL_WIFI_MAC_FILE}

    qrlMACFileDir=${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_PERSIST}
    wifiMACSrcFile=${qrlMACFileDir}/${QRL_WIFI_MAC_FILE}

    mountPartition ${QRL_PARTITION_NAME_PERSIST}
    if [ $? -gt 0 ]
    then
	echo "[ERROR] Mounting partition ${QRL_PARTITION_NAME_PERSIST}"
	return 1
    fi
    
    if [ -f ${wifiMACSrcFile} ]
    then
	/bin/cp ${wifiMACSrcFile} ${qrlFwDestDir}
	retVal=$?
    else
	echo "[WARNING] MAC Address file not found or mount failed. Trying another location"
	# Check in system partition
	qrlMACFileDir=${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_SYSTEM}/${QRL_FW_ROOTDIR}/${QRL_FW_SUBDIR_3_0}
	wifiMACSrcFile=${qrlMACFileDir}/${QRL_WIFI_MAC_FILE}

	mountPartition ${QRL_PARTITION_NAME_SYSTEM}
	if [ $? -gt 0 ]
	then
	    echo "[ERROR] Mounting partition ${QRL_PARTITION_NAME_SYSTEM}"
	    return 1
	fi
    
	if [ -f ${wifiMACSrcFile} ]; then
	    /bin/cp ${wifiMACSrcFile} ${qrlFwDestDir}
	    retVal=$?
	else
	    echo "[WARNING] MAC Address file not found or mount failed."
	    return 1
	fi
    
    fi
    if [ ! -f ${wifiMACDestFile} ]; then
	echo "[ERROR] MAC file not copied"
	return 1
    fi
    echo "[INFO] Copied MAC address"
    return $retVal
}