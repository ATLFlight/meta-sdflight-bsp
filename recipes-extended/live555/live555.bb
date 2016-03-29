DESCRIPTION = "open source lib live555"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=68ad62c64cc6c620126241fd429e68fe"

SRC_URI = "git://github.com/hackeron/live555"
SRCREV = "35c375c6b15403869a27047f67a0d2d4fd0a16e2"
SRC_URI += "file://0001-change-mkfile.patch"
SRC_URI += "file://0001-live555-Move-method-bodies-out-of-MPEGVideoStreamPar.patch"
PV = "1.0"
PR = "r0"

PACKAGES = "${PN}"
PACKAGES += "${PN}-dbg"


CXXFLAGS += "-I ${WORKDIR}/live555-1.0/liveMedia/include"
CXXFLAGS += "-I ${WORKDIR}/live555-1.0/groupsock/include"
CXXFLAGS += "-I ${WORKDIR}/live555-1.0/BasicUsageEnvironment/include"
CXXFLAGS += "-I ${WORKDIR}/live555-1.0/UsageEnvironment/include -U SO_REUSEPORT"

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"

do_configure_append() {
    ./genMakefiles linux-with-shared-libraries
}


do_install_prepend() {
    cd ${WORKDIR}/image
    mkdir usr
    cd usr
    mkdir lib

    cp ../../live555-1.0/liveMedia/libliveMedia.so* ./lib/
    cp ../../live555-1.0/groupsock/lib* ./lib/
    cp ../../live555-1.0/UsageEnvironment/lib* ./lib/
    cp ../../live555-1.0/BasicUsageEnvironment/lib* ./lib/
    cd lib
    ln -fs libliveMedia.so* libliveMedia.so    
    ln -fs libgroupsock.so* libgroupsock.so
    ln -fs libUsageEnvironment.so* libUsageEnvironment.so
    ln -fs libBasicUsageEnvironment.so* libBasicUsageEnvironment.so
}
do_install_append() {
    install -d ${STAGING_DIR_TARGET}/usr/include/live555/
    install -m 0755 ${WORKDIR}/live555-1.0/liveMedia/include/*.hh ${STAGING_DIR_TARGET}/usr/include/live555/
    install -m 0755 ${WORKDIR}/live555-1.0/groupsock/include/* ${STAGING_DIR_TARGET}/usr/include/live555/
    install -m 0755 ${WORKDIR}/live555-1.0/BasicUsageEnvironment/include/* ${STAGING_DIR_TARGET}/usr/include/live555/
    install -m 0755 ${WORKDIR}/live555-1.0/UsageEnvironment/include/* ${STAGING_DIR_TARGET}/usr/include/live555/
}

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}
