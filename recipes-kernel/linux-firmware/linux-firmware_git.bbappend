FILESEXTRAPATHS_append := "${THISDIR}:"

# Patch was generated between 2 versions under the LICENCE.atheros_firmware (BSD-Clear) license
SRC_URI += "file://files/PS_ASIC.patch"

PACKAGES = "${PN}-ar3k ${PN}-ar3k-license ${PN}-atheros-license"

RDEPENDS_${PN} = ""
 
do_install() {
    install -d ${D}/lib/firmware/ar3k/1020201
    install -m 0644 ${S}/ar3k/1020201/RamPatch.txt    ${D}/${base_libdir}/firmware/ar3k/1020201/RamPatch.txt
    install -m 0644 ${S}/ar3k/1020201/PS_ASIC.pst     ${D}/${base_libdir}/firmware/ar3k/1020201/PS_ASIC.pst
    install -m 0644 ${S}/LICENCE.atheros_firmware     ${D}/${base_libdir}/firmware/LICENCE.atheros_firmware
    install -m 0644 ${S}/LICENSE.QualcommAtheros_ar3k ${D}/${base_libdir}/firmware/LICENSE.QualcommAtheros_ar3k
}
