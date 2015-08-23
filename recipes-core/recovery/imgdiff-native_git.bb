DESCRIPTION = "Android bootable recovery"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://NOTICE;md5=9645f39e9db895a4aa6e02cb57294595"
HOMEPAGE = "https://android.googlesource.com/platform/bootable/recovery"

PR = "r0"
PV = "1.0"

DEPENDS += "zlib-native bzip2-native"

inherit native autotools

SRC_URI = "git://codeaurora.org/quic/la/platform/bootable/recovery;nobranch=1;tag=LNX.LA.3.5.2-09410-8x74.0"
SRC_URI += "file://0001-imgdiff-Add-automake-support.patch"

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include \
                --with-core-headers=${STAGING_NATIVE_INCDIR} \
                --enable-shared=no"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git/applypatch', s)
    shutil.copyfile(wd+'/git/NOTICE', s+'/NOTICE')
}

