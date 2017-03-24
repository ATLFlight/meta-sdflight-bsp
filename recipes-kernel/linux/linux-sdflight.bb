# This file was derived from oe-core/meta-qr-linux/meta-som8064/recipes-kernel/linux/linux-qr-som8064.bb
SECTION = "kernel"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel
inherit kernel-module-split

DEPENDS += "dtbtool-native"

FILESPATH =+ "${SOURCE}:"
S         =  "${WORKDIR}/linux"
KBUILD_DEFCONFIG = "msm8974_defconfig"
SRC_URI = "file://linux"
SRC_URI += "\
	    file://defconfig \
	    file://sdflight.cfg \
            file://sdflight.scc \
            file://sdflight-user-config.cfg \
            file://0003-gcc4-9.patch \
            file://0004-list.patch \
            file://0005-ftrace.patch \
            file://0006-unresolved.patch \
            file://0006-gcc6.patch \
            file://0007-u16max.patch \
           "
SRC_URI += "https://releases.linaro.org/archive/14.09/ubuntu/ifc6410/initrd.img-3.4.0-linaro-ifc6410;downloadfilename=initrd.img;name=initrd"
SRC_URI[initrd.md5sum] = "d92fb01531698e30615f26efa2999c6c"
SRC_URI[initrd.sha256sum] = "d177ba515258df5fda6d34043261d694026c9e27f1ef8ec16674fa479c5b47fb"

# Install headers so they don't conflict with the system headers
KERNEL_SRC_PATH = "/usr/src/${MACHINE}"

COMPATIBLE_MACHINE_sdflight = "sdflight"
LINUX_KERNEL_TYPE = "standard"
LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

KERNEL_BUILD_DIR = "${WORKDIR}/build"

#PACKAGES_DYNAMIC += "^kernel-dev-.*"

PR = "r0"
PV = "${LINUX_VERSION}"

PROVIDES += "kernel-dev kernel-module-cfg80211"

#FILES_kernel-image = "/boot/zImage-3.4.0-sdflight"
#FILES_kernel-image += "/boot/devicetree-${LINUX_VERSION}.0${LINUX_VERSION_EXTENSION}.img"
#FILES_kernel-image += "/boot/initrd.img"

do_removegit () {
   rm -rf "${S}/.git"
   rm -rf "${S}/.meta"
   rm -rf "${S}/.metadir"
}

do_install_append() {
    oe_runmake headers_install INSTALL_HDR_PATH="${D}/${KERNEL_SRC_PATH}"
}

do_deploy_append() {
    #install ${KERNEL_BUILD_DIR}/arch/arm/boot/zImage ${D}/boot/sdflight/zImage
    install ${WORKDIR}/initrd.img ${DEPLOYDIR}/initrd.img
    echo "Building Eagle device tree..."
    oe_runmake ${EAGLE_KERNEL_DTB}
    dtbtool -o "${DEPLOYDIR}/devicetree-${PV}-${PR}-${MACHINE}.img" -p "${KERNEL_BUILD_DIR}/scripts/dtc/" -v "${KERNEL_BUILD_DIR}/arch/arm/boot/"
    cd ${DEPLOYDIR}
    rm -f devicetree.img
    ln -s devicetree-${PV}-${PR}-${MACHINE}.img devicetree.img
}

sysroot_stage_all_append() {
         sysroot_stage_dir "${D}/${KERNEL_SRC_PATH}" "${SYSROOT_DESTDIR}/${KERNEL_SRC_PATH}"
}

addtask do_removegit after do_unpack before do_kernel_checkout
