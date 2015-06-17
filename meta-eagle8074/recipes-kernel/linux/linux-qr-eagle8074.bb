# This file was derived from oe-core/meta-qr-linux/meta-som8064/recipes-kernel/linux/linux-qr-som8064.bb

KTAG_${MACHINE} = "LNX.LA.3.5-01620-8x74.0"
KBRANCH_${MACHINE} = "linux-${MACHINE}"
KBRANCH_DEFAULT = "linux-${MACHINE}"

require include/linux-caf.inc

SRC_URI = "git://codeaurora.org/kernel/msm;tag=LNX.LA.3.5-01620-8x74.0;protocol=git;bareclone=1;nobranch=1"
SRC_URI += "file://defconfig \
            file://${MACHINE}.scc \
            file://${MACHINE}.cfg \
            file://${MACHINE}-user-config.cfg \
            file://${MACHINE}-user-patches.scc \
            file://apq8074-v2.2-eagle.dts \
            file://apq8074-eagle.dtsi \
           "
SRC_URI += "http://releases.linaro.org/14.09/ubuntu/ifc6410/initrd.img-3.4.0-linaro-ifc6410;downloadfilename=initrd.img;name=initrd"
SRC_URI[initrd.md5sum] = "d92fb01531698e30615f26efa2999c6c"
SRC_URI[initrd.sha256sum] = "d177ba515258df5fda6d34043261d694026c9e27f1ef8ec16674fa479c5b47fb"

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

PR = "r0"
PV = "${LINUX_VERSION}+git${SRCPV}"

GCCVERSION="4.8%"

COMPATIBLE_MACHINE_eagle8074 = "eagle8074"
LINUX_VERSION_EXTENSION_eagle8074 = "-eagle8074"

PROVIDES += "kernel-module-cfg80211"

addtask unpack_dtb after do_kernel_checkout before do_validate_branches
