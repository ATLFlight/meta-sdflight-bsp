DESCRIPTION = "Android libhardware header files"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/frameworks/native/"

SRC_URI = "file://frameworks/native/"

PACKAGES = "${PN}"

INSANE_SKIP_${PN} = "installed-vs-shipped"

do_install_append() {
    install -d ${D}/usr/include/media/hardware
    install -m 644 ${S}/include/media/hardware/* -D ${D}/usr/include/media/hardware
}
