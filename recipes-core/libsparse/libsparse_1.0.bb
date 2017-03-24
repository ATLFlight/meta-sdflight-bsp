inherit autotools gettext pkgconfig

DESCRIPTION = "libsparse library"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-sdflight-bsp/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

DEPENDS += "zlib pkgconfig-native"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/system/core/${BPN}/"

SRC_URI = "file://system/core/${BPN}/"

BBCLASSEXTEND = "native"

do_install_append() {
	install -d ${D}/usr/share/pkgconfig
	install ${WORKDIR}/build/libsparse.pc ${D}/usr/share/pkgconfig/libsparse.pc
}
