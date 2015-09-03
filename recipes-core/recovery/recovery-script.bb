DESCRIPTION = "Recovery scripts"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV = "1.0"

inherit autotools

SRC_URI = "file://install-update.sh \
    file://factory-reset.sh \
    file://qrlUpdate.conf"

FILES_${PN} = "/usr/bin/*"
FILES_${PN} += "/etc/init/*"

do_install() {
    install -m 755 ${WORKDIR}/install-update.sh -D ${D}${bindir}/install-update
    install -m 755 ${WORKDIR}/factory-reset.sh -D ${D}${bindir}/factory-reset
    dest=/etc/init
    install -d ${D}${dest}
    install -m 755 ${WORKDIR}/qrlUpdate.conf ${D}${dest}
}

