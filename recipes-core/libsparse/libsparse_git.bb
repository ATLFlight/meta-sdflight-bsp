inherit autotools

DESCRIPTION = "libsparse library"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

DEPENDS += "zlib"

PR = "r0"
PV = "1.0"

SRC_URI = "git://codeaurora.org/quic/la/platform/system/core;nobranch=1;tag=LNX.LA.3.5.2-09410-8x74.0"
SRC_URI += "file://Makefile.am \
	file://configure.ac"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git/libsparse', s)
    shutil.copyfile(wd+'/Makefile.am', s+'/Makefile.am')
    shutil.copyfile(wd+'/configure.ac', s+'/configure.ac')
}
