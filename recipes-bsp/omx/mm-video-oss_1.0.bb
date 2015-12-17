DESCRIPTION = "OpenMAX video for MSM chipsets"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://NOTICE;md5=a489a6b9757555cb108ccea75b4fcdb4"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/hardware/qcom/media"

SRC_URI  = "file://hardware/qcom/media/"
SRC_URI += "file://venus_v4l2.rules"

PACKAGES = "${PN}"

DEPENDS += "glib-2.0 android-tools virtual/kernel live555 camera-hal"

PR = "r1"

inherit autotools

FILES_${PN} = "\
    ${libdir}/*.so* \
    ${bindir}/* \
    ${includedir}/* \
    ${datadir}/* \
    ${sysconfdir}/udev/rules.d/*"

# Skips check for .so symlinks
INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include"
CPPFLAGS_append += "-I${WORKSPACE}/hardware/qcom/display/libcopybit"
CPPFLAGS_append += "-I${WORKSPACE}/hardware/qcom/display/libgralloc"
CPPFLAGS_append += "-I${STAGING_INCDIR}/live555"

do_install_append() {
    dest=/etc/udev/rules.d
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/venus_v4l2.rules -D ${D}${dest}/venus_v4l2.rules
}
