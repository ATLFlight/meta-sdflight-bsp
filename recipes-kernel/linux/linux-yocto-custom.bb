# This file was derived from the linux-yocto-custom.bb recipe in
# oe-core.
#
# linux-yocto-custom.bb:
#
#   A yocto-bsp-generated kernel recipe that uses the linux-yocto and
#   oe-core kernel classes to apply a subset of yocto kernel
#   management to git managed kernel repositories.
#
# Warning:
#
#   Building this kernel without providing a defconfig or BSP
#   configuration will result in build or boot errors. This is not a
#   bug.
#
# Notes:n
#
#   patches: patches can be merged into to the source git tree itself,
#            added via the SRC_URI, or controlled via a BSP
#            configuration.
#
#   example configuration addition:
#            SRC_URI += "file://smp.cfg"
#   example patch addition:
#            SRC_URI += "file://0001-linux-version-tweak.patch
#   example feature addition:
#            SRC_URI += "file://feature.scc"
#
inherit kernel
require linux-caf.inc

DEPENDS += "android-tools-native"

do_lk_mkimage() {
  # Make bootimage
  ver=`sed -r 's/#define UTS_RELEASE "(.*)"/\1/' ${WORKDIR}/image/usr/src/kernel/include/generated/utsrelease.h`
  # Update base address according to new memory map.
  ${STAGING_BINDIR_NATIVE}/mkbootimg --kernel ${WORKDIR}/linux-${MACHINE}-standard-build/arch/arm/boot/zImage \
	--ramdisk /dev/null \
        --cmdline "noinitrd console=ttyHSL0,115200,n8 root=/dev/mmcblk0p13 rw rootwait" \
	--base 0x80200000 \
        --pagesize 2048 \
	--output ${DEPLOY_DIR_IMAGE}/${PN}-boot-${MACHINE}.img
}

addtask lk_mkimage after do_deploy and before do_package

KTAG_DEFAULT = "AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241"
KBRANCH_DEFAULT = "linux-${MACHINE}"

SRC_URI = "git://codeaurora.org/kernel/msm.git;tag=AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241;protocol=git;bareclone=1"

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

PR = "r0"
PV = "${LINUX_VERSION}+git${SRCPV}"

GCCVERSION="4.7%"

