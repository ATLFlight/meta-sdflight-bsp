FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

RDEPENDS_${PN} = ""
DEPENDS = "libaio"

PARALLEL_MAKE = "-j 1"

SRC_URI += " \
        file://0001-Use-bash-for-all-shell-scripts.patch \
	file://0002-Updated-file_test.sh-for-qr-linux.patch \
        file://0003-fix-su-test.patch \
	file://0004-use-pkill-instead-of-killall.patch \
        file://0005-Fix-max_map_count-LTP-test.patch \
        file://0006-Fix-the-ldd-LTP-test.patch \
        file://0007-Remove-AIO-test-cases-not-supported-by-kernel.patch \
	file://cmds.ltp \
"

EXTRA_OECONF += "--with-expect --with-bash --with-perl --with-python"


# Do the same install as the version for meta except do not remove expect scripts
do_install(){
	install -d ${D}/opt/ltp/
	oe_runmake DESTDIR=${D} SKIP_IDCHECK=1 install
	
	# Copy POSIX test suite into ${D}/opt/ltp/testcases by manual
	cp -r testcases/open_posix_testsuite ${D}/opt/ltp/testcases
}


do_install_append() {
   install -m 644 ${WORKDIR}/cmds.ltp ${D}/opt/ltp/runtest
}
