inherit autotools

DESCRIPTION = "Android libhardware header files"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/frameworks/av/"
B = "${S}"

SRC_URI = "file://frameworks/av/"
SRC_URI += "file://system/core/include/usbhost"
SRC_URI += "file://0001-gcc6.patch"
SRC_URI += "file://0002-config.patch"

DEBIAN_NOAUTONAME_${PN} = "1"


# Need the kernel headers
DEPENDS += "virtual/kernel"
DEPENDS += "android-tools"
DEPENDS += "libhardware-headers"
DEPENDS += "system-headers"
DEPENDS += "frameworks-headers"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_DIR_TARGET}/usr/src/${MACHINE}/include"
EXTRA_OECONF_append = " MTP_EXTRA_CPPFLAGS='-I${STAGING_KERNEL_DIR}/include -I${WORKDIR}/system/core/include'"

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"
