DESCRIPTION = "bluez-clean-headers"
LICENSE = "NOT-APPLICABLE"
LIC_FILES_CHKSUM = "file://${S}/bluetooth/bluetooth.h;beginline=4;endline=15;md5=088845e34e98d65eca9075b7e28a74ee"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/system/bluetooth/bluez-clean-headers"

SRC_URI = "file://system/bluetooth/bluez-clean-headers/"

PACKAGES = "${PN}"

INSANE_SKIP_${PN} = "installed-vs-shipped"

do_install() {
    install -d ${D}${includedir}/bluetooth
    install -m 644 ${S}/bluetooth/bluetooth.h ${D}${includedir}/bluetooth/bluetooth.h
    install -m 644 ${S}/bluetooth/hci.h ${D}${includedir}/bluetooth/hci.h
    install -m 644 ${S}/bluetooth/hci_lib.h ${D}${includedir}/bluetooth/hci_lib.h
    install -m 644 ${S}/bluetooth/l2cap.h ${D}${includedir}/bluetooth/l2cap.h
    install -m 644 ${S}/bluetooth/rfcomm.h ${D}${includedir}/bluetooth/rfcomm.h
    install -m 644 ${S}/bluetooth/sco.h ${D}${includedir}/bluetooth/sco.h
}
