DESCRIPTION = "Compile the compat-wireless module"

inherit module

PR = "r0"

LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRCREV = "LNX.LA.3.5-01620-8x74.0"
SRC_URI = "git://codeaurora.org/platform/external/compat-wireless.git;revision=${SRCREV};protocol=git;nobranch=1"

SRC_URI += " \
   file://Kbuild.patch \
   file://qca6234.cfg \
   file://wpa_supplicant.conf \
   "

PROVIDES += "kernel-module-wlan"

PACKAGES = "kernel-module-wlan ${PN}"

FILES_kernel-module-wlan = " \
    ${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/wlan.ko \
    "

FILES_${PN} = "	\
    ${sysconfdir}/network/* \
    ${sysconfdir}/network/interfaces.d/* \
    /etc/* \
    "

do_unpack_append() {
    import os
    import shutil
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    os.rename(wd+'/git', s)
    shutil.copyfile(s+'/drivers/net/wireless/ath/ath6kl-3.5/android_sdio/Kbuild', s+'/Kbuild')
}

do_compile_prepend() {
    export BUILD_ATH6KL_VER_35=1
    export HAVE_CFG80211_KERNEL3_4=1
}

EXTRA_OEMAKE = "'KLIB_BUILD="${STAGING_KERNEL_DIR}" '"

do_install () {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless
	install -m 0644 ${S}/*${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless
	install -m 644 ${WORKDIR}/qca6234.cfg -D ${D}/etc/network/interfaces.d/qca6234.cfg
	dest=/etc/wpa_supplicant
	install -d ${D}${dest}
	install -m 644 ${WORKDIR}/wpa_supplicant.conf ${D}${dest}
}
