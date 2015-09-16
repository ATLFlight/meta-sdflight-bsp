DESCRIPTION = "Android safe iop"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://NOTICE;md5=e7235a4d576addf0c399983b1c7f673e"
HOMEPAGE = "https://android.googlesource.com/platform/external/safe-iop"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/external/safe-iop/"

SRC_URI = "file://external/safe-iop/"

PACKAGES = "${PN}"

INSANE_SKIP_${PN} = "installed-vs-shipped"

do_install() {
    install -d ${D}${includedir}
    install -m 644 ${S}/include/safe_iop.h ${D}${includedir}/safe_iop.h
}
