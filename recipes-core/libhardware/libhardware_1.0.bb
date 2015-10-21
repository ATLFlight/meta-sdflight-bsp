DESCRIPTION = "Dynamic library loader for hardware modules"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/NOTICE;md5=9645f39e9db895a4aa6e02cb57294595"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/libhardware"
SRC_URI = "file://hardware/libhardware"
SRC_URI += "file://0001-Port-libhardware-to-Linux.patch"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"

inherit autotools

DEPENDS += "android-tools"
DEPENDS += "libhardware-headers"
DEPENDS += "system-headers"
DEPENDS += "glib-2.0"

DEBIAN_NOAUTONAME_${PN} = "1"

INSANE_SKIP_${PN} = "installed-vs-shipped"

EXTRA_OECONF += "--with-glib"

CPPFLAGS += "-I${STAGING_INCDIR}/glib-2.0"
CPPFLAGS += "-I${STAGING_LIBDIR}/glib-2.0/include"
