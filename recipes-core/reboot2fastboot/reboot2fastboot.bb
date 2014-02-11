inherit autotools

DESCRIPTION = "reboot2fastboot causes target system to reboot into fastboot mode."
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://COPYING;md5=cc11e99d4e533c38781bc583ff279e4c"


PR = "r0"
PV="1.0"
PN = "reboot2fastboot"

PROVIDES = "reboot2fastboot"

#PACKAGE_ARCH = "armhf"

SRC_URI += "file://configure.ac \
	    file://Makefile.am \
            file://reboot2fastboot.c \
            file://__rfastboot.S \
            file://COPYING \
	    "


#EXTRA_OECONF = "--host=${ELT_TARGET_PREFIX}"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if not os.path.exists(s):
        os.makedirs(s)
    shutil.copy(wd+'/configure.ac', s)
    shutil.copy(wd+'/Makefile.am', s)
    shutil.copy(wd+'/reboot2fastboot.c', s)
    shutil.copy(wd+'/__rfastboot.S', s)
    shutil.copy(wd+'/COPYING', s)
}

