DESCRIPTION = "libselinux"
LICENSE = "Public-Domain"
LIC_FILES_CHKSUM = "file://NOTICE;md5=84b4d2c6ef954a2d4081e775a270d0d0"
HOMEPAGE = "https://android.googlesource.com/external/libselinux"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/external/libselinux/"

SRC_URI = "file://external/libselinux/"

DEPENDS += "libpcre"

PACKAGES = "${PN}"

inherit autotools pkgconfig

INSANE_SKIP_${PN} = "installed-vs-shipped"

BBCLASSEXTEND = "native"
