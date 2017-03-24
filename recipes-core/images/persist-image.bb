SUMMARY = "8x74 persist partition image"

# Cache partition is 32M
IMAGE_ROOTFS_SIZE = "32768"

inherit image
inherit android-partition

IMAGE_INSTALL = ""
IMAGE_LINGUAS = ""

IMAGE_PREPROCESS_COMMAND = "remove_extra_files; "

PACKAGE_INSTALL = "${IMAGE_INSTALL}"

