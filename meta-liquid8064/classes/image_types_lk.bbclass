inherit image_types kernel-arch

oe_mkimage () {
    mkbootimg --kernel ${DEPLOY_DIR_IMAGE}/zImage \
    	      --ramdisk /dev/null \
    	      --cmdline "noinitrd console=ttyHSL0,115200,n8 root=/dev/mmcblk0p13 rw rootwait" \
	      --base 0x80200000 \
	      --pagesize 2048
	      --output ${DEPLOY_DIR_IMAGE}/$1 
}

COMPRESSIONTYPES += "lk"

COMPRESS_DEPENDS.lk = "android_tools-native"
COMPRESS_CMD_lk = "oe_mkimage ${IMAGE_NAME}"

#COMPRESS_DEPENDS_gz.lk = "android_tools-native"
#COMPRESS_CMD_gz.lk = "${COMPRESS_CMD_gz}; oe_mkimage boot.img"
#COMPRESS_DEPENDS_gz.u-boot = "u-boot-mkimage-native"
#COMPRESS_CMD_gz.u-boot      = "${COMPRESS_CMD_gz}; oe_mkimage ${IMAGE_NAME}.rootfs.${type}.gz gzip"

IMAGE_TYPES += "ext4.lk"

