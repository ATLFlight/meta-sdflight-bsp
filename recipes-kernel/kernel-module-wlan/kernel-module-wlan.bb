DESCRIPTION = "Compile the prima module for use with the WCN3680 for Wi-Fi/Bluetooth."

inherit module

PR = "r0"

LICENSE = "ISC"

LIC_FILES_CHKSUM = "file://../COPYING;md5=552efe106a6bfffc3be6ad39e5273108"

SRC_URI = "git://codeaurora.org/platform/vendor/qcom-opensource/wlan/prima.git;tag=AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041;protocol=git"
SRC_URI += " \
	file://COPYING \
	file://prima.patch \
	file://prima-init \
	file://WCNSS_qcom_wlan_nv.bin \
	"

PACKAGES = "${PN}"

FILES_${PN} += " \
	    ${base_libdir}/firmware/wlan/prima/WCNSS_cfg.dat \
	    ${base_libdir}/firmware/wlan/prima/WCNSS_qcom_cfg.ini \
	    ${base_libdir}/modules/3.4.0-${MACHINE} \
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
	# Install prima-init
	mkdir -p ${D}/etc/network/if-up.d
	install ${WORKDIR}/prima-init ${D}/etc/network/if-up.d/prima-init
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
    MOUNT=/bin/mount

    QC_FW_DEVICE=/dev/mmcblk0p1	# eMMC partition that contains the firmware
    QC_FW_MOUNT_POINT=/firmware	# Where to mount the eMMC partition
    QC_FW_SUBDIR=image		# The subdir in eMMC partition where the fw is
    QC_FW_WIFI_FILE_NAME=wcnss	# The root of thw WCN fw filename
    QC_FW_WIFI_FILE_EXT=mdt		# The extension of the WCN fw filename
    QC_FW_TRIGGER_DEVICE=/dev/wcnss_wlan # The device to trigger for fw download
    QC_FW_DEST_DIR=/lib/firmware	     # Where to copy the fw files

    QC_PERSIST_DEVICE=/dev/mmcblk0p14 # eMMC partition that contains the firmware
    QC_PERSIST_MOUNT_POINT=/persist	# Where to mount the eMMC partition
    QC_NV_FILE_NAME=WCNSS_qcom_wlan_nv
    QC_NV_FILE_EXT=bin
    QC_NV_FILE_DEST_DIR=/lib/firmware/wlan/prima

    qcFwImgDir=${QC_FW_MOUNT_POINT}/${QC_FW_SUBDIR} # Where to copy fw files from
    wifiMdtSrcFile=${qcFwImgDir}/${QC_FW_WIFI_FILE_NAME}.${QC_FW_WIFI_FILE_EXT}
    wifiMdtDstFile=${QC_FW_DEST_DIR}/${QC_FW_WIFI_FILE_NAME}.${QC_FW_WIFI_FILE_EXT}

    # Mount the firmware partition if not already mounted
    if [ ! -d ${QC_FW_MOUNT_POINT} ]
    then
        /bin/mkdir -p ${QC_FW_MOUNT_POINT}
    fi

    if [ ! -d ${qcFwImgDir} ]
    then
        echo "INFO: Mounting firmware partition..."
        ${MOUNT} -t vfat ${QC_FW_DEVICE} ${QC_FW_MOUNT_POINT}
    else
        echo "INFO: Skipping mounting the firmware partition"
    fi

    if [ ! -e ${wifiMdtSrcFile} ]
    then
        echo "ERROR: Firmware not found or mount failed"
        return 1
    fi
    # Copy the wcnss* files from eMMC partition to /lib/firmware
    /bin/cp ${qcFwImgDir}/${QC_FW_WIFI_FILE_NAME}* ${QC_FW_DEST_DIR}

    if [ ! -f ${wifiMdtDstFile} ]
    then
        echo "ERROR: Firmware  not found. Giving up"
        exit 1
    fi

    # Always copy the NV file
    wifiMdtNVFile=${QC_PERSIST_MOUNT_POINT}/${QC_NV_FILE_NAME}.${QC_NV_FILE_EXT}
    wifiMdtNVDstFile=${QC_NV_FILE_DEST_DIR}/${QC_NV_FILE_NAME}.${QC_NV_FILE_EXT}

    if [ ! -d ${QC_PERSIST_MOUNT_POINT} ]; then
	/bin/mkdir -p ${QC_PERSIST_MOUNT_POINT}
    fi

    if [ ! -f ${wifiMdtNVFile} ]; then
	echo "INFO: Mounting persist partition..."
	${MOUNT} -t ext4 ${QC_PERSIST_DEVICE} ${QC_PERSIST_MOUNT_POINT}
    fi

    if [ -f ${wifiMdtNVFile} ]; then
	/bin/cp ${wifiMdtNVFile} ${wifiMdtNVDstFile}
    else
	echo "WARNING: NV File not found or mount failed"
    fi

    if [ ! -f ${wifiMdtNVDstFile} ]; then
	echo "ERROR: NV File not found, Giving up"
	exit 1
    fi
}
