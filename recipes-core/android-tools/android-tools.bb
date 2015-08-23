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
PV = "1.1.36"
PN = "android-tools"

PROVIDES = "android-tools"
DEPENDS += "zlib"
DEPENDS += "pseudo-native"
DEPENDS += "glib-2.0"
DEPENDS += "libselinux"
DEPENDS += "openssl"
DEPENDS += "ext4-utils"

PACKAGES = "${PN}"

inherit autotools gettext

SRC_URI = "git://codeaurora.org/quic/la/platform/system/core;nobranch=1"
SRCREV = "5b3a80a88527ad481add6c1ad4c6cde42fb859ff"
SRC_URI += "file://0001-system-core-QR-Linux-porting.patch \
    file://adb.conf"

EXTRA_OECONF = "--disable-shared \
    --with-host-os=${HOST_OS} \
    --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include"

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
