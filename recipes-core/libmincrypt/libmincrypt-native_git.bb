inherit native

PR = "r0"

MY_PN = "mincrypt"
MY_LPN = "libmincrypt"

DESCRIPTION = "Minimalistic encryption library from Android"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://NOTICE;md5=c19179f3430fd533888100ab6616e114"
HOMEPAGE = "http://android.git.kernel.org/?p=platform/system/core.git"

# Handle do_fetch ourselves...  The automated tools don't work nicely with this...
do_fetch () {
	install -d ${S}/include
	cp -rf ${WORKSPACE}/android_core/${MY_LPN}/* ${S}
	cp -rf ${WORKSPACE}/android_core/include/${MY_PN} ${S}/include
	cp -f ${THISDIR}/files/makefile ${S}
}

EXTRA_OEMAKE = "INCLUDES='-I./include'"

do_install() {
	install -d ${D}${includedir}/${MY_PN} ${D}${libdir}/${MY_PN}
	install include/${MY_PN}/*.h ${D}${includedir}/${MY_PN}
	install ${MY_LPN}.a ${D}${libdir}/${MY_PN}
}

NATIVE_INSTALL_WORKS = "1"

