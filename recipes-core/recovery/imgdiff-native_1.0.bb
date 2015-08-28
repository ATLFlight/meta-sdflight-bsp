DESCRIPTION = "Android bootable recovery"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://NOTICE;md5=9645f39e9db895a4aa6e02cb57294595"
HOMEPAGE = "https://android.googlesource.com/platform/bootable/recovery"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/bootable/applypatch/"

SRC_URI = "file://bootable/recovery/applypatch"

DEPENDS += "zlib-native bzip2-native"

inherit native autotools

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include \
                --with-core-headers=${STAGING_NATIVE_INCDIR} \
                --enable-shared=no"

do_unpack_append() {
    import shutil
    wd = d.getVar('WORKDIR',True)
    ws = d.getVar('WORKSPACE', True)
    shutil.copyfile(ws+'/bootable/recovery/NOTICE', wd+'/bootable/applypatch/NOTICE')
    shutil.move(wd+'/bootable/applypatch/imgdiff/Makefile.am', wd+'/bootable/applypatch/Makefile.am')
    shutil.move(wd+'/bootable/applypatch/imgdiff/configure.ac', wd+'/bootable/applypatch/configure.ac')
    shutil.rmtree(wd+'/bootable/applypatch/imgdiff')
}
