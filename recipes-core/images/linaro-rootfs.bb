LICENSE_PATH += "${COREBASE}/meta-qr-linux/files/custom-licenses"
LICENSE = "NOT_APPLICABLE"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/files/custom-licenses/LIC_NOT_APPLICABLE;md5=82052abe09eb68b997d7e9ec5c22e294"

PV = "1.0"
PR = "r1"
inherit native

SRC_URI += "git://git.linaro.org/ci/ubuntu-build-service;revision=${SRCREV};protocol=git"
SRC_URI += "file://0001-Remove-refs-to-localhost-so-it-builds-anywhere.patch"
SRC_URI += "file://fstab"
SRC_URI += "file://qrlConfig.conf"
SRC_URI += "file://qrlNetwork.conf"
SRC_URI += "file://interfaces"

SRCREV = "${AUTOREV}"
LINARO_IMG_NAME = "trusty-armhf-developer"
PARTITION_USERDATA_SIZE = "8G"
######################################################################
# This recipe uses Linaro's tools to build a root filesystem.
# 
# There's no getting around the need for root access to build a Linux
# root filesystem, so the most secure way to do this is to set up a
# virtual machine (a "chroot") and run as root inside that.
# 
# This recipe checks for the existence of an schroot instance (with
# a name specified in local.conf, or name "<username>_ubuntu_trusty".
# Please create such an schroot instance and specify it for use by
# this recipe.
# The schroot will also need the following packages involved
#    - debootstrap (and its dependencies)
#    - sudo
#    - live-build and qemu-user-static from ubuntu-build-services.git
######################################################################


do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

create_image() {
   schroot=$1
   # If the persist directory does not exist, we should create one with an
   # empty image file.
   if [ ! -e ${DEPLOY_DIR_IMAGE}/out ]; then
       mkdir -p ${DEPLOY_DIR_IMAGE}/out
   fi

   cmd="schroot -c ${schroot} -u root -d `pwd` -- rm -rf binary/dev"
   bbnote ${cmd}
   eval ${cmd}
   cmd="schroot -c ${schroot} -u root -d `pwd` -- mkdir binary/dev"
   bbnote ${cmd}
   eval ${cmd}
   cmd="schroot -c ${schroot} -u root -d `pwd` -- ${STAGING_BINDIR_NATIVE}/make_ext4fs -s -l ${PARTITION_USERDATA_SIZE} \
        ${DEPLOY_DIR_IMAGE}/out/userdata.img binary"
   bbnote ${cmd}
   eval ${cmd}
}

customize() {
   schroot=$1
   cmd="schroot -c ${schroot} -u root -d `pwd` -- cp ${WORKDIR}/fstab binary/etc/"
   bbnote ${cmd}
   eval ${cmd}
   cmd="schroot -c ${schroot} -u root -d `pwd` -- cp ${WORKDIR}/qrlConfig.conf binary/etc/init/"
   bbnote ${cmd}
   eval ${cmd}
   cmd="schroot -c ${schroot} -u root -d `pwd` -- cp ${WORKDIR}/qrlNetwork.conf binary/etc/init/"
   bbnote ${cmd}
   eval ${cmd}
   cmd="schroot -c ${schroot} -u root -d `pwd` -- cp ${WORKDIR}/interfaces binary/etc/network/"
   bbnote ${cmd}
   eval ${cmd}
}

get_schroot_name() {
   # Compute what chroot name to use. If specified in local.conf, use that
   # value, else if the var is set to DEFAULT, compute the name
   schroot=
   if [ ${SCHROOT_NAME} == "DEFAULT" ]
   then
       schroot="`id -u -n`_ubuntu_trusty"
       bbnote "Will look for default chroot on this machine: ${schroot}"
   else
       schroot=${SCHROOT_NAME}
       bbnote "Will look for specified chroot on this machine: ${schroot}"
   fi
   cmd="schroot -l | grep ${schroot}"
   eval ${cmd}
   bbplain "Found:${schroot}"
}

check_pkg() {
   schroot=$1
   pkg=$2
   ver=$3
   cmd="schroot -c ${schroot} -u root -d `pwd` -- dpkg-query --show --showformat='\${Version}' ${pkg}"
   instVer=$(eval ${cmd})

   if [ "${ver}" != "" ]
   then
       case "${instVer}" in
	   *${ver}*)
              return 0
	      ;;
           *)
              return 1
              ;;
       esac
   fi
   return 0
}
   
check_schroot() {
   schroot=$1
   check_pkg ${schroot} 'live-build' '3.0.5-1linaro1'
   check_pkg ${schroot} 'qemu-user-static' ''
   check_pkg ${schroot} 'sudo' ''

}

do_configure() {
   cd ${LINARO_IMG_NAME}
   ./configure
}
    
do_image() {
   cd ${LINARO_IMG_NAME}
   # Find the chroot name
   # The function get_schroot_name will return all its stdout, so we
   # need to delete everything from start till the "Found:" string
   rawName=$(get_schroot_name)
   rawName=${rawName##*Found:}
   schroot=${rawName}
   check_schroot ${schroot}
   cmd="schroot -c ${schroot} -u root -p -d `pwd` -- make"
   bbnote "${cmd}"
   eval ${cmd}
   
   customize ${schroot}
   create_image ${schroot}
}

do_image_clean() {
   cd ${LINARO_IMG_NAME}
   rawName=$(get_schroot_name)
   rawName=${rawName##*Found:}
   schroot=${rawName}
   cmd="schroot -c ${schroot} -u root -p -d `pwd` -- make clean"
   eval ${cmd}
}

do_build[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"
do_update_package[depends] = "imgdiff-native:do_populate_sysroot"
do_update_package[depends] += "ext4-utils-native:do_populate_sysroot"
addtask image before do_build after do_patch do_compile
addtask image_clean

