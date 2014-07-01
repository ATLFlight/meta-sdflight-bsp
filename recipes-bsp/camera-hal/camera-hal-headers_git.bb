DESCRIPTION = "HAL libraries for camera"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/MODULE_LICENSE_BSD;md5=d41d8cd98f00b204e9800998ecf8427e \
				    file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=3775480a712fc46a69647678acb234cb"

PV = "1.1"
PR = "r5"

SRC_URI = "git://codeaurora.org/platform/hardware/qcom/camera;protocol=git"

SRC_URI_append_som8064 = " file://0001-som8064-baseline-for-linux.patch"
SRC_URI_append_ifc6410 = " file://0001-ifc6410-baseline-for-linux.patch"

PACKAGES = "${PN}"

SRCREV_som8064 = "AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041"
SRCREV_ifc6410 = "AU_LINUX_ANDROID_JB_2.5_AUTO.04.02.02.115.005"

inherit autotools

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_configure() {
}

do_make() {
}

do_compile() {
}

do_install() {
   install -d ${D}/usr/include
   install -d ${D}/usr/include/libcamera2
   cp -a ${S}/*.h ${D}/usr/include/libcamera2/
}
