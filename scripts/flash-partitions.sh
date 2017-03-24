#!/bin/bash

MACHINE = sdflight
IMAGEDIR = tmp-glibc/deploy/images/${MACHINE}

adb reboot bootloader || true

# Flash the LK bootloader
if [ -e ${IMAGEDIR}/emmc_appsboot.mbn ]; then
	sudo fastboot flash aboot ${IMAGEDIR}/emmc_appsboot.mbn
fi

# Flash the boot image with Linux kernel
if [ -e ${IMAGEDIR}/boot-image-${MACHINE}.img ]; then
	sudo fastboot flash boot ${IMAGEDIR}/boot-image-${MACHINE}.img
fi

# Flash the firmware to the cache partition
if [ -e ${IMAGEDIR}/cache-image-${MACHINE}.img ]; then
	sudo fastboot flash cache ${IMAGEDIR}/cache-image-${MACHINE}.img
fi

# Flash the rootfs to the userdata partition
if [ -e ${IMAGEDIR}/userdata-image-${MACHINE}.img ]; then
	sudo fastboot flash userdata ${IMAGEDIR}/userdata-image-${MACHINE}.img
fi

# Flash persistent data only if not upgrading SW
if [ "$1" = "--all" ]; then
	if [ -e ${IMAGEDIR}/persist-image-${MACHINE}.img ]; then
		sudo fastboot flash persist ${IMAGEDIR}/persist-image-${MACHINE}.img
	fi
fi

# Flash system image
# if [ -e system.img ]; then
# 	sudo fastboot flash system system.img
# fi

# Flash revovery image
# if [ -e recovery.img ]; then
# 	sudo fastboot flash recovery recovery.img
# fi
# if [ -e update.img ]; then
# 	sudo fastboot flash update update.img
# fi
# Flash factory image
# if [ -e factory.img ]; then
# 	sudo fastboot flash factory factory.img
# fi

sudo fastboot reboot
echo Flashing done, rebooting...
