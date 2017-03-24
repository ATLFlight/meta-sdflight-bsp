inherit deploy

DESCRIPTION = "Little Kernel bootloader"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5a1abdab641eec675725c843f43f03af"
HOMEPAGE = "https://www.codeaurora.org/gitweb/quic/la?p=kernel/lk.git"
PROVIDES = "virtual/bootloader"
FILESPATH =+ "${WORKSPACE}:"
SRC_URI = "file://bootable/bootloader/lk"
SRC_URI += "file://0004-morty.patch"

PV       = "1.0"
PR       = "r9"
S = "${WORKDIR}/bootable/bootloader/lk"

MY_TARGET          = "msm8974"

PARALLEL_MAKE = "-j 1"

BOOTLOADER_NAME        = "emmc_appsboot"

EXTRA_OEMAKE = "TOOLCHAIN_PREFIX='arm-oe-linux-gnueabi-' ${MY_TARGET} EMMC_BOOT=1 SIGNED_KERNEL=0 ENABLE_THUMB=false "

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
