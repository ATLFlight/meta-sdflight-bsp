inherit kernel-yocto

# find the master/machine source tag. In the same way that the fetcher proceses
# git repositories in the SRC_URI we take the first repo found, first tag.
def get_machine_tag(d, default):
    fetch = bb.fetch2.Fetch([], d)
    for url in fetch.urls:
        urldata = fetch.ud[url]
        parm = urldata.parm
        if "tag" in parm:
            tags = urldata.parm.get("tag").split(',')
            return tags[0]
	    
    return default

do_kernel_checkout() {
	set +e

	# A linux yocto SRC_URI should use the bareclone option. That
	# ensures that all the branches are available in the WORKDIR version
	# of the repository.
	source_dir=`echo ${S} | sed 's%/$%%'`
	source_workdir="${WORKDIR}/git"
	if [ -d "${WORKDIR}/git/" ] && [ -d "${WORKDIR}/git/.git" ]; then
		# case2: the repository is a non-bare clone

		# if S is WORKDIR/git, then we shouldn't be moving or deleting the tree.
		if [ "${source_dir}" != "${source_workdir}" ]; then
			rm -rf ${S}
			mv ${WORKDIR}/git ${S}
		fi
		cd ${S}
	elif [ -d "${WORKDIR}/git/" ] && [ ! -d "${WORKDIR}/git/.git" ]; then
		# case2: the repository is a bare clone

		# if S is WORKDIR/git, then we shouldn't be moving or deleting the tree.
		if [ "${source_dir}" != "${source_workdir}" ]; then
			rm -rf ${S}
			mkdir -p ${S}/.git
			mv ${WORKDIR}/git/* ${S}/.git
			rm -rf ${WORKDIR}/git/
		fi
		cd ${S}	
		git config core.bare false
	else
		# case 3: we have no git repository at all. 
		# To support low bandwidth options for building the kernel, we'll just 
		# convert the tree to a git repo and let the rest of the process work unchanged
		
		# if ${S} hasn't been set to the proper subdirectory a default of "linux" is 
		# used, but we can't initialize that empty directory. So check it and throw a
		# clear error

	        cd ${S}
		if [ ! -f "Makefile" ]; then
			echo "[ERROR]: S is not set to the linux source directory. Check "
			echo "         the recipe and set S to the proper extracted subdirectory"
			exit 1
		fi
		git init
		git add .
		git commit -q -m "baseline commit: creating repo for ${PN}-${PV}"
	fi
	# end debare

	machine_branch="${@ get_machine_branch(d, "${KBRANCH}" )}"
        machine_tag="${@ get_machine_tag(d, "${KTAG}" )}"
	# convert any remote branches to local tracking ones
	for i in `git branch -a | grep remotes | grep -v HEAD`; do
		b=`echo $i | cut -d' ' -f2 | sed 's%remotes/origin/%%'`;
		git show-ref --quiet --verify -- "refs/heads/$b"
		if [ $? -ne 0 ]; then
			git branch $b $i > /dev/null
		fi
	done

	# If a tag has been specified, check out the tag. Otherwise,
	# check out the machine branch.
	if [ -n "${machine_tag}" ]; then 
	    echo "Checking out ${machine_tag}"
	    git checkout -f "${machine_tag}"
	elif [ -n "${machine_branch}" ]; then
	    echo "Checking out ${machine_branch}"
	    git checkout -f "refs/heads/${machine_branch}"
	else
	    echo "Checking out master"
	    git checkout -f master
	fi

}

LK_ROOT_DEV ?= ""
LK_CMDLINE_OPTIONS ?= ""

do_lk_mkimage() {
  # Make bootimage
  ver=`sed -r 's/#define UTS_RELEASE "(.*)"/\1/' ${WORKDIR}/image/usr/src/kernel/include/generated/utsrelease.h`
  # Update base address according to new memory map.
  if [ ! -z "${SERIAL_CONSOLES}" ] ; then
      serialport=`echo "${SERIAL_CONSOLES}" | sed 's/.*\;//'`
      baudrate=`echo "${SERIAL_CONSOLES}" | sed 's/\;.*//'`
  else
      serialport="ttyHSL0"
      baudrate="115200"
  fi

  kernel_workdir=${WORKDIR}/linux-${MACHINE}-standard-build
  kernel_imgdir=${kernel_workdir}/arch/arm/boot
  kernel_image=${kernel_imgdir}/zImage
  cmd_line="console=${serialport},${baudrate},n8 root=${LK_ROOT_DEV} rw rootwait ${LK_CMDLINE_OPTIONS}"

  # Build final boot image
  # We depend on an initrd.img file being present in the WORKDIR. This will
  # usually be downloaded by the recipe

  # Build with device tree
  set -x
  if [ -n "${QRLINUX_DTB}" ] ;
  then
     master_dt_image=${kernel_imgdir}/masterDTB

     # Create new dtb header ot be appended to kernel image
     # This is rquired for LK bootloader compatibility, otherwise its not required. 
     ${STAGING_BINDIR_NATIVE}/dtbtool -o ${master_dt_image} -p ${kernel_workdir}/scripts/dtc/ -v ${kernel_imgdir}/

     ${STAGING_BINDIR_NATIVE}/mkbootimg --kernel ${kernel_image} \
	   --dt ${master_dt_image} \
       --base 0x80200000 \
       --ramdisk ${WORKDIR}/initrd.img \
       --ramdisk_offset 0x02D00000 \
       --cmdline "${cmd_line}" \
       --pagesize 2048 \
       --output ${DEPLOY_DIR_IMAGE}/boot-${MACHINE}.img
     cp ${master_dt_image} ${DEPLOY_DIR_IMAGE}/
  else
     ${STAGING_BINDIR_NATIVE}/mkbootimg --kernel ${kernel_image} \
       --base 0x80200000 \
       --ramdisk ${WORKDIR}/initrd.img \
       --ramdisk_offset 0x02D00000 \
       --cmdline "${cmd_line}" \
       --pagesize 2048 \
       --output ${DEPLOY_DIR_IMAGE}/boot-${MACHINE}.img
  fi
  install -d ${DEPLOY_DIR_IMAGE}/out
  cp ${DEPLOY_DIR_IMAGE}/boot-${MACHINE}.img ${DEPLOY_DIR_IMAGE}/out/boot.img
  set +x
}

do_compile_dtb() {
	if [ -n "${QRLINUX_DTB}" ] ;
	then
		echo "Building device tree ${QRLINUX_KERNEL_DEVICE_TREE}..."
		oe_runmake ${QRLINUX_DTB}
	else
		echo "Device tree not enabled"
	fi
}

addtask lk_mkimage after do_deploy and before do_package
addtask compile_dtb after do_compile_kernelmodules before do_strip

DEPENDS += "android-tools-native"
DEPENDS += "dtbtool-native"
