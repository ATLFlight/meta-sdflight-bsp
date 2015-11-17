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
SRC_URI += "file://10-hook_config_syslog.chroot"

SRCREV = "${AUTOREV}"
LINARO_IMG_NAME = "trusty-armhf-developer"
PARTITION_USERDATA_SIZE = "8G"
######################################################################
# This recipe uses Linaro's tools to build a root filesystem.
# 
# There's no getting around the need for root access to build a Linux
# root filesystem. There are two ways to accomplish this:
#    1. The cleanest way is to set up a virtual machine (a "chroot")
#       on the build machine, and run as root inside that.
#    2. Provide password-less sudo privilege to the user doing the
#       build.
# 
# This recipe checks for the value of SCHROOT_NAME variable (local.conf).
# Depending on what it's set to, the following happens:
#    - DEFAULT:     Checks for existence of an schroot instance with
#                   the default name "<username>_ubuntu_trusty" and uses it
#    - NO_CHROOT:   Doesn't use chroot. Instead does everything using sudo
#    - <some name>: Checks for existence of an schroot instance
#                   with specified name, and uses it
#                   
# Please create such an schroot instance and specify it for use by
# this recipe, or provide password-less sudo.
#
# schroot:
# ========
# To setup an schroot instance:
# - First install these packages on your build machine:
#      - schroot, debootstrap
# - Now set up schroot filesystem using debootstrap. E.g. the following
#   sets up trusty, at the location <dir>, by downloading from
#   archive.ubuntu.com.
#   > sudo debootstrap --no-check-gpg --variant=buildd --arch amd64 trusty <dir> http://archive.ubuntu.com/ubuntu
# - Add the schroot to /etc/schroot/schroot.conf, with the name you
#   desire.
# - Add the schroot name to build/conf/local.conf
# See "man schroot" for details
#
# sudo:
# =====
# - First install these packages on your build machine:
#      - schroot, debootstrap
# - Configure password-less sudo access. E.g. the following provides
#   password-less sudo access to user "build_acct":
#   > sudo visudo
#     <Add to end of file this line>--+
#                                     |
#                                     v
#     build_acct ALL = (ALL) NOPASSWD: ALL
# - Set the schroot name in build/conf/local.conf to NO_CHROOT
######################################################################


