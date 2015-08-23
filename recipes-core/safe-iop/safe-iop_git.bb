DESCRIPTION = "Android safe iop"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://NOTICE;md5=e7235a4d576addf0c399983b1c7f673e"
HOMEPAGE = "https://android.googlesource.com/platform/external/safe-iop"

PR = "r0"
PV = "1.1.36"

SRC_URI = "git://codeaurora.org/quic/la/platform/external/safe-iop;nobranch=1;tag=LNX.LA.3.5.2-09410-8x74.0"

PACKAGES = "${PN}"

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

do_install() {
    install -d ${D}${includedir}
    install -m 644 ${S}/include/safe_iop.h ${D}${includedir}/safe_iop.h
}
