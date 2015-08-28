DESCRIPTION = "Android bootable recovery image"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"
HOMEPAGE = "https://android.googlesource.com/platform/bootable/recovery"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"

SRC_URI += "file://build/ \
    file://init \
    file://recovery.fstab"

SRC_URI += "http://releases.linaro.org/14.09/ubuntu/ifc6410/initrd.img-3.4.0-linaro-ifc6410;downloadfilename=initrd.img;name=initrd"
SRC_URI[initrd.md5sum] = "d92fb01531698e30615f26efa2999c6c"
SRC_URI[initrd.sha256sum] = "d177ba515258df5fda6d34043261d694026c9e27f1ef8ec16674fa479c5b47fb"

DEPENDS += "virtual/kernel recovery dumpkey-java"

inherit base

LK_ROOT_DEV ?= ""
LK_CMDLINE_OPTIONS ?= ""

do_configure() {
}

do_compile() {
    set -x
    # Unpack Linaro's initrd
    install -d ${WORKDIR}/initrd
    cd ${WORKDIR}/initrd
    gzip -dc ${WORKDIR}/initrd.img | cpio -id

    # Copy files to initrd
    install -m 755 ${STAGING_BINDIR}/recovery -D ${WORKDIR}/initrd/sbin/recovery
    install -m 755 ${WORKDIR}/init -D ${WORKDIR}/initrd/init
    install -m 644 ${WORKDIR}/recovery.fstab -D ${WORKDIR}/initrd/etc/recovery.fstab
    install -m 644 ${WORKDIR}/recovery.fstab -D ${STAGING_DIR}/${MACHINE}/etc/recovery.fstab

    # Create build.prop only with machine name
    echo "ro.product.device=${MACHINE}" > ${WORKDIR}/initrd/build.prop

    # Dump public keys
    java -jar ${STAGING_BINDIR_NATIVE}/dumpkey.jar \
        `ls ${WORKDIR}/build/target/product/security/*.pem` \
        > ${WORKDIR}/keys
    install -m 644 ${WORKDIR}/keys -D ${WORKDIR}/initrd/res/keys

    # Create the image
    find ./ | cpio -H newc -o > ${WORKDIR}/initrd-recovery.cpio
    cd -
    minigzip ${WORKDIR}/initrd-recovery.cpio
    mv ${WORKDIR}/initrd-recovery.cpio.gz ${WORKDIR}/initrd-recovery.img
    set +x
}

do_install() {
    if [ ! -z "${SERIAL_CONSOLES}" ] ; then
        serialport=`echo "${SERIAL_CONSOLES}" | sed 's/.*\;//'`
        baudrate=`echo "${SERIAL_CONSOLES}" | sed 's/\;.*//'`
    else
        serialport="ttyHSL0"
        baudrate="115200"
    fi

    set -x
    cmd_line="console=${serialport},${baudrate},n8 root=${LK_ROOT_DEV} rw rootwait ${LK_CMDLINE_OPTIONS}"
    kernel_image=${DEPLOY_DIR_IMAGE}/zImage
    master_dt_image=${DEPLOY_DIR_IMAGE}/masterDTB

    ${STAGING_BINDIR_NATIVE}/mkbootimg --kernel ${kernel_image} \
        --dt ${master_dt_image} \
        --base 0x80200000 \
        --ramdisk ${WORKDIR}/initrd-recovery.img \
        --ramdisk_offset 0x02D00000 \
        --cmdline "${cmd_line}" \
        --pagesize 2048 \
        --output ${DEPLOY_DIR_IMAGE}/recovery-${MACHINE}.img
    install -d ${DEPLOY_DIR_IMAGE}/out
    cp ${DEPLOY_DIR_IMAGE}/recovery-${MACHINE}.img ${DEPLOY_DIR_IMAGE}/out/recovery.img
    set +x
}

