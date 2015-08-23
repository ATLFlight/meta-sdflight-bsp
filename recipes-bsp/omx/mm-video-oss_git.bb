DESCRIPTION = "OpenMAX video for MSM chipsets"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://NOTICE;md5=3c567309c019d31c938d51a3317a2693"

SRC_URI = "git://codeaurora.org/platform/hardware/qcom/media;protocol=git;nobranch=1"

SRC_URI += "file://venus_v4l2.rules"
SRC_URI += "file://0001-mm-video-oss_compilation.patch"
SRC_URI += "file://0002-Add-license-file.patch"
SRC_URI += "file://0003-mm-video-oss-add-support-for-shared-mem-encoding.patch"
SRC_URI += "file://0004-Enable-meta-buffer-mode-to-allow-camera-to-share-buf.patch"

PACKAGES = "${PN}"

SRCREV = "LNX.LA.3.5.2-09410-8x74.0"

DEPENDS += "glib-2.0 android-tools virtual/kernel"

PV = "1.0"
PR = "r0"

inherit autotools

FILES_${PN} = "\
    /usr/lib/*.so* \
    /usr/bin/* \
    /usr/include/* \
    /usr/share/* \
    /etc/udev/rules.d/*"

# Skips check for .so symlinks
INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_install_append() {
    dest=/etc/udev/rules.d
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/venus_v4l2.rules -D ${D}${dest}/venus_v4l2.rules
}
