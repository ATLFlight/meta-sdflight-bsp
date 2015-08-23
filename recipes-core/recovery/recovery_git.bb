DESCRIPTION = "Android bootable recovery"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://NOTICE;md5=9645f39e9db895a4aa6e02cb57294595"
HOMEPAGE = "https://android.googlesource.com/platform/bootable/recovery"

PR = "r0"
PV = "1.0"

SRC_URI = "git://codeaurora.org/quic/la/platform/bootable/recovery;nobranch=1;tag=LNX.LA.3.5.2-09410-8x74.0"

DEPENDS += "virtual/kernel android-tools ext4-utils safe-iop libselinux libsparse"

PACKAGES = "${PN}"

inherit autotools

INSANE_SKIP_${PN} = "installed-vs-shipped"

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include \
                --with-core-headers=${STAGING_INCDIR} \
                --enable-shared=no"

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
    install -m 755 ${D}${bindir}/recovery -D ${STAGING_BINDIR}/
    install -m 755 ${D}${bindir}/updater -D ${STAGING_BINDIR}/
    install -m 755 ${D}${bindir}/applypatch -D ${STAGING_BINDIR}/
}
