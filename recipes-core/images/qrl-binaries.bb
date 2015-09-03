LICENSE = "BSD-3-Clause-Clear"

PV = "1.0"
PR = "r1"

QRL_BINARIES_TOOLS_LOCATION = "${STAGING_BINDIR_NATIVE}"

# APQ8074 partition sizes
PARTITION_PERSIST_SIZE = "32M"
PARTITION_SYSTEM_SIZE = "1G"
PARTITION_UPDATE_SIZE = "1G"
PARTITION_FACTORY_SIZE = "1G"

# Using default platform key during development
PRODUCT_KEY ?= "${WORKSPACE}/build/target/product/security/testkey"

DEPENDS += " \
    glib-2.0 \
    android-tools \
    virtual/kernel \
    compat-wireless-${MACHINE} \
    qrl-networking \
    camera-hal \
    lk \
    mm-video-oss \
    hostapd  \
    libnl \
    dnsmasq \
    setup-softap \
    sdcard-automount \
    recovery \
    recovery-image \
    recovery-script \
    signapk-java \
    live555 \
    libjpeg-turbo \
    frameworks-av \
"

PKGLIST_OS = " \
    libglib-2.0-0_2.38.2-r0 \
    libz1 \
    libgcc-s1 \
    libnl-3-200 \
    libnl-route-3-200 \
    libnl-nf-3-200 \
    libnl-genl-3-200 \
    libnl-3-cli \
    android-tools \
    qrl-networking \
    camera-hal \
    mm-video-oss \
    hostapd \
    libcrypto1.0.0 \
    dnsmasq \
    setup-softap \
    sdcard-automount \
    recovery-script \
    live555 \
    libjpeg-turbo \
    frameworks-av \
"

PKGLIST_KERNEL = " \
    kernel-image-3.4.0-${MACHINE} \
    kernel-3.4.0-${MACHINE} \
    kernel-module-wlan \
    depmodwrapper-cross \
    compat-wireless \
"

inherit base

copy_package() {
    pkg=$1
    dest=$2

    foundPkg=false
    # Non arch dependent os package check
    if [ -f ${DEPLOY_DIR}/deb/all/${pkg}_*all.deb ]; then
        foundPkg=true
        install -m 644 ${DEPLOY_DIR}/deb/all/${pkg}_*all.deb ${IMAGE_ROOTFS}/${dest}
    fi

    # Arch dependent os package check
    if [ -f ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/${pkg}_*${DPKG_ARCH}.deb ]; then
        foundPkg=true
        install -m 644 ${DEPLOY_DIR}/deb/${TUNE_PKGARCH}/${pkg}_*${DPKG_ARCH}.deb ${IMAGE_ROOTFS}/${dest}
    fi

    # Kernel package check
    if [ -f ${DEPLOY_DIR}/deb/${TUNE_ARCH}/${pkg}_*${DPKG_ARCH}.deb ]; then
        foundPkg=true
        install -m 644 ${DEPLOY_DIR}/deb/${TUNE_ARCH}/${pkg}_*${DPKG_ARCH}.deb ${IMAGE_ROOTFS}/${dest}
    fi

    # Nothing found at all
    if ( ! $foundPkg ); then
        bberror "${pkg} not found"
        exit 1;
    fi
}

# tar up the necessary packages and put them in out
# These files will be copied to the stock Ubuntu image,
# extracted, and installed on-target.
# Expects type in $1 and PKGLIST in $2.
copy_packages_aux() {
    type=$1
    # Shift to get the PKGLIST as $@
    shift
    pkglist=$@
    # For each of them, copy the packages to a temp directory, and create
    # a tar file. Then copy this file to out
    mkdir -p ${IMAGE_ROOTFS}/deb_${type}
    for pkg in ${pkglist}
    do
        copy_package ${pkg} deb_${type}
    done

    cd ${IMAGE_ROOTFS}/deb_${type}
    tar zcf qrlPackages_${type}.tgz *
    mv qrlPackages_${type}.tgz ${DEPLOY_DIR_IMAGE}/out
}

