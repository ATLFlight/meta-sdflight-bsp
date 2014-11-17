# This file was derived from the linux-yocto-custom.bb recipe in
# oe-core.
#
# linux-yocto-custom.bb:
#
#   A yocto-bsp-generated kernel recipe that uses the linux-yocto and
#   oe-core kernel classes to apply a subset of yocto kernel
#   management to git managed kernel repositories.
#
# Warning:
#
#   Building this kernel without providing a defconfig or BSP
#   configuration will result in build or boot errors. This is not a
#   bug.
#
# Notes:
#
#   patches: patches can be merged into to the source git tree itself,
#            added via the SRC_URI, or controlled via a BSP
#            configuration.
#
#   example configuration addition:
#            SRC_URI += "file://smp.cfg"
#   example patch addition:
#            SRC_URI += "file://0001-linux-version-tweak.patch
#   example feature addition:
#            SRC_URI += "file://feature.scc"
#

KTAG_som8064 = "AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041" 
KBRANCH_som8064 = "linux-${MACHINE}"
KBRANCH_DEFAULT = "linux-${MACHINE}"

require include/linux-caf.inc

SRC_URI = "git://codeaurora.org/kernel/msm.git;tag=AU_LINUX_ANDROID_KK_2.7_RB1.04.04.02.007.041;protocol=git;bareclone=1;nobranch=1"
SRC_URI += "file://defconfig \
            file://som8064.scc \
            file://som8064.cfg \
            file://som8064-user-config.cfg \
            file://som8064-user-patches.scc \
           "

SRC_URI += "https://releases.linaro.org/14.09/ubuntu/ifc6410/initrd.img-3.4.0-linaro-ifc6410;downloadfilename=initrd.img;name=initrd"
SRC_URI[initrd.md5sum] = "d92fb01531698e30615f26efa2999c6c"
SRC_URI[initrd.sha256sum] = "d177ba515258df5fda6d34043261d694026c9e27f1ef8ec16674fa479c5b47fb"

SRC_URI += "https://www.codeaurora.org/cgit/quic/la/kernel/msm/patch/\?id=e6fffec6636ffa3062891bae69a3895bbb73b148;patch=1;downloadfilename=arm-7668-1-fix-memset-related-crashes-caused-by-recent-gcc-4.7.2-optimizations.patch;name=memsetPatch1"
SRC_URI[memsetPatch1.md5sum] = "f6c2c2bdfd9471c02024ed05b143271f"
SRC_URI[memsetPatch1.sha256sum] = "9901c8c1171b2f529bfeb033acabc811aea4fea6dc65e8d6134317e1c518d4cd"
SRC_URI += "https://www.codeaurora.org/cgit/quic/la/kernel/msm/patch/\?id=d1814ea12da067fda7bac933e06ef205163617f6;patch=1;downloadfilename=arm-7670-1-fix-the-memset-fix.patch;name=memsetPatch2"
SRC_URI[memsetPatch2.md5sum] = "74dfd49a0d50fb88a3750956275510ad"
SRC_URI[memsetPatch2.sha256sum] = "afd3fa8f7d5f72ac4707e16bdb0b680f54fafa56cb1e20de7ce2eb0a2e6ecf9d"

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

PR = "r0"
PV = "${LINUX_VERSION}+git${SRCPV}"

GCCVERSION="4.8%"

COMPATIBLE_MACHINE_som8064 = "som8064"
LINUX_VERSION_EXTENSION_som8064 = "-som8064"

PROVIDES += "kernel-module-cfg80211"

