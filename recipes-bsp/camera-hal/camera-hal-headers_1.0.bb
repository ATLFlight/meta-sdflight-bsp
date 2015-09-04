DESCRIPTION = "HAL libraries for camera"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/hardware/qcom/camera"

SRC_URI = "file://hardware/qcom/camera/"

PACKAGES = "${PN}"
PR = "r1"

INSANE_SKIP_${PN} += "installed-vs-shipped"

do_install() {
   install -d ${D}${includedir}/camera-hal
   cp -a ${S}/QCamera2/stack/common/*.h ${D}${includedir}/camera-hal
}
