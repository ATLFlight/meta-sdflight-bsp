FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

RDEPENDS_${PN} = ""
DEPENDS = ""

SRC_URI += " \
	file://0001-Use-bash-for-all-shell-scripts.patch \
	file://cmds.ltp \
"

do_install_append() {
   install -m 644 ${WORKDIR}/cmds.ltp ${D}/opt/ltp/runtest
}
