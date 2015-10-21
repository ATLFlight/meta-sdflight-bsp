DESCRIPTION = "Power HAL"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${S}/power.c;endline=28;md5=2652078bff9af7c723ce4b0a48945106"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/device/qcom/power"
SRC_URI = "file://device/qcom/common/power"
PR = "r1"

PACKAGES = "${PN}"
FILES_${PN} = "/usr/lib/*.so"

inherit autotools

DEBIAN_NOAUTONAME_${PN} = "1"

INSANE_SKIP_${PN} = "installed-vs-shipped"

DEPENDS += "android-tools"
DEPENDS += "libhardware-headers"
DEPENDS += "system-headers"
