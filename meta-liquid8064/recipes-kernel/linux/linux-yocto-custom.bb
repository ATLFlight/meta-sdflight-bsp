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

inherit kernel
require linux-caf.inc
inherit image_types_img

KTAG_DEFAULT = "AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241"
KTAG_liquid8064 = "AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241"
KBRANCH_DEFAULT = "linux-${MACHINE}"
KBRANCH_liquid8064 = "linux-${MACHINE}"

SRC_URI = "git://codeaurora.org/kernel/msm.git;tag=AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241;protocol=git;bareclone=1"

SRC_URI += "file://defconfig"

SRC_URI += "file://liquid8064.scc \
            file://liquid8064.cfg \
            file://liquid8064-user-config.cfg \
            file://liquid8064-user-patches.scc \
           "

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-liquid8064"

# tag: AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241
SRCREV="960880659d78027b3fc0274d3acf64b3c5b34bf8"
#SRCREV="AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241"

PR = "r0"
PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE_liquid8064 = "liquid8064"

KERNEL_IMAGETYPE = "lkImage"
GCCVERSION="4.8%"

