DESCRIPTION = "Post-boot configuration"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"

PR = "r0"
PV = "1.0"

SRC_URI = "file://post-boot.init"

PACKAGES = "${PN}"
FILES_${PN} = "/etc/init.d/post-boot"

do_install() {
    dest=${sysconfdir}/init.d
    install -d ${D}${dest}
    install -m 0755 ${WORKDIR}/post-boot.init ${D}${dest}/post-boot
}

# TODO - disable ondemand init script if present
