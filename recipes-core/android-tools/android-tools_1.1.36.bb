inherit autotools gettext

DESCRIPTION = "Tools and libraries from Android"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://libcutils/MODULE_LICENSE_APACHE2;md5=d41d8cd98f00b204e9800998ecf8427e \
                    file://liblog/NOTICE;md5=9645f39e9db895a4aa6e02cb57294595 \
                    file://libzipfile/MODULE_LICENSE_APACHE2;md5=d41d8cd98f00b204e9800998ecf8427e \
                    file://libmincrypt/NOTICE;md5=c19179f3430fd533888100ab6616e114 \
                    file://logcat/MODULE_LICENSE_APACHE2;md5=d41d8cd98f00b204e9800998ecf8427e \
                    file://adb/MODULE_LICENSE_APACHE2;md5=d41d8cd98f00b204e9800998ecf8427e"
HOMEPAGE = "http://android.git.kernel.org/?p=platform/system/core.git"

PR = "r1"

DEPENDS += "glib-2.0"
DEPENDS += "libcap"
DEPENDS += "openssl"
DEPENDS += "zlib"
DEPENDS += "libselinux"
DEPENDS += "ext4-utils"
DEPENDS_append_class-native = " pseudo-native"

PACKAGES = "${PN}"

BBCLASSEXTEND = "native"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/system/core"

SRC_URI = "file://system/core/"
SRC_URI_append_arm = " file://adb.conf"

EXTRA_OECONF_arm = "--disable-shared \
    --with-host-os=${HOST_OS} \
    --with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include"

INSANE_SKIP_${PN} = "installed-vs-shipped"
FILES_${PN}-dev += "${includedir}/cutils"

do_install_append() {
	install -m 0644 ${S}/include/private/android_filesystem_capability.h -D ${D}${includedir}/private/android_filesystem_capability.h
	install -m 0644 ${S}/include/private/android_filesystem_config.h -D ${D}${includedir}/private/android_filesystem_config.h
}

do_install_append_arm() {
	install -m 0644 ${WORKDIR}/adb.conf -D ${D}${sysconfdir}/init/adb.conf
	for h in ${S}/include/cutils/*
	do
	  install -m 0644 ${S}/include/cutils/`basename ${h}` -D ${D}${includedir}/cutils/`basename ${h}`
	done
	install -m 0644 ${S}/include/android/log.h -D ${D}${includedir}/android/log.h
}
