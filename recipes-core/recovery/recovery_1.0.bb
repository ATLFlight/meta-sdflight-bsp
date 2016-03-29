DESCRIPTION = "Android bootable recovery"
LICENSE = "Apache-2.0 & BSD"
LIC_FILES_CHKSUM = "file://NOTICE;md5=9645f39e9db895a4aa6e02cb57294595"
HOMEPAGE = "https://android.googlesource.com/platform/bootable/recovery"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/bootable/recovery/"

SRC_URI = "file://bootable/recovery/"

DEPENDS += "virtual/kernel android-tools ext4-utils safe-iop libselinux libsparse"

PACKAGES = "${PN}"

inherit autotools

INSANE_SKIP_${PN} = "installed-vs-shipped"

EXTRA_OECONF = "--with-sanitized-headers=${STAGING_INCDIR}/linux-headers/usr/include \
                --with-core-headers=${STAGING_INCDIR} \
                --enable-shared=no"

do_unpack_append() {
    import shutil
    ws = d.getVar('WORKSPACE',True)
    s = d.getVar('S',True)
    old = ws+'/device/qcom/common/recovery/oem-recovery'
    shutil.rmtree(s+'/oem-recovery', ignore_errors=True)
    shutil.copytree(old, s+'/oem-recovery')
}

do_install_append() {
    install -m 755 ${D}${bindir}/recovery -D ${STAGING_BINDIR}/
    install -m 755 ${D}${bindir}/updater -D ${STAGING_BINDIR}/
    install -m 755 ${D}${bindir}/applypatch -D ${STAGING_BINDIR}/
}
