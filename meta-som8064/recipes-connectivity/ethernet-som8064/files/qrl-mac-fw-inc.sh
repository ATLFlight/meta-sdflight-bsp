#!/bin/sh
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This script copies the firmware from a target-specific location to
## where the kernel expects it (/lib/firmware)
###############################################################################

QRL_COMMON_INCS_DIR=/usr/local/qr-linux
# Include common files
. ${QRL_COMMON_INCS_DIR}/qrl-common-inc.sh

PRINTF=/usr/bin/printf
DD=/bin/dd

QRL_NV_FILE_LOC=/lib/firmware/wlan/prima
QRL_RAND_MAC_ROOT='00:0A:F5:89:89:' # The last (random) byte gets appended to this
globalWlanMAC= 

QRL_FW_SUBDIR=image		# The subdir in eMMC partition where the fw is
QRL_FW_WIFI_FILE_NAME=wcnss	# The root of thw WCN fw filename
QRL_FW_WIFI_FILE_EXT=mdt	# The extension of the WCN fw filename

QRL_NV_FILE_NAME=WCNSS_qcom_wlan_nv # The NV.bin file name and extension
QRL_NV_FILE_EXT=bin
QRL_NV_FILE_DEST_DIR=${QRL_LIB_FIRMWARE}/wlan/prima # Where to copy NV file

##
## getWlanMACFromNVFile:
##    Read the WCNSS NV file and extract the MAC address from it.
##    Returns 0 or 1, and sets globalWlanMAC
##    
getWlanMACFromNVFile() {
    nvFile=${QRL_NV_FILE_LOC}/${QRL_NV_FILE_NAME}.${QRL_NV_FILE_EXT}
    if [ ! -r ${nvFile} ] 
    then
	echo "[ERROR] Can't read from file ${nvFile}"
	return 1
    fi
    globalWlanMAC=`od -A n -j 10 -N 6 -t xC ${nvFile} | sed -e "s/ //" -e "s/ /:/g"`
    if [ ${globalWlanMAC} = "00:00:00:00:00:00" ]
    then
	echo "[INFO] No MAC address set in NV file"
	return 1
    fi
    return 0
}
    
##
## setWlanMACInNVFile:
##    Set the provided MAC address to the WCNSS NV file in /lib/firmware.
##    Don't touch the persist partition
##    
setWlanMACInNVFile() {
    nvFile=${QRL_NV_FILE_LOC}/${QRL_NV_FILE_NAME}.${QRL_NV_FILE_EXT}
    if [ ! -w ${nvFile} ] 
    then
	echo "[ERROR] Can't write file ${nvFile}, or not present"
	return 1
    fi
    existingMAC=`od -A n -j 10 -N 6 -t xC ${nvFile} | sed -e "s/ //" -e "s/ /:/g"`
    if [ ${existingMAC} != "00:00:00:00:00:00" ]
    then
	echo "[WARNING] Overwriting existing MAC address: ${existingMAC}"
    fi
    
    echo "[INFO] Setting MAC address to ${macToUse} in ${nvFile}"
    macToUse=":${1}" # Prepend a :, for the sed step next
    macToUseHex=$( echo ${macToUse} | sed 's/:/\\x/g' )
    ${PRINTF} ${macToUseHex} | ${DD} of=${nvFile} bs=1 seek=10 count=6 conv=notrunc > /dev/null
}

##
## configMACAddr:
##    Configure the MAC address for interface ($1) to value ($2)
##    For the SOM, we can't set the ethernet address. The WLAN
##    MAC address can be set only from the NV file.
##    Random value and specified value may also be specified
##    
configMACAddr() {
    local interface=$1
    local macAddr=$2

    case $interface in
	wlan)
	    macToUse=
	    case $macAddr in
		auto)
		    getWlanMACFromNVFile
		    if [ $? -eq 0 ]
		    then
			echo "[INFO] Read MAC from NV file: ${globalWlanMAC}"
			macToUse=${globalWlanMAC}
		    else
			echo "[ERROR] Could not get MAC from NV file"
			return 1
		    fi
		    ;;
		random)
                    # Get a 2-digit random nunmber for changing the MAC address
		    mac=$(( 1+$(od -An -N2 -i /dev/random)%(100) ))
		    randMac=${QRL_RAND_MAC_ROOT}${mac}
		    echo "[INFO] Generated random Wi-Fi MAC address: $randMac"
		    macToUse=${randMac}
		    ;;
		*)
		    macToUse=${macAddr}
		    ;;
	    esac
	    setWlanMACInNVFile ${macToUse}
	    return $?
	    ;;
	eth)
	    echo "[ERROR] Can't set ethernet MAC address for this device type"
	    return 1
	    ;;
	*)
	    echo "[ERROR] Don't undertand interface: $interface"
	    return 1
	    ;;
    esac
}


# Where to copy the firmware files from
qrlFwImgDir=${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_MODEM}/${QRL_FW_SUBDIR}

# The .mdt file's full source and destination paths
wifiMdtSrcFile=${qrlFwImgDir}/${QRL_FW_WIFI_FILE_NAME}.${QRL_FW_WIFI_FILE_EXT}
wifiMdtDstFile=${QRL_LIB_FIRMWARE}/${QRL_FW_WIFI_FILE_NAME}.${QRL_FW_WIFI_FILE_EXT}

##
## copyFirmware
##    Function called by target-independent script to copy firmware
##    Copies the firmware from target-specific source, to target-specfic location
##    under /lib/firmware
##    
copyFirmware() {
    mountPartition ${QRL_PARTITION_NAME_MODEM}
    if [ $? -gt 0 ]
    then
	echo "[ERROR] Mounting partition ${QRL_PARTITION_NAME_MODEM}"
	return 1
    fi
    
    if [ ! -e ${wifiMdtSrcFile} ]
    then
	echo "[ERROR] Firmware not found or mount failed"
	return 1
    fi

    # Copy the wcnss* files from eMMC partition to /lib/firmware
    /bin/cp ${qrlFwImgDir}/${QRL_FW_WIFI_FILE_NAME}* ${QRL_LIB_FIRMWARE}
    
    if [ ! -f ${wifiMdtDstFile} ]
    then
	echo "[ERROR] Firmware  not found. Giving up"
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

    qrlNVFileDir=${QRL_DEFAULT_MOUNTROOT}/${QRL_PARTITION_NAME_PERSIST}
    wifiMdtNVFile=${qrlNVFileDir}/${QRL_NV_FILE_NAME}.${QRL_NV_FILE_EXT}
    wifiMdtNVDstFile=${QRL_NV_FILE_DEST_DIR}/${QRL_NV_FILE_NAME}.${QRL_NV_FILE_EXT}

    mountPartition ${QRL_PARTITION_NAME_PERSIST}
    if [ $? -gt 0 ]
    then
	echo "[ERROR] Mounting partition ${QRL_PARTITION_NAME_PERSIST}"
	return 1
    fi
    
    if [ -f ${wifiMdtNVFile} ]; then
	/bin/cp ${wifiMdtNVFile} ${wifiMdtNVDstFile}
    else
	echo "[WARNING] NV File not found or mount failed"
	return 1
    fi
    
    if [ ! -f ${wifiMdtNVDstFile} ]; then
	echo "[ERROR] NV File not found, Giving up"
	return 1
    fi
    echo "[INFO] Copied MAC address"
    return 0
}

