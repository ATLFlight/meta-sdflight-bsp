LICENSE = "BSD-3-Clause-Clear"

PV = "1.0"
PR = "r1"

QRL_BINARIES_TOOLS_LOCATION = "${STAGING_BINDIR_NATIVE}"

# APQ8074 partition sizes
PARTITION_PERSIST_SIZE = "32M"
PARTITION_SYSTEM_SIZE = "1G"

DEPENDS += " \
    android-tools \
    virtual/kernel \
    compat-wireless-${MACHINE} \
    qrl-networking \
    camera-hal \
    lk \
    mm-video-oss \
"

PKGLIST_OS = " \
    libglib-2.0-0_2.38.2-r0 \
    libz1 \
    libgcc-s1 \
    android-tools \
    qrl-networking \
    camera-hal \
    mm-video-oss \
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
    for tgz in `ls ${DEPLOY_DIR_IMAGE}/out/qrlPackage*tgz`
    do
        tar xzf $tgz -C ${IMAGE_ROOTFS}/system_rootfs/qrlPackages
    done

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

copy_packages() {
    if [ -e ${IMAGE_ROOTFS} ]; then
        rm -rf ${IMAGE_ROOTFS}
    fi
    mkdir -p ${IMAGE_ROOTFS}

    if [ -e ${DEPLOY_DIR}/persist/${MACHINE} ]; then
        install -m 644 ${DEPLOY_DIR}/persist/${MACHINE}/* ${IMAGE_ROOTFS}
    fi

    copy_packages_aux os ${PKGLIST_OS}

    # Now create a tgz of the kernel modules
    cd ${DEPLOY_DIR}/deb/${TUNE_ARCH}
    tar zcf qrlPackages_kernel.tgz *.deb
    mv qrlPackages_kernel.tgz ${DEPLOY_DIR_IMAGE}/out
}

do_copy_packages() {
    copy_packages
}

do_image() {
    copy_packages
    create_system_image
    create_persist_image
}

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_build[noexec] = "0"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"
do_image[depends] = "make-ext4fs-native:do_populate_sysroot"

addtask image after do_build
addtask copy_packages after do_build
