DESCRIPTION = "Android libhardware header files"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/system/core/"

SRC_URI = "file://system/core/"

PACKAGES = "${PN}"

INSANE_SKIP_${PN} = "installed-vs-shipped"

do_install_append() {
    dest=/etc/udev/rules.d
    install -d ${D}/usr/include/system
    install -d ${D}/usr/include/sync
    install -d ${D}/usr/include/utils
    install -m 644 ${S}/include/system/* -D ${D}/usr/include/system
    install -m 644 ${S}/include/sync/* -D ${D}/usr/include/sync
    install -m 644 ${S}/include/utils/* -D ${D}/usr/include/utils
}