# System image is created by taking all the qrlPackage*tgz files from the out directory,
# untarring them in system_rootfs directory, and then creating an image out of it,
# placing it in the out dir. It contains all the QRL packages we need to install on
# a stock Ubuntu rootfs.
create_system_image() {
    mkdir -p ${IMAGE_ROOTFS}/system_rootfs/qrlPackages
    mkdir -p ${IMAGE_ROOTFS}/system_rootfs/etc
    for tgz in `ls ${DEPLOY_DIR_IMAGE}/out/qrlPackage*tgz`
    do
        tar xzf $tgz -C ${IMAGE_ROOTFS}/system_rootfs/qrlPackages
    done

    # Create build.prop based on .qrlBuildVersion
    version_file=${COREBASE}/.qrlBuildVersion
    if [ -f ${version_file} ]; then
        build_date=`sed -n '2p' ${version_file}`
        build_version=$(head -n 1 ${version_file})
    else
        build_date=`date +"%Y-%m-%d %H:%M:%S"`
        build_version=${MACHINE}-`date +%F-%H%M%S`
    fi
    build_utc=`date -d "${build_date}" +%s`
    manufacturer="qcom"
    fingerprint="${manufacturer}/${MACHINE}/${build_version}/${build_date}"
    echo "ro.build.id=${build_version}" > ${IMAGE_ROOTFS}/system_rootfs/build.prop
    echo "ro.build.fingerprint=${fingerprint}" \
        >> ${IMAGE_ROOTFS}/system_rootfs/build.prop
    echo "ro.product.device=${MACHINE}" >> ${IMAGE_ROOTFS}/system_rootfs/build.prop
    echo "ro.build.date.utc=${build_utc}" >> ${IMAGE_ROOTFS}/system_rootfs/build.prop

    ${QRL_BINARIES_TOOLS_LOCATION}/make_ext4fs -s -l ${PARTITION_SYSTEM_SIZE} \
        ${DEPLOY_DIR_IMAGE}/out/system.img ${IMAGE_ROOTFS}/system_rootfs
}

create_persist_image() {
    # If the persist directory does not exist, we should create one with an
    # empty image file.
    if [ ! -e ${DEPLOY_DIR}/persist/${MACHINE} ]; then
        mkdir -p ${DEPLOY_DIR}/persist/${MACHINE}
        echo "" > ${DEPLOY_DIR}/persist/${MACHINE}/readme.txt
    fi

    ${QRL_BINARIES_TOOLS_LOCATION}/make_ext4fs -s -l ${PARTITION_PERSIST_SIZE} \
        ${DEPLOY_DIR_IMAGE}/out/persist.img ${DEPLOY_DIR}/persist/${MACHINE}
}

create_update_image() {
    # If the update directory does not exist, we should create one with an
    # empty image file.
    if [ ! -e ${DEPLOY_DIR}/update/${MACHINE} ]; then
        mkdir -p ${DEPLOY_DIR}/update/${MACHINE}
        echo "" > ${DEPLOY_DIR}/update/${MACHINE}/readme.txt
    fi

    ${QRL_BINARIES_TOOLS_LOCATION}/make_ext4fs -s -l ${PARTITION_UPDATE_SIZE} \
        ${DEPLOY_DIR_IMAGE}/out/update.img ${DEPLOY_DIR}/update/${MACHINE}
}

