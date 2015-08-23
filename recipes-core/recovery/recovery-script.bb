DESCRIPTION = "Recovery scripts"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV = "1.0"

inherit autotools

SRC_URI = "file://install-update.sh"

FILES_${PN} = "/usr/bin/*"

do_install() {
    install -m 755 ${WORKDIR}/install-update.sh -D ${D}${bindir}/install-update
}

