DESCRIPTION = "HAL libraries for camera"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"

FILESPATH =+ "${WORKSPACE}:"

SRC_URI = "file://hardware/qcom/camera/"

S = "${WORKDIR}/hardware/qcom/camera"
PR = "r1"

PACKAGES = "${PN}"
PACKAGES += "${PN}-dbg"

inherit autotools

# Need the kernel headers
DEPENDS += "virtual/kernel"
DEPENDS += "android-tools"
DEPENDS += "libhardware-headers"
DEPENDS += "libhardware"
DEPENDS += "power-hal"
DEPENDS += "system-headers"
DEPENDS += "frameworks-headers"
DEPENDS += "frameworks-av"
DEPENDS += "mm-video-oss-headers"

CFLAGS += "-I./mm-camera-interface"
CFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include"
CFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include/media"
CFLAGS += "-I${STAGING_INCDIR}/omx"

CXXFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include"
CXXFLAGS += "-I${STAGING_INCDIR}/glib-2.0"
CXXFLAGS += "-I${STAGING_LIBDIR}/glib-2.0/include"
CXXFLAGS += "-I${STAGING_INCDIR}/omx"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/include"
EXTRA_OECONF_append = " --enable-target=msm8974"


FILES_${PN}_append += "/usr/lib/hw/*"
FILES_${PN} += "/usr/lib/*.so"

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"
