inherit native

PR = "r4"

MY_PN = "mkbootimg"

DESCRIPTION = "Boot image creation tool from Android"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"
HOMEPAGE = "http://android.git.kernel.org/?p=platform/system/core.git"
PROVIDES = "mkbootimg-native"

DEPENDS = "libmincrypt-native"

# Handle do_fetch ourselves...  The automated tools don't work nicely with this...
do_fetch () {
	install -d ${S}
	cp -rf ${WORKSPACE}/android_core/${MY_PN}/* ${S}
	cp -f ${THISDIR}/files/makefile ${S}
}

EXTRA_OEMAKE = "INCLUDES='-Imincrypt' LIBS='${libdir}/mincrypt/libmincrypt.a'"

do_install() {
	install -d ${D}${bindir}
	install ${MY_PN} ${D}${bindir}
}

NATIVE_INSTALL_WORKS = "1"