## Override bluetooth kernel components
do_kernel_checkout_append() {
    btsrc=${COREBASE}/../kernel-v3.4.66
    btdst=${WORKDIR}/linux 
    # Note that at this point we are in a headless state, that will
    # be converted to a branch (KERNEL_BRANCH) in do_patch.
    	
    # Copy baseline bluetooth
    /bin/cp -fr ${btsrc}/net/bluetooth/* ${btdst}/net/bluetooth
    /bin/cp -fr ${btsrc}/include/net/bluetooth/* ${btdst}/include/net/bluetooth
    /bin/cp -fr ${btsrc}/drivers/bluetooth/* ${btdst}/drivers/bluetooth
	
    curdir=`pwd`
    cd ${btdst}
    git status
    echo "Commiting baseline bluetooth"
    git add -A
    git commit -m "Updated bluetooth baseline" 
    cd ${curdir}
}


# copied from kernel.bbclass in meta to override the find command
kernel_do_install() {
	#
	# First install the modules
	#
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE
	if (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
		oe_runmake DEPMOD=echo INSTALL_MOD_PATH="${D}" modules_install
		rm "${D}/lib/modules/${KERNEL_VERSION}/build"
		rm "${D}/lib/modules/${KERNEL_VERSION}/source"
		# If the kernel/ directory is empty remove it to prevent QA issues
		rmdir --ignore-fail-on-non-empty "${D}/lib/modules/${KERNEL_VERSION}/kernel"
	else
		bbnote "no modules to install"
	fi

	#
	# Install various kernel output (zImage, map file, config, module support files)
	#
	install -d ${D}/${KERNEL_IMAGEDEST}
	install -d ${D}/boot
	install -m 0644 ${KERNEL_OUTPUT} ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
	install -m 0644 System.map ${D}/boot/System.map-${KERNEL_VERSION}
	install -m 0644 .config ${D}/boot/config-${KERNEL_VERSION}
	install -m 0644 vmlinux ${D}/boot/vmlinux-${KERNEL_VERSION}
	[ -e Module.symvers ] && install -m 0644 Module.symvers ${D}/boot/Module.symvers-${KERNEL_VERSION}
	install -d ${D}${sysconfdir}/modules-load.d
	install -d ${D}${sysconfdir}/modprobe.d

	#
	# Support for external module building - create a minimal copy of the
	# kernel source tree.
	#
	kerneldir=${D}${KERNEL_SRC_PATH}
	install -d $kerneldir

	#
	# Store the kernel version in sysroots for module-base.bbclass
	#

	echo "${KERNEL_VERSION}" > $kerneldir/kernel-abiversion

	#
	# Store kernel image name to allow use during image generation
	#

	echo "${KERNEL_IMAGE_BASE_NAME}" >$kerneldir/kernel-image-name

	#
	# Copy the entire source tree. In case an external build directory is
	# used, copy the build directory over first, then copy over the source
	# dir. This ensures the original Makefiles are used and not the
	# redirecting Makefiles in the build directory.
	#
        # Original find command , remove the source dir exclusion
	#find . -depth -not -name "*.cmd" -not -name "*.o" -not -name "*.so.dbg" -not -path "./Documentation*" -not -path "./source*" -not -path "./.*" -print0 | cpio --null -pdlu $kerneldir
	find . -depth -not -name "*.cmd" -not -name "*.o" -not -name "*.so.dbg" -not -path "./Documentation*" -not -path "./.*" -print0 | cpio --null -pdlu $kerneldir
	cp .config $kerneldir
	if [ "${S}" != "${B}" ]; then
		pwd="$PWD"
		cd "${S}"
		find . -depth -not -path "./Documentation*" -not -path "./.*" -print0 | cpio --null -pdlu $kerneldir
		cd "$pwd"
	fi

	# Test to ensure that the output file and image type are not actually
	# the same file. If hardlinking is used, they will be the same, and there's
	# no need to install.
	! [ ${KERNEL_OUTPUT} -ef $kerneldir/${KERNEL_IMAGETYPE} ] && install -m 0644 ${KERNEL_OUTPUT} $kerneldir/${KERNEL_IMAGETYPE}
	install -m 0644 System.map $kerneldir/System.map-${KERNEL_VERSION}

	# Dummy Makefile so the clean below works
        mkdir $kerneldir/Documentation
        touch $kerneldir/Documentation/Makefile

	#
	# Clean and remove files not needed for building modules.
	# Some distributions go through a lot more trouble to strip out
	# unecessary headers, for now, we just prune the obvious bits.
	#
	# We don't want to leave host-arch binaries in /sysroots, so
	# we clean the scripts dir while leaving the generated config
	# and include files.
	#
	oe_runmake -C $kerneldir CC="${KERNEL_CC}" LD="${KERNEL_LD}" clean _mrproper_scripts 

	# hide directories that shouldn't have their .c, s and S files deleted
	for d in tools scripts lib; do
		mv $kerneldir/$d $kerneldir/.$d
	done

	# delete .c, .s and .S files, unless we hid a directory as .<dir>. This technique is 
	# much faster than find -prune and -exec
	find $kerneldir -not -path '*/\.*' -type f -name "*.[csS]" -delete

	# put the hidden dirs back
	for d in tools scripts lib; do
		mv $kerneldir/.$d $kerneldir/$d
	done

	# As of Linux kernel version 3.0.1, the clean target removes
	# arch/powerpc/lib/crtsavres.o which is present in
	# KBUILD_LDFLAGS_MODULE, making it required to build external modules.
	if [ ${ARCH} = "powerpc" ]; then
		cp -l arch/powerpc/lib/crtsavres.o $kerneldir/arch/powerpc/lib/crtsavres.o
	fi

	# Necessary for building modules like compat-wireless.
	if [ -f include/generated/bounds.h ]; then
		cp -l include/generated/bounds.h $kerneldir/include/generated/bounds.h
	fi
	if [ -d arch/${ARCH}/include/generated ]; then
		mkdir -p $kerneldir/arch/${ARCH}/include/generated/
		cp -flR arch/${ARCH}/include/generated/* $kerneldir/arch/${ARCH}/include/generated/
	fi

	# Remove the following binaries which cause strip or arch QA errors
	# during do_package for cross-compiled platforms
	bin_files="arch/powerpc/boot/addnote arch/powerpc/boot/hack-coff \
	           arch/powerpc/boot/mktree scripts/kconfig/zconf.tab.o \
		   scripts/kconfig/conf.o scripts/kconfig/kxgettext.o"
	for entry in $bin_files; do
		rm -f $kerneldir/$entry
	done

	# kernels <2.6.30 don't have $kerneldir/tools directory so we check if it exists before calling sed
	if [ -f $kerneldir/tools/perf/Makefile ]; then
		# Fix SLANG_INC for slang.h
		sed -i 's#-I/usr/include/slang#-I=/usr/include/slang#g' $kerneldir/tools/perf/Makefile
	fi
}
