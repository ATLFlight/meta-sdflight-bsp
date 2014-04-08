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
# Notes:
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

KTAG_som8064 = "AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041" 
KBRANCH_som8064 = "linux-${MACHINE}"
KBRANCH_DEFAULT = "linux-${MACHINE}"

require include/linux-caf.inc

SRC_URI = "git://codeaurora.org/kernel/msm.git;tag=AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041;protocol=git;bareclone=1"
SRC_URI += "file://defconfig \
            file://som8064.scc \
            file://som8064.cfg \
            file://som8064-user-config.cfg \
			file://som8064-user-patches.scc \
           "

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

PR = "r0"
PV = "${LINUX_VERSION}+git${SRCPV}"

GCCVERSION="4.7%"

COMPATIBLE_MACHINE_som8064 = "som8064"
LINUX_VERSION_EXTENSION_som8064 = "-som8064"

PROVIDES += "kernel-module-cfg80211"
