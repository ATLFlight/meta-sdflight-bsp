DESCRIPTION = "This recipe includes the core packages needed for an apq system."
AUTHOR = "Gene W. Marsh <gmarsh@codeaurora.org>"

LICENSE = "BSD-3-Clause-Clear" 
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

inherit image_types
inherit multistrap-image

SRC_URI += " \
   file://adb.conf \
   file://apt.conf \
   file://config.sh.in \
   file://fstab \
   file://init \
   file://installPkgs.sh \
   file://interfaces \
   file://multistrap.conf \
   file://wpa_supplicant.conf \
   file://udev_files_to_keep.grep \
   "

DEPENDS += "virtual/kernel"

PV = "CLAR-0018"

IMAGE_FSTYPES = "ext4"
IMAGE_LINGUAS = " "

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
MULTISTRAP_BUILD_Raring = "0"

MULTISTRAP_SOURCE_Modules = "copy://${DEPLOY_DIR}/deb/${MACHINE_ARCH} ./"
MULTISTRAP_DEBOOTSTRAP_Modules = "1"
MULTISTRAP_APTSOURCES_Modules = "0"
MULTISTRAP_BUILD_Modules = "1"

MULTISTRAP_SOURCE_Packages = "copy://${DEPLOY_DIR}/deb/${TUNE_PKGARCH} ./"
MULTISTRAP_DEBOOTSTRAP_Packages = "1"
MULTISTRAP_APTSOURCES_Packages = "0"
MULTISTRAP_BUILD_Packages = "1"

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

PACKAGE_GROUP_ubuntu = "ubuntu-minimal vim-tiny less apt perl iputils-ping openssh-client openssh-server iproute wpasupplicant wireless-tools module-init-tools strace tcpdump iperf build-essential logrotate expect file bluetooth bluez bluez-tools obexftp python-gobject python-dbus ussp-push"
MULTISTRAP_SECTION_ubuntu = "Raring"

PACKAGE_GROUP_userpkgs = "reboot2fastboot android-tools serial-console"
MULTISTRAP_SECTION_userpkgs = "Packages"
DEPENDS += "configdb"
DEPENDS += "dsutils"
DEPENDS += "diag"
DEPENDS += "mp-decision"
DEPENDS += "qcom-common"
DEPENDS += "qmi"
DEPENDS += "qmi-framework"
DEPENDS += "thermal"
DEPENDS += "xmllib"

IMAGE_FEATURES += "ubuntu userpkgs"

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
    # Set file system root in config.sh
    cp ${WORKDIR}/config.sh.in ${WORKDIR}/config.sh
    sed -e "s|@LK_ROOT_DEV@|${LK_ROOT_DEV}|" -i ${WORKDIR}/config.sh
    # Replace place holders with build system values.
}

MULTISTRAP_PREPROCESS_COMMAND = "fixup_conf"

fixup_sysroot() {
    install ${WORKDIR}/config.sh ${IMAGE_ROOTFS}/config.sh
    install ${WORKDIR}/installPkgs.sh ${IMAGE_ROOTFS}/installPkgs.sh
    install -b -S .upstart ${WORKDIR}/init ${IMAGE_ROOTFS}/sbin/init
    install -m 644 ${WORKDIR}/fstab ${IMAGE_ROOTFS}/etc/fstab
    install -m 644 ${WORKDIR}/interfaces ${IMAGE_ROOTFS}/etc/network/interfaces
    install -m 644 ${WORKDIR}/wpa_supplicant.conf ${IMAGE_ROOTFS}/etc/wpa_supplicant/wpa_supplicant.conf
    echo ${PN}-${PV}-`date '+%F-%T'`-`id -un` > ${IMAGE_ROOTFS}/etc/clarence-version
    sed -i -e 's/DEFAULT_RUNLEVEL=2/DEFAULT_RUNLEVEL=1/' ${IMAGE_ROOTFS}/etc/init/rc-sysinit.conf
    sed -i -e 's/rmdir/rm -rf/' ${IMAGE_ROOTFS}/var/lib/dpkg/info/base-files.postinst
    find ${IMAGE_ROOTFS} -name \*.rules | grep -v -f ${WORKDIR}/udev_files_to_keep.grep | xargs rm -f

}

IMAGE_PREPROCESS_COMMAND = "fixup_sysroot"
