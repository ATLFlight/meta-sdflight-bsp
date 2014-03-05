DESCRIPTION = "Compile the prima module for use with the WCN3680 for Wi-Fi/Bluetooth."

inherit module

PR = "r0"

LICENSE = "ISC"

LIC_FILES_CHKSUM = "file://../COPYING;md5=552efe106a6bfffc3be6ad39e5273108"

SRC_URI = "git://git.quicinc.com:29418/platform/vendor/qcom-opensource/wlan/prima.git;tag=AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241;protocol=ssh"
SRC_URI += " \
	file://COPYING \
	file://prima.patch \
	file://prima-init.sh \
	file://prima.cfg \
	"


PACKAGES = "${PN}"

FILES_${PN} += " \
	    ${base_libdir}/firmware/wlan/prima/* \
	    ${base_libdir}/modules/3.4.0-caf-standard/* \
	    ${sysconfdir}/network/* \
	    ${sysconfdir}/network/interfaces.d/* \
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
	# Install prima-init.sh and prima.cfg
	mkdir -p ${D}/etc/network
	install ${WORKDIR}/prima-init.sh ${D}/etc/network/prima-init.sh
	mkdir -p ${D}/etc/network/interfaces.d
	install -m 644 ${WORKDIR}/prima.cfg ${D}/etc/network/interfaces.d/prima.cfg
}

