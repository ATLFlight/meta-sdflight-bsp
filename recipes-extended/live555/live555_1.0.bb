DESCRIPTION = "open source lib live555"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=68ad62c64cc6c620126241fd429e68fe"

SRC_URI = "git://github.com/hackeron/live555"
SRCREV = "35c375c6b15403869a27047f67a0d2d4fd0a16e2"
SRC_URI += "file://0001-change-mkfile.patch"
SRC_URI += "file://0001-live555-Move-method-bodies-out-of-MPEGVideoStreamPar.patch"
PR = "r0"

PACKAGES = "${PN}"
PACKAGES += "${PN}-dbg"

FILES_${PN} += "/usr/include/live555/*"
FILES_${PN} += "/usr/lib/*"

CXXFLAGS += "-I ${WORKDIR}/live555-${PV}/liveMedia/include"
CXXFLAGS += "-I ${WORKDIR}/live555-${PV}/groupsock/include"
CXXFLAGS += "-I ${WORKDIR}/live555-${PV}/BasicUsageEnvironment/include"
CXXFLAGS += "-I ${WORKDIR}/live555-${PV}/UsageEnvironment/include -U SO_REUSEPORT"

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "staticdev"

do_configure_append() {
    ./genMakefiles linux-with-shared-libraries
}

do_install() {
    install -d ${D}${libdir}
    cp ${WORKDIR}/live555-${PV}/liveMedia/libliveMedia.so* ${D}${libdir}
    cp ${WORKDIR}/live555-${PV}/groupsock/lib* ${D}${libdir}
    cp ${WORKDIR}/live555-${PV}/UsageEnvironment/lib* ${D}${libdir}
    cp ${WORKDIR}/live555-${PV}/BasicUsageEnvironment/lib* ${D}${libdir}
    cd ${D}${libdir}

    ln -fs libliveMedia.so.49.* libliveMedia.so
    ln -fs libliveMedia.so.49.* libliveMedia.so.49

    ln -fs libgroupsock.so.7.* libgroupsock.so
    ln -fs libgroupsock.so.7.* libgroupsock.so.7

    ln -fs libUsageEnvironment.so.3.* libUsageEnvironment.so
    ln -fs libUsageEnvironment.so.3.* libUsageEnvironment.so.3

    ln -fs libBasicUsageEnvironment.so.1.* libBasicUsageEnvironment.so
    ln -fs libBasicUsageEnvironment.so.1.* libBasicUsageEnvironment.so.1

    install -d ${D}${includedir}/live555/
    install -m 0755 ${WORKDIR}/live555-${PV}/liveMedia/include/*.hh ${D}${includedir}/live555
    install -m 0755 ${WORKDIR}/live555-${PV}/groupsock/include/* ${D}${includedir}/live555
    install -m 0755 ${WORKDIR}/live555-${PV}/BasicUsageEnvironment/include/* ${D}${includedir}/live555
    install -m 0755 ${WORKDIR}/live555-${PV}/UsageEnvironment/include/* ${D}${includedir}/live555
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
