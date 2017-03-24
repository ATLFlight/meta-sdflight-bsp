FILESEXTRAPATHS_prepend_sdflight := "${THISDIR}/${PN}:"

RDEPENDS_${PN} += "android-tools"

do_install_append_sdflight() {
    ln -sf ../init.d/start_usb ${D}${sysconfdir}/rcS.d/S60start_usb
    ln -sf ../init.d/start_adbd ${D}${sysconfdir}/rcS.d/S61start_adbd
    ln -sf ../init.d/start_q6 ${D}${sysconfdir}/rcS.d/S04start_q6
    ln -sf ../init.d/adsprpcd ${D}${sysconfdir}/rcS.d/S05adsprpcd
    ln -sf ../init.d/post-boot ${D}${sysconfdir}/rcS.d/S05adsprpcd
}

PACKAGE_ARCH_sdflight = "${MACHINE_ARCH}"

