DESCRIPTION = "CAF Kernel"
SECTION = "kernel"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

INC_PR = "r0"

DEPENDS += "xz-native bc-native"

# A KMACHINE is the mapping of a yocto $MACHINE to what is built
# by the kernel. This is typically the branch that should be built,
# and it can be specific to the machine or shared
# KMACHINE = "UNDEFINED"

LINUX_KERNEL_TYPE ?= "standard"

# KMETA ?= ""
KBRANCH ?= "kk_2.7"
KTAG ?= "AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041"
KMACHINE ?= "${MACHINE}"
SRCREV_FORMAT ?= "meta_machine" 

LINUX_VERSION_EXTENSION ?= "-caf-${LINUX_KERNEL_TYPE}"

do_patch[depends] = "kern-tools-native:do_populate_sysroot"

addtask kernel_configme before do_configure after do_patch

# Pick up shared functions
inherit kernel
inherit kernel-caf
#require recipes-kernel/linux/linux-dtb.inc

B = "${WORKDIR}/linux-${MACHINE}-${LINUX_KERNEL_TYPE}-build"

do_install_append(){
	if [ -n "${KMETA}" ]; then
		rm -rf ${STAGING_KERNEL_DIR}/${KMETA}
	fi

    # linux kernel headers
    install -d ${STAGING_INCDIR}/linux-headers
    cd ${WORKDIR}/linux
    oe_runmake headers_install O=${STAGING_INCDIR}/linux-headers

}

# extra tasks
addtask kernel_link_vmlinux after do_compile before do_install
addtask validate_branches before do_patch after do_kernel_checkout
addtask kernel_configcheck after do_configure before do_compile
