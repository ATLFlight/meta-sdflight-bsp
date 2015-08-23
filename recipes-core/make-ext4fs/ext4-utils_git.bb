inherit autotools

DESCRIPTION = "ext4-utils library"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

DEPENDS += "libsparse zlib libselinux"

PR = "r0"
PV="1.0"

SRC_URI = "git://codeaurora.org/quic/la/platform/system/extras;nobranch=1;tag=LNX.LA.3.5.2-09410-8x74.0"
SRC_URI += "file://0001-Add-automake-support.patch"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}
