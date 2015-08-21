DESCRIPTION = "HAL libraries for camera"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

SRCREV = "LNX.LA.3.5.2-09410-8x74.0"

SRC_URI = "git://codeaurora.org/quic/la/platform/hardware/qcom/camera;protocol=git;nobranch=1"

SRC_URI += "file://0001-camera-hal-compilation-changes-for-QR-Linux.patch"
SRC_URI += "file://0002-camera-hal-limit-number-of-frame-dump-to-100-and-dum.patch"
SRC_URI += "file://0003-camera-hal-create-qcamlib-wrapper-library.patch"
SRC_URI += "file://0004-camera-hal-reduce-logging-in-camera-app.patch"
SRC_URI += "file://0005-camera-hal-remove-android-includes-references-from-M.patch"
SRC_URI += "file://0006-camera-hal-qcamlib-API-update-for-video-stream.patch"
SRC_URI += "file://0007-camera-hal-add-copy-buffer-API-in-qcamlib.patch"
SRC_URI += "file://0008-camera-hal-disable-CAF-in-video.patch"
SRC_URI += "file://0009-camera-hal-compilation-changes-for-eagle8074.patch"
SRC_URI += "file://0010-camera-hal-qcamlib-ov7251-resolution-update.patch"
SRC_URI += "file://0011-eagle-ov7521-set-default-snapshot-resolution-to-640x480.patch"

#SRC_URI += "file://work.patch"

PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"
PACKAGES += "${PN}-dbg"

inherit autotools

# Need the kernel headers
DEPENDS += "virtual/kernel"
DEPENDS += "mm-camera-headers"
DEPENDS += "android-tools"
DEPENDS += "mm-video-oss"
DEPENDS += "libhardware-headers"
DEPENDS += "system-headers"

CFLAGS += "-I./mm-camera-interface"
CFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include"
CFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include/media"
CFLAGS += "-I${STAGING_INCDIR}/mm-core"
CFLAGS += "-I${STAGING_INCDIR}/omx/inc"

EXTRA_OECONF_append = " --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/include"
EXTRA_OECONF_append = " --enable-target=msm8974"


FILES_${PN}_append += "/usr/lib/hw/*"
FILES_${PN} += "/usr/lib/*.so"

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}
