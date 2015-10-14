DESCRIPTION = "libjpeg-turbo"
LICENSE = "IJG & BSD-3-Clause & Zlib"
LIC_FILES_CHKSUM = "file://README;md5=aebe10abe0cac14c7e5c16a6d2ea494e \
                    file://README-turbo.txt;md5=91d786de7bc53a1ad837c8a42619ef4b"

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
