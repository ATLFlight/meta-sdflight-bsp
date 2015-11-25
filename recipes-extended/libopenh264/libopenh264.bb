DESCRIPTION = "open source lib openh264"
LICENSE = "BSD-2-Clause"

LIC_FILES_CHKSUM = "file://${WORKDIR}/libopenh264-1.4.0/LICENSE;md5=bb6d3771da6a07d33fd50d4d9aa73bcf"

SRC_URI="git://github.com/cisco/openh264"
SRCREV = "b700b67bbad51bf326ace75954e056e0c8a6c5f4"
SRC_URI += "file://0001-change-mkfile.patch"

PV = "1.4.0"
pr = "r0"

ARCH = "armv7l"

PACKAGES = "${PN}"
PACKAGES += "${PN}-dbg"

INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"
CXXFLAGS += "-fPIC -MMD -MP -DHAVE_NEON"
CFLAGS += "-fPIC -MMD -MP -DHAVE_NEON"
LDFLAGS += " -lpthread"
do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S',True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git',s)
}

DEBIAN_NOAUTONAME_${PN}="1"
do_install_prepend() {
    cd ${WORKDIR}/image
    mkdir usr
    cd usr
    mkdir lib
    cp ../../libopenh264-1.4.0/libopenh264.so.* ./lib/
    cd lib
    ln -fs libopenh264.so.* libopenh264.so
}
do_install_append() {
    install -m 0755 ${WORKDIR}/libopenh264-1.4.0/codec/api/svc/* ${STAGING_DIR_TARGET}/usr/include/
}
