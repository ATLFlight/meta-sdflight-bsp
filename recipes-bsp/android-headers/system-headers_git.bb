DESCRIPTION = "Android libhardware header files"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://codeaurora.org/quic/la/platform/system/core;protocol=git;nobranch=1"

PACKAGES = "${PN}"

SRCREV = "LNX.LA.3.5-01620-8x74.0"

PV = "1.0"
PR = "r0"

INSANE_SKIP_${PN} = "installed-vs-shipped"

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
    install -d ${D}/usr/include/system
    install -d ${D}/usr/include/sync
    install -d ${D}/usr/include/utils
    install -m 644 ${S}/include/system/* -D ${D}/usr/include/system
    install -m 644 ${S}/include/sync/* -D ${D}/usr/include/sync
    install -m 644 ${S}/include/utils/* -D ${D}/usr/include/utils
}
