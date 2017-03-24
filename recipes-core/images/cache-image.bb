SUMMARY = "8x74 firmware image (cache partition)"

# Cache partition is 32M
IMAGE_ROOTFS_SIZE = "32767"

inherit image
inherit android-partition

IMAGE_LINGUAS = ""

IMAGE_INSTALL = "adreno200-firmware"
IMAGE_INSTALL += "mm-video-firmware"
IMAGE_INSTALL += "sdk-add-on-firmware"
IMAGE_INSTALL += "ath6kl-firmware"
IMAGE_INSTALL += "mm-camera-firmware"

# Only ar3k is copied via bbappend file
IMAGE_INSTALL += "linux-firmware-ar3k"

PACKAGE_INSTALL = "${IMAGE_INSTALL}"

# Create an empty partition
IMAGE_PREPROCESS_COMMAND = "remove_extra_files; "

IMAGE_PREPROCESS_COMMAND += "move_firmware_files; "
