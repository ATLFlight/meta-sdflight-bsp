DESCRIPTION = "OpenMAX video for MSM chipsets"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://NOTICE;md5=a489a6b9757555cb108ccea75b4fcdb4"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/hardware/qcom/media"

SRC_URI = "file://hardware/qcom/media/"

PACKAGES = "${PN}"
PR = "r1"

INSANE_SKIP_${PN} += "installed-vs-shipped"

do_install() {
   install -d ${D}${includedir}/omx
   cp -a ${S}/mm-core/inc/*.h ${D}${includedir}/omx
   cp -a ${S}/libstagefrighthw/QComOMXMetadata.h ${D}${includedir}/omx
}
