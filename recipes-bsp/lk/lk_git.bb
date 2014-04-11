inherit deploy

DESCRIPTION = "Little Kernel bootloader"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=5a1abdab641eec675725c843f43f03af"
HOMEPAGE = "https://www.codeaurora.org/gitweb/quic/la?p=kernel/lk.git"
PROVIDES = "virtual/bootloader"

SRC_URI  = "git://codeaurora.org/kernel/lk;protocol=git;tag=AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241 \
	 file://NOTICE \
	 file://0001-lk.patch \
	 file://0101-APQ8064_SOM_CDP-basic-changes-for-som-bringup.patch \
	 file://0102-SOM-Machine-type-definition.patch \
	 file://0103-SOM-Carrier-RevB-board-serial-port-rework-fix-with-L.patch \
	 file://0104-SOM-UART_4wire-on-GSBI1-as-default-serial-port.patch"

PV       = "1.0"
PR       = "r9"

PACKAGE_ARCH = "${MACHINE_ARCH}"

MY_TARGET          = "msm8960"

PARALLEL_MAKE = "-j 1"

BOOTLOADER_NAME         = "appsboot"
BOOTLOADER_NAME_som8064 = "emmc_appsboot"

EXTRA_OEMAKE = "TOOLCHAIN_PREFIX='arm-none-linux-gnueabi-' ${MY_TARGET}"
EXTRA_OEMAKE_append_som8064  = " EMMC_BOOT=1 SIGNED_KERNEL=0 ENABLE_THUMB=false "

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
    PATH=/pkg/asw/compilers/codesourcery/arm-2010q1/bin:$PATH
    make ${PARALLEL_MAKE} ${EXTRA_OEMAKE}
}

do_install() {
    install -d ${D}/usr/share/lk
    install -m 644 ${WORKDIR}/NOTICE ${D}/usr/share/lk/NOTICE
}

FILES_${PN} = "/usr/share/lk/NOTICE"

do_deploy () {
	install -d ${DEPLOYDIR}/out
        install ${S}/build-${MY_TARGET}/${BOOTLOADER_NAME}.mbn ${DEPLOYDIR}/out
}

do_deploy[dirs] = "${S} ${DEPLOYDIR}"
addtask deploy before do_build after do_install

PACKAGE_STRIP = "no"
