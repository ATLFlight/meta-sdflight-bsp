DESCRIPTION = "Compile the compat-wireless module"

inherit module

PR = "r1"

LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=d7810fab7487fb0aad327b76f1be7cd7"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/external/compat-wireless"

SRC_URI = "file://external/compat-wireless/"

SRC_URI += " \
   file://qca6234.cfg \
   file://wpa_supplicant.conf \
   "

PROVIDES += "kernel-module-wlan compat-wireless-${MACHINE}"

PACKAGES = "kernel-module-wlan ${PN}"

FILES_kernel-module-wlan = " \
    ${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless/wlan.ko \
    "

FILES_${PN} = "	\
    ${sysconfdir}/network/* \
    ${sysconfdir}/network/interfaces.d/* \
    /etc/* \
    "

do_compile_prepend() {
    export BUILD_ATH6KL_VER_35=1
    export HAVE_CFG80211_KERNEL3_4=1
}

EXTRA_OEMAKE = "'KLIB_BUILD="${STAGING_KERNEL_DIR}" '"

do_install () {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless
	install -m 0644 ${S}/*${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/wireless
	install -m 644 ${WORKDIR}/qca6234.cfg -D ${D}${sysconfdir}/network/interfaces.d/qca6234.cfg
	dest=/etc/wpa_supplicant
	install -d ${D}${dest}
	install -m 644 ${WORKDIR}/wpa_supplicant.conf ${D}${dest}
}
