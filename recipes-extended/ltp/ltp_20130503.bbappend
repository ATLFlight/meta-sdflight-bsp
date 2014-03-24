FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

RDEPENDS_${PN} = ""
DEPENDS = ""

PARALLEL_MAKE = "-j 1"

SRC_URI += " \
	file://0001-Use-bash-for-all-shell-scripts.patch \
	file://0002-Updated-file_test.sh-for-qr-linux.patch \
	file://cmds.ltp \
"

EXTRA_OECONF += "--with-expect --with-bash --with-perl --with-python"

do_install_append() {
   install -m 644 ${WORKDIR}/cmds.ltp ${D}/opt/ltp/runtest
}
