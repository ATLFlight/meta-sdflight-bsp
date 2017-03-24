SUMMARY = "sdflight factory partition image"

# Factory partition is 512M
IMAGE_ROOTFS_SIZE = "524288"

inherit image
inherit android-partition

IMAGE_INSTALL = ""
IMAGE_LINGUAS = ""

#IMAGE_INSTALL += "sdflight-factory-image"

IMAGE_PREPROCESS_COMMAND = "remove_extra_files; "

PACKAGE_INSTALL = "${IMAGE_INSTALL}"

