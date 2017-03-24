inherit update-rc.d

DESCRIPTION = "sdcard automount"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM ="file://${WORKDIR}/81-automount-sdcard.rules;endline=36;md5=fa9c5b43c0cb444527de5ab2b1d8b49c"

PR = "r0"
PV = "1.0"

PROVIDES = "sdcard-automount"

SRC_URI = "file://81-automount-sdcard.rules \
           file://sdcard-mount-check.init \
          "

PACKAGES = "${PN}"
FILES_${PN} = "/etc/udev/rules.d \ 
               /etc/init.d/ "

INITSCRIPT_NAME = "${PN}"
INITSCRIPT_PARAMS = "defaults 10"

do_install() {
    dest=/etc/udev/rules.d
    # Install 81-automount-sdcard.rules 
    install -d ${D}${dest}
    install -m 644 ${WORKDIR}/81-automount-sdcard.rules ${D}${dest}
    
    dest=/etc/init.d
    # Install sdcard-mount-check.init
    install -d ${D}${dest}
    install -m 755 ${WORKDIR}/sdcard-mount-check.init ${D}${dest}/sdcard-mount-check
    
}
