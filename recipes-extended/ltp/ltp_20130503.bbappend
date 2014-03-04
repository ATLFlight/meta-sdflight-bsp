FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

RDEPENDS_${PN} = "acl openssl"

SRC_URI += "file://cmds.ltp"

do_install_append() {
   install -m 644 ${WORKDIR}/cmds.ltp ${D}/opt/ltp/runtest
}
