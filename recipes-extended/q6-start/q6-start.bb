DESCRIPTION = "Script to start Hexagon DSP"
LICENSE          = "BSD"
LIC_FILES_CHKSUM = "file://${WORKDIR}/q6-start.sh;endline=29;md5=f228597da0c86a5400e2dc9b655ca990"

PR = "r1"
PV = "1.0"

PROVIDES = "q6-start"

SRC_URI  = "file://q6-start.sh"


PACKAGES = "${PN}"
FILES_${PN} = "/usr/local/bin/*sh"
FILES_${PN} += "/usr/share/data/adsp"

do_install() {
    dest=/usr/local/bin
    install -d ${D}${dest}
    install -m 755 ${WORKDIR}/q6-start.sh ${D}${dest}

    # Create the folder for ADSP dynamic libs
    install -d ${D}/usr/share/data/adsp
}