do_unpack_append() {
    import shutil
    import os
    import glob
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    li = d.getVar('LINARO_IMG_NAME',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
    # Copy all the hook files 1*-hook*chroot to the customizations directory
    for hook in glob.glob(wd+"/1*-hook*chroot"):
        bb.note(hook)
        shutil.copy(hook, s+'/'+li+'/customization/hooks')
}

create_image_sudo() {
   # If the persist directory does not exist, we should create one with an
   # empty image file.
   if [ ! -e ${DEPLOY_DIR_IMAGE}/out ]; then
       mkdir -p ${DEPLOY_DIR_IMAGE}/out
   fi

   cmd="sudo rm -rf binary/dev"
   bbnote ${cmd}
   eval ${cmd}
   cmd="sudo mkdir binary/dev"
   bbnote ${cmd}
   eval ${cmd}
   cmd="sudo ${STAGING_BINDIR_NATIVE}/make_ext4fs -s -l ${PARTITION_USERDATA_SIZE} \
        ${DEPLOY_DIR_IMAGE}/out/userdata.img binary"
   bbnote ${cmd}
   eval ${cmd}
}

create_image_schroot() {
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

customize_sudo() {
   cmd="sudo cp ${WORKDIR}/fstab binary/etc/"
   bbnote ${cmd}
   eval ${cmd}
   cmd="sudo cp ${WORKDIR}/qrlConfig.conf binary/etc/init/"
   bbnote ${cmd}
   eval ${cmd}
   cmd="sudo cp ${WORKDIR}/qrlNetwork.conf binary/etc/init/"
   bbnote ${cmd}
   eval ${cmd}
   cmd="sudo cp ${WORKDIR}/interfaces binary/etc/network/"
   bbnote ${cmd}
   eval ${cmd}
}

customize_schroot() {
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
   # If specified chroot name is "NO_CHROOT"
   if [ ${SCHROOT_NAME} == "NO_CHROOT" ]
   then
       bbnote "Not using chroot. Checking if this user has sudo privileges"
       sudo ls
       if [ $? == 0 ]
       then
	   bbnote "Looks like this user has sudo. Proceeding without chroot"
	   bbplain "Found:NO_CHROOT"
	   return 0
       fi
   fi
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

#
# check_inst_pkg_*: Check for a package, and install if necessary
# name: name of package
# ver : required version, or '' if no specific version is required
# dir : directory from where to install. If '', use apt-get install instead
# 
check_inst_pkg_sudo() {
   pkg=$1
   ver=$2
   dir=$3
   installCmd=''
   if [ "${dir}" == '' ]
   then
       installCmd="sudo apt-get install --no-upgrade -y --allow-unauthenticated ${pkg}"
   else
       installCmd="sudo /usr/bin/dpkg --admindir=/var/lib/dpkg --install ${dir}/${pkg}*"
   fi

   # We expect the following command to fail
   set +e
   instVer=$(/usr/bin/dpkg-query --show --showformat='${Version}' --admindir=/var/lib/dpkg ${pkg})
   set -e

   if [ "${ver}" != '' ]
   then
      case "${instVer}" in
	   *${ver}*)
              # Right version is intalled
              return 0
	      ;;
	   '')
              # No version is intalled, so install the right version
	      if [ ${dir} == '' ]
	      then
		  installCmd="${installCmd}=${ver}"
	      else
		  installCmd="${installCmd}${ver}*"
	      fi
              # Don't return. Go on to install the package
	      ;;
           *)
              # Error: Some other version is intalled
              return 1
              ;;
       esac
   else
      case "${instVer}" in
	   '')
              # No version is intalled, install without worrying about version
	      ;;
	   *)
              # Some version is intalled, and we aren't picky, so return
	      return 0
	      ;;
      esac
   fi
   bbnote "Installing package using: ${installCmd}"
   set -e
   eval ${installCmd}
}

check_inst_pkg_schroot() {
   schroot=$1
   pkg=$2
   ver=$3
   dir=$4
   installCmd=''
   if [ "${dir}" == '' ]
   then
       installCmd="schroot -c ${schroot} -u root -d `pwd` -- apt-get install -y --allow-unauthenticated ${pkg}"
   else
       installCmd="schroot -c ${schroot} -u root -d `pwd` -- dpkg --install ${dir}/${pkg}*"
   fi

   set +e
   cmd="schroot -c ${schroot} -u root -d `pwd` -- dpkg-query --show --showformat='\${Version}' ${pkg}"
   instVer=$(eval ${cmd})
   set -e

   if [ "${ver}" != '' ]
   then
      case "${instVer}" in
	   *${ver}*)
              # Right version is intalled
              return 0
	      ;;
	   '')
              # No version is intalled, so install the right version
	      if [ ${dir} == '' ]
	      then
		  installCmd="${installCmd}=${ver}"
	      else
		  installCmd="${installCmd}${ver}*"
	      fi
              # Don't return. Go on to install the package
	      ;;
           *)
              # Error: Some other version is intalled
              return 1
              ;;
       esac
   else
      case "${instVer}" in
	   '')
              # No version is intalled, install without worrying about version
	      ;;
	   *)
              # Some version is intalled, and we aren't picky, so return
	      return 0
	      ;;
      esac
   fi
   bbnote "Installing package using: ${installCmd}"
   eval ${installCmd}
}

