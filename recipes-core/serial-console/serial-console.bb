DESCRIPTION = "Serial port configuration"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV = "1.0"

PROVIDES = "serial-console"

SERIAL_CONSOLE ?= "115200 ttyHSL0"

SRC_URI = "file://serial-console.conf"

PACKAGES = "${PN}"

do_install() {
    if  [ ! -z "${SERIAL_CONSOLES}" ] ; then
	serialport=`echo "${SERIAL_CONSOLES}" | sed 's/.*\;//'`
	baudrate=`echo "${SERIAL_CONSOLES}" | sed 's/\;.*//'`
	install -d ${D}${sysconfdir}/init
	install -m 644 ${WORKDIR}/serial-console.conf ${D}${sysconfdir}/init/serial-console.conf
	sed -i -e s/\@SERIALPORT\@/$serialport/g -e s/\@BAUDRATE\@/$baudrate/g ${D}${sysconfdir}/init/serial-console.conf
	echo "$serialport" >> ${D}${sysconfdir}/securetty
    fi
}
