DESCRIPTION = "Compile the prima module for use with the WCN3680 for Wi-Fi/Bluetooth."

inherit module

PR = "r0"

LICENSE = "ISC"

LIC_FILES_CHKSUM = "file://../COPYING;md5=552efe106a6bfffc3be6ad39e5273108"

SRC_URI = "git://codeaurora.org/platform/vendor/qcom-opensource/wlan/prima.git;tag=AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041;protocol=git;nobranch=1"
SRC_URI += " \
	file://COPYING \
	file://prima.patch \
	file://prima-init \
	file://prima.cfg\
	file://WCNSS_qcom_wlan_nv.bin \
	"

PACKAGES = "${PN}"

FILES_${PN} += " \
	    ${base_libdir}/firmware/wlan/prima/WCNSS_cfg.dat \
	    ${base_libdir}/firmware/wlan/prima/WCNSS_qcom_cfg.ini \
	    ${base_libdir}/modules/3.4.0-${MACHINE} \
	    ${sysconfdir}/network/* \
	    "

do_unpack_append() {
    import os
    import shutil
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    os.rename(wd+'/git', s)
    
}

module_do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	CROSS_COMPILE=${CROSS_COMPILE} make -C ${STAGING_KERNEL_DIR}/source/../linux-${MACHINE}-standard-build ARCH=arm M=${S} O=${WORKDIR} -j4
}

module_do_install() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	CROSS_COMPILE=${CROSS_COMPILE} make V=1 -C ${STAGING_KERNEL_DIR}/source/../linux-${MACHINE}-standard-build ARCH=arm M=${S} O=${WORKDIR} INSTALL_MOD_PATH=${D} -j4 modules_install 

	# Install firmware
	mkdir -p ${D}/lib/firmware/wlan/prima
	install -m 644 ${S}/firmware_bin/WCNSS_cfg.dat ${S}/firmware_bin/WCNSS_qcom_cfg.ini ${S}/firmware_bin/WCNSS_qcom_wlan_nv.bin ${D}/lib/firmware/wlan/prima
	# Install prima-init and prima.cfg
	install -m 755 ${WORKDIR}/prima-init -D ${D}/etc/network/if-pre-up.d/prima-init
	install -m 644 ${WORKDIR}/prima.cfg -D ${D}/etc/network/interfaces.d/prima.cfg

}

do_package_append() {

    import shutil

    deploy_dir = d.getVar('DEPLOY_DIR', True)
    machine = d.getVar('MACHINE', True)
    destdir = deploy_dir+'/persist/'+machine
    try:
        os.makedirs(destdir)
    except:
        pass
    workdir = d.getVar('WORKDIR', True)
    shutil.copy(workdir+'/WCNSS_qcom_wlan_nv.bin', destdir)
}

pkg_postinst_${PN}() {
    /usr/local/qr-linux/qrl-copy-firmware.sh
}
