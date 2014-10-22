DESCRIPTION = "Networking configuration for IFC6410"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV = "1.0"

SRC_URI = "file://eth0 \
           file://eth0-configmac \
           file://qrl-mac-fw-inc.sh"

PACKAGES = "${PN}"
FILES_${PN} = "/usr/local/qr-linux/*sh"
FILES_${PN} += "/etc/network/*"

do_install() {
    dest=/usr/local/qr-linux
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/qrl-mac-fw-inc.sh ${D}${dest}

    dest=${sysconfdir}/network/if-pre-up.d
    install -d ${D}${dest}
    install -m 755 ${WORKDIR}/eth0 ${D}${dest}
    install -m 755 ${WORKDIR}/eth0-configmac ${D}${dest}
}
