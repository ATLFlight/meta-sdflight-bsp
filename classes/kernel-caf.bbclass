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

do_patch_prepend() {
    # Avoid issues with createme/configme scripts
    # in multiple runs
    kver=${S}/.meta/cfg/kernel-cache/kver
    if [ -f $kver ]; then
        exit 0
    fi
}

do_kernel_checkout() {
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
