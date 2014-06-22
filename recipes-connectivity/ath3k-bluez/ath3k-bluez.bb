require ath3k-bluez.inc

PR = "r1"

SRC_URI += "file://Makefile.am"
SRC_URI += "file://configure.ac"
SRC_URI += "file://bt.init.sh"
SRC_URI += "file://0000-ATH3K-IgnoreFirmware.patch"

SRC_URI[md5sum] = "fb42cb7038c380eb0e2fa208987c96ad"
SRC_URI[sha256sum] = "59738410ade9f0e61a13c0f77d9aaffaafe49ba9418107e4ad75fe52846f7487"

do_copy_ath3k_files() {
   cd ${S}
   cp -f ${WORKDIR}/configure.ac .
   cp -f ${WORKDIR}/Makefile.am .
   cp -f ${WORKDIR}/bt.init.sh .
   ln -sf lib bluetooth
}

do_install_append() {
	# install -m 0644 ${S}/audio/audio.conf ${D}/${sysconfdir}/bluetooth/
	# install -m 0644 ${S}/network/network.conf ${D}/${sysconfdir}/bluetooth/
	# install -m 0644 ${S}/input/input.conf ${D}/${sysconfdir}/bluetooth/
	# at_console doesn't really work with the current state of OE, so punch some more holes so people can actually use BT
	# install -m 0644 ${WORKDIR}/bluetooth.conf ${D}/${sysconfdir}/dbus-1/system.d/
	install -m 0755 ${WORKDIR}/bt.init.sh ${D}/usr/bin/
}

addtask do_copy_ath3k_files after do_unpack before do_patch
