LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

DEPENDS += "virtual/kernel android-tools-native"

# Skip tasks
do_fetch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
}

do_deploy() {
    mkbootimg --kernel ${DEPLOY_DIR_IMAGE}/zImage \
	--dt ${DEPLOY_DIR_IMAGE}/devicetree.img \
	--base ${KERNEL_BASE} \
	--ramdisk ${DEPLOY_DIR_IMAGE}/initrd.img \
	--ramdisk_offset ${RAMDISK_OFFSET} \
	--cmdline "${KERNEL_CMDLINE}" \
	--pagesize ${PAGE_SIZE} \
	--output ${DEPLOY_DIR_IMAGE}/boot-image-${MACHINE}.img
}

addtask do_deploy after do_install

do_deploy[depends] += "virtual/kernel:do_deploy"
