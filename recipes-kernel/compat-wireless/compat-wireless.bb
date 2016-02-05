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
    #select wifi module based on the value set in local.conf
    if [ ${WIFI_MODULE} == "qca6234" ]; then
	export BUILD_ATH6KL_VER_35_SDIO=1
    elif [ ${WIFI_MODULE} == "qca9375" ]; then
	export BUILD_ATH6KL_VER_35_USB=1
    else
	export BUILD_ATH6KL_VER_35_SDIO=1
    fi
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

# Save the user modified conf file
pkg_prerm_${PN}() {
    file_md5sum() {
        pn=$1
        fn=$2
        md5=`cat /var/lib/dpkg/info/${pn}.md5sums | grep ${fn} | cut -d' ' -f1`
        echo $md5
    }

    if [ -f /etc/wpa_supplicant/wpa_supplicant.conf ]; then
        cp /etc/wpa_supplicant/wpa_supplicant.conf /tmp/wpa_supplicant.conf.tmp
        echo $(file_md5sum compat-wireless wpa_supplicant.conf) > /tmp/wpa_supplicant.conf.md5
    fi
}

pkg_postinst_${PN}() {
    # Try to restore the user version if possible
    if [ -f /tmp/wpa_supplicant.conf.tmp ]; then
        # Restore the user file if the new file has same
        # contents of file in the previous package version
        new_md5=`md5sum /etc/wpa_supplicant/wpa_supplicant.conf | cut -d' ' -f1`
        old_md5=`cat /tmp/wpa_supplicant.conf.md5`
        if [ "$old_md5" = "$new_md5" ]; then
            install -m 644 /tmp/wpa_supplicant.conf.tmp /etc/wpa_supplicant/wpa_supplicant.conf
        fi
    fi
}
