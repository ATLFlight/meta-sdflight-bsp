DESCRIPTION = "sdcard automount"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV = "1.0"

PROVIDES = "sdcard-automount"

SRC_URI = "file://81-automount-sdcard.rules \
           file://sdcard-mount-check.conf \
          "

PACKAGES = "${PN}"
FILES_${PN} = "/etc/udev/rules.d \ 
               /etc/init/ "

do_install() {
    dest=/etc/udev/rules.d
    # Install 81-automount-sdcard.rules 
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/81-automount-sdcard.rules ${D}${dest}
    
    dest=/etc/init
    # Install sdcard-mount-check.conf
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/sdcard-mount-check.conf ${D}${dest}
    
}
