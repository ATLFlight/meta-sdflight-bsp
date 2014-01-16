inherit native autotools gettext



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
PN = "android_tools"

PROVIDES = "android_tools-native"

SRC_URI = "git://codeaurora.org/platform/system/core;tag=AU_LINUX_BASE_TARGET_ALL.01.01.036"
SRC_URI += "file://Makefile.am \
            file://Makefile_adb.am \
            file://Makefile_mkbootimg.am \
            file://Makefile_libmincrypt.am \
	    file://configure.ac"

SRCREV="1c246a945e1e2338d5a647379cd79ae2351f213b"

EXTRA_OEMAKE = "INCLUDES='-I${S}/include'"

do_mv_git() {
  rm -rf ${S}
  mv ${WORKDIR}/git ${S}
  cp ${WORKDIR}/configure.ac ${S}/configure.ac
  cp ${WORKDIR}/Makefile.am ${S}
  cp ${WORKDIR}/Makefile_adb.am ${S}/adb/Makefile.am
  cp ${WORKDIR}/Makefile_mkbootimg.am ${S}/mkbootimg/Makefile.am
  cp ${WORKDIR}/Makefile_libmincrypt.am ${S}/libmincrypt/Makefile.am
}

addtask mv_git after do_unpack before do_patch

NATIVE_INSTALL_WORKS = "1"

