require ath3k-bluez.inc

PR = "r1"

SRC_URI += "file://Makefile.am"
SRC_URI += "file://configure.ac"
SRC_URI += "file://bt.init.sh"
SRC_URI += "file://bt-init.conf"
SRC_URI += "file://0000-ATH3K-IgnoreFirmware.patch"

SRC_URI[md5sum] = "fb42cb7038c380eb0e2fa208987c96ad"
SRC_URI[sha256sum] = "59738410ade9f0e61a13c0f77d9aaffaafe49ba9418107e4ad75fe52846f7487"

do_unpack_append() {
    bb.build.exec_func('do_copy_files', d)
}

do_copy_files() {
    cp -f ${WORKDIR}/configure.ac ${S}/configure.ac
    cp -f ${WORKDIR}/Makefile.am ${S}/Makefile.am
    ln -sf ${S}/lib ${S}/bluetooth
}

do_install_append() {
    install -m 0644 ${WORKDIR}/bt-init.conf -D ${D}/etc/init/bt-init.conf
    install -m 0755 ${WORKDIR}/bt.init.sh -D ${D}${bindir}/bt.init.sh
}
