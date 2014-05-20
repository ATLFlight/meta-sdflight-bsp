DESCRIPTION = "Ethernet configuration"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV = "1.0"

SRC_URI = "file://eth0.cfg \
           file://eth0-config-macaddr"

PACKAGES = "${PN}"

do_install() {
    mkdir -p ${D}/etc/network/interfaces.d		
    install -m 644 ${WORKDIR}/eth0.cfg ${D}/etc/network/interfaces.d/
    mkdir -p ${D}/etc/network/if-pre-up.d		
    install -m 755 ${WORKDIR}/eth0-config-macaddr ${D}/etc/network/if-pre-up.d
}
