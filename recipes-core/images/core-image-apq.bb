LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://../COPYING;md5=11c1d78c92548a586eafd0c08349534b"

inherit image_types
inherit multistrap-image

#IMAGE_INSTALL = "image-base"
IMAGE_FSTYPES = "ext4"
IMAGE_LINGUAS = " "
IMAGE_ROOTFS_SIZE = "8192"

fixup_sysroot() {
    install ${WORKDIR}/config.sh ${IMAGE_ROOTFS}/config.sh
    install ${WORKDIR}/prima-init.sh ${IMAGE_ROOTFS}/etc/network/prima-init.sh
    install -b -S .upstart ${WORKDIR}/init ${IMAGE_ROOTFS}/sbin/init
    install -m 644 ${WORKDIR}/serial-console.conf ${IMAGE_ROOTFS}/etc/init/serial-console.conf
    install -m 644 ${WORKDIR}/interfaces ${IMAGE_ROOTFS}/etc/network/interfaces
    install -m 644 ${WORKDIR}/wpa_supplicant.conf ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant.conf
    install -m 644 -D ${WORKDIR}/authorized_keys ${IMAGE_ROOTFS}/root/.ssh/authorized_keys
    install -m 644 ${WORKDIR}/fstab ${IMAGE_ROOTFS}/etc/fstab
    echo "ttyHLS0" >> ${IMAGE_ROOTFS}/etc/securetty
    sed -i -e 's/DEFAULT_RUNLEVEL=2/DEFAULT_RUNLEVEL=1/' ${IMAGE_ROOTFS}/etc/init/rc-sysinit.conf
    sed -i -e 's/rmdir/rm -rf/' ${IMAGE_ROOTFS}/var/lib/dpkg/info/base-files.postinst
    find ${IMAGE_ROOTFS} -name \*.rules | grep -v -f ${WORKDIR}/udev_files_to_keep.grep | xargs rm -f
}

IMAGE_PREPROCESS_COMMAND = "fixup_sysroot"

SRC_URI += " \
   file://apt.conf \
   file://multistrap.conf \
   file://COPYING \
   file://authorized_keys \
   file://config.sh \
   file://fstab \
   file://init \
   file://interfaces \
   file://multistrap.conf \
   file://prima-init.sh \
   file://serial-console.conf \
   file://wpa_supplicant.conf \
   file://udev_files_to_keep.grep \
   "
