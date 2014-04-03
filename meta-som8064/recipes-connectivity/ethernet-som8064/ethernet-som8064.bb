DESCRIPTION = "Ethernet configuration"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV = "1.0"

SRC_URI = "file://eth0.cfg \
	   file://qrl-mac-fw-inc.sh \
	   "

PACKAGES = "${PN}"
FILES_${PN} += " \
	    ${sysconfdir}/etc/network/interfaces.d/* \
	    /usr/local/qr-linux/* \
	    "
do_install_append() {
    install -m 644 ${WORKDIR}/eth0.cfg -D ${D}${sysconfdir}/network/interfaces.d/eth0.cfg

    # Install qrl-mac-fw-inc.sh
    install -m 644 ${WORKDIR}/qrl-mac-fw-inc.sh -D ${D}/usr/local/qr-linux/qrl-mac-fw-inc.sh
}
