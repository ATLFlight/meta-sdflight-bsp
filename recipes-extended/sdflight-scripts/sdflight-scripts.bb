DESCRIPTION = "SDFlight Miscellaneous scripts"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"

PR = "r0"
PV = "1.0"

SRC_URI = "file://board-name.sh"

PACKAGES = "${PN}"
FILES_${PN} = "/usr/local/bin/*"

do_install() {
    dest=/usr/local/bin
    install -d ${D}${dest}
    install -m 755 ${WORKDIR}/board-name.sh ${D}${dest}
}

