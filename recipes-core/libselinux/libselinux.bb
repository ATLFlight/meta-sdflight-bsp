DESCRIPTION = "libselinux"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://NOTICE;md5=84b4d2c6ef954a2d4081e775a270d0d0"
HOMEPAGE = "https://android.googlesource.com/external/libselinux"

PR = "r0"
PV = "1.0"

SRC_URI = "git://codeaurora.org/quic/la/platform/external/libselinux;nobranch=1;tag=LNX.LA.3.5.2-09410-8x74.0"
SRC_URI += "file://0001-libselinux-Automake-changes.patch"

DEPENDS += "libpcre"

PACKAGES = "${PN}"

inherit autotools

INSANE_SKIP_${PN} = "installed-vs-shipped"

EXTRA_OECONF = "--with-core-headers=${STAGING_INCDIR}"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}
