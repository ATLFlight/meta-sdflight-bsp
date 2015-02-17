DESCRIPTION = "Tools and libraries from Android"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://libcutils/MODULE_LICENSE_APACHE2;md5=d41d8cd98f00b204e9800998ecf8427e \
file://liblog/NOTICE;md5=9645f39e9db895a4aa6e02cb57294595 \
file://libzipfile/MODULE_LICENSE_APACHE2;md5=d41d8cd98f00b204e9800998ecf8427e \
file://libmincrypt/NOTICE;md5=c19179f3430fd533888100ab6616e114 \
file://logcat/MODULE_LICENSE_APACHE2;md5=d41d8cd98f00b204e9800998ecf8427e \
file://adb/MODULE_LICENSE_APACHE2;md5=d41d8cd98f00b204e9800998ecf8427e"
HOMEPAGE = "http://android.git.kernel.org/?p=platform/system/core.git"

PR = "r0"
PV="1.1.36"
PN = "android-tools"

PROVIDES = "android-tools"
DEPENDS += "zlib"

PACKAGES = "${PN}"

inherit autotools gettext

SRC_URI = "git://codeaurora.org/platform/system/core;branch=redcloud;tag=AU_LINUX_BASE_TARGET_ALL.01.01.036"
SRC_URI += "file://0001-QR-Linux-Patches.patch"
SRC_URI += "file://adb.conf"


EXTRA_OECONF = "--disable-shared"
EXTRA_OEMAKE = "INCLUDES='-I${S}/include'"

INSANE_SKIP_${PN} = "installed-vs-shipped"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_install_append() {

	install -m 0644 ${WORKDIR}/adb.conf -D ${D}${sysconfdir}/init/adb.conf
}