copy_packages() {
    if [ -e ${IMAGE_ROOTFS} ]; then
        rm -rf ${IMAGE_ROOTFS}
    fi
    mkdir -p ${IMAGE_ROOTFS}

    if [ -e ${DEPLOY_DIR}/persist/${MACHINE} ]; then
        install -m 644 ${DEPLOY_DIR}/persist/${MACHINE}/* ${IMAGE_ROOTFS}
    fi

    copy_packages_aux os ${PKGLIST_OS}

    copy_packages_aux kernel ${PKGLIST_KERNEL}
}

do_copy_packages() {
    copy_packages
}

do_image() {
    copy_packages
    create_system_image
    create_persist_image
    create_update_image
}

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/build/"

SRC_URI += "file://build/ \
    file://device/qcom/common/releasetools.py \
    file://device/qcom/msm8974/radio/filesmap"

do_target_files() {
    target_files=${MACHINE}-target_files
    target_folder=${WORKDIR}/${target_files}
    if [ -d ${target_folder} ]; then
        rm -r ${target_folder}
    fi

    # Create misc_info.txt
    install -d ${target_folder}/META
    echo "recovery_api_version=3" > ${target_folder}/META/misc_info.txt
    echo "fstab_version=2" >> ${target_folder}/META/misc_info.txt
    echo "multistage_support=1" >> ${target_folder}/META/misc_info.txt
    echo "update_rename_support=1" >> ${target_folder}/META/misc_info.txt

    # Bootable images and ota
    install -m 644 ${DEPLOY_DIR_IMAGE}/out/boot.img -D ${target_folder}/BOOTABLE_IMAGES/boot.img
    install -m 644 ${DEPLOY_DIR_IMAGE}/out/recovery.img -D ${target_folder}/BOOTABLE_IMAGES/recovery.img
    install -m 644 ${STAGING_BINDIR}/updater -D ${target_folder}/OTA/bin/updater
    install -m 644 ${STAGING_BINDIR}/applypatch -D ${target_folder}/OTA/bin/applypatch
    install -m 644 ${STAGING_DIR}/${MACHINE}/etc/recovery.fstab \
        -D ${target_folder}/RECOVERY/RAMDISK/etc/recovery.fstab
    # Radio folder
    install -m 644 ${WORKDIR}/device/qcom/msm8974/radio/filesmap -D ${target_folder}/RADIO/filesmap
    if [ -f ${DEPLOY_DIR_IMAGE}/out/emmc_appsboot.mbn ]; then
        install -m 644 ${DEPLOY_DIR_IMAGE}/out/emmc_appsboot.mbn ${target_folder}/RADIO/emmc_appsboot.mbn
    fi

    # System folder
    install -d ${target_folder}/SYSTEM/
    cp -r ${IMAGE_ROOTFS}/system_rootfs/* ${target_folder}/SYSTEM/

    # Create filesystem_config.txt:
    # This file is composed by one line per folder/file in the system image.
    # Each line of the file is of the format: "complete_filename user_id group_id permission_octet"
    # It is generated by processing the result of the find command on the system folder.
    file_config="${target_folder}/META/filesystem_config.txt"
    if [ -f ${file_config} ]; then
        rm ${file_config}
    fi
    system_folder=$target_folder/SYSTEM
    echo "system 0 0 755" >> $file_config
    cd $system_folder
    find * -exec stat --format="system/%n 0 0 %a" "{}" \; >> $file_config
    cd -

    # Enable extension of this function
    target_files_extension ${target_folder} ${file_config}

    # Zip and deploy
    if [ -f ${WORKDIR}/${target_files}.zip ]; then
        rm -f ${WORKDIR}/${target_files}.zip
    fi
    cd ${target_folder}
    zip -ry ${WORKDIR}/${target_files}.zip ./
    cd -

    cp ${WORKDIR}/${target_files}.zip ${DEPLOY_DIR_IMAGE}/out/
}

target_files_extension() {
    target_folder=$1
    shift
    file_config=$@
}

do_update_package() {
    set -x
    if [ -f ${DEPLOY_DIR_IMAGE}/${MACHINE}-ota.zip ]; then
        rm ${DEPLOY_DIR_IMAGE}/${MACHINE}-ota.zip
    fi
    ${WORKDIR}/build/tools/releasetools/ota_from_target_files \
        -n -d MMC -s ${WORKDIR}/device/qcom/common/releasetools.py -v \
        -p ${STAGING_BINDIR_NATIVE} \
        --signapk_path signapk.jar -k ${PRODUCT_KEY} \
        ${DEPLOY_DIR_IMAGE}/out/${MACHINE}-target_files.zip \
        ${DEPLOY_DIR_IMAGE}/out/${MACHINE}-ota.zip
    set +x
}

do_factory_image() {
    set -x
    if [ -d ${IMAGE_ROOTFS}/factory_rootfs ]; then
        rm -rf ${IMAGE_ROOTFS}/factory_rootfs
    fi
    mkdir -p ${IMAGE_ROOTFS}/factory_rootfs
    ${WORKDIR}/build/tools/releasetools/ota_from_target_files \
        -n -d MMC -s ${WORKDIR}/device/qcom/common/releasetools.py -v \
        -p ${STAGING_BINDIR_NATIVE} \
        --signapk_path signapk.jar -k ${PRODUCT_KEY} \
        --factory --no_prereq \
        ${DEPLOY_DIR_IMAGE}/out/${MACHINE}-target_files.zip \
        ${IMAGE_ROOTFS}/factory_rootfs/${MACHINE}-factory.zip
    ${QRL_BINARIES_TOOLS_LOCATION}/make_ext4fs -s -l ${PARTITION_FACTORY_SIZE} \
        ${DEPLOY_DIR_IMAGE}/out/factory.img ${IMAGE_ROOTFS}/factory_rootfs
    set +x
}

do_patch[noexec] = "0"
do_configure[noexec] = "1"
do_build[noexec] = "0"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"
do_image[depends] = "ext4-utils-native:do_populate_sysroot"
do_update_package[depends] = "imgdiff-native:do_populate_sysroot"

addtask image after do_build
addtask target_files after do_image
addtask update_package after do_target_files
addtask factory_image after do_update_package
