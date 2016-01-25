DESCRIPTION = "ueventd"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV = "1.0"

PROVIDES = "ueventd"

SRC_URI = "file://ueventd \
          "

PACKAGES = "${PN}"
FILES_${PN} = "/etc/init.d/ \
               /etc/rcS.d/  \
		/etc/rcS.d/S90ueventd  \
"

do_install() {
    dest=/etc/init.d/

    install -d ${D}${dest}
    install -m 0755    ${WORKDIR}/ueventd		${D}${dest}/

#
# Create runlevel links
#

    update-rc.d -r ${D} ueventd start 90 S .

}
