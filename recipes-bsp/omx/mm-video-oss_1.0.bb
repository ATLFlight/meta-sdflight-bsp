inherit autotools pkgconfig

DESCRIPTION = "OpenMAX video for MSM chipsets"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://NOTICE;md5=a489a6b9757555cb108ccea75b4fcdb4"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/hardware/qcom/media"
B = "${S}"

SRC_URI  = "file://hardware/qcom/media/"
SRC_URI += "file://hardware/qcom/display/libcopybit"
SRC_URI += "file://hardware/qcom/display/libgralloc"
SRC_URI += "file://venus_v4l2.rules"
SRC_URI += "file://0001-gcc6.patch"

PACKAGES = "${PN}"

DEPENDS += "glib-2.0 android-tools virtual/kernel live555 camera-hal adreno200"

PR = "r1"

FILES_${PN} = "\
    ${libdir}/*.so* \
    ${bindir}/* \
    ${includedir}/* \
    ${datadir}/* \
    ${sysconfdir}/udev/rules.d/*"

# Skips check for .so symlinks
INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_DIR_TARGET}/usr/src/${MACHINE}/include"
CPPFLAGS_append += "-I${WORKDIR}/hardware/qcom/display/libcopybit"
CPPFLAGS_append += "-I${WORKDIR}/hardware/qcom/display/libgralloc"
CPPFLAGS_append += "-I${WORKDIR}/adreno200/include/private/C2D"
CPPFLAGS_append += "-I${STAGING_INCDIR}/live555"

do_install_append() {
    dest=/etc/udev/rules.d
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/venus_v4l2.rules -D ${D}${dest}/venus_v4l2.rules
}
