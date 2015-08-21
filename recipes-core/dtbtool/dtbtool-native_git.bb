inherit native autotools gettext
inherit native

DESCRIPTION = "dtbtool for generating master dtb image"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV="1.0"
PN = "dtbtool"

PROVIDES = "dtbtool-native"

SRC_URI = "git://codeaurora.org/quic/la/device/qcom/common;nobranch=1;tag=LNX.LA.3.5.2-09410-8x74.0"
SRC_URI += "file://0001-dtbtool-for-linux.patch"

EXTRA_OEMAKE = "INCLUDES='-I${S}/include'"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

NATIVE_INSTALL_WORKS = "1"
