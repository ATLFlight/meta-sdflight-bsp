DESCRIPTION = "Post-boot configuration"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"

PR = "r0"
PV = "1.0"

SRC_URI = "file://post-boot.conf"

PACKAGES = "${PN}"
FILES_${PN} = "/etc/init/post-boot.conf"

do_install() {
    dest=/etc/init
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/post-boot.conf ${D}${dest}
}

pkg_postinst_${PN} () {
    update-rc.d ondemand disable
    update-rc.d cpufrequtils disable
}