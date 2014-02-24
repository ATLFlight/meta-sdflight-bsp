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

LK_ROOT_DEV ?= "/dev/mmcblk0p13"
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
  ${STAGING_BINDIR_NATIVE}/mkbootimg --kernel ${WORKDIR}/linux-${MACHINE}-standard-build/arch/arm/boot/zImage \
	--ramdisk /dev/null \
        --cmdline "noinitrd console=${serialport},${baudrate},n8 root=${LK_ROOT_DEV} rw rootwait ${LK_CMDLINE_OPTIONS}" \
	--base 0x80200000 \
        --pagesize 2048 \
	--output ${DEPLOY_DIR_IMAGE}/${PN}-boot-${MACHINE}.img
}

addtask lk_mkimage after do_deploy and before do_package

DEPENDS += "android-tools-native"

