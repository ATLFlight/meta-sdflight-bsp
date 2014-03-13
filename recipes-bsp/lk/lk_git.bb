inherit deploy

DESCRIPTION = "Little Kernel bootloader"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=0835ade698e0bcf8506ecda2f7b4f302"
HOMEPAGE = "https://www.codeaurora.org/gitweb/quic/la?p=kernel/lk.git"
PROVIDES = "virtual/bootloader"
SRC_URI  = "git://codeaurora.org/kernel/lk;protocol=git;tag=AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241 \
	 file://0001-lk.patch \
	 file://0101-APQ8064_SOM_CDP-basic-changes-for-som-bringup.patch \
	 file://0102-SOM-Machine-type-definition.patch \
	 file://0103-SOM-Carrier-RevB-board-serial-port-rework-fix-with-L.patch \
	 file://0104-SOM-UART_4wire-on-GSBI1-as-default-serial-port.patch"

PR       = "r9"

PACKAGE_ARCH = "${MACHINE_ARCH}"

#re-use non-perf settings

#LIBGCC_9615-cdp    = "${STAGING_LIBDIR}/${TARGET_SYS}/4.6.3/libgcc.a"
#LIBGCC_mdm9625     = "${STAGING_LIBDIR}/${TARGET_SYS}/4.6.3/libgcc.a"

MY_TARGET          = "msm8960"


BOOTLOADER_NAME         = "appsboot"
BOOTLOADER_NAME_msm8960 = "emmc_appsboot"
BOOTLOADER_NAME_msm8974 = "emmc_appsboot"
BOOTLOADER_NAME_msm8610 = "emmc_appsboot"

EXTRA_OEMAKE = "TOOLCHAIN_PREFIX='${TARGET_PREFIX}' ${MY_TARGET}"
EXTRA_OEMAKE_append_9615-cdp = " LIBGCC='${LIBGCC}'"
EXTRA_OEMAKE_append_mdm9625  = " LIBGCC='${LIBGCC}'"
EXTRA_OEMAKE_append_msm8960  = " EMMC_BOOT=1 SIGNED_KERNEL=0 ENABLE_THUMB=false "
EXTRA_OEMAKE_append_msm8974  = " EMMC_BOOT=1 SIGNED_KERNEL=1"
EXTRA_OEMAKE_append_msm8610  = " EMMC_BOOT=1 SIGNED_KERNEL=1"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_install() {
	install	-d ${D}/boot
	install build-${MY_TARGET}/${BOOTLOADER_NAME}.{mbn,raw} ${D}/boot
}

do_install_append_msm8960() {
	install build-${MY_TARGET}/EMMCBOOT.MBN ${D}/boot
}

do_install_append_msm8974() {
	install build-${MY_TARGET}/EMMCBOOT.MBN ${D}/boot
}

do_install_append_msm8610() {
	install build-${MY_TARGET}/EMMCBOOT.MBN ${D}/boot
}

FILES_${PN} = "/boot"

do_deploy () {
        install ${S}/build-${MY_TARGET}/${BOOTLOADER_NAME}.{mbn,raw} ${DEPLOYDIR}
}
do_deploy[dirs] = "${S} ${DEPLOYDIR}"
addtask deploy before do_build after do_install

PACKAGE_STRIP = "no"
