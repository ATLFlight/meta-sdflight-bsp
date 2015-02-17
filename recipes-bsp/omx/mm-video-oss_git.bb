DESCRIPTION = "OpenMAX video for MSM chipsets"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/\
${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10\
					file://${S}/libstagefrighthw/NOTICE;md5=384ada94f865c1c1442771ab380fb00c \
					file://${S}/libstagefrighthw/MODULE_LICENSE_APACHE2;md5=d41d8cd98f00b204e9800998ecf8427e"

SRC_URI = "git://codeaurora.org/platform/hardware/qcom/media;protocol=git;nobranch=1"

PACKAGES = "${PN}"

SRCREV_som8064       = "AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041"
SRCREV_som8064-revB  = "AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041"
SRCREV_som8064-const = "AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041"
SRCREV_ifc6410       = "AU_LINUX_ANDROID_JB_2.5_AUTO.04.02.02.115.005"

PACKAGES = "${PN}"

PV = "1.1"
PR = "r16"

LV = "1.0.0"

inherit autotools

BASEMACHINE = "msm8960"

FILES_${PN} = "\
    /usr/lib/* \
    /usr/bin/* \
    /usr/include/* \
    /usr/share/*"

#Skips check for .so symlinks
INSANE_SKIP_${PN} = "dev-so"

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_configure() {
}

do_make() {
}

do_install() {
	install -d ${D}/usr/include/mm-core
	install -m 0644 ${S}/mm-core/inc/*.h ${D}/usr/include/mm-core
}
