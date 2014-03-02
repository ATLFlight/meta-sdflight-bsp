DESCRIPTION = "This recipe includes the core packages needed for an apq system."
AUTHOR = "Gene W. Marsh <gmarsh@codeaurora.org>"

LICENSE = "BSD-3-Clause-Clear" 
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=af4568eb99af15f8fbea8230e6762581" 

inherit image_types
inherit multistrap-image

SRC_URI += " \
   file://adb.conf \
   file://apt.conf \
   file://multistrap.conf \
   file://authorized_keys \
   file://config.sh \
   file://fstab \
   file://init \
   file://interfaces \
   file://multistrap.conf \
   file://wpa_supplicant.conf \
   file://udev_files_to_keep.grep \
   "

DEPENDS += "virtual/kernel virtual/wlan-module"
DEPENDS += "reboot2fastboot android-tools diag testtools serial-console"

PV = "CLAR-0018-rc"

#IMAGE_INSTALL = "image-base"
IMAGE_FSTYPES = "ext4"
IMAGE_LINGUAS = " "
IMAGE_ROOTFS_SIZE_ext4 = "1800000"

# Overall multistrap configuration
#     - From this the multistrap.conf file will be generated

# define the possible multistrap sections
#   Each section will specify how multistrap will download the dpkg from
MULTISTRAP_SECTIONS = "Raring Modules Packages"

# The value of the default section must be one of the sections listed in the above MULTISTRAP_SECTIONS variable
MULTISTRAP_DEFAULT_SECTION = "Packages"

MULTISTRAP_GENERAL_noauth = 'true'
MULTISTRAP_GENERAL_unpack = 'true'
MULTISTRAP_GENERAL_arch   = 'armhf'
MULTISTRAP_GENERAL_configscript = '@WORKDIR@/config.sh'


MULTISTRAP_SOURCE_Raring = "http://ports.ubuntu.com"
MULTISTRAP_SUITE_Raring = "raring"
MULTISTRAP_COMPONENTS_Raring = "main universe"
MULTISTRAP_DEBOOTSTRAP_Raring = "1"
MULTISTRAP_APTSOURCES_Raring = "1"


MULTISTRAP_SOURCE_Modules = "copy://${DEPLOY_DIR}/deb/${MACHINE_ARCH} ./"
MULTISTRAP_DEBOOTSTRAP_Modules = "1"
MULTISTRAP_APTSOURCES_Modules = "0"

MULTISTRAP_SOURCE_Packages = "copy://${DEPLOY_DIR}/deb/${TUNE_PKGARCH} ./"
MULTISTRAP_DEBOOTSTRAP_Packages = "1"
MULTISTRAP_APTSOURCES_Packages = "0"


# Define package groups here, which are lists of packages and the multistrap section they belong to
#      If a package group is defined and the multistrap section is not defined, it will be placed in the default section

# Some explanation for why some of these packages are included
# - vim-tiny, less: For basic editing
# - apt: Obvious
# - module-init-tools: For dealing with kernel modules
# - Networking:
#    - iputils-ping openssh-*: For basic networking
#    - iproute: To use iproute2 for network link management
#    - wpasupplicant: To manage Wi-Fi
#    - wireless-tools: To get iwconfig family of tools (deprecated) to manually manage Wi-Fi

PACKAGE_GROUP_ubuntu = "ubuntu-minimal vim-tiny less apt perl iputils-ping openssh-client openssh-server iproute wpasupplicant wireless-tools module-init-tools strace tcpdump iperf logrotate expect file gcc udhcpd bluetooth bluez bluez-tools obexftp python-gobject python-dbus ussp-push"
MULTISTRAP_SECTION_ubuntu = "Raring"

MULTISTRAP_SECTION_kernelmods = "Modules"

PACKAGE_GROUP_userpkgs = "reboot2fastboot android-tools diag testtools serial-console"
MULTISTRAP_SECTION_userpkgs = "Packages"


fixup_conf() {
    # Convert flat directories to package repositories
    CURDIR=`pwd`
    for dir in `ls ${DEPLOY_DIR}/deb`
      do
         cd ${DEPLOY_DIR}/deb/${dir}
         dpkg-scanpackages . /dev/null | gzip -9c > Packages.gz
         dpkg-scansources . /dev/null | gzip -9c > Sources.gz
      done
    cd ${CURDIR}
    # Replace place holders with build system values.
    sed -e "s|@DEPLOY_DIR@|${DEPLOY_DIR}|" -e "s|@MACHINE_ARCH@|${MACHINE_ARCH}|" -e "s|@WORKDIR@|${WORKDIR}|" -e "s|@TUNE_PKGARCH@|${TUNE_PKGARCH}|" -e "s|@QRL_MACHINE_MODULES@|${QRL_MACHINE_MODULES}|" -i ${WORKDIR}/multistrap.conf
}

MULTISTRAP_PREPROCESS_COMMAND = "fixup_conf"

fixup_sysroot() {
    install ${WORKDIR}/config.sh ${IMAGE_ROOTFS}/config.sh
    install -b -S .upstart ${WORKDIR}/init ${IMAGE_ROOTFS}/sbin/init
    install -m 644 ${WORKDIR}/fstab ${IMAGE_ROOTFS}/etc/fstab
    install -m 644 ${WORKDIR}/interfaces ${IMAGE_ROOTFS}/etc/network/interfaces
    install -m 644 ${WORKDIR}/wpa_supplicant.conf ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant.conf
    install -m 644 -D ${WORKDIR}/authorized_keys ${IMAGE_ROOTFS}/root/.ssh/authorized_keys
    echo ${PN}-${PV}-`date '+%F-%T'`-`id -un` > ${IMAGE_ROOTFS}/etc/clarence-version
    sed -i -e 's/DEFAULT_RUNLEVEL=2/DEFAULT_RUNLEVEL=1/' ${IMAGE_ROOTFS}/etc/init/rc-sysinit.conf
    sed -i -e 's/rmdir/rm -rf/' ${IMAGE_ROOTFS}/var/lib/dpkg/info/base-files.postinst
    find ${IMAGE_ROOTFS} -name \*.rules | grep -v -f ${WORKDIR}/udev_files_to_keep.grep | xargs rm -f

}

IMAGE_PREPROCESS_COMMAND = "fixup_sysroot"
