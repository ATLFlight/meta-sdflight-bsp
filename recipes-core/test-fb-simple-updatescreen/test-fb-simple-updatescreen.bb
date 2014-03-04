inherit autotools

DESCRIPTION = "Sends a test pattern to the framebuffer"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b0b57e6089d2e897908c55f8aaca81a"


PR = "r0"
PV="1.0"
PN = "test-fb-simple-updatescreen"

PROVIDES = "test-fb-simple-updatescreen"

#PACKAGE_ARCH = "armhf"

SRC_URI += "file://configure.ac \
	         file://README \
	         file://Makefile.am \
            file://test-fb-simple-updatescreen.c \
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
    shutil.copy(wd+'/test-fb-simple-updatescreen.c', s)
    shutil.copy(wd+'/COPYING', s)
}

