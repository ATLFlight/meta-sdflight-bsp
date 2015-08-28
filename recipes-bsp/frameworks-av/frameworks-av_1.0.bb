DESCRIPTION = "Android libhardware header files"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/frameworks/av/"

SRC_URI = "file://frameworks/av/"

DEBIAN_NOAUTONAME_${PN} = "1"

inherit autotools

# Need the kernel headers
DEPENDS += "virtual/kernel"
DEPENDS += "android-tools"
DEPENDS += "libhardware-headers"
DEPENDS += "system-headers"
DEPENDS += "frameworks-headers"

CXXFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/include"
EXTRA_OECONF_append = " --enable-target=msm8974"

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"
