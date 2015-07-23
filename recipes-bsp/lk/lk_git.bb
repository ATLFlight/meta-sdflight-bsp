inherit deploy

DESCRIPTION = "Little Kernel bootloader"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=5a1abdab641eec675725c843f43f03af"
HOMEPAGE = "https://www.codeaurora.org/gitweb/quic/la?p=kernel/lk.git"
PROVIDES = "virtual/bootloader"

SRC_URI  = "git://codeaurora.org/kernel/lk;protocol=git;nobranch=1"

SRCREV = "LNX.LA.3.5.2-09410-8x74.0"

SRC_URI += "\
         file://0300-Explicitly-set-arm-mode-if-THUMB-support-is-disabled.patch \
         file://0001-Eagle-hardware-support.patch \
		 file://0004-Override-platform-ID-for-Eagle.patch"

PV       = "1.0"
PR       = "r9"

PACKAGE_ARCH = "${MACHINE_ARCH}"

MY_TARGET          = "msm8974"

PARALLEL_MAKE = "-j 1"

BOOTLOADER_NAME        = "emmc_appsboot"

EXTRA_OEMAKE = "TOOLCHAIN_PREFIX='arm-linux-gnueabihf-' ${MY_TARGET} EMMC_BOOT=1 SIGNED_KERNEL=0 ENABLE_THUMB=false "

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_compile() {
    make ${PARALLEL_MAKE} ${EXTRA_OEMAKE}
}

do_install() {
    install -d ${D}/usr/share/lk
}

do_deploy () {
	install -d ${DEPLOYDIR}/out
        install ${S}/build-${MY_TARGET}/${BOOTLOADER_NAME}.mbn ${DEPLOYDIR}/out
        install ${S}/build-${MY_TARGET}/lk ${DEPLOYDIR}/out
}

do_deploy[dirs] = "${S} ${DEPLOYDIR}"
addtask deploy before do_build after do_install

PACKAGE_STRIP = "no"
