DESCRIPTION = "Android libhardware header files"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESPATH =+ "${WORKSPACE}:"
SRC_URI = "file://hardware/libhardware/"

S = "${WORKDIR}/hardware/libhardware"
PACKAGES = "${PN}"

PR = "r1"

INSANE_SKIP_${PN} = "installed-vs-shipped"

do_install() {
    install -d ${D}/${includedir}/hardware
    install -m 644 ${S}/include/hardware/* -D ${D}/${includedir}/hardware
}
