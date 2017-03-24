DESCRIPTION = "Common networking scripts"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-sdflight-bsp/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV = "1.0"

PROVIDES = "qrl-networking"

SRC_URI = "file://qrl-common-inc.sh \
           file://qrl-config-macaddr.sh \
           file://qrl-copy-firmware.sh \
          "

PACKAGES = "${PN}"
FILES_${PN} = "/usr/local/qr-linux/*sh"

do_install() {
    dest=/usr/local/qr-linux
    # Install qrl-*.sh
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/qrl-common-inc.sh ${D}${dest}
    install -m 755 ${WORKDIR}/qrl-config-macaddr.sh ${D}${dest}
    install -m 755 ${WORKDIR}/qrl-copy-firmware.sh ${D}${dest}
}