check_inst_debootstrap_sudo() {
   pkg=$1
   ver=$2
   dir=$3
   installCmd=''

   . /etc/lsb-release
   if [ ${DISTRIB_CODENAME} == "precise" ]
   then
       # On precise, we will just download the right version of debootstrap and use dpkg --install
       getCmd="wget --quiet ${dir}/d/${pkg}/${pkg}_${ver}_all.deb"
       bbnote "On precise, getting debootstrap using: ${getCmd}"
       eval ${getCmd}
       installCmd="sudo /usr/bin/dpkg --admindir=/var/lib/dpkg --install ./${pkg}*"
   else
       # On others, we will use apt-get install
       installCmd="sudo apt-get install -y --allow-unauthenticated ${pkg}"
   fi
   # We expect the following command to fail
   set +e
   instVer=$(/usr/bin/dpkg-query --show --showformat='${Version}' --admindir=/var/lib/dpkg ${pkg})
   set -e

   case "${instVer}" in
       *${ver}*)
           # Right version is intalled
           return 0
	   ;;
       '')
           # No version is intalled, so install the right version
           # Don't return. Go on to install the package
	   :
	   ;;
       *)
           # Some other version is intalled. If on precise, that's a problem, else not
           if [ ${DISTRIB_CODENAME} == "precise" ]
	   then
	       return 1
	   else
	       return 0
	   fi
           ;;
   esac
   bbnote "Installing package using: ${installCmd}"
   set -e
   eval ${installCmd}
}
   
check_pkgs_sudo() {
   set -x
   check_inst_debootstrap_sudo 'debootstrap' '1.0.40~ubuntu0.9' 'http://mirrors.kernel.org/ubuntu/pool/main/'
   set +x
   check_inst_pkg_sudo 'live-build' '3.0.5-1linaro1' '../packages'
   check_inst_pkg_sudo 'qemu-user-static' '' '../packages'
   check_inst_pkg_sudo 'binfmt-support' '' ''
}

check_pkgs_schroot() {
   schroot=$1
   set -x
   check_inst_pkg_schroot 'debootstrap' '' ''
   set +x
   check_inst_pkg_schroot ${schroot} 'live-build' '3.0.5-1linaro1' '../packages'
   check_inst_pkg_schroot ${schroot} 'qemu-user-static' '1.3.0-2012.12-0ubuntu1~linaro1' '../packages'
   check_inst_pkg_schroot ${schroot} 'sudo' '' ''
   check_inst_pkg_sudo 'binfmt-support' '' ''
}

image_sudo() {
   cd ${LINARO_IMG_NAME}
   check_pkgs_sudo
   ./configure
   set +e
   make
   set -e
   
   customize_sudo ${schroot}
   create_image_sudo ${schroot}
}

image_schroot() {
   schroot=$1
   cd ${LINARO_IMG_NAME}
   check_pkgs_schroot ${schroot}
   cmd="schroot -c ${schroot} -u root -p -d `pwd` -- ./configure"
   bbnote "${cmd}"
   eval ${cmd}
   cmd="schroot -c ${schroot} -u root -p -d `pwd` -- make"
   bbnote "${cmd}"
   eval ${cmd}
   
   customize_schroot ${schroot}
   create_image_schroot ${schroot}
}

image_clean_sudo() {
   sudo make clean
   sudo rm -rf cache chroot
}

image_clean_schroot() {
   schroot=$1
   cmd="schroot -c ${schroot} -u root -p -d `pwd` -- make clean"
   eval ${cmd}
   cmd="schroot -c ${schroot} -u root -p -d `pwd` -- rm -rf cache chroot"
   eval ${cmd}
}
######################################################################
# Tasks
######################################################################

do_image() {
   # Find the chroot name
   # The function get_schroot_name will return all its stdout, so we
   # need to delete everything from start till the "Found:" string
   rawName=$(get_schroot_name)
   rawName=${rawName##*Found:}
   schroot=${rawName}
   if [ ${schroot} == "NO_CHROOT" ]
   then
       bbnote "do_image using sudo-based method"
       image_sudo
   else
       bbnote "do_image using schroot-based method"
       image_schroot ${schroot}
   fi
}

do_image_clean() {
   cd ${LINARO_IMG_NAME}
   rawName=$(get_schroot_name)
   rawName=${rawName##*Found:}
   schroot=${rawName}
   if [ ${schroot} == "NO_CHROOT" ]
   then
       image_clean_sudo
   else
       image_clean_schroot ${schroot}
   fi
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
addtask image_clean before do_clean

