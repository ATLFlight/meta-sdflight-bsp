DESCRIPTION = "open source lib turob jpeg"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"

JPEGTAG = "1.4.0"

SRC_URI = "git://github.com/libjpeg-turbo/libjpeg-turbo.git;protocol=git;tag=${JPEGTAG}"

GITDIR = "${DL_DIR}/libjpeg-turbo-1.4.0"

PV = "1.4.0"
PR = "r0"

PACKAGES = "${PN}"
PACKAGES += "${PN}-dbg"

inherit  autotools

DEPENDS += "system-headers"

CFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include"
CFLAGS += "-I${STAGING_INCDIR}/linux-headers/usr/include/media"

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S',True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git',s)
}
